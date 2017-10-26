(ns main
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [handler]
            [ring.middleware.json :refer [wrap-json-body]]
            [ring.middleware.params :refer [wrap-params]]
            [org.httpkit.server :refer [run-server]]))

(defroutes app-routes
  (GET "/webhook" [] handler/verification)
  (POST "/webhook" [] handler/handle-event)
  (route/not-found "Not Found"))

(def app (-> app-routes
             (wrap-json-body {:keywords? true})
             wrap-params))

(run-server app {:port 3000})
