(ns ln.codax-manager
  (:require [codax.core :as c]
             [clojure.java.io :as io])
   (:import java.sql.DriverManager javax.swing.JOptionPane)
  (:gen-class ))


(defn read-props-text-file []
  (read-string (slurp "limsnucleus.properties")))

;;(read-props-text-file)


(defn create-ln-props-from-text []
 (let [props (c/open-database! "ln-props")]
  (c/with-write-transaction [props tx]
    (-> tx
  (c/assoc-at [:assets ] (read-props-text-file))
  (c/assoc-at [:assets :session] {:project-id 1
	                          :project-sys-name ""
	                          :user-id 1
                                  :user-name ""
                                  :plateset-id 1
                                  :plateset-sys-name ""
	                          :user-group-id 1
                                  :user-group ""
	                          :session-id 1
                                  :working-dir ""
                                  :authenticated false
                                  })))
 (c/close-database! props)))

(defn login-to-elephantsql []
 (let [props (c/open-database! "ln-props")]
  (c/with-write-transaction [props tx]
    (-> tx
        (c/assoc-at [:assets :conn] {:source "test"
  	                             :dbname "klohymim"
 	                             :host  "raja.db.elephantsql.com"
  	                             :port  5432
  	                             :user  "klohymim"
  	                             :password  "hwc3v4_rbkT-1EL2KI-JBaqFq0thCXM_"
 	                             :sslmode  false
                                     :auto-login true
 	                             :base.help.url  "http://labsolns.com/software/" 
                                     }) 
        (c/assoc-at [:assets :session] {:project-id 1
	                                :project-sys-name "PRJ-1"
	                                :user-id 3
                                        :user-name "klohymim"
                                        :plateset-id 1
                                        :plateset-sys-name ""
	                                :user-group-id 2
                                        :user-group "user"
	                                :session-id nil
                                        :working-dir ""
                                        :authenticated true
                                        })))
  (c/close-database! props)))


(defn open-or-create-props
  ;;1. check working directory - /home/user/my-working-dir
  ;;2. check home directory      /home/user
  []
  (if (.exists (io/as-file "ln-props"))
    (def props (c/open-database! "ln-props"))  
    (if (.exists (io/as-file (str (java.lang.System/getProperty "user.home") "/ln-props") ))
      (def props (c/open-database! (str (java.lang.System/getProperty "user.home") "/ln-props") ))
      (if (.exists (io/as-file (str (java.lang.System/getProperty "user.dir") "/limsnucleus.properties") ))
        (do
          (create-ln-props-from-text)
          (def props (c/open-database! "ln-props")))
        (do            ;;no limsnucleus.properties - login to elephantSQL
          (login-to-elephantsql)
          (def props (c/open-database! "ln-props"))
          (JOptionPane/showMessageDialog nil "limsnucleus.properties file is missing\nLogging in to example database!"  )
          )))))


(open-or-create-props)
;;(create-ln-props-from-text)
;;(open-or-create-props)
;;(print-ap)
;;(c/close-database! props)
;;


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
    (-> tx
     (c/assoc-at   [:assets :conn :user] u)
     (c/assoc-at  [:assets :conn :password] p) 
     (c/assoc-at  [:assets :conn :auto-login] al))))

;;(set-u-p-al "ln_admin" "welcome" true)
;;(str props)
;;(print-ap)
;;(open-or-create-props)
;;(c/close-database! props)
;;(c/destroy-database! props)


(defn set-uid-ugid-ug-auth [ uid ugid ug auth ]
  ;; user-id user-group-id user-group-name authenticated
  (c/with-write-transaction [props tx]
    (-> tx
      (c/assoc-at  [:assets :session :user-id] uid)   
      (c/assoc-at  [:assets :session :user-group-id] ugid)
      (c/assoc-at  [:assets :session :user-group] ug)
      (c/assoc-at  [:assets :session :authenticated] auth))))

(defn set-authenticated [b]
  (c/with-write-transaction [props tx]
    (c/assoc-at tx  [:assets :session :authenticated] b)))

(defn get-authenticated []
  (c/get-at! props [:assets :session :authenticated ]))

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

;;(get-user-group)
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
;;(set-project-sys-name "PRJ-??")
  ;;(print-ap)


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
          (c/get-at! props [:assets :conn :help-url-prefix ]))
;;(get-help-url-prefix)

(defn pretty-print []
  (do
    (println "All values")
    (println "-------------------")
    (println "conn")
    (println (str ":auto-login " (get-auto-login)))
    (println (str ":dbname     " (get-dbname)))
    (println (str ":host       " (get-host)))
    (println (str ":user-name  " (get-user)))
    (println (str ":password   " (get-password)))
    (println (str ":source     " (get-source)))
    (println (str ":sslmode    " (get-sslmode)))
    (println (str ":help-url-prefix    " (get-help-url-prefix)))
    (println "-------------------")
    (println "session")
    (println (str ":plateset-id       " (get-plate-set-id)))
    (println (str ":session-id        " (get-session-id)))
    (println (str ":user-group        " (get-user-group)))
    (println (str ":authenticated     " (get-authenticated)))
    (println (str ":plateset-sys-name " (get-plate-set-sys-name)))
    (println (str ":project-id        " (get-project-id)))
    (println (str ":user-id           " (get-user-id)))
    (println (str ":user-group-id     " (get-user-group-id)))
    (println "-------------------------")
    
    ))



(defn testup []
  (do
    (pretty-print)
    (set-uid-ugid-ug-auth 10 10 "adminy3" false )
    (set-u-p-al "dodo" "pass1" true)
    (pretty-print)
    (print-ap)
    (println "*****")(println "*****")(println "*****")
    ))

;;(testup)
	
(defn look [] 
    (def props (c/open-database! "ln-props"))
   
    (pretty-print)
    (println "*****")(println "*****")(println "*****")
    (c/close-database! props))

;;(look)
