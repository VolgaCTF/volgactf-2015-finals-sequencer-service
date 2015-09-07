(ns sequencer.handler
  (:require
    [sequencer.routes :refer [sequencer-routes]]
    [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
    [hiccup.bootstrap.middleware :refer [wrap-bootstrap-resources]]
    [immutant.web :as immutant]
    [immutant.web.middleware :as mw])
  (:gen-class))

(def sequencer-defaults
  (-> site-defaults
      (assoc-in  [:session] false)
      (assoc-in [:security :anti-forgery] false)))



(def app
  (mw/wrap-session (wrap-defaults (wrap-bootstrap-resources #'sequencer-routes) sequencer-defaults)))

(defn -main
  "Start the server"
  [& args]
  (immutant/run #'app {:host "0.0.0.0" :port 8080 :path "/"}))
