(ns handler
  (:require [clojure.string :as str]
            [clojure.pprint :refer [pprint]]
            [dl] [fb]))

(def verify-token (System/getenv "VERIFY_TOKEN"))

(defn verification [{{mode "hub.mode"
                      token "hub.verify_token"
                      challenge "hub.challenge"} :params}]
  (if (and (= "subscribe" mode)
           (= verify-token token))
    challenge
    {:status 403}))

(defn handle-message [sender-psid {:keys [text nlp attachments]}]
  (let [greeting (first (get-in nlp [:entities :greeting]))
        image-url (or (fb/attachment-url (first attachments))
                      (some->> text (re-find #"https?://\S+")))]
    (cond
      (some-> greeting :confidence (> 0.8))
      (fb/send-message sender-psid
                       {:text "Xin chào!"})

      image-url
      (let [[status body] (dl/memo-label-image image-url)]
        (case status
          200
          (let [[vn-name predict-result web-url] body
                template (fb/generic-template vn-name predict-result image-url web-url)]
            (fb/send-message sender-psid
                             {:attachment template}))

          400
          (fb/send-message sender-psid
                           {:text "I can't see Butterfly in your message. Make sure your message has a butterfly image link, or you can send an image directly!"})

          (fb/send-message sender-psid
                           {:text "Oops! Something went wrong."})))

      :else
      (fb/send-message sender-psid
                       {:text "Send me your butterfly photo and we will help you classify it at Species level."}))))

(defn handle-postback [sender-psid {:keys [payload]}]
  (case payload
    "yes"
    (fb/send-message sender-psid
                     {:text "Thanks!"})

    "no"
    (fb/send-message sender-psid
                     {:text "Oops! Try sending another image."})))

(defn handle-event [{:keys [body]}]
  ;(pprint body)
  (if (= "page" (:object body))
    (do
      (doseq [entry (:entry body)]
        (let [webhook-event (first (:messaging entry))
              sender-psid (get-in webhook-event [:sender :id])]
          (cond
            (:message webhook-event) (handle-message sender-psid (:message webhook-event))
            (:postback webhook-event) (handle-postback sender-psid (:postback webhook-event)))))
      "EVENT_RECEIVED")
    {:status 404}))
