(ns util
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.pprint :refer [print-table]]
            [clojure.java.jdbc :as jdbc]
            [db]
            [image-resizer.core :refer [crop-from dimensions]]
            [image-resizer.format :as format])
  (:import (java.io File)
           (javax.imageio ImageIO)))

(defn- ext [url])

(defn save-image [url file]
  (with-open [in (io/input-stream (str/replace url #" " "%20"))
              out (io/output-stream file)]
    (io/copy in out)))

(defn- crop-image [^File image dest-dir x y]
  (let [buffered-image (ImageIO/read image)
        file-name (.getName image)
        [width height] (dimensions buffered-image)]
    (format/as-file
     (crop-from image x y (- width (* 2 x)) (- height (* 2 y)))
     (str dest-dir "/" file-name)
     :verbatim)))

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

(defn cleanup []
  (doseq [dir (->> (file-seq (io/file "../data-train"))
                   rest
                   (filter #(.isDirectory %)))]
    (let [no-imgs (count (.list dir))
          no-vn-imgs (->> (.list dir)
                          (filter #(str/starts-with? % "vn"))
                          count)]
      (when (or (< no-imgs 30)
                (zero? no-vn-imgs))
        (println "remove" (.getName dir))
        (doseq [f (reverse (file-seq dir))]
          (io/delete-file f))))))

; cp -r data data-train
; rename 's/-/_/g' *
;(cleanup)

#_(print-table
   (some->> (jdbc/query db/db-spec
                        ["SELECT vn_name, genus, species
                        FROM butterfly
                        WHERE vn_name IS NOT NULL;"])
            (map #(assoc % :no-imgs (count (.list (io/file (str "../data/" (:genus %) "_" (:species %)))))))
            (map #(assoc % :train? (when (>= (:no-imgs %) 30) "✅")))
            (sort-by :no-imgs >)))
