(ns auth-provider.login
  (:require [ring.util.response :refer [response redirect header]]
            [hydra.core :as hydra]
            [selmer.parser :as selmer]))

(def redirect-default-login (redirect (System/getenv "URL_DEFAULT_REDIRECT")))

(defn selmer-response [file context]
  (-> (response (selmer/render-file file context))
      (header "Content-Type" "text/html")))

(defn ?login->user-uuid [user]
  {:user/uuid (str "uuid-" (rand-int 999999))})

(defn login-get [{:keys [query-params]}]
  (selmer-response "login.html" {})
  #_(let [{:strs [login_challenge]} query-params
        {:keys [skip subject]} (:body (hydra/get-login login_challenge))]
    (cond
      (nil? login_challenge) redirect-default-login
      skip (-> (hydra/accept-login login_challenge {:subject subject})
               (hydra/response->redirect))
      :else (selmer-response "login.html" {}))))

(defn login-post [{:keys [form-params query-params]}]
  (let [{:strs [login_challenge]} query-params
        {:keys [remember?] :as post} (clojure.walk/keywordize-keys form-params)
        user (-> (select-keys [:user/email :user/password] post)
                 (?login->user-uuid))]
    (cond
      (nil? login_challenge) redirect-default-login
      (:user/uuid user) (or (->> {:subject (:user/uuid user)
                                  :remember (= "on" remember?)
                                  :remember_for 60}
                                 (hydra/accept-login login_challenge)
                                 (hydra/response->redirect))
                            redirect-default-login)
      :else (selmer-response "login.html" {:unidentified? true}))))

(comment
  (hydra/get-login "ff1db5f034df4432bf1ada1afbcd1069"))