#
# This is a Shiny web application. You can run the application by clicking
# the 'Run App' button above.
#
# Find out more about building applications with Shiny here:
#
#    http://shiny.rstudio.com/
#
# http://zevross.com/blog/2016/04/19/r-powered-web-applications-with-shiny-a-tutorial-and-cheat-sheet-with-40-example-apps/


library(shiny)
library(DBI)
library(pool)
library(ggplot2)
        
        pool <- dbPool(
          drv = RPostgres::Postgres(),
          dbname = "lndb",
          host = "192.168.1.11",
          user = "ln_admin",
          password = "welcome",
          bigint = c("integer64", "integer", "numeric", "character")
        )

# Define UI for application that draws a histogram
ui <- fluidPage(
   
   # Application title
   titlePanel("LIMS*Nucleus"),
   
   # Sidebar with a slider input for number of bins 
   sidebarLayout(
        sidebarPanel(
          selectInput("response", "Response:",
                      c("Background Subtracted" = 0,
                        "Normalized" = 1,
                        "Norm + control" = 2,
                        "% enhanced" = 3)),
          selectInput("threshold", "Threshold:",
                      c("mean pos" = 1,
                        "mean neg + 2sd" = 2,
                        "mean neg + 3sd" = 3))),
      # Show a plot of the generated distribution
      mainPanel(
        plotOutput("plot1", click = "plot_click")
      )
   )
)

# Define server logic required to draw a histogram
server <- function(input, output) {
  
  output$plot1 <- renderPlot({
 ##   sql <- "SELECT  project_sys_name as "Project", descr as "Description" FROM project;"
 ##   query <- sqlInterpolate(pool, sql)
    d <-dbGetQuery(pool, "SELECT * FROM get_scatter_plot_data(1)")
    d2 <-dbGetQuery(pool, "SELECT * FROM assay_run_stats where assay_run_id =1;")
    
    response <- as.numeric(input$response)
    threshold <- as.numeric(input$threshold)
    
    ## 0 raw
    ## 1 norm
    ## 2 norm_pos
    ## 3 p_enhanced
    if(response ==0){
      ylabel <- "Background Substracted"
      df <- d[,c(2,3,4,8)]}
    if(response ==1){
      ylabel <- "Normalized"
      df <- d[,c(2,3,5,8)]}
    if(response ==2){
      ylabel <- "Normalized to Positive Control"
      df <- d[,c(2,3,6,8)]}
    if(response ==3){
      ylabel <- "% Enhanced"
      df <- d[,c(2,3,7,8)]}
    
    
    ## Threshold
    ## 1  mean-pos
    ## 2  mean-neg-2-sd
    ## 3  mean-neg-3-sd
    
    if(threshold ==1){
     threshold.text <- "> mean(pos)"
      threshold_num <- d2[d2$response_type==response, "mean_pos" ]
    }
    if(threshold ==2){
    threshold.text <- "mean(neg) + 2SD"
      threshold_num <- d2[d2$response_type==response, "mean_neg_2_sd" ]
    }
    if(threshold ==3){
     threshold.text <- "mean(neg) + 3SD"
      threshold_num <- d2[d2$response_type==response, "mean_neg_3_sd" ]
    }
    
    names(df) <- c("plate","well","response","type")
    df <- df[order(df$response),]
    df$index <- (nrow(df)):1
    num.hits <- nrow(df[df$response > threshold_num,])
    mycols <- c(4="grey",3="black",1= "green",2="red")
    names(mycols) <- levels(df$type)
    
    ##print(df)
    ggplot(df, aes(index, response )) + geom_point(col=df$type) + ylab(ylabel) + xlab("Index") +
       geom_abline(slope=0, intercept=threshold_num, linetype="dotted")  +
       geom_text(label=paste0(threshold.text, "; hits: ", num.hits ), x= nrow(df)*0.1 , y=threshold_num - 0.05*threshold_num) +
      theme(legend.position="bottom",legend.box = "horizontal") +
    scale_color_manual(name="type", values=c(4="grey",3="black",1= "green",2="red"))
    
##    legend("topright",  c("unknown","positive","negative","blank"), fill=c("white", "green", "red", "grey"))
    
})
}

# Run the application 
shinyApp(ui = ui, server = server)

