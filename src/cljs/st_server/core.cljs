(ns st-server.core
    (:require [reagent.core :as reagent :refer [atom]]
              [reagent.session :as session]
              [secretary.core :as secretary :include-macros true]
              [accountant.core :as accountant]))

;; -------------------------
;; Elements

(defn input-element
  "An input element which updates its value on change"
  [id name type value]
  [:div {:class "field"}
    [:div name]
    [:input {:id id
           :name name
           :class "form-control"
           :type type
           :required true
           :placeholder name
           :value @value
           :on-change #(reset! value (-> % .-target .-value))}]])

(defn account-input
  [account-address-atom]
  (input-element "account" "account" "text" account-address-atom))

(defn password-input
  [password-address-atom]
  (input-element "password" "password" "password" password-address-atom))

(defn name-input
  [name-address-atom]
  (input-element "name" "name" "text" name-address-atom))

;; -------------------------
;; Views


(defn home-page []
  [:div [:h2 "Welcome to st-server"]
   [:div [:a {:href "/register"} "go to register page"]]])

(defn register-page []
 (let [account (atom nil)
       password (atom nil)
       name (atom nil)]
  (fn []
    [:div {:class "ui text container"}
      [:h2 {:class "ui center aligned header"} "ST"]
      [:form {:class "ui form" :method "POST"}
        [account-input account]
        [password-input password]
        [name-input name]
        [:center [:button {:class "ui secondary button center aligned"} "Register"]]]])))

(defn current-page []
  [:div [(session/get :current-page)]])

;; -------------------------
;; Routes

(secretary/defroute "/" []
  (session/put! :current-page #'home-page))

(secretary/defroute "/register" []
  (session/put! :current-page #'register-page))

;; -------------------------
;; Initialize app

(defn mount-root []
  (reagent/render [current-page] (.getElementById js/document "app")))

(defn init! []
  (accountant/configure-navigation!
    {:nav-handler
     (fn [path]
       (secretary/dispatch! path))
     :path-exists?
     (fn [path]
       (secretary/locate-route path))})
  (accountant/dispatch-current!)
  (mount-root))
