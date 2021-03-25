(defproject front-battle-api "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [compojure "1.6.1"]
                 [ring/ring-defaults "0.3.2"]
                 [org.clojure/java.jdbc "0.7.12"]
                 [honeysql "1.0.461"]
                 [org.postgresql/postgresql "42.2.19"]
                 [ragtime "0.8.0"]
                 [metosin/compojure-api "2.0.0-alpha31"]
                 [prismatic/schema "1.1.12"]
                 [yogthos/config "1.1.7"]
                 [ring/ring-json "0.5.0"]
                 [failjure "2.2.0"]]
  :plugins [[lein-ring "0.12.5"]]
  :ring {:handler front-battle-api.handler/app
         :nrepl {:start? true
                 :port 4000}}
  :profiles
  {:dev {:resource-paths ["config/dev"]
         :dependencies   [[javax.servlet/servlet-api "2.5"]
                          [ring/ring-mock "0.3.2"]]}
   :prod {:resource-paths ["config/prod"]}})
