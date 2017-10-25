(ns vncreatures
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [net.cgrand.enlive-html :refer :all]
            [util :refer [save-image]])
  (:import (java.net URL)))

(def base-url "http://www.vncreatures.net")

(defn butterflies [page]
  (let [dom (-> (str base-url "/kqtracuu.php?type=nhom&loai=3&page=" page)
                URL. html-resource)]
    (->> (select dom [[:a (attr-starts :href (format "./chitiet.php?page=%d&loai=3&ID=" page) :class "aLink")]])
         (map (fn [node]
                [(re-find #"\d+$" (get-in node [:attrs :href]))
                 (-> node text str/trim)]))
         (partition 2)
         (filter (fn [[[id1 vn-name] [id2 _]]]
                   (and (= id1 id2)
                        (str/starts-with? vn-name "Bướm"))))
         (map (fn [[[id vn-name] [_ latin-name]]]
                (let [[genus species] (str/split latin-name #"\s+")]
                  [id vn-name (str/lower-case genus) (str/lower-case species)]))))))

(def n (atom 0))

(doseq [[id vn-name genus species] (mapcat butterflies (range 1 6))]
  (.mkdir (io/file (str "../data/" genus)))
  (save-image (str base-url "/pictures/insect/" id "s.jpg") (str "../data/" genus "/" species "_vn_" (swap! n inc) ".jpg"))
  (save-image (str base-url "/pictures/insect/" id "_1s.jpg") (str "../data/" genus "/" species "_vn_" (swap! n inc) ".jpg"))
  (save-image (str base-url "/pictures/insect/" id "_2s.jpg") (str "../data/" genus "/" species "_vn_" (swap! n inc) ".jpg"))
  (save-image (str base-url "/pictures/insect/" id "_3s.jpg") (str "../data/" genus "/" species "_vn_" (swap! n inc) ".jpg")))
