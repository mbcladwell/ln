(ns ln.db-retriever
  (:require [next.jdbc :as j]
            [honeysql.core :as hsql]
            [honeysql.helpers :refer :all :as helpers]
           ;; [ln.db-manager :as dbm]
            [ln.codax-manager :as cm]
            [ln.db-manager :as dbm])
         ;;   [clojure.data.csv :as csv]
          ;;  [clojure.java.io :as io])
            
  (:import java.sql.DriverManager))




(defn authenticate-user
  ;;variable from codax
  []
  (let [user (cm/get-user)
        password (cm/get-password)
        results (j/execute-one! dbm/pg-db-admin ["SELECT lnuser.id, lnuser.password, lnuser_groups.id, lnuser_groups.usergroup  FROM lnuser, lnuser_groups  WHERE lnuser_groups.id = lnuser.usergroup and lnuser_name = ?"  user ])]
        (println (str "user: " user))
        (println password)
        (println results)
    (if (= password (:lnuser/password results) )
      (do
        (println "before uid ugid ug auth")
        (println  ( :lnuser_groups/usergroup results))
        (cm/set-uid-ugid-ug-auth
           (:lnuser/id  results)          
           ( :lnuser_groups/id results)
           ( :lnuser_groups/usergroup results)
           true)
        (println "after uid ugid ug auth")
          
          (dbm/define-pg-db));;valid
    (cm/set-authenticated false);;invalid
    )))

;;(authenticate-user)
;;(cm/print-ap)
;;(println dbm/pg-db)
;;(j/insert! dbm/pg-db :lnsession {:lnuser_id  1 } )


(defn register-session
  ;;user id
  [ uid ]
  (let [db (if (= (cm/get-source) "test") dbm/pg-db-admin-test dbm/pg-db-admin)
        user-id (j/execute-one! db ["INSERT INTO lnsession(lnuser_id) values(?)" uid]{:return-keys true} )
        ug-id (j/execute! db ["SELECT usergroup FROM lnuser WHERE lnuser.id =?"  (:lnsession/id user-id) ] )
        ]
    (cm/set-session-id  (:lnsession/id user-id) )
    (cm/set-user-group (:lnuser/usergroup ug-id))))

;;(register-session 3)
;;(:lnsession/id (j/execute-one! dbm/pg-db-admin ["INSERT INTO lnsession(lnuser_id) values(?)" 2]{:return-keys true} ))

;;(def db dbm/pg-db-admin)
;;(def user-id 1)
