(ns ln.session
  (:require 
            ;;[honeysql.core :as hsql]
            ;;[honeysql.helpers :refer :all :as helpers]
            [clojure.data.csv :as csv]
            [ln.codax-manager :as cm]          
           ;; [ln.db-inserter :as dbi]
            [ln.db-retriever :as dbr]
            [ln.dialog :as d])
   (:import javax.swing.JOptionPane)
  (:gen-class ))

;;https://push-language.hampshire.edu/t/calling-clojure-code-from-java/865
;;(open-props-if-exists)

(defn login-to-database
  ;;if user is blank or auto-login is false, pop up the login dialog
  ;;store results, validate results, and start dbm
  []
  (if(or (clojure.string/blank? (cm/get-user))
         (not (cm/get-auto-login)))
    (do
      (d/login-dialog)
      (cm/set-u-p-al (d/returned-login-map :name)
                  (d/returned-login-map :password)
                  (str (d/returned-login-map :store)))
     
      (dbr/authenticate-user)
      (if(cm/get-authenticated)
        (ln.DatabaseManager.)
        (JOptionPane/showMessageDialog nil "Invalid login credentials!" )));; the if is true i.e. need a login dialog
    (ln.DatabaseManager. )))  ;;if is false - can auto-login

;;(cm/set-u-p-al "aaa" "bbb" false)

;;(login-to-database)

(defn -main
  "I don't do a whole lot ... yet."
  [& args]    
  (login-to-database ))

;;(-main)



;;https://cb.codes/a-tutorial-of-stuart-sierras-component-for-clojure/

