(ns defacto-todo.dom)

(defn target-value [e]
  (some-> e .-target .-value))

(defn prevent-default [e]
  (doto e .preventDefault))
