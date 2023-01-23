(ns rest-service.core
  (:require [ring.adapter.jetty :as ring-jetty]
            [reitit.ring :as ring]
            [muuntaja.core :as m]
            [reitit.ring.middleware.muuntaja :as muuntaja])
  (:gen-class))

(def users (atom {}))

(defn create-user [{user :body-params}]
  (let [id (str (java.util.UUID/randomUUID))
        users (->> (assoc user :id id) (swap! users assoc id))]
    {:status 201
     :body users}))

(defn get-users [_]
  {:status 200
   :body @users})

(defn get-user [{{:keys [id]} :path-params}]
  {:status 200
   :body (get @users id)})

(def app
  (ring/ring-handler
   (ring/router
    ["/"
     ["users" {:get get-users
                :post create-user}]
     ["user/:id" {:get get-user}]]
    {:data {:muuntaja m/instance
            :middleware [muuntaja/format-middleware]}})))

(defn start []
  (ring-jetty/run-jetty app {:port 3000
                            :join? false}))

(defn -main
  [& args]
  (start))
