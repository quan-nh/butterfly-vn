(ns ifoundbutterflies
  (:require [clojure.java.io :as io]
            [image-resizer.core :refer [crop-from dimensions]]
            [image-resizer.format :as format])
  (:import
   [java.io File]
   [javax.imageio ImageIO]))

(defn crop [^File image]
  (let [buffered-image (ImageIO/read image)
        file-name (.getName image)
        [width height] (dimensions buffered-image)]
    (format/as-file
     (crop-from image 10 65 (- width 20) (- height 130))
     (str "./" file-name))))

(crop (io/file "/Users/quan/data/03HypolimnasMisippusKrushnameghKunte_aa747.jpg"))
