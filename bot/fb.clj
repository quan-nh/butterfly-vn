(ns fb
  (:require [clojure.core.memoize :as memo]
            [clojure.java.io :as io]
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

(defn generic-template [title subtitle image-url web-url]
  {:type "template"
   :payload {:template_type "generic"
             :elements [{:title title
                         :subtitle subtitle
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

(defn- user-profile
  "Retrieving a Person's Profile"
  [psid]
  (let [{:keys [error body]} @(http/get (str "https://graph.facebook.com/v2.6/" psid)
                                        {:query-params {:fields "first_name,last_name,profile_pic"
                                                        :access_token page-access-token}})]
    (when-not error
      (json/decode body true))))

(def memo-user-profile (memo/lu user-profile))

;; settings
;; https://developers.facebook.com/docs/messenger-platform/reference/messenger-profile-api/
(defn get-started []
  (let [{:keys [error body]} @(http/post "https://graph.facebook.com/v2.6/me/messenger_profile"
                                         {:query-params {:access_token page-access-token}
                                          :headers {"Content-Type" "application/json"}
                                          :body (json/encode {:get_started
                                                              {:payload "get_started"}})})]
    (if error
      (println "Failed, exception is " error)
      (println body))))

(defn greeting []
  (let [{:keys [error body]} @(http/post "https://graph.facebook.com/v2.6/me/messenger_profile"
                                         {:query-params {:access_token page-access-token}
                                          :headers {"Content-Type" "application/json"}
                                          :body (json/encode {:greeting
                                                              [{:locale "default"
                                                                :text "Hãy cho chúng tôi thấy Bướm của bạn\nChúng tôi sẽ cho bạn biết Bướm bạn thuộc Loài nào!"}]})})]
    (if error
      (println "Failed, exception is " error)
      (println body))))
