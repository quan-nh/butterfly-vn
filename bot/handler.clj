(ns handler
  (:require [clojure.string :as str]
            [dl] [fb]))

(def verify-token (System/getenv "VERIFY_TOKEN"))

(defn verification [{{mode "hub.mode"
                      token "hub.verify_token"
                      challenge "hub.challenge"} :params}]
  (if (and (= "subscribe" mode)
           (= verify-token token))
    challenge
    {:status 403}))

(defn handle-message [sender-psid {:keys [text attachments]}]
  (if-let [image-url (or (fb/attachment-url (first attachments))
                         (some->> text (re-find #"https?://\S+")))]
    (let [[status body] (dl/memo-label-image image-url)]
      (case status
        200
        (let [[text web-url] body
              template (fb/generic-template text image-url web-url)]
          (fb/send-message sender-psid
                           {:attachment template}))

        400
        (fb/send-message sender-psid
                         {:text "Send link or attach image!"})

        (fb/send-message sender-psid
                         {:text "Oops! Something went wrong."})))

    (fb/send-message sender-psid
                     {:text "Send link or attach image!"})))

(defn handle-postback [sender-psid {:keys [payload]}]
  (case payload
    "yes"
    (fb/send-message sender-psid
                     {:text "Thanks!"})

    "no"
    (fb/send-message sender-psid
                     {:text "Oops! Try sending another image."})))

(defn handle-event [{:keys [body]}]
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