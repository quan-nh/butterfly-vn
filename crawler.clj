(require '[clojure.java.io :as io]
         '[clojure.string :as str]
         '[net.cgrand.enlive-html :refer :all])
(import '[java.net URL])

(def n (atom 0))

(defn save-image [url dir prefix]
  (with-open [in (io/input-stream (str/replace url #" " "%20"))
              out (io/output-stream (str dir "/" prefix "_" (swap! n inc) ".jpg"))]
    (io/copy in out)))

(def links (let [dom (-> "http://www.butterflycircle.com/checklist/"
                         URL. html-resource)]
             (->> (select dom [:div.butterfly-box :a])
                  (map #(get-in % [:attrs :href]))
                  set)))

(defn butterfly [url]
  (let [dom (-> url URL. html-resource)
        genus (-> (select dom [:div#content-container :div.right :table.details [:tr (nth-child 3)] [:td (nth-child 2)] :i])
                  first
                  text
                  str/lower-case)
        species (-> (select dom [:div#content-container :div.right :table.details [:tr (nth-child 4)] [:td (nth-child 2)] :i])
                    first
                    text)
        imgs (->> (select dom [:div#gallery-thumbs :div :img])
                  (map #(get-in % [:attrs :data-image])))]
    [genus species imgs]))

(defn save-data []
  (doseq [link links]
    (println link)
    (let [[genus species imgs] (butterfly link)
          img-dir (str "data/" genus)]
      (.mkdir (io/file img-dir))
      (println "save" (count imgs) "images into dir" img-dir)
      (doall (map #(save-image % img-dir species) imgs)))))

(defn cleanup []
  (doseq [dir (->> (file-seq (io/file "data"))
                   rest
                   (filter #(.isDirectory %)))]
    (when (< (count (.list dir)) 30)
      (println "remove" (.getName dir))
      (doseq [f (reverse (file-seq dir))]
        (io/delete-file f)))))
