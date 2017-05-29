(ns st-server.users
  (:refer-clojure :exclude [read])
  (:require [clojure.java.jdbc :as jdbc]
            [st-server.db :as db]))

(def table-name "users")

; (def user 
;   {:account ""
;    :password ""
;    :name ""})

; (defn insert-root-user! []
;   (try
;     (do 
;       (db/insert! table-name {:wechat "admin" :name "admin" :name "admin"})
;       (println "Root user inserted"))
;     (catch Throwable ex)))

(defn get-all
  []
  (db/query "select * from users"))

(defn add 
  [m]
  (db/insert! table-name m))
 
(defn book
  [m]
  (let [new-bc (inc (get-bookcount m))]
    (db/update! table-name {:bookcount new-bc} ["wechat = ?" (:wechat m)])
    new-bc))

(defn get-bookcount
  [m]
  (try
    (:bookcount (first (db/query ["select bookcount from users where wechat = ?" (:wechat m)])))
    (catch Exception e (println e) 0)))

(defn init-table!
  []
  (db/create-table!
    table-name
    [[:wechat "varchar primary key"]
     [:name "varchar"]
     [:bookcount "bigint default 0"]
     [:timestamp "timestamp default CURRENT_TIMESTAMP"]
     [:face "blob"]]))