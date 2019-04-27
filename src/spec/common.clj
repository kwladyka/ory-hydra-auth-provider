(ns spec.common
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [clj-uuid :as uuid]))

(s/def ::uuid uuid/uuidable?)

(s/def ::url (s/with-gen (s/and string? (partial re-matches #"(http(s)?:\/\/.)?(www\.)?[-a-zA-Z0-9@:%._\+~#=]{2,256}\.[a-z]{2,6}\b([-a-zA-Z0-9@:%_\+.~#?&//=]*)"))
                         (partial gen/fmap #(str "http://example.com/" %) (s/gen string?))))

(s/def ::email (s/with-gen (s/and string? (partial re-matches #"^[a-z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,63}$"))
                           (partial gen/fmap #(str % "@example.com") (s/gen string?))))

(s/def ::instant inst?)

(comment
  (gen/sample (s/gen ::email)))