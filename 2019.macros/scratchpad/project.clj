(defproject scratchpad "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :main ^:skip-aot scratchpad.core
  :repl-options {:init-ns scratchpad.core}
  :global-vars  {*warn-on-reflection* true}
  :dependencies [
    [org.clojure/clojure                     "1.10.1"]
    [org.clojure/tools.logging                "1.2.1"]
    [org.clojure/tools.nrepl                 "0.2.13"]
    [org.clojure/core.async                 "0.4.500"]
    [cider/cider-nrepl                       "0.22.0"]
    [ch.qos.logback/logback-classic           "1.2.3"]
    [org.clojure/data.json                    "0.2.6"]
    [clj-time                                "0.15.0"]
    [org.clojure/core.cache                   "0.7.2"]
    [http-kit                                 "2.3.0"]
    [ring                                     "1.7.1"]
    [compojure                                "1.6.1"]
    [org.clojure/math.combinatorics           "0.1.6"]
    [com.cognitect.aws/api                  "0.8.243"]
    [com.cognitect.aws/endpoints         "1.1.11.490"]
    [com.cognitect.aws/autoscaling      "697.2.391.0"]
    [com.cognitect.aws/cloudfront       "697.2.391.0"]
    [com.cognitect.aws/ec2              "698.2.395.0"]
    [com.cognitect.aws/ecr              "701.2.394.0"]
    [com.cognitect.aws/iam              "697.2.391.0"]
    [com.cognitect.aws/rds              "701.2.394.0"]
    [com.cognitect.aws/redshift         "697.2.391.0"]
    [com.cognitect.aws/route53          "697.2.391.0"]
    [com.cognitect.aws/route53domains   "697.2.391.0"]
    [com.cognitect.aws/s3               "697.2.391.0"]
    [com.cognitect.aws/sns              "697.2.391.0"]
    [com.cognitect.aws/sqs              "697.2.391.0"]

    [com.github.kyleburton/impresario         "1.0.13"
     :exclusions [org.clojure/tools.logging]]
    ])

