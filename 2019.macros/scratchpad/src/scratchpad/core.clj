(ns scratchpad.core
  (:require
   [nrepl.server          :refer [start-server start-server]]
   [cider.nrepl           :refer [cider-nrepl-handler]]
   [clojure.tools.logging :as log]
   [clojure.spec.alpha    :as s]))

(defonce nrepl-server (atom nil))
(defonce config (atom {:nrepl {:port 4001}}))

(defn -main [& args]
  (reset! nrepl-server (start-server
                        :port (-> @config :nrepl :port)
                        :handler cider-nrepl-handler))
  (log/infof "nrepl is running %s" @config))
