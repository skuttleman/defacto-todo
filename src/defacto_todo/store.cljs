(ns defacto-todo.store
  (:require
    [clojure.string :as string]
    [defacto.core :as defacto]
    [defacto.forms.core :as forms]
    [defacto.forms.plus :as forms+]
    [defacto.resources.core :as res]))

(def ^:private page-form [::forms+/valid [::todo#create]])

(defn load-page! [store]
  (defacto/emit! store [::forms/created page-form {:todos/priority :medium}])
  (defacto/dispatch! store [::res/submit! [::todo#fetch]]))

(defn subs [store]
  {:sub:todos   (defacto/subscribe store [::?:todos])
   :sub:todones (defacto/subscribe store [::?:todones])})

(defn ?:form [store]
  (defacto/subscribe store [::forms+/?:form+ page-form]))

(defn todid! [store todo-id]
  (defacto/dispatch! store [::res/submit! [::todo#delete todo-id]]))

(defn form-cleanup! [store]
  (defacto/emit! store [::forms+/destroyed page-form]))

(defn submit! [store]
  (fn [_]
    (defacto/dispatch! store [::forms+/submit! page-form])))

(defn errors [form+]
  (when (res/error? form+)
    (::forms/errors (res/payload form+))))

(defn with-form-attrs [store form+ path]
  (let [form-data (forms/data form+)]
    {:value     (get-in form-data path)
     :on-change (fn [value]
                  (defacto/emit! store [::forms/changed page-form path value]))}))

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