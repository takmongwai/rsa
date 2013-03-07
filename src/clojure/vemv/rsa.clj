(ns vemv.rsa
  (:require [hiccup.core :refer [html]]
            [ring.adapter.jetty :as jetty]
            [ring.util.response :as response]
            [compojure.core :refer (GET POST routes)]
            [compojure.route :refer [resources]]
            [compojure.handler]
            [net.cgrand.enlive-html :as enlive]
            [clj-http.client :as client])
  (:import vemv.RSA))

(let [[public private] (RSA/generate)]

  (def server-public-key public)

  (def server-private-key private))

(def users (atom {}))

(defn uid []
  (first (.split (str (java.util.UUID/randomUUID)) "-")))

(defn signup-page [uid public private]
  (html [:html
         [:head]
         [:body
          "User id: " [:p uid]
          "Public key: " [:p public]
          "Private key:" [:p private]]]))

(def app
  (compojure.handler/site
   (routes
    (resources "/")
    (GET "/" [] "Home")
    (GET "/public-key" [] server-public-key)
    (GET "/test/:message" [message]
         (str "You wrote: "(RSA/decrypt server-private-key message)))
    (GET "/new-user" []
         (let [[public private] (RSA/generate)
               uid (uid)]
           (swap! users assoc uid {:public-key public})
           (signup-page uid public private)))
    (GET "/challenge/:uid" [uid]
         (let [challenge (str (java.util.UUID/randomUUID))]
           (swap! users update-in [uid] assoc :challenge challenge)
           (RSA/encrypt (-> @users (get uid) :public-key) challenge)))
    (GET "/login/:uid/:challenge" [uid challenge]
         (str (= challenge (-> @users (get uid) :challenge)))))))
        
(defonce jetty (jetty/run-jetty #'app {:port 8000 :join? false}))

;; -------------------

(comment
  (:body (client/get (str "http://localhost:8000/test/" (RSA/encrypt server-public-key "hello")))))

;; -------------------

(comment
  (let [[uid pub priv] (map (comp first :content)
                            (enlive/select (enlive/html-snippet
                                            (:body (client/get "http://localhost:8000/new-user")))
                                           [:p]))
        challenge (:body (client/get (str "http://localhost:8000/challenge/" uid)))
        solution (RSA/decrypt priv challenge)]
    (:body (client/get (str "http://localhost:8000/login/" uid "/" solution)))))
