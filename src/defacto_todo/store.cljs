(ns defacto-todo.store
  "The `defacto` store interactions"
  (:require
    [clojure.string :as string]
    [defacto.core :as defacto]
    [defacto.forms.core :as forms]
    [defacto.forms.plus :as forms+]
    [defacto.resources.core :as res]))

(def ^:dynamic *store*)

(def ^:private ^:const page-form [::forms+/valid [::todo#create]])

(defn load-page!
  "Loads page data on initial render"
  []
  (defacto/emit! *store* [::forms/created page-form {:todos/priority :medium}])
  (defacto/dispatch! *store* [::res/submit! [::todo#fetch]]))

(defn page-subscriptions
  "The subscriptions relevant to the todos page"
  []
  {:sub:todos   (defacto/subscribe *store* [::?:todos])
   :sub:todones (defacto/subscribe *store* [::?:todones])})

(defn form-data
  "The page's form"
  []
  (forms/data @(defacto/subscribe *store* [::forms+/?:form+ page-form])))

(defn form-errors
  "The page's form"
  []
  (let [res @(defacto/subscribe *store* [::forms+/?:form+ page-form])]
    (when (res/error? res)
      (res/payload res))))

(defn ->on-change
  "Makes an \"on-change\" handler which updates a path in the form"
  [path]
  (fn [value]
    (defacto/dispatch! *store* [::forms/changed page-form path value])))

(defn todid!
  "Marks a \"todo\" as done"
  [todo-id]
  (defacto/dispatch! *store* [::res/submit! [::todo#delete todo-id]]))

(defn submit!
  "submission input handler (i.e. on-click) for submitting the form"
  [_]
  (defacto/dispatch! *store* [::forms+/submit! page-form]))

(defn form-cleanup!
  "cleans up form in db"
  []
  (defacto/emit! *store* [::forms+/destroyed page-form]))

(defn errors
  "The page's form errors"
  [form+]
  (when (res/error? form+)
    (::forms/errors (res/payload form+))))

(defn ^:private all-todos [db]
  (res/payload (defacto/query-responder db [::res/?:resource [::todo#fetch]])))

(defmethod res/->request-spec ::todo#create
  [_ {::forms/keys [data]}]
  {:params      {:api  :POST:todos
                 :todo data}
   :ok-commands [[::res/submit! [::todo#fetch]]]})

(defmethod res/->request-spec ::todo#fetch
  [_ _]
  {:params {:api :GET:todos}})

(defmethod res/->request-spec ::todo#delete
  [[_ todo-id] _]
  {:params      {:api     :DELETE:todos:id
                 :todo-id todo-id}
   :ok-commands [[::res/submit! [::todo#fetch]]]})

(defmethod forms+/validate ::todo#create
  [_ params]
  (when (string/blank? (:todos/description params))
    {:todos/description "you gotta have a description!"}))

(defmethod defacto/query-responder ::?:todos
  [db _]
  (->> (all-todos db)
       (remove :todos/done?)
       (sort-by (juxt (comp {:low 0 :medium 1 :high 2}
                            :todos/priority)
                      :todos/created-at)
                >)))

(defmethod defacto/query-responder ::?:todones
  [db _]
  (->> (all-todos db)
       (filter :todos/done?)
       (sort-by :todos/todone-at >)))
