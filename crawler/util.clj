(ns util
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.pprint :refer [print-table]]))

(defn- ext [url] (str/lower-case (subs url (str/last-index-of url "."))))

(defn save-image [url file]
  (try
    (with-open [in (io/input-stream (str/replace url #" " "%20"))
                out (io/output-stream (str file (ext url)))]
      (io/copy in out))
    (catch Exception e (println "caught exception: " (.getMessage e)))))

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

(print-table
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
                :train? (when (>= no-imgs 30) "✅")
                :vn? (when (pos? no-vn-imgs) "✅")})))
      (sort-by :no-imgs >)))
