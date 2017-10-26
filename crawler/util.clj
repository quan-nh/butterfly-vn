(ns util
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))

(defn save-image [url dest]
  (try
    (with-open [in (io/input-stream (str/replace url #" " "%20"))
                out (io/output-stream dest)]
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
