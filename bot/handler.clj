(ns handler
  (:require [clojure.string :as str]
            [db] [dl] [fb]))

(defn verification [{{mode "hub.mode"
                      token "hub.verify_token"
                      challenge "hub.challenge"} :params}]
  (if (and (= "subscribe" mode)
           (= (System/getenv "VERIFY_TOKEN") token))
    challenge
    {:status 403}))

(defn handle-message [sender-psid {:keys [text attachments]}]
  (if-let [image-url (or (fb/attachment-url (first attachments))
                         (some->> text (re-find #"https?://\S+")))]
    (let [payload-id (db/insert sender-psid text image-url)
          [status body] (dl/memo-label-image image-url)]
      (case status
        200
        (let [[text web-url] body
              template (fb/generic-template text image-url web-url payload-id)]
          (fb/send-message sender-psid
                           {:attachment template})
          (db/update-label payload-id body))

        400
        (fb/send-message sender-psid
                         {:text "Send link or attach image!"})

        (fb/send-message sender-psid
                         {:text "Oops! Something went wrong."})))

    (fb/send-message sender-psid
                     {:text "Send link or attach image!"})))

(defn handle-postback [sender-psid {:keys [payload]}]
  (cond
    (str/starts-with? payload "yes")
    (fb/send-message sender-psid
                     {:text "Thanks!"})

    (str/starts-with? payload "no")
    (fb/send-message sender-psid
                     {:text "Oops, try sending another image."}))
  (db/update-feedback sender-psid payload))

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
