(ns butterflycircle
  (:require [clojure.java.io :as io]
            [clojure.java.jdbc :as jdbc]
            [clojure.string :as str]
            [db]
            [net.cgrand.enlive-html :refer :all]
            [util :refer [save-image crop-images]])
  (:import (java.net URL)))

(def links (let [dom (-> "http://www.butterflycircle.com/checklist/"
                         URL. html-resource)]
             (->> (select dom [:div.butterfly-box :a])
                  (map #(get-in % [:attrs :href]))
                  set)))

(defn- normalize [name] (-> name str/trim str/lower-case))

(defn- keywordize [name] (-> name (str/replace #"\s+" "-") (str/replace #":" "") keyword))

(defn- capitalize-words 
  "Capitalize every word in a string"
  [s]
  (->> (str/split (str s) #"\b") 
       (map str/capitalize)
       str/join))

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
    (let [{:keys [genus species imgs]} (butterfly link)
          dir (str "../data-butterflycircle/" (str/capitalize genus) "_" species)]
      (.mkdir (io/file dir))
      (println "saving" (count imgs) "images to dir" dir)
      (doall
       (pmap
        (fn [img]
          (let [ext (str/lower-case (subs img (str/last-index-of img ".")))]
            (save-image img (str dir "/butterflycircle_" (swap! n inc) ext))))
        imgs)))))

;(crop-images "../data-butterflycircle" "../data" 20 30)

(defn insert-db []
  (doseq [link links]
    (println link)
    (let [{:keys [family subfamily genus species common-name] :as bf} (butterfly link)]
      (prn (select-keys bf [:family :genus :species]))
      (when-not (seq (jdbc/query db/db-spec ["SELECT * FROM butterfly WHERE genus = ? AND species = ?" (str/capitalize genus) species]))
        (println "inserting..")
        (jdbc/insert! db/db-spec :butterfly {:family (str/capitalize family)
                                             :subfamily (str/capitalize subfamily)
                                             :genus (str/capitalize genus)
                                             :species species
                                             :common_name (capitalize-words common-name)})))))
