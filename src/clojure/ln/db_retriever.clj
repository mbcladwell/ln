(ns ln.db-retriever
  (:require [next.jdbc :as j]
            [codax.core :as c]
            ;;[honeysql.core :as hsql]
            ;;[honeysql.helpers :refer :all :as helpers]
            [ln.codax-manager :as cm])
          ;;  [ln.db-manager :as dbm])
         ;;   [clojure.data.csv :as csv]
          ;;  [clojure.java.io :as io])
            
  (:import java.sql.DriverManager))




(defn authenticate-user
  ;;variable from codax
  []
  (let [
        user (cm/get-user)
        password (cm/get-password)
        results (j/execute-one! cm/conn ["SELECT lnuser.id, lnuser.password, lnuser_groups.id, lnuser_groups.usergroup  FROM lnuser, lnuser_groups  WHERE lnuser_groups.id = lnuser.usergroup_id and lnuser_name = ?"  user ])]
    (if (= password (:lnuser/password results) )
      (do
        (cm/set-uid-ugid-ug-auth
           (get  results :lnuser/id)         
           (get results :lnuser_groups/id)
           (get  results :lnuser_groups/usergroup)
           true))
      (cm/set-authenticated false));;invalid
    ))

;;(authenticate-user)
;;(cm/print-ap)
;;(j/insert! dbm/pg-db :lnsession {:lnuser_id  1 } )


(defn register-session
  ;;user id
  [ uid ]
(let [
        user-id-pre (j/execute-one!  cm/conn  ["INSERT INTO lnsession(lnuser_id) values(?)" uid] )
        ;;user-id (first(vals  user-id-pre))  ;;ERROR!! insert gives the count, not the uid
        ug-id-pre (j/execute-one!  cm/conn ["SELECT usergroup_id FROM lnuser WHERE lnuser.id =?" uid ] )
        ug-id (first (vals  ug-id-pre))
        ug-name-pre (j/execute-one!  cm/conn ["SELECT usergroup FROM lnuser_groups WHERE id =?" uid ] )
        ug-name (first (vals  ug-name-pre))
        ]
    (c/with-write-transaction [cm/props tx]
    (-> tx
      (c/assoc-at  [:assets :session :user-id] uid )   
      (c/assoc-at  [:assets :session :user-group-id] ug-id)
      (c/assoc-at  [:assets :session :user-group] ug-name)))))
    

(defn get-layout-id-for-plate-set-sys-name [s]
  
(let [
        layout-id-pre (j/execute-one!  cm/conn  ["SELECT plate_layout_name_id FROM plate_set WHERE plate_set_sys_name = (?)" s] )
        layout-id (first(vals  layout-id-pre))
      ]
  layout-id
  ))

;;(get-layout-id-for-plate-set-sys-name "PS-6")
;;(register-session 2)
;;(:lnsession/id (j/execute-one! dbm/pg-db-admin ["INSERT INTO lnsession(lnuser_id) values(?)" 2]{:return-keys true} ))

(defn get-layout-name-descr-for-layout-id [i]
  
(let [
        results-pre (j/execute-one!  cm/conn  ["SELECT plate_layout_name.name, plate_layout_name.descr FROM  plate_layout_name WHERE plate_layout_name.id = (?)" i] )
        results (str (first(vals  results-pre)) ";" (first(rest(vals results-pre))))
      ]
  results
  ))

 
;;(get-layout-name-descr-for-layout-id 1)
