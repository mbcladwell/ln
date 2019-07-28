(ns ln.db-manager
  (:require [clojure.java.jdbc :as j]
            [ln.codax-manager :as cm])
  (:gen-class))



(def pg-db  {:dbtype "postgresql"
            :dbname "lndb"
            :host (cm/get-host)
            :user (cm/get-user)
             :password (cm/get-password)
             :port (cm/get-port)
            :ssl (cm/get-sslmode)
            :sslfactory "org.postgresql.ssl.NonValidatingFactory"})



(defn  get-connection-string [target]	  
  (case target
  	"heroku" (str "jdbc:postgresql://"  (cm/get-host) ":" (cm/get-port)  "/" (cm/get-dbname) "?sslmode=require&user=" (cm/get-user) "&password="  (cm/get-password))
	  "local" (str "jdbc:postgresql://" (cm/get-host) "/" (cm/get-dbname))	   
	  "elephantsql" (str "jdbc:postgresql://" (cm/get-host) ":" (cm/get-port) "/" (cm/get-dbname) "?user=" (cm/get-user) "&password=" (cm/get-password) "&SSL=true" )))

;;(get-connection-string "heroku")
