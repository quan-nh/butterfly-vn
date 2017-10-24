(require '[clojure.java.io :as io]
         '[clojure.string :as str]
         '[net.cgrand.enlive-html :refer :all])
(import '[java.net URL])

(def links (let [dom (-> "http://www.butterflycircle.com/checklist/"
                         URL. html-resource)]
             (->> (select dom [:div.butterfly-box :a])
                  (map #(get-in % [:attrs :href]))
                  set)))

(defn save-image [url dir]
  (let [name (subs url (inc (str/last-index-of url "/")))]
    (with-open [in (io/input-stream (str/replace url #" " "%20"))
                out (io/output-stream (str dir "/" name))]
      (io/copy in out))))

(doseq [link links]
  (println link)
  (let [dom (-> link URL. html-resource)
        name (-> (select dom [:div#content-container :div.right :h1 :i])
                 first
                 text)
        imgs (->> (select dom [:div#gallery-thumbs :div :img])
                  (map #(get-in % [:attrs :data-image])))
        img-dir (str "data/" name)]

    (println img-dir)
    (.mkdir (java.io.File. img-dir))

    (println "save" (count imgs) "images")
    (doall (map #(save-image % img-dir) imgs))))
