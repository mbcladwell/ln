(ns ln.db-retriever
  (:require [clojure.java.jdbc :as j]
            [honeysql.core :as hsql]
            [honeysql.helpers :refer :all :as helpers]
            [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [ln.session :as s])         
  (:import java.sql.DriverManager))




(defn authenticate-user
  ;;variable from codax
  []
  (let [user (s/get-user)
        password (s/get-password)
        results (j/query s/pg-db ["SELECT lnuser.id, lnuser.password, lnuser_groups.id, lnuser_groups.groupname  FROM lnuser, lnuser_groups  WHERE lnuser_groups.id = lnuser.usergroup and lnuser_name = ?"  user ])]
    (if (= password (get (first results) :password))
      (do (s/set-uid-ugid-ug-auth
           (get (first results) :id)         
           (get (first results) :id_2)
           (get (first results) :groupname)
           true)
          (let [ result2 (j/insert! s/pg-db :lnsession {:lnuser_id  (get (first results) :id) } )]
           (s/set-session-id (get (first result2) :id)) ));;valid
    (s/set-authenticated false);;invalid
    )))

;;(authenticate-user)
;;(s/print-ap)

(j/insert! pg-db :lnsession {:lnuser_id  1 } )
