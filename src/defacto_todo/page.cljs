(ns defacto-todo.page
  "The app's [[reagent.core]] components"
  (:require
    [defacto-todo.dom :as dom]
    [defacto-todo.store :as store]
    [reagent.core :as r]))

(defn ^:private with-form-attrs [form-data path]
  {:value     (get-in form-data path)
   :on-change (store/->on-change path)})

(defn ^:private field [for & body]
  (into [:fieldset.field.mb-4
         [:label.label {:for for} (name for)]]
        body))

(defn ^:private description-field [form-data errors]
  [:<>
   [:input#description.input (-> (with-form-attrs form-data [:todos/description])
                                 (update :on-change comp dom/target-value)
                                 (cond-> (:todos/description errors) (assoc :class ["is-danger"])))]
   (when-let [err (:todos/description errors)]
     [:p.help.is-danger err])])

(defn ^:private priority-field [form-data errors]
  [:<>
   [:select#priority.input (-> (with-form-attrs form-data [:todos/priority])
                               (update :value #(name (or % "")))
                               (update :on-change comp keyword dom/target-value)
                               (cond-> (:todos/priority errors) (assoc :class ["is-danger"])))
    [:option {:value "high"} "YESTERDAY!"]
    [:option {:value "medium"} "Soon-ish"]
    [:option {:value "low"} "whenever…"]]
   (when-let [err (:todos/priority errors)]
     [:p.help.is-danger err])])

(defn ^:private todo-form []
  (let [form-data (store/form-data)
        errors (store/form-errors)]
    [:form.form {:on-submit (comp store/submit! dom/prevent-default)}
     [:h2.subtitle "What needs to get todone?"]
     [field :description
      [description-field form-data errors]]
     [field :priority
      [priority-field form-data errors]]
     [:button.button.is-success {:type :submit} "Submit"]]))

(defn ^:private todos-list [todos]
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
                                        (store/todid! todo-id))}
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

(defn ^:private todos []
  (r/with-let [_ (store/load-page!)
               {:keys [sub:todos sub:todones]} (store/page-subscriptions)]
    [:div
     (when-let [todos (seq @sub:todos)]
       [todos-list todos])
     (when-let [todos (seq @sub:todones)]
       [todone-list todos])]
    (finally
      (store/form-cleanup!))))

(defn root
  "The mounted app."
  []
  [:div.container
   [:h1.title "Todo it, defacto™ style!"]
   [todo-form]
   [todos]])
