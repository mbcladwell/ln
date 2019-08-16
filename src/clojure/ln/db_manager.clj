(ns ln.db-manager
  (:require [next.jdbc :as j]
            [ln.codax-manager :as cm])
  (:gen-class))

;;used to validate user
(def pg-db-admin  {:dbtype "postgresql"
                   :dbname (cm/get-dbname)
                   :host (cm/get-host)
                   :user "ln_admin"
                   :password "welcome"
                   :port (cm/get-port)
                   :ssl (cm/get-sslmode)
                   :sslfactory "org.postgresql.ssl.NonValidatingFactory"})

(def pg-db-admin-test  {:dbtype "postgresql"
                        :dbname (cm/get-dbname)
                        :host (cm/get-host)
                        :user "klohymim"
                        :password "hwc3v4_rbkT-1EL2KI-JBaqFq0thCXM_"
                        :port (cm/get-port)
                        :ssl (cm/get-sslmode)
                        :sslfactory "org.postgresql.ssl.NonValidatingFactory"})


(defn define-pg-db []
;;connection used for authenticated user
  (def pg-db  (j/get-datasource {:dbtype "postgresql"
                                 :dbname (cm/get-dbname)
                                 :host (cm/get-host)
                                 :user (cm/get-user)
                                 :password (cm/get-password)
                                 :port (cm/get-port)
                                 :ssl (cm/get-sslmode)
                                 :sslfactory "org.postgresql.ssl.NonValidatingFactory"})))

;;(println pg-db)

(defn  get-connection-string [target]	  
  (case target
    "heroku" (str "jdbc:postgresql://"  (cm/get-host) ":" (cm/get-port)  "/" (cm/get-dbname) "?sslmode=require&user=" (cm/get-user) "&password="  (cm/get-password))
    "local" (str "jdbc:postgresql://" (cm/get-host) "/" (cm/get-dbname))	   
    "elephantsql" (str "jdbc:postgresql://" (cm/get-host) ":" (cm/get-port) "/" (cm/get-dbname) "?user=" (cm/get-user) "&password=" (cm/get-password) "&SSL=true" )
"test" (str "jdbc:postgresql://" (cm/get-host) ":" (cm/get-port) "/" (cm/get-dbname) "?user=" (cm/get-user) "&password=" (cm/get-password) "&SSL=true" )))
;;(get-connection-string "heroku")
