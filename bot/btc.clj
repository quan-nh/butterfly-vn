(ns btc
  (:require [clojure.core.memoize :as memo]
            [clojure.java.io :as io]
            [cheshire.core :as json]
            [org.httpkit.client :as http]))

(defn- coindesk []
  (let [{:keys [error body]} @(http/get "https://api.coindesk.com/v1/bpi/currentprice/VND.json")]
    (when-not error
      (let [bpi (:bpi (json/parse-stream (io/reader body) true))]
        (str "1 BTC\n"
             (get-in bpi [:USD :rate]) " USD\n"
             (get-in bpi [:VND :rate]) " VND")))))

(def memo-coindesk
  (memo/ttl coindesk :ttl/threshold 300000))
