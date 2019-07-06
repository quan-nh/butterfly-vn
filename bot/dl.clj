(ns dl
  (:require [clojure.core.memoize :as memo]
            [clojure.string :as str]
            [clojure.java.jdbc :as jdbc]
            [cheshire.core :as json]
            [org.httpkit.client :as http]
            [clojure.java.io :as io])
  (:import (java.net URLEncoder)
           (com.google.auth.oauth2 GoogleCredentials)
           (java.util Base64)))

(def dl-server (System/getenv "DL_SERVER"))
(def credential (-> "butterfly-service-account.json"
                    io/resource
                    io/input-stream
                    GoogleCredentials/fromStream
                    (.createScoped ["https://www.googleapis.com/auth/cloud-platform"])))

(def db-spec {:classname   "org.sqlite.JDBC"
              :subprotocol "sqlite"
              :subname     ":resource:butterfly.db"})

(defn- url-encode [url] (some-> url (URLEncoder/encode "UTF-8") (.replace "+" "%20")))

(defn- parse-result [{label :displayName, {:keys [score]} :classification}]
  (let [[genus species] (str/split label #"_")
        latin-name (str genus " " (str/lower-case species))
        {:keys [vn_name common_name url]} (or (first (jdbc/query
                                                       db-spec
                                                       ["SELECT vn_name, common_name, url
                                                        FROM butterfly
                                                        WHERE genus = ? AND species = ?"
                                                        genus (str/lower-case species)]))
                                              {:vn_name latin-name
                                               :url     "http://www.vncreatures.net/hinhanh.php?nhom=1&loai=3"})]
    [(or vn_name common_name) (str latin-name " ~ " (* 100 score) "%") url]))

(defn- label-image [image-url]
  (.refreshIfExpired credential)
  (try
    (let [access-token (-> credential .getAccessToken .getTokenValue)
          {img :body} @(http/get image-url {:as :byte-array :insecure? true})
          img-bytes (-> (Base64/getEncoder) (.encodeToString img))
          {:keys [status body]} @(http/post dl-server
                                            {:headers {"Content-Type"  "application/json"
                                                       "Authorization" (str "Bearer " access-token)}
                                             :body    (json/encode
                                                        {:payload {:image {:imageBytes img-bytes}}
                                                         :params  {:score_threshold "0.3"}})})
          payload (:payload (json/decode body true))]
      (cond
        (pos? (count payload))
        [status (parse-result (first payload))]

        (and (= 200 status)
             (zero? (count payload)))
        [400]

        :else
        [500 body]))
    (catch Exception e
      [500 (.getMessage e)])))

(def memo-label-image
  (memo/lu label-image :lu/threshold 100))
