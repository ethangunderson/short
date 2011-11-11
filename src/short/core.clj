(ns short.core
  (:use compojure.core)
  (:require [compojure.route :as route]
            [compojure.handler :as handler]
            [clj-riak.client :as client])
  (:import (java.security MessageDigest)))

(def rc (client/init {:host "127.0.0.1" :port 8081}))

(defn get-hash-str [data-bytes]
  (apply str 
  (map 
    #(.substring 
      (Integer/toString 
    (+ (bit-and % 0xff) 0x100) 16) 1)
    data-bytes)
  ))

(defn sha1 [string]
  (get-hash-str (.digest (java.security.MessageDigest/getInstance "sha1") (.getBytes string) )))

(defn shorten [url]
  (subs (sha1 url) 0 7))

(defn store [url short-code]
  (client/put rc "urls" short-code
    {:value (.getBytes url)
     :content-type "text/plain"}))

(defn shorten-and-store [url]
  (let [short-code (shorten url)]
    (store url short-code)
    short-code))

(defn externalize [url]
  (if (= "http://" (subs url 0 7)) url (str "http://" url)))

(defn get-url [short-code]
  (String. (:value (client/get rc "urls" short-code))))

(defn redirect [url]
  {:status 302
   :headers {"Location" (str (externalize url))}
   :body ""})

(defroutes main-routes
  (GET "/shorten" {params :params} (str (shorten-and-store (:url params))))
  (GET "/:short-code" [short-code] (redirect (get-url short-code)))
  (route/resources "/")
  (route/not-found "Page not found"))

(def app
  (handler/site main-routes))
