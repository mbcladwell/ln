(ns lnmanager.session
  (:require [clojure.java.jdbc :as sql]
            [honeysql.core :as hsql]
            [honeysql.helpers :refer :all :as helpers]
            [clojure.data.csv :as csv]
            [codax.core :as c]
            [clojure.java.io :as io])
  (:import java.sql.DriverManager)
  (:gen-class ))

(load "/lnmanager/db")
(load "/lnmanager/dialog")


(defn open-props-if-exists
  ;;1. check working directory - /home/user/my-working-dir
  ;;2. check home directory      /home/user
  ;;3. launch DialogPropertiesNotFound()
  []
  (if (.exists (io/as-file "ln-props"))
    (def props (c/open-database! "ln-props"))  
    (if (.exists (io/as-file (str (java.lang.System/getProperty "user.home") "/ln-props") ))
      (def props (c/open-database! (str (java.lang.System/getProperty "user.home") "/ln-props") ))
      (lnmanager.DialogPropertiesNotFound.))))

;;https://push-language.hampshire.edu/t/calling-clojure-code-from-java/865
;;(open-props-if-exists)


(defn setup-local-postgres-session []
(c/assoc-at! props [:conn] {:host "127.0.0.1"
	              :port "5432"
	              :sslmode "false"
	              :source "local"
                      :dbname "lndb"
                      :help-url-prefix "labsolns.com/software"
                      :password "welcome"
	              :user "ln-admin"	  
	              :temp-dir  (java.lang.System/getProperty "java.io.tmpdir")
	              :working-dir  (java.lang.System/getProperty "user.dir")
                      :home-dir  (java.lang.System/getProperty "user.home")}))
                                     
;;(c/get-at! props [:conn :port])

;;(c/update-at! props [:conn :source ]  {:source "dooby"})
;;(c/assoc-at! props [:conn :source ]  {:source "dooby"})


(defn set-ln-props [ path-to-db ]
    (def props (c/open-database! path-to-db))  )

(defn create-ln-props
  ;;
  [ host port sslmode user password]
  (def props (c/open-database! (str (java.lang.System/getProperty "user.dir") "/ln-props")))

(c/assoc-at! props [:assets :conn] {:host host
	              :port port
	              :sslmode sslmode
	              :source "local"
                      :dbname "lndb"
                      :help-url-prefix "labsolns.com/software"
                      :password password
	              :user user  
	              :temp-dir  (java.lang.System/getProperty "java.io.tmpdir")
	              :working-dir  (java.lang.System/getProperty "user.dir")
                            :home-dir  (java.lang.System/getProperty "user.home")}))

;;(create-ln-props "127.0.0.1" "5432" "false" "ln_admin" "welcome")

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
    (println "All ln-props")
    (println "------------")
    (println (str "Host: " (c/get-at! props [:assets :conn :host]) ))
    (println (str "Port: " (c/get-at! props [:assets :conn :port]) ))
    (println (str "ssl-mode: " (c/get-at! props [:assets :conn :sslmode]) ))
    (println (str "Source: " (c/get-at! props [:assets :conn :source]) ))
    (println (str "dbname: " (c/get-at! props [:assets :conn :dbname]) ))
    (println (str "help-url-prefix: " (c/get-at! props [:assets :conn :help-url-prefix]) ))
    (println (str "password: " (c/get-at! props [:assets :conn :password]) ))
    (println (str "user: " (c/get-at! props [:assets :conn :user]) ))))

(defn  get-connection-url [target]	  
  (case target
  	"heroku" (str "jdbc:postgresql://"  (get-host) ":" (get-port)  "/" (get-dbname) "?sslmode=require&user=" (get-user) "&password="  (get-password))
	  "local" (str "jdbc:postgresql://" (get-host) "/" (get-dbname));	   
	  "elephantsql" (str "jdbc:postgresql://" (get-host) ":" (get-port) "/" (get-dbname) "?user=" (get-user) "&password=" (get-password) "&SSL=true" )))

(get-connection-url (get-source)) 

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


(defn set-user [u]
        (c/assoc-at! props  [:assets :conn :user] u))


(defn set-password [p]
        (c/assoc-at! props  [:assets :conn :password] p))


;;(set-user "dopey")
;;(print-all-props)
;;(c/close-database! props)


(defn load-props
  [file-name]
  (with-open [^java.io.Reader reader (clojure.java.io/reader file-name)] 
    (let [props (java.util.Properties.)]
      (.load props reader)
      (into {} (for [[k v] props] [(keyword k) (read-string v)])))))




(defn setup-heroku []
:host  "ec2-50-19-114-27.compute-1.amazonaws.com";
:port  "5432";
:sslmode  "require";	   
  :dbname  "d6dmueahka0hch";
  :help_url_prefix  "http://labsolns.com/software/";
		:password  "c5644d221fa636d8d8065d336014723f66df0c6b78e7a5390453c4a18c9b20b2";
		:user  "dpstpnuvjslqch";
		:URL  (str "jdbc:postgresql://"  :host  ":" :port "/" :dbname  "?sslmode=require&user=" :user "&password=" :password ))


(defn setup-elephant-sql []
		:host  "raja.db.elephantsql.com";
		:port  "5432";
		:sslmode  "require";	      
		:dbname  "klohymim";
		:source  "elephantsql";
		:help_url_prefix "http://labsolns.com/software/";
		:password  "hwc3v4_rbkT-1EL2KI-JBaqFq0thCXM_";
		:user  "klohymim";
	    	:URL  (str "jdbc:postgresql://"  :host  ":" :port  "/" :dbname  "?user=" :user  "&password=" :password  "&SSL=true" ))

	;;	(post-load-properties "elephantsql"))



(defn set-user-id [i]
        (c/assoc-at! props  [:assets :session :user-id i]))


(defn get-user-id []
  (c/get-at! props [:assets :session :user-id ]))


(defn set-user-group [s]
        (c/assoc-at! props  [:assets :session :user-group s]))

(defn get-user-group []
  (c/get-at! props [:assets :session :user-group ]))


(defn get-user-group-id []
  (c/get-at! props [:assets :session :user-group-id ]))

(defn set-user-group-id [i]
        (c/assoc-at! props  [:assets :session :user-group-id i]))

(defn set-project-id [i]
        (c/assoc-at! props  [:assets :session :project-id i]))

(defn get-project-id []
  (c/get-at! props [:assets :session :project-id ]))

(defn set-project-sys-name [s]
        (c/assoc-at! props  [:assets :session :project-sys-name s]))

(defn get-project-sys-name []
  (c/get-at! props [:assets :session :project-sys-name ]))

(defn set-plate-set-sys-name [s]
        (c/assoc-at! props  [:assets :session :plate-set-sys-name s]))

(defn get-plate-set-sys-name []
  (c/get-at! props [:assets :session :plate-set-sys-name ]))

(defn set-plate-set-id [i]
        (c/assoc-at! props  [:assets :session :plate-set-id i]))

(defn get-plate-set-id []
  (c/get-at! props [:assets :session :plate-set-id ]))


(defn get-session-id []
  (c/get-at! props [:assets :session :session-id ]))

(defn set-session-id [i]
        (c/assoc-at! props  [:assets :session :session-id i]))
  
(defn get-home-dir []
   (java.lang.System/getProperty "user.home"))
  
(defn get-temp-dir []
   (java.lang.System/getProperty "java.io.tmpdir"))
             
  
(defn get-working-dir []
   (java.lang.System/getProperty "user.dir"))


(defn get-help-url-prefix []
  (c/get-at! props [:assets :props :help-url-prefix ]))



  (defn set-authenticated [b]
        (c/assoc-at! props  [:assets :session :authenticated b]))

(defn get-authenticated []
  (c/get-at! props [:assets :session :authenticated ]))
