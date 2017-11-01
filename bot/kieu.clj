(ns kieu
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))

(defonce content (->> (io/resource "kieu.txt")
                      slurp
                      str/split-lines
                      (partition 4)
                      (map (fn [[a b c d]]
                             (str a "\n" b "\n" c "\n" d)))))

(defn query [text]
  (try
    (->> content
         (filter #(str/includes? (str/lower-case %)  (str/lower-case text)))
         rand-nth)
    (catch Exception _
      (rand-nth content))))
