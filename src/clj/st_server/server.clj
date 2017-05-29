(ns st-server.server
  (:require [st-server.handler :refer [app]]
            [config.core :refer [env]]
            [ring.server.standalone :refer [serve]]
            [ring.adapter.jetty :refer [run-jetty]]
            [st-server.users :as users])
  (:gen-class))

(defn init 
  []
  (users/init-table!))

(defonce server (atom nil))

(defn start-server
  [& [port]]
  (let [port (if port (Integer/parseInt port) 2333)]
    (reset! server 
            (serve #'app
                  {:port port
                  :auto-reload? true
                  :join true
                  :init init}))
    (println (str "You can view the site at http://localhost:" port))))

(defn stop-server []
  (.stop @server)
  (reset! server nil))

; (defn -main [& args]
;   (let [port (Integer/parseInt (or (env :port) "3000"))]
;     (run-jetty app {:port port :join? false})))