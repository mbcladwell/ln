(defproject ln "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :source-paths ["src/clojure/"]
  :java-source-paths ["src/java"]
  :resource-paths ["resources"]
 ;; :prep-tasks ["javac" "compile"]
  :javac-options     ["-target" "1.8" "-source" "1.8"]
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [cider/cider-nrepl "0.11.0-SNAPSHOT"]
                 [seesaw "1.5.0"]
                 [org.clojure/java.jdbc "0.7.5"]
                 [java-jdbc/dsl "0.1.3" ]
                 [seancorfield/next.jdbc "1.0.5"]
     ;;            [org.xerial/sqlite-jdbc "3.7.2"]
                 [org.clojure/tools.namespace "0.2.11"]
                 [honeysql "0.9.1"]
                 [org.clojure/data.csv "0.1.4"]
                 [incanter/incanter-core "1.9.1"]
                 
                 [mysql/mysql-connector-java "8.0.11"]
    ;;             [javax.help/javahelp "2.0.05"]
                 [javax.swing/jlfgr "1.0"]
                 [leinjacker "0.4.2"]
                 [lein-codox "0.10.3"]
                 [codax "1.3.1"]
                 [com.google.guava/guava "23.0"]
                 [org.apache/poi "4.0.1"]
                 [org.apache/poi-ooxml "4.0.1"]
                 [org.apache.xmlbeans/xmlbeans "3.1.0"]
                 [org.apache.commons/commons-collections4 "4.2"]
                 [org.apache.commons/commons-compress"1.18"]
                 [org.apache.poi/ooxml-schemas "1.4"]
                 [org.postgresql/postgresql "42.2.5"]]


:repl-options {:nrepl-middleware
                 [cider.nrepl.middleware.apropos/wrap-apropos
                  cider.nrepl.middleware.classpath/wrap-classpath
                  cider.nrepl.middleware.complete/wrap-complete
                  cider.nrepl.middleware.info/wrap-info
                  cider.nrepl.middleware.inspect/wrap-inspect
                  cider.nrepl.middleware.macroexpand/wrap-macroexpand
                  cider.nrepl.middleware.ns/wrap-ns
                  cider.nrepl.middleware.resource/wrap-resource
                  cider.nrepl.middleware.stacktrace/wrap-stacktrace
                  cider.nrepl.middleware.test/wrap-test
                  cider.nrepl.middleware.trace/wrap-trace
                  cider.nrepl.middleware.undef/wrap-undef]}
  :jvm-opts ["-Xmx2G"]
;;  :main ^:skip-aot ln.session
  :main ln.session
  :aot [ ]
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
