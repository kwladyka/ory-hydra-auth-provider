(ns user
  (:require [auth-provider.server :as server]
            [selmer.parser :as selmer]
            [taoensso.timbre :as timbre]
    ;; ring
            [org.httpkit.server :as httpkit]
            [ring.middleware.stacktrace :refer [wrap-stacktrace]]
            [ring.middleware.session :refer [wrap-session]]
            [ring.middleware.session.memory :refer [memory-store]]
            [ring-debug-logging.core :refer [wrap-with-logger]]))

;;; ring

(timbre/set-level! :debug)
(selmer/cache-off!)

(def app
  (-> server/app
      (wrap-stacktrace)
      (wrap-with-logger)))

(defonce app-server nil)

(defn app-ring-start [_]
  (httpkit/run-server app {:port 3000}))

(defn app-ring-stop [server]
  (when server
    (server))
  nil)

(defn app-start []
  (alter-var-root #'app-server app-ring-start)
  :started)

(defn app-stop []
  (alter-var-root #'app-server app-ring-stop)
  :stopped)

;;; refresh

(defn dev-start []
  (app-start))

(defn dev-stop []
  (app-stop))

(defn dev-restart []
  (dev-stop)
  (dev-start)
  :restarted)

(comment
  (dev-restart))