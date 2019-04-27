(ns auth-provider.utils
  (:require [db.flyway :as flyway]
            [ring.middleware.session :refer [wrap-session]]
            [ring.middleware.session.memory :refer [memory-store]]
            #_[clojure.java.jdbc :as jdbc]
            #_[auth-provider.db.postgres :as postgres])
  (:import (ch.qos.logback.classic Logger Level)))

(.setLevel (org.slf4j.LoggerFactory/getLogger "org.flywaydb") Level/WARN)

(defn get-status [mock]
  (get-in mock [:response :status]))

#_(defn rollback-db [tests]
  (jdbc/with-db-transaction [t-con postgres/db]
    (jdbc/db-set-rollback-only! t-con)
    (binding [postgres/db t-con]
      (tests))))

(defn wrap-test [tests]
  (flyway/restart-db)
  (tests))