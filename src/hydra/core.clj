(ns hydra.core
  (:require [ring.util.response :refer [response redirect header]]
            [org.httpkit.client :as http]
            [cheshire.core :as cheshire]
            [ring.util.codec :refer [url-encode]]
            [taoensso.timbre :as timbre]))

; https://www.ory.sh/docs/hydra/sdk/api

(def hydra-admin-url (System/getenv "URL_HYDRA_ADMIN"))

(defn response->redirect [hydra-response]
  (some-> (get-in hydra-response [:body :redirect_to])
          (redirect)))

(defn response->edn [{:keys [error] :as response}]
  (when error
    (throw
      (ex-info "ory-hydra connection" response)))
  (timbre/debug "Hydra response" response)
  (if (<= 200 (:status response) 302)
    (update response :body #(cheshire/parse-string % true))
    (timbre/warn "Hydra request" response)))

(defn request-get [flow challenge]
  (let [url (str hydra-admin-url "/oauth2/auth/requests/" flow)]
    (response->edn @(http/get url {:query-params {(str flow "_challenge") challenge}}))))

(defn request-put [flow challenge action body]
  (let [url (str hydra-admin-url "/oauth2/auth/requests/" flow "/" action)]
    (response->edn @(http/put url {:body (cheshire/encode body)
                                   :headers {"Content-Type" "application/json"}
                                   :query-params {(str flow "_challenge") challenge}}))))

(defn request-delete [flow subject client-id]
  (let [url (str hydra-admin-url "/oauth2/auth/sessions/" flow)]
    (response->edn @(http/delete url {:query-params {:subject subject
                                                     :client client-id}}))))

(defn get-login [challenge]
  (request-get "login" challenge))

(defn accept-login [challenge body]
  (request-put "login" challenge "accept" body))

(defn reject-login
  "{:error code
    :error_description detailed description}"
  [challenge body]
  (request-put "login" challenge "reject" body))

(defn get-consent [challenge]
  (request-get "consent" challenge))

(defn accept-consent [challenge body]
  (request-put "consent" challenge "accept" body))

(defn reject-consent
  "{:error code
    :error_description detailed description}"
  [challenge body]
  (request-put "consent" challenge "reject" body))

(defn get-logout [challenge]
  (request-get "logout" challenge))

(defn accept-logout [challenge body]
  (request-put "logout" challenge "accept" body))

(defn reject-logout
  "{:error code
    :error_description detailed description}"
  [challenge body]
  (request-put "logout" challenge "reject" body))



(defn delete-login
  "Logout from all devices"
  [subject]
  (request-delete "login" subject nil))

(defn delete-consent [subject client-id]
  (request-delete "consent" subject client-id))

; refresh token ?