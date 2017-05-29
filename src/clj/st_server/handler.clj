(ns st-server.handler
  (:require [compojure.core :refer [GET POST defroutes]]
            [compojure.route :refer [not-found resources]]
            [hiccup.page :refer [include-js include-css html5]]
            [clojure.tools.logging :as log]
            [st-server.middleware :refer [wrap-middleware]]
            [config.core :refer [env]]
            [st-server.users :as users]))

(def mount-target
  [:div#app
      [:h3 "ClojureScript has not been compiled!"]
      [:p "please run "
       [:b "lein figwheel"]
       " in order to start the compiler"]])

(defn head []
  [:head
   [:meta {:charset "utf-8"}]
   [:meta {:name "viewport"
           :content "width=device-width, initial-scale=1"}]
   (include-css "https://cdn.bootcss.com/semantic-ui/2.2.10/semantic.min.css")])

(defn loading-page []
  (html5
    (head)
    [:body
     mount-target
     (include-js "https://cdn.bootcss.com/jquery/2.2.1/jquery.js")     
     (include-js "https://cdn.bootcss.com/semantic-ui/2.2.10/semantic.min.js")
     (include-js "/js/app.js")]))

(defn what-is-my-ip [request]
  {:status 200
   :headers {"Content-Type" "text/plain"}
   :body (:remote-addr request)})

(defn get-all-users [request]
  (users/get-all))

(defn register [request]
  (let [params (:form-params request)]
    (fn [] 
      (println params))))

(defroutes routes
  (GET "/" [] (loading-page))
  (GET "/about" [] (loading-page))
  (GET "/register" [] (loading-page))

  (GET "/ip" [] what-is-my-ip)
  (GET "/users" [] get-all-users)


  (POST "/register" [] register)

  (resources "/")
  (not-found "Not Found"))

(def app 
  (-> routes
      wrap-middleware))
; (def app (wrap-middleware #'routes))
