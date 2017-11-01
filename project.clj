(defproject messenger-bot "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :repositories [["local-repo" "file:repo"]]
  :dependencies [[org.clojure/clojure "1.9.0-beta3"]
                 [org.clojure/core.memoize "0.5.8"]
                 [ring/ring-core "1.6.2"]
                 [ring/ring-json "0.5.0-beta1"]
                 [compojure "1.6.0"]
                 [http-kit "2.2.0"]
                 [org.clojure/java.jdbc "0.7.3"]
                 [org.xerial/sqlite-jdbc "3.20.1"]
                 [vn.hus/nlp-tokenizer "4.1.1"]]
  :source-paths ["bot"]
  :resource-paths ["db"]
  :uberjar-name "messenger-bot-standalone.jar"
  :profiles {:uberjar {:aot :all}})
