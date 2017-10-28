(ns fb
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [cheshire.core :as json]
            [org.httpkit.client :as http]))

(def page-access-token (System/getenv "PAGE_ACCESS_TOKEN"))

(defn attachment-url [attachment]
  (cond
    (= "image" (:type attachment))
    (get-in attachment [:payload :url])

    (:url attachment)
    (let [query-params (some-> (:url attachment) io/as-url .getQuery (str/split #"&"))
          u (some->> query-params
                     (map #(str/split % #"="))
                     (filter (fn [[k _]] (= "u" k)))
                     first
                     second)]
      (some-> u (java.net.URLDecoder/decode "UTF-8")))
      
    :else nil))

(defn generic-template [text image-url web-url]
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
                                    :payload "yes"},
                                   {:type "postback"
                                    :title "👎"
                                    :payload "no"}]}]}})

(defn send-message [sender-psid response]
  (http/post "https://graph.facebook.com/v2.6/me/messages"
             {:query-params {:access_token page-access-token}
              :headers {"Content-Type" "application/json"}
              :body (json/encode {:recipient {:id sender-psid}
                                  :message response})}
             (fn [{:keys [error]}]
               (when error
                 (println "Failed, exception is " error)))))

(defn greeting
  "docs https://developers.facebook.com/docs/messenger-platform/thread-settings/greeting-text/"
  []
  (http/post "https://graph.facebook.com/v2.6/me/thread_settings"
             {:query-params {:access_token page-access-token}
              :headers {"Content-Type" "application/json"}
              :body (json/encode {:setting_type "greeting"
                                  :greeting {:text "Hi {{user_first_name}}, welcome to Butterfly World.\nSend me your butterfly photo and we will help you classify it at Genus level."}})}
             (fn [{:keys [error]}]
               (when error
                 (println "Failed, exception is " error)))))
