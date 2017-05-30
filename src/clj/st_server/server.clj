(ns st-server.server
  (:require [st-server.handler :refer [app]]
            [st-server.users :as users]
            [config.core :refer [env]]
            [ring.server.standalone :refer [serve]]
            [ring.adapter.jetty :refer [run-jetty]])
  (:gen-class))

(defn init 
  []
  (users/init-table!))

(defn -main [& args]
  (let [port (Integer/parseInt (or (env :port) "3000"))]
    (run-jetty app {
                    :port port 
                    :auto-reload? true
                    :init init
                    :join? false})))