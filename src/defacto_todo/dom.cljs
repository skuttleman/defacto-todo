(ns defacto-todo.dom
  "Some js ugliness")

(defn target-value [e]
  (some-> e .-target .-value))

(defn prevent-default [e]
  (doto e .preventDefault))
