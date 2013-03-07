(defproject vemv.rsa "0.1.0-SNAPSHOT"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.0"]
                 [org.openid4java/openid4java-consumer "0.9.5"]
                 [ring "1.1.6"]
                 [compojure "1.1.3"]
                 [enlive "1.0.1"]
                 [clj-http "0.6.5"]
                 [org.bouncycastle/bcprov-jdk16 "1.46"]]
  :source-paths ["src/clojure"]
  :java-source-paths ["src/java"])
