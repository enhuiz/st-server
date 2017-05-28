(ns st-server.users
  (:refer-clojure :exclude [read])
  (:require [clojure.java.jdbc :as jdbc]
            [st-server.db :as db]))

(def user 
  {:account ""
   :password ""
   :name ""})

(def table-name "users")

(defn insert-root-user! []
  (println "inserting root user")
  (db/insert! table-name {:account "admin" :password "admin" :name "admin"}))

(defn init-table!
  []
  (db/create-table!
    table-name 
    [[:id "bigint primary key auto_increment"]
     [:account "varchar unique"]
     [:password "varchar"]
     [:name "varchar"]])
  (insert-root-user!))