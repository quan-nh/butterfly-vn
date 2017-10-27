(ns fb
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [cheshire.core :as json]
            [org.httpkit.client :as http]))

(defn attachment-url [attachment]
  (cond
    (= "image" (:type attachment))
    (get-in attachment [:payload :url])

    (:url attachment)
    (let [query-params (-> (:url attachment) io/as-url .getQuery (str/split #"&"))
          u (->> query-params
                 (map #(str/split % #"="))
                 (filter (fn [[k _]] (= "u" k)))
                 first
                 second)]
      (java.net.URLDecoder/decode u "UTF-8"))))

(defn generic-template [text image-url web-url payload-id]
  {:type "template"
   :payload {:template_type "generic"
             :elements [{:title "Giống (chi)"
                         :subtitle text
                         :image_url image-url
                         :buttons [{:type "web_url"
                                    :url web-url
                                    :title "Chi tiết"}
                                   {:type "postback"
                                    :title "👍"
                                    :payload (str "yes-" payload-id)},
                                   {:type "postback"
                                    :title "👎"
                                    :payload (str "no-" payload-id)}]}]}})

(defn send-message [sender-psid response]
  (http/post "https://graph.facebook.com/v2.6/me/messages"
             {:query-params {:access_token (System/getenv "PAGE_ACCESS_TOKEN")}
              :headers {"Content-Type" "application/json"}
              :body (json/encode {:recipient {:id sender-psid}
                                  :message response})}
             (fn [{:keys [error]}]
               (when error
                 (println "Failed, exception is " error)))))
