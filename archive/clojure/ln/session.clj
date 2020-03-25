(ns ln.session
  (:require 
            ;;[honeysql.core :as hsql]
   ;;[honeysql.helpers :refer :all :as helpers]
   [codax.core :as c]
   [clojure.data.csv :as csv]
   [ln.codax-manager :as cm]
   [clojure.java.browse :as browse]
   [next.jdbc :as j]  
   [ln.dialog :as d])
  (:import javax.swing.JOptionPane)
  (:gen-class ))

;;https://push-language.hampshire.edu/t/calling-clojure-code-from-java/865
;;(open-props-if-exists)


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
    



(defn login-to-database
  ;;if user is blank or auto-login is false, pop up the login dialog
  ;;store results, validate results, and start dbm
  []

  (if (cm/get-init)
  (ln.DialogPropertiesNotFound.(cm/get-all-props))
  (if(or (clojure.string/blank? (cm/get-user))
         (not (cm/get-auto-login)))
    (do
      (d/login-dialog)
      (loop [completed? (realized? d/p)] ;;p is the promise the dialog has completed
                                        ;;codax open continuously to this point
      (if (eval completed?)
        (do        
          (authenticate-user)      
        (if(cm/get-authenticated)
          (do
            (register-session (cm/get-user-id))
            (ln.DatabaseManager.))
          (do
            (cm/set-auto-login false)
            (JOptionPane/showMessageDialog nil "Invalid login credentials!" ))))
      (recur  (realized? d/p)))));; the if is true i.e. need a login dialog
    (do
      (register-session (cm/get-user-id))
      (ln.DatabaseManager. )))))  ;;if is false - can auto-login

;;(cm/set-u-p-al "aaa" "bbb" false)

;;(login-to-database)

(defn open-help-page [s]
  (browse/browse-url (str (cm/get-help-url-prefix) s)))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]    
  (login-to-database ))

;;(-main)



;;https://cb.codes/a-tutorial-of-stuart-sierras-component-for-clojure/

