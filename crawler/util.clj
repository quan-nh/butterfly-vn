(ns util
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.pprint :refer [print-table]]
            [clojure.java.jdbc :as jdbc]
            [clj-http.client :as http]
            [db]
            [image-resizer.core :refer [crop-from dimensions]]
            [image-resizer.format :as format])
  (:import (java.io File)
           (javax.imageio ImageIO)))

(defn save-image [url file]
  (with-open [out (io/output-stream file)]
    (io/copy (:body (http/get (str/replace url #" " "%20") {:as :stream}))
             out)))

(defn- crop-image [^File image dest-dir x y]
  (try
    (let [buffered-image (ImageIO/read image)
          file-name (.getName image)
          [width height] (dimensions buffered-image)]
      (format/as-file
        (crop-from image x y (- width (* 2 x)) (- height (* 2 y)))
        (str dest-dir "/" file-name)
        :verbatim))
    (catch Exception _)))

(defn crop-images [source dest x y]
  (doseq [dir (->> (file-seq (io/file source))
                   rest
                   (filter #(.isDirectory %)))]
    (let [dest-dir (str dest "/" (.getName dir))]
      (println "crop image, save to dir" dest-dir)
      (.mkdir (io/file dest-dir))
      (doall
        (pmap #(crop-image % dest-dir x y)
              (rest (file-seq dir)))))))

(def img-dir "./img")
(def csv-file "./butterfly_all_data.csv")
(def bucket "gs://butterfly-244505-vcm/img/butterfly")

(defn ->csv
  "Put info to csv file for AutoML training"
  []
  (doseq [dir (->> (file-seq (io/file img-dir))
                   rest
                   (filter #(.isDirectory %))
                   (filter #(or (and (>= (count (.list %)) 30)
                                     (pos? (count (filter (fn [name] (str/starts-with? name "vn"))
                                                          (.list %)))))
                                (>= (count (.list %)) 100))))]
    (doseq [file (rest (file-seq dir))]
      (spit csv-file (str bucket "/" (.getName dir) "/" (.getName file) "," (.getName dir) "\n") :append true))))

#_(let [vn-data (->> (jdbc/query db/db-spec
                                 ["SELECT vn_name, genus, species
                                FROM butterfly
                                WHERE vn_name IS NOT NULL;"])
                     (reduce (fn [m {:keys [vn_name genus species]}]
                               (assoc m (str genus "_" species) vn_name))
                             {}))
        dir (->> (file-seq (io/file "./img"))
                 rest
                 (filter #(.isDirectory %))
                 (map (fn [dir]
                        {:name       (-> (.getName dir) (str/split #"/") last)
                         :no-imgs    (count (.list dir))
                         :no-vn-imgs (->> (.list dir)
                                          (filter #(str/starts-with? % "vn"))
                                          count)})))]
    (print-table [:vn-name :name :no-imgs :no-vn-imgs :train?]
                 (some->> dir
                          (filter #(pos? (:no-vn-imgs %)))
                          (map #(assoc % :vn-name (get vn-data (:name %))))
                          (map #(assoc % :train? (when (>= (:no-imgs %) 30) "✅")))
                          (sort-by :no-imgs >))))
