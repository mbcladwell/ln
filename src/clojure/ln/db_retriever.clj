(ns ln.db-retriever
  (:require [clojure.java.jdbc :as j]
            [honeysql.core :as hsql]
            [honeysql.helpers :refer :all :as helpers]
            [ln.db-manager :as dbm]
            [ln.codax-manager :as cm])
         ;;   [clojure.data.csv :as csv]
          ;;  [clojure.java.io :as io])
            
  (:import java.sql.DriverManager))




(defn authenticate-user
  ;;variable from codax
  []
  (let [user (cm/get-user)
        password (cm/get-password)
        results (j/query dbm/pg-db ["SELECT lnuser.id, lnuser.password, lnuser_groups.id, lnuser_groups.groupname  FROM lnuser, lnuser_groups  WHERE lnuser_groups.id = lnuser.usergroup and lnuser_name = ?"  user ])]
    (if (= password (get (first results) :password))
      (do (cm/set-uid-ugid-ug-auth
           (get (first results) :id)         
           (get (first results) :id_2)
           (get (first results) :groupname)
           true)
          (let [ result2 (j/insert! dbm/pg-db :lnsession {:lnuser_id  (get (first results) :id) } )]
           (cm/set-session-id (get (first result2) :id)) ));;valid
    (cm/set-authenticated false);;invalid
    )))

;;(authenticate-user)
;;(cm/print-ap)

;;(j/insert! dbm/pg-db :lnsession {:lnuser_id  1 } )
