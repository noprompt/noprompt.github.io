(defproject blog "0.1.0-SNAPSHOT"
  :description "Prom is cancelled"
  :url "http://noprompt.github.io/"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [garden "1.1.5"]]
  :plugins [[lein-garden "0.1.5"]]
  :source-paths ["src"]
  :garden {:builds [{:id "main"
                     :stylesheet blog.core/main
                     :compiler {:output-to "../../css/main.css"
                                :vendors [:moz :webkit]
                                :pretty-print? true}}]})
