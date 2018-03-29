(ns db
  (:require [clojure.java.jdbc :as jdbc]))

(def db-spec {:classname   "org.sqlite.JDBC"
              :subprotocol "sqlite"
              :subname   "../db/butterfly.db"})

(defn butterfly-table-ddl []
  (jdbc/db-do-commands db-spec
                       [(jdbc/create-table-ddl :butterfly
                                               [[:superfamily :text]
                                                [:family :text]
                                                [:subfamily :text]
                                                [:tribe :text]
                                                [:genus :text]
                                                [:species :text]
                                                [:common_name :text]
                                                [:vn_name :text]
                                                [:url :text]])
                        "CREATE UNIQUE INDEX name_ix ON butterfly ( genus, species );"]))
