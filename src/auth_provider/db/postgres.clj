(ns auth-provider.db.postgres
  (:require [clojure.java.jdbc :as jdbc]
            [clj-postgresql.core]
            [clojure.reflect :refer [resolve-class]]))

(def db "jdbc:postgresql://localhost:5432/postgres?user=postgres&password=secret")

(def insert! (comp first (partial jdbc/insert! db)))
(def delete! (comp first (partial jdbc/delete! db)))
(def update! (comp first (partial jdbc/update! db)))
(def query (partial jdbc/query db))

#_(defn- class-exists? [c]
    (resolve-class (.getContextClassLoader (Thread/currentThread)) c))

#_(defn- instance? [class entity]
    (when (class-exists? class)
      (instance? (resolve class) entity)))

(defn duplicated-key?
  "Check if expecpection is duplicated key.
  Keey error in postresql is 23505."
  [entity]
  (= "23505" (.getSQLState entity))
  #_(when (instance? 'org.postgresql.util.PSQLException entity)
      (= "23505" (.getSQLState entity))))

(comment
  (count (query "SELECT * FROM sessions")))
