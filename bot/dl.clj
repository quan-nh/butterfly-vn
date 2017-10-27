(ns dl
  (:require [clojure.core.memoize :as memo]
            [cheshire.core :as json]
            [org.httpkit.client :as http]))

(defn- url-encode [url] (some-> url (java.net.URLEncoder/encode "UTF-8") (.replace "+" "%20")))

(defn- parse-result [body]
  (let [[r1 r2] (json/decode body true)
        text (str (:label r1) " - " (:score r1) "\n" (:label r2) " - " (:score r2))
        web-url (str "http://www.vncreatures.net/hinhanh.php?nhom=1&loai=3&lang=L&s2=Tra+c%E1%BB%A9u+%E1%BA%A3nh&keyword=" (:label r1))]
    [text web-url]))

(defn- label-image [image-url]
  (let [{:keys [status body]} @(http/get (System/getenv "DL_SERVICE")
                                         {:query-params {:image_url (url-encode image-url)}})]
    (if (= 200 status)
      [status (parse-result body)]
      [status body])))

(def memo-label-image
  (memo/lu label-image :lu/threshold 100))
