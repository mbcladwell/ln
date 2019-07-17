(ns ln.session
  (:use [ln dialog] :reload)

  (:require [clojure.java.jdbc :as sql]
            [honeysql.core :as hsql]
            [honeysql.helpers :refer :all :as helpers]
            [clojure.data.csv :as csv]
            [codax.core :as c]
            [clojure.java.io :as io])
  (:import java.sql.DriverManager)
  (:gen-class ))

(def props "")


(defn set-user [u]
  (c/with-write-transaction [props tx]
        (c/assoc-at tx [:assets :conn :user] u)))


(defn set-password [p]
  (c/with-write-transaction [props tx]
        (c/assoc-at! tx  [:assets :conn :password] p)))



(defn get-host []
   (c/get-at! props [:assets :conn :host]))

(defn get-port []
  (c/get-at! props [:assets :conn :port]))

(defn get-source []
  (c/get-at! props [:assets :conn :source]))

(defn get-dbname []
  (c/get-at! props [:assets :conn :dbname]))

(defn get-user []
  (c/get-at! props [:assets :conn :user]))

(defn get-password []
  (c/get-at! props [:assets :conn :password]))


(defn set-ln-props [ path-to-db ]
    (def props (c/open-database! path-to-db))  )

(defn create-ln-props
  [ host port dbname source sslmode user password]
  (def props (c/open-database! (str (java.lang.System/getProperty "user.dir") "/ln-props")))
  (c/with-write-transaction [props tx]
  (c/assoc-at tx [:assets :conn] {:host host
	                              :port port
	                              :sslmode sslmode	              
                                      :dbname dbname
                                      :source source
                                      :password password
	                              :user user	  })))


;;(create-ln-props "127.0.0.1" "5432" "lndb" false "ln_admin" "welcome")

(defn  get-connection-string [target]	  
  (case target
  	"heroku" (str "jdbc:postgresql://"  (get-host) ":" (get-port)  "/" (get-dbname) "?sslmode=require&user=" (get-user) "&password="  (get-password))
	  "local" (str "jdbc:postgresql://" (get-host) "/" (get-dbname))	   
	  "elephantsql" (str "jdbc:postgresql://" (get-host) ":" (get-port) "/" (get-dbname) "?user=" (get-user) "&password=" (get-password) "&SSL=true" )))

;;(get-connection-string "heroku")


(defn open-props-if-exists
  ;;1. check working directory - /home/user/my-working-dir
  ;;2. check home directory      /home/user
  ;;3. launch DialogPropertiesNotFound()
  []
  (if (.exists (io/as-file "ln-props"))
    (def props (c/open-database! "ln-props"))  
    (if (.exists (io/as-file (str (java.lang.System/getProperty "user.home") "/ln-props") ))
      (def props (c/open-database! (str (java.lang.System/getProperty "user.home") "/ln-props") ))
      (ln.DialogPropertiesNotFound.))))

;;https://push-language.hampshire.edu/t/calling-clojure-code-from-java/865
;;(open-props-if-exists)




(defn login-to-database
  []
  (if(clojure.string/blank? (c/get-at! props [:assets :conn :user]))
    (do
      (println "before login dialog request")
       (login-dialog)
      (println "after login dialog request")
       (if(ln.dialog/returned-login-map :store)
           (do
             (set-user (ln.dialog/returned-login-map :name))
             (set-password (ln.dialog/returned-login-map :password))
             (ln.DatabaseManager. ) ;;login after storing user/pass
             (ln.DatabaseManager. )) ;;ELSE login without storing
           ) ;;if store name is blank
    (ln.DatabaseManager. ))  ;;if store has no user
    (ln.DatabaseManager. )  ;;there is a user
    ))

;;(login-to-database)

(defn get-all-props
  ;;note that the keys must be quoted for java
  []
  (into {} (java.util.HashMap.
           {":host" (c/get-at! props [:assets :conn :host])
            ":port" (c/get-at! props [:assets :conn :port])
           ":sslmode" (c/get-at! props [:assets :conn :sslmode])
          ":source" (c/get-at! props [:assets :conn :source])
          ":dbname" (c/get-at! props [:assets :conn :dbname])
          ":help-url-prefix" (c/get-at! props [:assets :conn :help-url-prefix])
          ":password" (c/get-at! props [:assets :conn :password])
          ":user" (c/get-at! props [:assets :conn :user])})))

(defn get-all-props-clj
  ;;a map for clojure
  [] 
  ({:host (c/get-at! props [:assets :conn :host])
    :port (c/get-at! props [:assets :conn :port])
    :sslmode (c/get-at! props [:assets :conn :sslmode])
    :source (c/get-at! props [:assets :conn :source])
    :dbname (c/get-at! props [:assets :conn :dbname])
    :help-url-prefix (c/get-at! props [:assets :conn :help-url-prefix])
    :password (c/get-at! props [:assets :conn :password])
    :user (c/get-at! props [:assets :conn :user])}))



(defn print-all-props []
  (do
    (println ":conn in ln-props")
    (println "------------")
    (println (str "Host: " (c/get-at! props [:assets :conn :host]) ))
    (println (str "Port: " (c/get-at! props [:assets :conn :port]) ))
    (println (str "ssl-mode: " (c/get-at! props [:assets :conn :sslmode]) ))
    (println (str "Source: " (c/get-at! props [:assets :conn :source]) ))
    (println (str "dbname: " (c/get-at! props [:assets :conn :dbname]) ))
    (println (str "help-url-prefix: " (c/get-at! props [:assets :conn :help-url-prefix]) ))
    (println (str "password: " (c/get-at! props [:assets :conn :password]) ))
    (println (str "user: " (c/get-at! props [:assets :conn :user]) ))))


  (defn print-ap 
    "This version prints everything"
    []
    (println (str "Whole map: " (c/get-at! props []) )))

  ;;(print-ap)
  ;;(print-all-props)
  


(defn set-user-id [i]
  (c/with-write-transaction [props tx]
        (c/assoc-at tx  [:assets :session :user-id] i)))

;;(set-user-id 100)

(defn get-user-id []
  (c/get-at! props [:assets :session :user-id ]))


(defn set-user-group [s]
    (c/with-write-transaction [props tx]
        (c/assoc-at tx  [:assets :session :user-group] s)))

(defn get-user-group []
  (c/get-at! props [:assets :session :user-group ]))


(defn get-user-group-id []
  (c/get-at! props [:assets :session :user-group-id ]))

(defn set-user-group-id [i]
    (c/with-write-transaction [props tx]
        (c/assoc-at tx  [:assets :session :user-group-id] i)))


(defn set-project-id [i]
  (c/with-write-transaction [props tx]
    (c/assoc-at  [:assets :session :project-id] i)))

(defn get-project-id []
  (c/get-at! props [:assets :session :project-id ]))

(defn set-project-sys-name [s]
    (c/with-write-transaction [props tx]

        (c/assoc-at tx  [:assets :session :project-sys-name] s)))

(defn get-project-sys-name []
  (c/with-write-transaction [props tx]
    (c/get-at tx [:assets :session :project-sys-name ])))

  (defn set-plate-set-sys-name [s]
      (c/with-write-transaction [props tx]

        (c/assoc-at tx  [:assets :session :plate-set-sys-name] s)))

(defn get-plate-set-sys-name []
  (c/get-at! props [:assets :session :plate-set-sys-name ]))

  (defn set-plate-set-id [i]
      (c/with-write-transaction [props tx]

        (c/assoc-at tx  [:assets :session :plate-set-id ] i)))

(defn get-plate-set-id []
  (c/get-at! props [:assets :session :plate-set-id ]))


(defn set-plate-id [i]
   (c/with-write-transaction [props tx]
        (c/assoc-at tx  [:assets :session :plate-id ] i)))

(defn get-plate-id []
  (c/get-at! props [:assets :session :plate-id ]))


(defn get-session-id []
  (c/get-at! props [:assets :session :session-id ]))

  (defn set-session-id [i]
      (c/with-write-transaction [props tx]
        (c/assoc-at tx  [:assets :session :session-id] i)))
  
(defn get-home-dir []
   (java.lang.System/getProperty "user.home"))
  
(defn get-temp-dir []
   (java.lang.System/getProperty "java.io.tmpdir"))
             
  
(defn get-working-dir []
   (java.lang.System/getProperty "user.dir"))


    (defn get-help-url-prefix []
        (c/with-write-transaction [props tx]
          (c/assoc-at tx [:assets :props :help-url-prefix ])))



      (defn set-authenticated [b]
          (c/with-write-transaction [props tx]
        (c/assoc-at tx  [:assets :session :authenticated] b)))

(defn get-authenticated []
  (c/get-at! props [:assets :session :authenticated ]))


(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (open-props-if-exists)
  (println "opened-props")
  (login-to-database)
  (println "logged in to db"))

;;(-main)

;;(c/close-database! props)
