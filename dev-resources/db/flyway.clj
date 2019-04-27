(ns db.flyway
  (:require [flyway.flyway :as fw]
            [auth-provider.db.postgres :as postgres]))

;;;; Flyway commands

;;; migrate -	Migrates the database
;;; clean -	Drops all objects in the configured schemas
;;; info - Prints the details and status information about all the migrations
;;; validate -	Validates the applied migrations against the ones available on the classpath
;;; baseline -	Baselines an existing database, excluding all migrations up to and including baselineVersion
;;; repair -	Repairs the metadata table

(def flyway-db (fw/flyway {:url postgres/db
                           :locations ["filesystem:dev-resources/db/migration"]}))

(defn restart-db []
  (fw/clean flyway-db)
  (fw/migrate flyway-db))

(defn wrap-test [tests]
  (restart-db)
  (tests))

(comment
  (restart-db))