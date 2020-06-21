#
# This is a Shiny web application. You can run the application by clicking
# the 'Run App' button above.
#
# Find out more about building applications with Shiny here:
#
#    http://shiny.rstudio.com/
#

library(shiny)
library(DBI)
library(pool)
        
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
         sliderInput("bins",
                     "Number of bins:",
                     min = 1,
                     max = 50,
                     value = 30)
      ),
      
      # Show a plot of the generated distribution
      mainPanel(
        DT::dataTableOutput("tbl")
      )
   )
)

# Define server logic required to draw a histogram
server <- function(input, output) {
  
  output$tbl <- DT::renderDataTable({
 ##   sql <- "SELECT  project_sys_name as "Project", descr as "Description" FROM project;"
 ##   query <- sqlInterpolate(pool, sql)
    dbGetQuery(pool, "SELECT project_sys_name AS \"ID\", descr as \"Description\", project_name as \"Name\"  FROM project")
  })
}

# Run the application 
shinyApp(ui = ui, server = server)

