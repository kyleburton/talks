(ns scratchpad.workflow
  (:require
   [impresario.dsl :as dsl :refer [on-enter! defpredicate on-transition! on-transition-any! defmachine *context* *current-state* *next-state* state]]
   [impresario.core :as wf :refer [register-workflow]]))


(on-enter! :start
           (assoc *context* :transitions []))


(defpredicate :start :closed
  true)

(defpredicate :open :closed
  (get *context* :close-door))

(defpredicate :closed :locked
  (get *context* :lock-door))

(on-enter! :locked
           (assoc *context* :locked? true))

(defpredicate :closed :open
  (and
   (not (get *context* :locked?))
   (not (get *context* :lock-door))))

(defpredicate :locked :closed
  (get *context* :locked?))

(on-transition! :locked :closed
                (dissoc *context* :locked?))

(on-transition-any!
 (update-in
  (dissoc *context* :close-door :lock-door :unlock-door)
  [:transitions]
  conj
  [*current-state* :=> *next-state*]))

(defmachine :door-workflow
  (state :start {:start true :transitions [:closed]})
  (state :closed {:transitions [:locked
                                :open]})
  (state :locked {:transitions [:closed]})
  (state :open   {:transitions [:closed]}))

(register-workflow :door-workflow door-workflow)



(comment
  (def door1-ctx (wf/initialize-workflow :door-workflow {:on-enter 0 :on-exit 0}))

  (def step2 (wf/transition! :door-workflow :start door1-ctx))

  (let [[next-state new-ctx] (wf/transition! :door-workflow :start door1-ctx)]
    (println (format "next-state=%s; new-ctx=%s" next-state new-ctx)))

)
