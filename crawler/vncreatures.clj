(ns vncreatures
  (:require [clojure.java.io :as io]
            [clojure.java.jdbc :as jdbc]
            [clojure.string :as str]
            [db]
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

(defn save-data []
  (doseq [[id vn-name genus species] (mapcat butterflies (range 1 7))]
    (.mkdir (io/file (str "../data/" genus)))
    (save-image (str base-url "/pictures/insect/" id "s.jpg") (str "../data/" genus "/" species "_vn_" (swap! n inc)))
    (save-image (str base-url "/pictures/insect/" id "_1s.jpg") (str "../data/" genus "/" species "_vn_" (swap! n inc)))
    (save-image (str base-url "/pictures/insect/" id "_2s.jpg") (str "../data/" genus "/" species "_vn_" (swap! n inc)))
    (save-image (str base-url "/pictures/insect/" id "_3s.jpg") (str "../data/" genus "/" species "_vn_" (swap! n inc)))))

(defn insert-db []
  (doseq [[id vn-name genus species] (mapcat butterflies (range 1 7))]
    (if (seq (jdbc/query db/db-spec ["SELECT * FROM butterfly WHERE genus = ? AND species = ?" genus species]))
      (jdbc/update! db/db-spec :butterfly
                    {:vn_name vn-name
                     :url_vncreatures (str base-url "/chitiet.php?loai=3&ID=" id)}
                    ["genus = ? AND species = ?" genus species])
      (jdbc/insert! db/db-spec :butterfly {:genus genus
                                           :species species
                                           :vn_name vn-name
                                           :url_vncreatures (str base-url "/chitiet.php?loai=3&ID=" id)}))))
