(ns st-server.users
  (:refer-clojure :exclude [read])
  (:require [clojure.java.jdbc :as jdbc]
            [st-server.db :as db]))

(def table-name "users")

; (def user 
;   {:account ""
;    :password ""
;    :name ""})

(defn insert-root-user! []
  (try
    (do 
      (db/insert! table-name {:account "admin" :password "admin" :name "admin"})
      (println "Root user inserted"))
    (catch Throwable ex)))

(defn get-all
  []
  (db/query "select account, name from users"))

(defn init-table!
  []
  (db/create-table!
    table-name
    [[:account "varchar primary key"]
     [:password "varchar"]
     [:name "varchar"]])
  (insert-root-user!))