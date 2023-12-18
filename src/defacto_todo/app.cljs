(ns defacto-todo.app
  (:require
    [clojure.pprint :as pp]
    [defacto.core :as defacto]
    [defacto.resources.core :as res]
    [reagent.core :as r]
    [reagent.dom :as rdom]))

(defn app [store]
  [:div [:pre (with-out-str (pp/pprint @store))]])

(defn ^:private request-fn [spec params]
  )

(defn init! []
  (let [store (defacto/create (res/with-ctx request-fn) {} {:->sub r/atom})]
    (rdom/render [app store] (.getElementById js/document "root"))))
