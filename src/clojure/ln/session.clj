(ns ln.session
  (:require [clojure.java.jdbc :as sql]
            ;;[honeysql.core :as hsql]
            ;;[honeysql.helpers :refer :all :as helpers]
            [clojure.data.csv :as csv]
            [codax.core :as c]
            [clojure.java.io :as io]
            [ln.dialog] [ln.db-inserter] [ln.db-retriever] [ln.codax-manager])
  (:import java.sql.DriverManager javax.swing.JOptionPane)
  (:gen-class ))

(def props "")


(defn read-props-text-file []
  (read-string (slurp "limsnucleus.properties")))

;;(read-props-text-file)

(defn set-ln-props [ path-to-db ]
  (def props (c/open-database! path-to-db))  )

;;(set-ln-props "./ln-props")


(defn create-ln-props-from-text []
 (let [props (c/open-database! "ln-props")]
  (c/with-write-transaction [props tx]
    (-> tx
  (c/assoc-at [:assets ] (read-props-text-file))
  (c/assoc-at [:assets :session] {:project-id 0
	                          :project-sys-name ""
	                          :user-id 0
                                  :user-sys-name ""
                                  :plateset-id 0
                                  :plateset-sys-name ""
	                             :user-group-id 0
	                             :session-id 0
                                     :working-dir ""
                                  })))
 (c/close-database! props)))

;;(create-ln-props-from-text)
  ;;(print-ap)


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

(defn get-sslmode []
  (c/get-at! props [:assets :conn :sslmode]))

(defn get-auto-login []
  (c/get-at! props [:assets :conn :auto-login]))

(defn set-auto-login [b]
  (c/with-write-transaction [props tx]
        (c/assoc-at tx  [:assets :conn :auto-login] b)))

(defn set-u-p-al
  ;;user name, password, auto-login
  ;;only used during login
  [u p al]
  (c/with-write-transaction [props tx]
    (c/merge-at tx [:assets] {:conn {:user u :password p :auto-login al}})))
  
 


(defn set-uid-ugid-ug-auth [ uid ugid ug auth ]
  ;; user-id user-group-id user-group-name authenticated
  (c/with-write-transaction [props tx]
      (c/merge-at tx  [:assets] {:session {:user-id uid :user-group-id ugid :user-group ug :authenticated auth}})))



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


;;(create-ln-props "127.0.0.1" "5432" "lndb" "local" "false" "ln_admin" "welcome")



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


(def pg-db  {:dbtype "postgresql"
            :dbname "lndb"
            :host (get-host)
            :user (get-user)
             :password (get-password)
             :port (get-port)
            :ssl (get-sslmode)
            :sslfactory "org.postgresql.ssl.NonValidatingFactory"})




      (defn set-authenticated [b]
          (c/with-write-transaction [props tx]
        (c/assoc-at tx  [:assets :session :authenticated] b)))

(defn get-authenticated []
  (c/get-at! props [:assets :session :authenticated ]))



(defn login-to-database
  ;;if user is blank or auto-login is false, pop up the login dialog
  ;;store results, validate results, and start dbm
  []
  (if(or (clojure.string/blank? (c/get-at! props [:assets :conn :user]))
         (not (c/get-at! props [:assets :conn :auto-login])))
    (do
      (login-dialog)
      (set-u-p-al (ln.dialog/returned-login-map :name)
                  (ln.dialog/returned-login-map :password)
                  (ln.dialog/returned-login-map :store))
      (ln.db-retriever/authenticate-user)
      (if(get-authenticated)
        (ln.DatabaseManager.)
        (JOptionPane/showMessageDialog nil "Login credentials are invalid!" )));; the if is true i.e. need a login dialog
    (ln.DatabaseManager. )))  ;;if is false - can auto-login
    

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
    (c/assoc-at tx  [:assets :session :project-id] i)))

(defn get-project-id []
  (c/get-at! props [:assets :session :project-id ]))

(defn set-project-sys-name [s]
    (c/with-write-transaction [props tx]
        (c/assoc-at tx  [:assets :session :project-sys-name] s)))

(defn get-project-sys-name []
    (c/get-at! props [:assets :session :project-sys-name ]))

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



(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (if (not (.exists (io/as-file "ln-props")))
    
    (if (not (.exists (io/as-file (str (java.lang.System/getProperty "user.dir") "/limsnucleus.properties") )))
      (JOptionPane/showMessageDialog nil "limsnucleus.properties file is missing!"  )
      (create-ln-props-from-text))

    )  
  (def props (c/open-database! "ln-props"))
  (login-to-database ))

;;(-main)
;;(print-ap)
;;(c/close-database! props)

;;https://cb.codes/a-tutorial-of-stuart-sierras-component-for-clojure/

