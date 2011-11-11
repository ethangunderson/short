(defproject short "1.0.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.2.0"]
                 [org.clojure/clojure-contrib "1.2.0"]
                 [compojure "0.6.4"]
                 [org.clojars.brenton/clj-riak "0.1.0-SNAPSHOT"]]
  :dev-dependencies [[lein-ring "0.4.5"]]
  :ring {:handler short.core/app})
