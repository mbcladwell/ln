(ns lnmanager.dialog
   (:use [seesaw core table dev mig border])

  (:require [clojure.java.io :as io]
            [clojure.string ] )
  (:import [javax.swing JFileChooser JEditorPane JFrame JScrollPane BorderFactory AbstractButton]
           java.awt.Font java.awt.Toolkit )
  (:import [java.net.URL])
  (:gen-class))


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
                                  [(checkbox :text "Update ln-props?" :id :cbox :selected? true) "span 2"]
                                  [ "           " ]
                                  
                                  [(button :text "Login"
                                           :listen [:mouse-clicked (fn [e] (if (config  (select (to-root e)  [:#cbox]) :selected?)
                                                                            (do
                                                                             (lnmanager.session/set-user (config  (select (to-root e)  [:#nameid]) :text))
                                                                             (lnmanager.session/set-password (config  (select (to-root e)  [:#passid]) :text))                                                             
                                                                             (lnmanager.session/print-all-props))))])]
                                  
                                  [(button :text "Cancel")]]))) 
pack!
show!))
(login-dialog)

