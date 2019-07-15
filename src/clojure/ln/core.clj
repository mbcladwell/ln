(ns ln.core
  (:require [ln.session]
             [ln.dialog] )
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (ln.session/open-props-if-exists))


