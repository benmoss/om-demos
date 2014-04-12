(defproject om-demos "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"

  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/clojurescript "0.0-2173"]
                 [org.clojure/core.async "0.1.267.0-0d7780-alpha"]
                 [com.facebook/react "0.9.0.1"]
                 [om "0.5.3"]]

  :plugins [[lein-cljsbuild "1.0.2"]]

  :source-paths ["src"]

  :cljsbuild {
    :builds [{:id "om-demos"
              :source-paths ["src"]
              :compiler {
                :output-to "om_demos.js"
                :output-dir "out"
                :optimizations :none
                :source-map true}}]})
