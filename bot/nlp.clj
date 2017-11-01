(ns nlp
  (:require [clojure.java.io :as io]
            [clojure.string :as str])
  (:import (vn.hus.nlp.tokenizer VietTokenizer)))

(defonce non-words #{"." "," ";" "?" "!" "(" ")" "\"" ":" "-" "..."})
(defonce stop-words (-> (io/resource "stopwords.txt")
                        slurp
                        str/split-lines
                        set))

(defonce vn-tokenizer (VietTokenizer.))

(defn segment [s]
  (str/split
   (->> s
        str/lower-case
        (.segment vn-tokenizer))
   #" "))

(defn words [s]
  (->> (segment s)
       (remove #(contains? non-words %))
       (remove #(contains? stop-words %))
       (remove #(re-find #"\d+" %))
       (map #(str/replace % #"_" " "))
       set))
