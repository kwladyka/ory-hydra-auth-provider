(ns auth-provider.server-test
  (:require [clojure.test :refer :all]
            [auth-provider.utils :refer [wrap-test get-status] :as utils]
            [peridot.core :as peridot]
            [auth-provider.server :as server]
            [ring.middleware.session :refer [wrap-session]]
            [ring.middleware.session.memory :refer [memory-store]]))

(use-fixtures :once wrap-test)

(defn request-authorize [params]
  (-> (peridot/session server/app)
      (peridot/content-type "application/edn")
      (peridot/request "/authorize" :request-method :post
                       :params params)))

(deftest auth-teset
  (testing "authorization"
    (is (request-authorize {:email "foo@example.com"
                            :password "qwaszx"}))))

#_(-> (peridot/session server/app)
    (peridot/header "Accept" "text/html"))

(comment
  (-> (peridot/session server/app)
      (peridot/request  "/authorize" :request-method :get)))