(defproject messenger-bot "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0-beta3"]
                 [org.clojure/core.memoize "0.5.8"]
                 [ring/ring-core "1.6.2"]
                 [ring/ring-json "0.5.0-beta1"]
                 [compojure "1.6.0"]
                 [http-kit "2.2.0"]]
  :uberjar-name "messenger-bot-standalone.jar"
  :profiles {:uberjar {:aot :all}})
