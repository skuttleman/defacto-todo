(ns defacto-todo.app
  "A basic TODO example that uses defacto with reagent"
  (:require
    [clojure.core.async :as async]
    [defacto-todo.backend :as be]
    [defacto-todo.page :as page]
    [defacto-todo.store :as store]
    [defacto.core :as defacto]
    [defacto.resources.core :as res]
    [reagent.core :as r]
    [reagent.dom :as rdom]))

(defmethod defacto/event-reducer ::reloaded
  [_ [_ next-db]]
  next-db)

(defn ^:private request-fn [_ params]
  (async/go
    (try
      [::res/ok (be/do-request params)]
      (catch :default ex
        [::res/err (ex-data ex)]))))

(defn load!
  "Called by shadow-cljs when the dev env is reloaded"
  []
  (let [db @store/*store*]
    (rdom/render [page/root]
                 (.getElementById js/document "root")
                 (fn []
                   (defacto/emit! store/*store* [::reloaded db])))))

(defn init!
  "Called by shadow-cljs when the page is loaded"
  []
  (let [init-db {}]
    (set! store/*store* (-> {}
                            (res/with-ctx request-fn)
                            (defacto/create init-db {:->sub r/atom})))
    (rdom/render [page/root] (.getElementById js/document "root"))))
