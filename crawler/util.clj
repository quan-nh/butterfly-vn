(ns util
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.pprint :refer [print-table]]
            [clojure.java.jdbc :as jdbc]
            [db]
            [doric.core :refer [table]]
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
    (when (< (count (.list dir)) 30)
      (println "remove" (.getName dir))
      (doseq [f (reverse (file-seq dir))]
        (io/delete-file f)))))

; cp -r data data-train
;(cleanup)

#_(let [data (->> (jdbc/query db/db-spec
                              ["SELECT genus, GROUP_CONCAT(vn_name) vn_names
                              FROM butterfly
                              WHERE vn_name IS NOT NULL
                              GROUP BY genus;"])
                  (reduce (fn [m {:keys [genus vn_names]}]
                            (assoc m genus vn_names)) {}))]
    (println
     (table [:genus :no-imgs :no-vn-imgs :train? :species]
            (->> (file-seq (io/file "../data"))
                 rest
                 (filter #(.isDirectory %))
                 (map (fn [dir]
                        (let [no-imgs (count (.list dir))
                              no-vn-imgs (->> (.list dir)
                                              (filter #(str/includes? % "_vn_"))
                                              count)]
                          {:genus (.getName dir)
                           :no-imgs no-imgs
                           :no-vn-imgs no-vn-imgs
                           :train? (when (>= no-imgs 30) "✅")
                           :species (get data (.getName dir))})))
                 (sort-by :no-imgs >)))))