(ns st-server.core
    (:require [reagent.core :as reagent :refer [atom]]
              [reagent.session :as session]
              [ajax.core :refer [GET POST]]
              [cljs.reader :refer [read-string]]
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

(defn wechat-input
  [wechat-atom]
  (input-element "" "Wechat" "text" wechat-atom))

(defn name-input
  [name-atom]
  (input-element "" "Name" "text" name-atom))

(defn face-input
  [face-atom]
  [:div
    [:p "My Face"]
    [:p (first @face-atom)]
    [:label.ui.icon.basic.button {:for "file"}
      ; [:i.image.icon]
      "Select"]
    [:input#file {:type "file" 
                  :hidden "true"  
                  :value @face-atom
                  :on-change (fn [e] 
                                (reset! face-atom (-> e .-target .-files)))}]])

;; -------------------------
;; Views

(def users-atom (atom []))
(GET "/users" {:handler #(reset! users-atom (read-string (str "[" % "]")))})

(defn users-view []
  [:div.ui.list
    (for [ [i m] (sort-by (comp :bookcount second) > (map-indexed vector @users-atom))]
      ^{:index i}
        [:div.item
        [:img.avatar.image {:src "https://semantic-ui.com/images/avatar2/small/rachel.png"}]
        [:div.content
          [:p.header (:name m)]
          [:p.description (str "@" (:wechat m))]
          [:br]
          [:p.description (apply str (filter #(re-seq #"[\w :+]" %) (str "Joined at: " (:timestamp m))))]
          [:div.ui.labeled.button.mini {:on-click 
                                          #(GET 
                                          "/book"
                                          {:params m
                                            :handler (fn [new-bc]
                                                      (swap! users-atom update-in [i] (fn [val]
                                                          (update-in m [:bookcount] (fn [val] new-bc)))))})}
            [:div.ui.red.button.mini
              [:i.heart.icon] "Book"]
              [:div.ui.basic.red.left.pointing.label (:bookcount m)]]]])])

(defn home-page []
  [:div.ui.text.container
    [:div [:h2.ui.center.aligned.header "Welcome to bookface"]
      [users-view]]])

(defn join-page []
 (let [wechat-atom (atom nil)
       name-atom (atom nil)
       bookcount-atom (atom nil)
       timestamp-atom (atom nil)
       face-atom (atom nil)]
  (fn []
    [:div.ui.text.container
      [:h2 {:class "ui center aligned header"} "Join bookface"]
      [:form {:class "ui form" :method "POST"}
        [wechat-input wechat-atom]
        [name-input name-atom]
        [face-input face-atom]
        [:center [:button {:class "ui red button"} "Join"]]]])))

(defn current-page []
  [:div [(session/get :current-page)]])

;; -------------------------
;; Routes

(secretary/defroute "/" []
  (session/put! :current-page #'home-page))

(secretary/defroute "/join" []
  (session/put! :current-page #'join-page))

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
