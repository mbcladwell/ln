(ns ln.db-retriever
  (:require [clojure.java.jdbc :as j]
            [honeysql.core :as hsql]
            [honeysql.helpers :refer :all :as helpers]
            [clojure.data.csv :as csv]
            [clojure.java.io :as io])
           
  (:import java.sql.DriverManager))



(defn authenticate-user
  ;;variable from codax
  []
  (let [user (ln.session/get-user)
        password (ln.session/get-password)
        results (j/query pg-db ["SELECT id, password FROM lnuser WHERE lnuser_name = ?"  user ])]
    (if (= password (results :password)))
    (do );;valid
    (do );;invalid
    ))

;;(authenticate-user)
