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
      [:h3 "Loading..."]])

(defn head []
  [:head
   [:meta {:charset "utf-8"}]
   [:meta {:name "viewport"
           :content "width=device-width, initial-scale=1"}]
   (include-css "https://cdn.bootcss.com/semantic-ui/2.2.10/semantic.min.css")
   [:body
    [:div.ui.menu
    [:a.header.item {:href "/"} "bookface"]
    [:div.right.menu
      [:a.item {:href "/join"} "Join"]]]]])

(defn loading-page []
  (html5
    (head)
     mount-target
     (include-js "https://cdn.bootcss.com/jquery/2.2.1/jquery.js")     
     (include-js "https://cdn.bootcss.com/semantic-ui/2.2.10/semantic.min.js")
     (include-js "/js/app.js")))

(defn get-all-users [request]
  (users/get-all))

(defn join-result
  [result]
  (html5
    (head)
    [:body
      [:p (if result "Success" "Duplicated wechat!")]]))

(defn join [request]
  (let [m (:form-params request)]
    (join-result (users/add m))))

(defn book [request]
  (let [m (:params request)]
    (str (users/book m))))

(defroutes routes
  (GET "/" [] (loading-page))
  (GET "/join" [] (loading-page))
  (POST "/join" [] join)

  (GET "/book" [] book)

  (GET "/users" [] get-all-users)
  (resources "/")
  (not-found "Not Found"))

(def app 
  (-> routes
      wrap-middleware))
; (def app (wrap-middleware #'routes))
