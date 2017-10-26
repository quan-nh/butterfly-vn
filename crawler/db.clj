(ns db
  (:require [clojure.java.jdbc :as jdbc]))

(def db-spec {:classname   "org.sqlite.JDBC"
              :subprotocol "sqlite"
              :subname     "../db/butterfly.db"})

(def butterfly-table-ddl
  (jdbc/create-table-ddl :butterfly
                         [[:family :text]
                          [:subfamily :text]
                          [:genus :text]
                          [:species :text]
                          [:subspecies :text]
                          [:common_name :text]
                          [:vn_name :text]
                          [:wingspan :text]
                          [:status :text]
                          [:url_butterflycircle :text]
                          [:url_vncreatures :text]]))

#_(jdbc/db-do-commands db-spec
                     [butterfly-table-ddl
                      "CREATE UNIQUE INDEX name_ix ON butterfly ( genus, species, subspecies );"])
