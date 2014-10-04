(ns com.gfredericks.replog
  (:require [clojure.tools.nrepl.middleware :as mid])
  (:require [clojure.tools.nrepl.misc :refer [response-for]])
  (:require [clojure.tools.nrepl.transport :as transport]))

(def ^:dynamic *history*)

(defn wrap-history
  [handler]
  (let [history (atom [])]
    (fn [msg]
      (if (= "eval" (:op msg))
        (let [his @history]
          (swap! (:session msg) assoc #'*history* his)
          (-> msg
              (update-in [:transport]
                         (fn [t]
                           (reify transport/Transport
                             (recv [this] (transport/recv t))
                             (recv [this timeout] (transport/recv t timeout))
                             (send [this msg]
                               (when-let [[_ v] (find msg :value)]
                                 (let [new-count (count (swap! history conj v))]
                                   (transport/send t
                                                   (response-for msg
                                                                 {:out (format "&%d:\n" (dec new-count))}))))
                               (transport/send t msg )))))
              (update-in [:code]
                         (fn [s]
                           (format "(let [[%s] com.gfredericks.replog/*history*]\n%s\n)"
                                   (->> (range (count his))
                                        (map #(format "&%d" %))
                                        (clojure.string/join " "))
                                   s)))
              (handler)))
        (handler msg)))))

(mid/set-descriptor! #'wrap-history
                     {:handles {}
                      :expects #{"eval"}
                      :requires #{#'clojure.tools.nrepl.middleware.pr-values/pr-values
                                  #'clojure.tools.nrepl.middleware.session/session}})
