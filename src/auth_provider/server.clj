(ns auth-provider.server
  (:require
    ;[integrant.core :as ig]
    ;[clojure.spec.alpha :as s]

    ;[auth-provider.db.postgres :as postgres]
    [auth-provider.login :as login]
    [auth-provider.consent :as consent]

    [bidi.ring :as bidi]
    [selmer.parser :as selmer]
    [taoensso.timbre :as timbre]

    [ring.util
     [response :refer [response redirect not-found resource-response file-response header]]]
    [ring.middleware
     [resource :refer [wrap-resource]]
     [content-type :refer [wrap-content-type]]
     [not-modified :refer [wrap-not-modified]]
     [keyword-params :refer [wrap-keyword-params]]
     [format :refer [wrap-restful-format]]
     [params :refer [wrap-params]]]
    [org.httpkit.server :as http-server]))

(timbre/set-level! (or (keyword (System/getenv "LOG_LEVEL"))
                       :warn))
(selmer/set-resource-path! (clojure.java.io/resource "templates"))

;(s/check-asserts true)

(def route
  ["/" {"login" {:get login/login-get
                 :post login/login-post}
        "consent" {:get consent/consent-get
                   :post consent/consent-post}
        ;"style.css" (resource-response "public/style.css")
        true (constantly (not-found "404 not found"))}])

(def handler
  (bidi/make-handler route))

(def app
  (-> handler
      ;(wrap-keyword-params {:parse-namespaces? true})
      (wrap-restful-format)
      (wrap-params)
      (wrap-resource "public")
      (wrap-content-type)
      ;(wrap-not-modified)
      ))

(defn -main []
  (timbre/info "App running")
  (http-server/run-server app {:port 80}))