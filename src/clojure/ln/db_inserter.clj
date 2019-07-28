(ns ln.db-inserter
  (:require [clojure.java.jdbc :as j]
            [honeysql.core :as hsql]
            [honeysql.helpers :refer :all :as helpers]
            [ln.db-manager :as dbm]
            [ln.codax-manager :as cm])
         ;;   [clojure.data.csv :as csv]
           ;; [clojure.java.io :as io])
           
  (:import java.sql.DriverManager))



