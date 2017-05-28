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
  [table-name instance]
  (-> (jdbc/insert! db (keyword table-name) instance)
   first))

(defn query-all
  [table] (jdbc/query db ["select * from ?" table]))

(defn create-table!
  [table-name fields]
  (jdbc/with-db-transaction [conn db]
    (if-not (exists? table-name)
      (do (println "creating table" table-name)
          (jdbc/execute! conn 
            [(apply jdbc/create-table-ddl (concat [(keyword table-name)] fields))]))
      (println "table" table-name "already exists"))))
