(ns defacto-todo.app
  "A basic TODO example that uses defacto with reagent"
  (:require
    [clojure.core.async :as async]
    [defacto-todo.backend :as be]
    [defacto-todo.page :as page]
    [defacto.core :as defacto]
    [defacto.resources.core :as res]
    [reagent.core :as r]
    [reagent.dom :as rdom]))

(defn ^:private request-fn [_ params]
  (async/go
    (try
      [::res/ok (be/do-request params)]
      (catch :default ex
        [::res/err (ex-data ex)]))))

(defn init! []
  (let [init-db {}
        store (-> {}
                  (res/with-ctx request-fn)
                  (defacto/create init-db {:->sub r/atom}))]
    (rdom/render [page/root store] (.getElementById js/document "root"))))
