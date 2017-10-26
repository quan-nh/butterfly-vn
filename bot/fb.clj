(ns fb
  (:require [cheshire.core :as json]
            [org.httpkit.client :as http]))

(defn attachment-url [attachment]
  ;; link in text file
  ; [{:title "pbs.twimg.com", :url "https://l.facebook.com/l.php?u=https%3A%2F%2Fpbs.twimg.com%2Fmedia%2FC7knzrWVwAATSPm.jpg&h=ATOIp..", :type "fallback", :payload nil}]
  ;; attach message
  ; [{:type "image", :payload {:url "https://scontent.xx.fbcdn.net/v/t35.0-12/22833638_206949.."}}]
  "https://pbs.twimg.com/media/CpJj7qFWcAEnwaH.jpg")

(defn generic-template [text image-url web-url payload-id]
  {:type "template"
   :payload {:template_type "generic"
             :elements [{:title "Giống (chi) - Genus"
                         :subtitle text
                         :image_url image-url
                         :buttons [{:type "web_url"
                                    :url web-url
                                    :title "Chi tiết"}
                                   {:type "postback"
                                    :title "Yes!"
                                    :payload (str "yes-" payload-id)},
                                   {:type "postback"
                                    :title "No!"
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
