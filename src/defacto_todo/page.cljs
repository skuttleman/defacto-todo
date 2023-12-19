(ns defacto-todo.page
  (:require
    [defacto-todo.dom :as dom]
    [defacto-todo.store :as store]
    [reagent.core :as r]))

(defn ^:private field [for & body]
  (into [:fieldset.field.mb-4
         [:label.label {:for for} (name for)]]
        body))

(defn ^:private description-field [store form+ errors]
  [:<>
   [:input#description.input (-> (store/with-form-attrs store form+ [:todos/description])
                                 (update :on-change comp dom/target-value)
                                 (cond-> (:todos/description errors) (assoc :class ["is-danger"])))]
   (when-let [err (:todos/description errors)]
     [:p.help.is-danger err])])

(defn ^:private priority-field [store form+ errors]
  [:<>
   [:select#priority.input (-> (store/with-form-attrs store form+ [:todos/priority])
                               (update :value #(name (or % "")))
                               (update :on-change comp keyword dom/target-value)
                               (cond-> (:todos/priority errors) (assoc :class ["is-danger"])))
    [:option {:value "high"} "YESTERDAY!"]
    [:option {:value "medium"} "Soon-ish"]
    [:option {:value "low"} "whenever…"]]
   (when-let [err (:todos/priority errors)]
     [:p.help.is-danger err])])

(defn ^:private todo-form [store]
  (r/with-let [sub (store/?:form+ store)]
    (let [form+ @sub
          errors (store/errors form+)]
      [:form.form {:on-submit (comp (store/submit! store) dom/prevent-default)}
       [:h2.subtitle "What needs to get todone?"]
       [field :description
        [description-field store form+ errors]]
       [field :priority
        [priority-field store form+ errors]]
       [:button.button.is-success {:type :submit} "Submit"]])
    (finally
      (store/form-cleanup! store))))

(defn ^:private todos-list [store todos]
  [:ul
   (for [{todo-id :todos/id :todos/keys [description priority]} todos]
     ^{:key todo-id}
     [:li.mt-2.is-flex.is-align-items-center
      [:pre.px-2.py-2 {:style {:background-color (case priority
                                                   :high :orange
                                                   :medium :lightgreen
                                                   nil)}}
       (str priority)]
      [:span.ml-4 description]
      [:button.button.ml-4 {:on-click (fn [_]
                                        (store/todid! store todo-id))}
       "To" [:em "done!"]]])])

(defn ^:private todone-list [todos]
  [:div.mt-4
   [:p [:strong "You've been busy!"]]
   [:ul
    (for [{todo-id :todos/id :as todo} todos]
      ^{:key todo-id}
      [:li.mt-2.is-flex.is-align-items-center
       [:pre.px-2.py-2 (str :todone)]
       [:span.ml-4 (:todos/description todo)]])]])

(defn ^:private todos [store]
  (r/with-let [_ (store/load-page! store)
               {:keys [sub:todos sub:todones]} (store/subs store)]
    [:div
     (when-let [todos (seq @sub:todos)]
       [todos-list store todos])
     (when-let [todos (seq @sub:todones)]
       [todone-list todos])]))

(defn root [store]
  [:div.container
   [:h1.title "Todo it, defacto™ style!"]
   [todo-form store]
   [todos store]])
