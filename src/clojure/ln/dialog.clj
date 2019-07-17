(ns ln.dialog
  (:use [seesaw core table dev mig border])

  (:require [clojure.java.io :as io]
            [clojure.string ])  
  (:import [javax.swing JFileChooser JEditorPane JFrame JScrollPane BorderFactory AbstractButton]
           java.awt.Font java.awt.Toolkit )
  (:import [java.net.URL])
  (:gen-class))

(def returned-login-map {})

(defn login-dialog
  ;;
  []
  (-> (let [ name-input (text :columns 30 :id :nameid )
            pass-input (text :columns 30 :id :passid)]        
        (frame :title "Login to LIMS*Nucleus"
               ;;do not on exit close or you will kill repl
               :size [500 :by 240]
               :content  (mig-panel
                          :constraints ["wrap 4"]
                          :items [ [(label :text "Name: "
                                           :h-text-position :right) ]
                                  [ name-input "span 2" ]
                                  [ "           " ]
                                  [ "Password:" ]
                                  [ pass-input "span 2"]
                                  [ "           " ]
                                  [ "           " ]
                                  [ "           " ]
                                  [(checkbox :text "Update ln-props?" :id :cbox :selected? false) "span 2"]
                                  [ "           " ]
                                  
                                  [(button :text "Login"
                                           :listen [:mouse-clicked
                                                    (fn [e] (def returned-login-map (hash-map
                                                                               :name (config  (select (to-root e)  [:#nameid]) :text)
                                                                                                       :password (config  (select (to-root e)  [:#passid]) :text)
                                                                                                       :store (config  (select (to-root e)  [:#cbox]) :selected?)))
                                      (hide! (to-root e)) )])]
                                  [(button :text "Cancel"
                                           :listen [:mouse-clicked (fn [e] (hide! (to-root e)))] )]]))) 
      pack!
      show! (move! :to [ ( - ( / (.getWidth (.getScreenSize (Toolkit/getDefaultToolkit))) 2) 320),
                                ( - ( / (.getHeight (.getScreenSize (Toolkit/getDefaultToolkit))) 2) 240) ]  ) ))
