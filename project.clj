(defproject sequencer "0.1.0"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [compojure "1.4.0"]
                 [hiccup "1.0.5"]
                 [hiccup-bootstrap-3 "0.2.0-SNAPSHOT"]
                 [ring/ring-defaults "0.1.5"]
                 [com.novemberain/monger "3.0.0"]
                 [org.immutant/immutant "2.0.2"]
                 [crypto-password "0.1.3"]
                 [clj-time "0.11.0"]]
  :main ^:skip-aot sequencer.handler
  :target-path "target/%s"
  :jvm-opts ["-server" "-Djava.security.manager" "-Djava.security.policy=java.policy"]
  :profiles {:uberjar {:aot :all}})
