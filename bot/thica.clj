(ns thica
  (:require [clojure.core.memoize :as memo]
            [clojure.string :as str]
            [cheshire.core :as json]
            [org.httpkit.client :as http]))

(defn- tai-hoa []
  (let [{:keys [error body]} @(http/get "http://www.thica.net/"
                                        {:query-params {:a "tai-hoa"}})]
    (when-not error
      (let [data (:data (json/parse-string body true))
            content (-> (:content data)
                        (str/replace #"<[^>]*>" ""))
            author (get-in data [:author :name])
            title (get-in data [:origin :title])]
        (str content "\n" title " - " author)))))

(def memo-tai-hoa
  (memo/ttl tai-hoa :ttl/threshold 300000))
