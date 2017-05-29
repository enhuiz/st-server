(ns st-server.server
  (:require [st-server.handler :refer [app]]
            [config.core :refer [env]]
            [ring.server.standalone :refer [serve]]
            [ring.adapter.jetty :refer [run-jetty]])
  (:gen-class))

(defn -main [& args]
  (let [port (Integer/parseInt (or (env :port) "3000"))]
    (run-jetty app {:port port :join? false})))