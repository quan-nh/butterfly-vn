(ns butterflycircle
  (:require [clojure.java.io :as io]
            [clojure.java.jdbc :as jdbc]
            [clojure.string :as str]
            [db]
            [net.cgrand.enlive-html :refer :all]
            [util :refer [save-image]])
  (:import (java.net URL)))

(def links (let [dom (-> "http://www.butterflycircle.com/checklist/"
                         URL. html-resource)]
             (->> (select dom [:div.butterfly-box :a])
                  (map #(get-in % [:attrs :href]))
                  set)))

(defn- normalize [name] (-> name str/trim str/lower-case))

(defn- keywordize [name] (-> name (str/replace #"\s+" "-") (str/replace #":" "") keyword))

(defn- parse-tr [tr]
  (->> (select tr [:td])
       (map text)
       (map normalize)))

(defn butterfly [url]
  (let [dom (-> url URL. html-resource)
        details (->> (select dom [:div#content-container :div.right :table.details :tr])
                     (map parse-tr)
                     (reduce (fn [m [k v]]
                               (assoc m (keywordize k) v)) {}))
        imgs (->> (select dom [:div#gallery-thumbs :div :img])
                  (map #(get-in % [:attrs :data-image])))]
    (assoc details :imgs imgs)))

(def n (atom 0))

(defn save-data []
  (doseq [link links]
    (println link)
    (let [{:keys [genus species imgs]} (butterfly link)]
      (.mkdir (io/file (str "../data/" genus)))
      (println "saving" (count imgs) "images")
      (doseq [img imgs]
        (save-image img (str "../data/" genus "/" species "_" (swap! n inc) ".jpg"))))))

(defn insert-db []
  (doseq [link links]
    (println link)
    (let [{:keys [family subfamily genus species subspecies common-name wingspan status] :as bf} (butterfly link)]
      (prn (select-keys bf [:family :subfamily :genus :species :subspecies]))
      (jdbc/insert! db/db-spec :butterfly {:family family
                                           :subfamily subfamily
                                           :genus genus
                                           :species species
                                           :subspecies subspecies
                                           :common_name common-name
                                           :wingspan wingspan
                                           :status status
                                           :url_butterflycircle link}))))
