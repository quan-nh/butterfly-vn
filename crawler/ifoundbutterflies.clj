(ns ifoundbutterflies
  (:require [clojure.java.io :as io]
            [clojure.java.jdbc :as jdbc]
            [clojure.string :as str]
            [db]
            [net.cgrand.enlive-html :refer :all]
            [util :refer [save-image crop-images]])
  (:import (java.net URL)))

(def base-url "http://www.ifoundbutterflies.org")

(def links
  (let [dom (-> (str base-url "/history-of-species-pages")
                URL. html-resource)]
    (->> (select dom [:div#introtext_95 [:a (attr-starts :href "/sp/")]])
         (map #(get-in % [:attrs :href]))
         set)))

(defn butterfly [url]
  (let [dom (-> (str base-url url) URL. html-resource)
        superfamily (-> dom (select [:a.txn.superfamily]) first text)
        family (-> dom (select [:a.txn.family]) first text)
        subfamily (-> dom (select [:a.txn.subfamily]) first text)
        tribe (-> dom (select [:a.txn.tribe]) first text)
        genus (-> dom (select [:a.txn.genus]) first text)
        species (last (str/split url #"-"))
        common-name (-> dom (select [:div#content :h1]) first text
                        (str/split #" – ") last)
        imgs  (->> (select dom [:a.a_gal])
                   (map #(get-in % [:attrs :href])))]
    {:superfamily superfamily
     :family family
     :subfamily subfamily
     :tribe tribe
     :genus genus
     :species species
     :common-name common-name
     :imgs imgs}))

(def visited-links (atom #{}))
(def error-links (atom #{}))

(defn save-data [links]
  (doseq [link links]
    (println link)
    (try
      (let [{:keys [genus species imgs]} (butterfly link)
            img-dir (str "../data-ifoundbutterflies/" genus "_" species)]
        (.mkdir (io/file img-dir))
        (println "saving" (count imgs) "images")
        (doseq [img imgs]
          (save-image (str base-url img)
                      (str img-dir
                           "/ifoundbutterflies_"
                           (-> img (str/split #"/") last)))))
      (swap! visited-links #(conj % link))
      (catch Exception e
        (println "Exception:" (.getMessage e))
        (swap! error-links #(conj % link)))))
  (prn @visited-links)
  (prn @error-links))

;mv data-ifoundbutterflies/Papilio_clytia data-ifoundbutterflies/Chilasa_clytia
;mv data-ifoundbutterflies/Eurema_andersoni data-ifoundbutterflies/Eurema_andersonii
;(crop-images "../data-ifoundbutterflies" "../data" 10 65)

(defn insert-db []
  (doseq [link links]
    (println link)
    (let [{:keys [superfamily family subfamily tribe genus species common-name] :as bf} (butterfly link)]
      (prn (select-keys bf [:family :genus :species]))
      (jdbc/insert! db/db-spec :butterfly {:superfamily superfamily
                                           :family family
                                           :subfamily subfamily
                                           :tribe tribe
                                           :genus genus
                                           :species species
                                           :common_name common-name
                                           :url (str base-url link)})))
  (jdbc/update! db/db-spec :butterfly
                {:genus "Chilasa"}
                ["genus = ? AND species = ?" "Papilio" "clytia"])
  (jdbc/update! db/db-spec :butterfly
                {:species "andersonii"}
                ["genus = ? AND species = ?" "Eurema" "andersoni"]))
