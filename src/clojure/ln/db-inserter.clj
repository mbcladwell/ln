(ns ln.db
  (:require [clojure.java.jdbc :as j]
            [honeysql.core :as hsql]
            [honeysql.helpers :refer :all :as helpers]
            [clojure.data.csv :as csv]
            [clojure.java.io :as io])
           
  (:import java.sql.DriverManager))


(def pg-db  {:dbtype "postgresql"
            :dbname "lndb"
            :host "127.0.0.1"
            :user "ln_admin"
             :password "welcome"
             :port "5432"
            :ssl false
            :sslfactory "org.postgresql.ssl.NonValidatingFactory"})

(j/insert! pg-db :plate {:barcode "fhd23j4j5"
                            :plate_type_id 1
                            :plate_format_id 96
                            :plate_layout_name_id 1})

  (j/execute! pg-db
              ["insert into plate (barcode, plate_type_id, plate_format_id, plate_layout_name_id) values (?,?,?,?) " "38ie8eu474" 1 96 1]
              {:return-keys? true})


 (j/db-do-return-prepared-keys pg-db
              ["insert into plate (barcode, plate_type_id, plate_format_id, plate_layout_name_id) values (?,?,?,?) " "38ie8eu474" 1 96 1]
              )


  
