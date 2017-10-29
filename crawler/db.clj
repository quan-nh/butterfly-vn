(ns db
  (:require [clojure.java.jdbc :as jdbc]))

(def db-spec {:classname   "org.sqlite.JDBC"
              :subprotocol "sqlite"
              :subname     "../db/butterfly.db"})

(def butterfly-table-ddl
  (jdbc/create-table-ddl :butterfly
                         [[:superfamily :text]
                          [:family :text]
                          [:subfamily :text]
                          [:tribe :text]
                          [:genus :text]
                          [:species :text]
                          [:common_name :text]
                          [:vn_name :text]
                          [:url :text]]))

#_(jdbc/db-do-commands db-spec
                     [butterfly-table-ddl
                      "CREATE UNIQUE INDEX name_ix ON butterfly ( genus, species );"])
