(ns spec.util
  (:require [clojure.spec.alpha :as s]))

(defn get-keys [spec-def]
  (->> (s/get-spec spec-def)
       (s/form)
       (filter vector?)
       (flatten)))

(defn filter-keys
  "Filter keys by s/keys :req :req-un :opt :opn-un in map m."
  [spec-def m]
  (->> (get-keys spec-def)
       (select-keys m)))

(defn assert-strict [spec m]
  (filter-keys spec m)
  (s/assert spec m))

(defn ?spec-problems [spec value]
  (->> (s/explain-data spec value)
       ::s/problems
       (map :via)))

(defn ?spec->msg [spec value messages]
  (->> (?spec-problems spec value)
       (map reverse)
       (keep #(some messages %))
       (not-empty)))

(defn ?conform-strict
  "Like original conform, but return nil instead ::s/invalid"
  [spec value]
  (let [new-value (->> (filter-keys spec value)
                       (s/conform spec))]
    (when-not (= new-value ::s/invalid)
      new-value)))

(defn conform-strict-or-fail [spec value]
  (if-let [new-value (?conform-strict spec value)]
    new-value
    (assert spec value)))