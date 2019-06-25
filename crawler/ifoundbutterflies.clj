(ns ifoundbutterflies
  "Butterflies of India"
  (:require [clojure.java.io :as io]
            [clojure.java.jdbc :as jdbc]
            [clojure.string :as str]
            [clj-http.client :as http]
            [com.climate.claypoole :as cp]
            [db]
            [net.cgrand.enlive-html :refer :all]
            [util :refer [crop-images]])
  (:import (java.io StringReader)))

(def base-url "https://www.ifoundbutterflies.org")
(def cm (clj-http.conn-mgr/make-reusable-conn-manager {:insecure? true}))

(def links
  (let [dom (-> (str base-url "/history-of-species-pages")
                (http/get {:connection-manager cm}) :body
                StringReader. html-resource)]
    (->> (select dom [:div#introtext_95 [:a (attr-starts :href "/sp/")]])
         (map #(get-in % [:attrs :href]))
         set)))

(defn butterfly [url]
  (let [dom (-> (str base-url url)
                (http/get {:connection-manager cm}) :body
                StringReader. html-resource)
        superfamily (-> dom (select [:a.txn.superfamily]) first text)
        family (-> dom (select [:a.txn.family]) first text)
        subfamily (-> dom (select [:a.txn.subfamily]) first text)
        tribe (-> dom (select [:a.txn.tribe]) first text)
        genus (-> dom (select [:a.txn.genus]) first text)
        species (last (str/split url #"-"))
        common-name (-> dom (select [:div#content :h1]) first text
                        (str/split #" – ") last)
        imgs (->> (select dom [:a.a_gal])
                  (map #(get-in % [:attrs :href])))]
    {:superfamily superfamily
     :family      family
     :subfamily   subfamily
     :tribe       tribe
     :genus       genus
     :species     species
     :common-name common-name
     :imgs        imgs}))

(def base-dir "./img-ifoundbutterflies")
(def csv-file "./all_data.csv")
(def bucket "gs://butterfly-244505-vcm/img/butterfly")

(defn- save-image [img dir label]
  (let [url (str base-url img)
        file (str "ifoundbutterflies_" (-> img (str/split #"/") last))]
    (with-open [out (io/output-stream (str dir "/" file))]
      (io/copy (:body (http/get (str/replace url #" " "%20")
                                {:as                 :stream
                                 :connection-manager cm}))
               out))
    (spit csv-file (str bucket "/" label "/" file "," label "\n") :append true)))

(defn save-data []
  (doseq [link links]
    (println link)
    (try
      (let [{:keys [genus species imgs]} (butterfly link)
            label (str genus "_" species)
            dir (str base-dir "/" label)]
        (.mkdir (io/file dir))
        (println "saving" (count imgs) "images")
        (cp/pmap 4 #(save-image % dir label) imgs))
      (catch Exception e
        (println "Exception:" (.getMessage e))))))

;mv img-ifoundbutterflies/Papilio_clytia img-ifoundbutterflies/Chilasa_clytia
;mv img-ifoundbutterflies/Eurema_andersoni img-ifoundbutterflies/Eurema_andersonii
;(crop-images "./img-ifoundbutterflies" "./img" 10 65)

(defn insert-db []
  (doseq [link links]
    (println link)
    (let [{:keys [superfamily family subfamily tribe genus species common-name] :as bf} (butterfly link)]
      (prn (select-keys bf [:family :genus :species]))
      (jdbc/insert! db/db-spec :butterfly {:superfamily superfamily
                                           :family      family
                                           :subfamily   subfamily
                                           :tribe       tribe
                                           :genus       genus
                                           :species     species
                                           :common_name common-name
                                           :url         (str base-url link)})))
  (jdbc/update! db/db-spec :butterfly
                {:genus "Chilasa"}
                ["genus = ? AND species = ?" "Papilio" "clytia"])
  (jdbc/update! db/db-spec :butterfly
                {:species "andersonii"}
                ["genus = ? AND species = ?" "Eurema" "andersoni"]))

;(clj-http.conn-mgr/shutdown-manager cm)
