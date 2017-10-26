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
  (if (seq attachments)
    (let [image-url (fb/attachment-url (first attachments))
          payload-id (db/save sender-psid text image-url)
          [text web-url] (dl/label-image image-url)
          template (fb/generic-template text image-url web-url payload-id)]
      (fb/send-message sender-psid
                       {:attachment template}))
    (fb/send-message sender-psid
                     {:text "Send link or attach image!"})))

(defn handle-postback [sender-psid {:keys [payload]}]
  ;; todo log feedback
  (cond
    (str/starts-with? payload "yes") (fb/send-message sender-psid
                                                      {:text "Thanks!"})
    (str/starts-with? payload "no") (fb/send-message sender-psid
                                                     {:text "Oops, try sending another image."})))

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
