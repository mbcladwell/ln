(ns ln.db-inserter
  (:require [clojure.java.jdbc :as j]
            [honeysql.core :as hsql]
            [honeysql.helpers :refer :all :as helpers]
           
            [ln.codax-manager :as cm]
            [ln.db-manager :as dbm])

         ;;    [ln.db-manager :as dbm])
         ;;   [clojure.data.csv :as csv]
           ;; [clojure.java.io :as io])
           
  (:import java.sql.DriverManager))



