(ns handler
  (:require [dl]
            [fb]))

(def verify-token (System/getenv "VERIFY_TOKEN"))

(defn verification [{{mode      "hub.mode"
                      token     "hub.verify_token"
                      challenge "hub.challenge"} :params}]
  (if (and (= "subscribe" mode)
           (= verify-token token))
    challenge
    {:status 403}))

(defn- predict-image [sender-id image-url]
  (let [[status body] (dl/memo-label-image image-url)]
    (case status
      200
      (let [[vn-name predict-result web-url] body
            template (fb/generic-template vn-name predict-result image-url web-url)]
        (fb/send-message sender-id
                         {:attachment template}))

      400
      (fb/send-message sender-id
                       {:text "Butterfly not found!"})

      (do
        (prn body)
        (fb/send-message sender-id
                         {:text "Oops! Something went wrong."})))))

(defn- handle-message [sender-id {:keys [text attachments]}]
  (if-let [image-url (or (fb/attachment-url (first attachments))
                         (some->> text (re-find #"https?://\S+")))]
    (predict-image sender-id image-url)
    (fb/send-message sender-id
                     {:text "I can't see Butterfly in your message. Make sure your message has a butterfly image link, or you can send an image directly!"})))

(defn- handle-postback [sender-id {:keys [payload]}]
  (case payload
    "get_started"
    (fb/send-message sender-id
                     {:text "Send me your butterfly photo and we will help you classify it at Species level."})

    "yes"
    (fb/send-message sender-id
                     {:text "Thanks!"})

    "no"
    (fb/send-message sender-id
                     {:text "Oops! Try sending another image."})))

(def received-messages (atom #{}))

(defn handle-event [{{:keys [object entry]} :body}]
  (if (= "page" object)
    (do
      (doseq [{:keys [messaging]} entry]
        (let [{:keys [sender recipient timestamp message postback]} (first messaging)
              msg-id (str (:id sender) "_" (:id recipient) "_" timestamp)]
          (when-not (@received-messages msg-id)
            (swap! received-messages conj msg-id)
            (fb/send-action (:id sender) "typing_on")
            (if message
              (handle-message (:id sender) message)
              (handle-postback (:id sender) postback)))))
      "EVENT_RECEIVED")
    {:status 404}))
