(ns db
  (:require [clojure.java.jdbc :as jdbc]
            [clojure.string :as str]))

(def ^:private db-spec {:classname   "org.sqlite.JDBC"
                        :subprotocol "sqlite"
                        :subname     "../db/messenger.db"})

(defn ^:private messenger-table-ddl []
  (jdbc/db-do-commands db-spec
                       [(jdbc/create-table-ddl :messenger
                                               [[:sender_psid :text]
                                                [:text :text]
                                                [:image_url :text]
                                                [:label_image :text]
                                                [:feedback :text]])]))

(defn insert [sender-psid text image-url]
  (let [rowid (first (jdbc/insert! db-spec
                                   :messenger
                                   {:sender_psid sender-psid
                                    :text text
                                    :image_url image-url}))]
    (get rowid (keyword "last_insert_rowid()"))))

(defn update-label [rowid label-image]
  (jdbc/update! db-spec :messenger
                {:label_image label-image}
                ["rowid = ?" rowid]))

(defn update-feedback [sender-psid payload]
  (let [[feedback rowid] (str/split payload #"-")]
    (jdbc/update! db-spec :messenger
                  {:feedback feedback}
                  ["rowid = ? AND sender_psid = ?" rowid sender-psid])))
