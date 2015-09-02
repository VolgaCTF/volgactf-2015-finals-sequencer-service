(ns sequencer.utils)

(defn map-keys
  "Accepts a function and a map, returns the map where a function applyed to keys"
  [mp fn]
  (zipmap (map fn (keys mp)) (vals mp)))

(defn transform
  [dbo]
  (map-keys dbo keyword))

(defmacro assuming
  "Guard body with a series of tests. Each clause is a test-expression
  followed by a failure value. Tests will be performed in order; if
  each test succeeds, then body is evaluated. Otherwise, fail-expr is
  evaluated with the symbol 'why bound to the failure value associated
  with the failing test."
  [[& clauses] body & [fail-expr]]
  `(if-let [[~'why]
            (cond
              ~@(mapcat (fn [[test fail-value]]
                          [`(not ~test) [fail-value]])
                        (partition 2 clauses)))]
     ~fail-expr
     ~body))

(defn call [^String nm & args]
  (when-let [fun (ns-resolve *ns* (symbol nm))]
    (apply fun args)))

(defn in?
  "true if seq contains elm"
  [seq elm]
  (some #(= elm %) seq))

(defn transform-values-to-string
  "Transform a map of anything to map of strings"
  [m]
  (into {} (map #(assoc m %1 (apply str [(get m %1)])) (keys m))))