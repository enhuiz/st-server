(ns st-server.db
  (:require [clojure.java.jdbc :as jdbc]))

(def db {:classname "org.h2.Driver"
         :subprotocol "h2:file"
         :subname "./db/db"})

(defn exists?
  "Check whether a given table exists."
  [table]
  (try
    (do
      (->> (format "select 1 from %s" table)
           (vector)
           (jdbc/query db))
      true)
    (catch Throwable ex
      false)))

(defn insert!
  [table m]
  (-> (jdbc/insert! db (keyword table) m)
   first))

(defn update!
  [table m sql]
  (jdbc/update! db (keyword table) m sql))

; (defn query-all
;   [table]
;   (jdbc/query db ["select * from ?" (keyword table)]))

(defn query
  [sql] (jdbc/query db sql))

(defn create-table!
  [table fields]
  (jdbc/with-db-transaction [conn db]
    (if-not (exists? table)
      (do (println "creating table" table)
          (jdbc/execute! conn 
            [(apply jdbc/create-table-ddl (concat [(keyword table)] fields))]))
      (println "table" table "already exists"))))
