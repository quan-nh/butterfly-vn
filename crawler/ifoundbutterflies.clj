(ns ifoundbutterflies
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [image-resizer.core :refer [crop-from dimensions]]
            [image-resizer.format :as format]
            [net.cgrand.enlive-html :refer :all]
            [util :refer [save-image]])
  (:import (java.net URL)
           (java.io File)
           (javax.imageio ImageIO)))

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
            img-dir (str "../data-ifoundbutterflies/" genus "-" species)]
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

(defn crop-image [^File image dest-dir]
  (let [buffered-image (ImageIO/read image)
        file-name (.getName image)
        [width height] (dimensions buffered-image)]
    (format/as-file
     (crop-from image 10 65 (- width 20) (- height 130))
     (str dest-dir "/" file-name)
     :verbatim)))

;mv data-ifoundbutterflies/Papilio-clytia data-ifoundbutterflies/Chilasa-clytia     
(defn crop-images []
  (doseq [dir (->> (file-seq (io/file "../data-ifoundbutterflies"))
                   rest
                   (filter #(.isDirectory %)))]
    (let [dest-dir (str "../data/" (.getName dir))]
      (println "crop image, save to dir" dest-dir)
      (.mkdir (io/file dest-dir))
      (doall
       (pmap #(crop-image % dest-dir)
             (rest (file-seq dir)))))))
