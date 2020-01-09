(ns ln.codax-manager
  (:require [codax.core :as c]
            [clojure.java.io :as io]
            [next.jdbc :as j]
            )
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
	                          :project-sys-name "PRJ-1"
	                          :user-id 1
                                  :plate-set-id 1
                                  :plate-set-sys-name "PS-1"
                                  :plate-id 1
                                  :plate-sys-name "PLT-1"
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
                                     :dbtype "postgres"
  	                             :dbname "klohymim"
 	                             :host  "raja.db.elephantsql.com"
  	                             :port  5432
  	                             :user  "klohymim"
  	                             :password  "hwc3v4_rbkT-1EL2KI-JBaqFq0thCXM_"                       
 	                             :sslmode  false
 	                             :help-url-prefix  "http://labsolns.com/software/" 
                                     }) 
        (c/assoc-at [:assets :session] {:project-id 1
	                                :project-sys-name "PRJ-1"
                                        :user "ln_user"
                                        :password "welcome"
	                                :user-id 2
                                        :plate-set-id 1
                                        :plate-set-sys-name "PS-1"
                                        :plate-id 1
                                        :plate-sys-name "PLT-1"
	                                :user-group-id 2
                                        :user-group "user"
	                                :session-id 1
                                        :working-dir ""
                                        :auto-login true
                                        :authenticated true
                                        })))
  (c/close-database! props)))

;;(login-to-elephantsql)
;;psql postgres://klohymim:hwc3v4_rbkT-1EL2KI-JBaqFq0thCXM_@raja.db.elephantsql.com:5432/klohymim


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


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Connection variables
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn get-dbtype
  "mysql; postgresql"
  []
  (c/get-at! props [:assets :conn :dbtype]))

(defn get-db-user []
  (c/get-at! props [:assets :conn :user]))

(defn get-db-password []
  (c/get-at! props [:assets :conn :password]))

(defn set-db-user [u]
  (c/with-write-transaction [props tx]
        (c/assoc-at tx [:assets :conn :user] u)))

(defn set-db-password [p]
  (c/with-write-transaction [props tx]
        (c/assoc-at tx  [:assets :conn :password] p)))

(defn get-host []
   (c/get-at! props [:assets :conn :host]))

(defn get-port []
  (c/get-at! props [:assets :conn :port]))

(defn get-source []
  (c/get-at! props [:assets :conn :source]))

(defn get-dbname []
  (c/get-at! props [:assets :conn :dbname]))

(defn get-sslmode []
  (c/get-at! props [:assets :conn :sslmode]))


;;(set-u-p-al "ln_admin" "welcome" true)
;;(str props)
;;(print-ap)
;;(open-or-create-props)
;;(c/close-database! props)
;;(c/destroy-database! props)


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



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Session variables
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn get-user []
  (c/get-at! props [:assets :session :user]))

(defn get-password []
  (c/get-at! props [:assets :session :password]))

(defn set-user [u]
  (c/with-write-transaction [props tx]
        (c/assoc-at tx [:assets :session :user] u)))

(defn set-password [p]
  (c/with-write-transaction [props tx]
        (c/assoc-at tx  [:assets :session :password] p)))

(defn set-authenticated [b]
  (c/with-write-transaction [props tx]
    (c/assoc-at tx  [:assets :session :authenticated] b)))

(defn get-authenticated []
  (c/get-at! props [:assets :session :authenticated ]))

(defn set-user-id [i]
  (c/with-write-transaction [props tx]
        (c/assoc-at tx  [:assets :session :user-id] i)))

;;(set-user-id 100)

(defn get-user-id []
(long  (c/get-at! props [:assets :session :user-id ])))


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

;;https://stackoverflow.com/questions/9457537/why-does-int-10-produce-a-long-instance
;; dont cast to int, gets promoted to Long upon java interop
(defn get-project-id  []
  (long  (c/get-at! props [:assets :session :project-id ])))

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

(defn set-plate-sys-name [s]
   (c/with-write-transaction [props tx]
        (c/assoc-at tx  [:assets :session :plate-sys-name ] s)))

(defn get-plate-sys-name []
  (c/get-at! props [:assets :session :plate-sys-name ]))


(defn get-session-id []
  (c/get-at! props [:assets :session :session-id ]))

(defn set-session-id [i]
  (c/with-write-transaction [props tx]
    (c/assoc-at tx  [:assets :session :session-id] i)))

(defn set-source [s]
  (c/with-write-transaction [props tx]
    (c/assoc-at tx  [:assets :conn :source] s)))

(defn get-home-dir []
   (java.lang.System/getProperty "user.home"))
  
(defn get-temp-dir []
   (java.lang.System/getProperty "java.io.tmpdir"))


(defn get-working-dir []
   (java.lang.System/getProperty "user.dir"))

(defn set-init [b]
  (c/with-write-transaction [props tx]
        (c/assoc-at tx [:assets :conn :init] b)))

(defn get-init []
  (c/get-at! props [:assets :conn :init ]))

(defn get-auto-login []
  (c/get-at! props [:assets :session :auto-login]))

(defn set-auto-login [b]
  (c/with-write-transaction [props tx]
        (c/assoc-at tx  [:assets :session :auto-login] b)))

(defn set-u-p-al
  ;;user name, password, auto-login
  ;;only used during login
  [u p al]
  (c/with-write-transaction [props tx]
    (-> tx
     (c/assoc-at   [:assets :session :user] u)
     (c/assoc-at  [:assets :session :password] p) 
     (c/assoc-at  [:assets :session :auto-login] al))))


(defn set-uid-ugid-ug-auth [ uid ugid ug auth ]
  ;; user-id user-group-id user-group-name authenticated
  (c/with-write-transaction [props tx]
    (-> tx
      (c/assoc-at  [:assets :session :user-id] uid)   
      (c/assoc-at  [:assets :session :user-group-id] ugid)
      (c/assoc-at  [:assets :session :user-group] ug)
      (c/assoc-at  [:assets :session :authenticated] auth))))



(defn set-wid-lkey-email [ wid lkey email]
  (c/with-write-transaction [props tx]
    (-> tx
      (c/assoc-at  [:assets :conn :customer-id] (clojure.string/trim wid))
      (c/assoc-at  [:assets :conn :license-key] (clojure.string/trim lkey))
      (c/assoc-at  [:assets :conn :email] (clojure.string/trim email)))))

(defn get-customer-id []
  (c/get-at! props [:assets :conn :customer-id]))

(defn get-license-key []
  (c/get-at! props [:assets :conn :license-key]))

(defn get-email []
  (c/get-at! props [:assets :conn :email]))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;Printing methods

    (defn get-help-url-prefix []
          (c/get-at! props [:assets :conn :help-url-prefix ]))
;;(get-help-url-prefix)

(def conn   {:dbtype (get-dbtype)
             :dbname (get-dbname)
             :host (get-host)
             :user (get-db-user)
             :password (get-db-password)
             :port (get-port)
             :useTimezone true
             :serverTimezone "UTC"
             :useSSL (get-sslmode)})

(defn update-ln-props [host port dbname source sslmode db-user db-password help-url user-dir]
  (c/with-write-transaction [props tx]
    (-> tx
      (c/assoc-at  [:assets :conn :host] host)   
      (c/assoc-at  [:assets :conn :port] port)
      (c/assoc-at  [:assets :conn :sslmode] sslmode)
      (c/assoc-at  [:assets :conn :source] source)
      (c/assoc-at  [:assets :conn :user] db-user)   
      (c/assoc-at  [:assets :conn :password] db-password)
      (c/assoc-at  [:assets :conn :help-url-prefix] help-url)
      (c/assoc-at  [:assets :conn :dbname] dbname))))


  (defn print-ap 
    "This version prints everything"
    []
    (println (str "Whole map: " (c/get-at! props []) )))

  ;;(print-ap)
  ;;(print-all-props)


    (defn get-help-url-prefix []
          (c/get-at! props [:assets :conn :help-url-prefix ]))
;;(get-help-url-prefix)

(defn  get-connection-string [target]	  
  (case target
    "heroku" (str "jdbc:postgresql://"  (get-host) ":" (get-port)  "/" (get-dbname) "?sslmode=require&user=" (get-db-user) "&password="  (get-db-password))
    "local" (str "jdbc:postgresql://" (get-host) ":" (get-port) "/" (get-dbname)  "?user=" (get-db-user) "&password=" (get-db-password) "&useSSL=false")
    "elephantsql" (str "jdbc:postgresql://" (get-host) ":" (get-port) "/" (get-dbname) "?user=" (get-db-user) "&password=" (get-db-password) "&SSL=" (get-sslmode))
    "postgres" (str "jdbc:postgresql://" (get-host) ":" (get-port) "/" (get-dbname)"?user="  (get-db-user) "&password=" (get-db-password) "&SSL="  (get-sslmode))
"test" "jdbc:postgresql://raja.db.elephantsql.com:5432/klohymim?user=klohymim&password=hwc3v4_rbkT-1EL2KI-JBaqFq0thCXM_&SSL=false" 
     "internal" (str "jdbc:postgresql://" (get-host)  ":" (get-port) "/" (get-dbname)  "?user=" (get-db-user) "&password=" (get-db-password) "&SSL=false")))

(defn pretty-print []
  (do
    (println "All values")
    (println "-------------------")
    (println "conn")
    (println (str ":init       " (get-init)))
    (println (str ":dbtype     " (get-dbtype)))
    (println (str ":dbname     " (get-dbname)))
    (println (str ":port       " (get-port)))
    (println (str ":host       " (get-host)))
    (println (str ":user       " (get-db-user)))
    (println (str ":password   " (get-db-password)))
    (println (str ":source     " (get-source)))
    (println (str ":sslmode    " (get-sslmode)))
    (println (str ":help-url-prefix    " (get-help-url-prefix)))
    (println (str ":customer-id    "  (c/get-at! props [:assets :conn :customer-id ])))
    (println (str ":license-key    "  (c/get-at! props [:assets :conn :license-key ])))
    (println (str ":email    "  (c/get-at! props [:assets :conn :email ])))
    
    (println "-------------------")
    (println "session")
    (println (str ":session-id        " (get-session-id)))
    (println (str ":user              " (get-user)))
    (println (str ":user-id           " (get-user-id)))
    (println (str ":password          " (get-password)))
    (println (str ":auto-login        " (get-auto-login)))
    (println (str ":user-group        " (get-user-group)))
    (println (str ":user-group-id     " (get-user-group-id)))
    (println (str ":authenticated     " (get-authenticated)))
    (println (str ":project-id        " (get-project-id)))
    (println (str ":project-sys-name  " (get-project-sys-name)))
    (println (str ":plate-set-id      " (get-plate-set-id)))
    (println (str ":plate-set-sys-name " (get-plate-set-sys-name)))
    (println (str ":plate-id          " (get-plate-id)))
    (println (str ":plate-sys-name    " (get-plate-sys-name)))
    (println "-------------------------")
    
    ))

(defn sha256 [string]
  (let [digest (.digest (java.security.MessageDigest/getInstance "SHA-256") (.getBytes string "UTF-8"))]
    (apply str (map (partial format "%02x") digest))))


(defn validate-license-key []
  (let [cust-id (c/get-at! props [:assets :conn :customer-id ])
        lic-key (c/get-at! props [:assets :conn :license-key ])
        email (c/get-at! props [:assets :conn :email ])
        hash (subs (sha256 (str cust-id email "lnsDFoKytr")) 0 34)
        ]
    (= lic-key hash)))

;; (validate-lic-key)
;; (open-or-create-props)

;; (defn update-ln-props []
;;   (c/with-write-transaction [props tx]
;;     (-> tx
;;       (c/assoc-at  [:assets :conn :customer-id] "1CK6KHY6MHgYvmRQ4PAafKYDrg1ejbH1cE")
;;       (c/assoc-at  [:assets :conn :license-key] "a77f0e51340a16a36420b021a53d9ea71d")
;;       (c/assoc-at  [:assets :conn :email] "info@labsolns.com"))))
;; (update-ln-props)

(defn update-ln-props-to-fail []
  (c/with-write-transaction [props tx]
    (-> tx
      (c/assoc-at  [:assets :conn :customer-id] "1CK6KHY6MHgYvmRQ4PAafKYDrg1ejbH1cE")
      (c/assoc-at  [:assets :conn :license-key] "a77f0e51340a16a36420b021a53d9ea")
      (c/assoc-at  [:assets :conn :email] "info@labsolns.com"))))
;;(update-ln-props-to-fail)
;;(print (subs (sha256 "1CK6KHY6MHgYvmRQ4PAafKYDrg1ejbH1cEinfo@labsolns.comlnsDFoKytr") 0 34))

(defn look [] 
    (def props (c/open-database! "ln-props"))
   
    (pretty-print)
    (println "*****")(println "*****")(println "*****")
    (c/close-database! props))

;;(look)
;;(set-db-password "welcome")
;;(get-db-password)
;;(ns-unmap 'ln.codax-manager 'conn)
;;(ns-unalias 'ln.codax-manager 'conn)
;;(get-auto-login)
;;(set-auto-login false)

;;(ns-unmap 'ln.codax-manager 'props)
;;(ns-unalias 'ln.codax-manager 'props)

;;(println conn)
;;(println props)
;;(open-or-create-props)
;;(print-ap)
;;(c/close-database! props)

