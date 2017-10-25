(ns butterflycircle
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [net.cgrand.enlive-html :refer :all]
            [util :refer [save-image]])
  (:import (java.net URL)))

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

(def n (atom 0))

(doseq [link links]
  (println link)
  (let [[genus species imgs] (butterfly link)]
    (.mkdir (io/file (str "../data/" genus)))
    (println "saving" (count imgs) "images")
    (doall (map #(save-image % (str "../data/" genus "/" species "_" (swap! n inc) ".jpg")) imgs))))
