(ns auth-provider.consent
  (:require [ring.util.response :refer [response redirect header]]
            [hydra.core :as hydra]
            [selmer.parser :as selmer]))

(defn inner-client? [client]
  (#{"auth-code-client-fooooooooo"} (:client_id client)))

(defn selmer-response [file context]
  (-> (response (selmer/render-file file context))
      (header "Content-Type" "text/html")))

(defn consent-get [{:keys [query-params]}]
  (let [{:strs [consent_challenge]} query-params
        {:keys [skip subject client requested_scope]} (:body (hydra/get-consent consent_challenge))]
    (cond
      (nil? consent_challenge) (redirect "/login")
      skip (-> (hydra/accept-consent consent_challenge {:subject subject})
               (hydra/response->redirect))
      (inner-client? client) (-> (hydra/accept-consent consent_challenge {:grant_scope requested_scope
                                                                          :remember true
                                                                          :remember_for 60})
                                 (hydra/response->redirect))
      :else (selmer-response "consent.html" {:requested_scope requested_scope
                                             :client (select-keys client [:grant_types :logo_uri :client_name :created_at])}))))

(defn consent-post [{:keys [form-params query-params]}]
  (let [{:strs [consent_challenge]} query-params
        {:keys [remember? grant_scope] :as post} (clojure.walk/keywordize-keys form-params)]
    (cond
      (nil? consent_challenge) (redirect "/login")
      :else (-> (hydra/accept-consent consent_challenge {:grant_scope grant_scope
                                                         :remember remember?
                                                         :remember_for 60})
                (hydra/response->redirect)))))

(comment
  (hydra/get-consent "e3a2faa427d04fd283043f6e6b7cdf8d"))