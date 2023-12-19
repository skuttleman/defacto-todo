(ns defacto-todo.backend
  (:require
    [datascript.core :as d]))

(defmulti ^{:arglists '([{:keys [api] :as req}])} do-request
          "Request Handler"
          :api)

(defonce ^:private conn
  (d/create-conn {:todos/id {:db/unique :db.unique/identity}}))

(defmethod do-request :POST:todos
  [{:keys [todo]}]
  (d/transact! conn [(assoc todo
                            :todos/id (random-uuid)
                            :todos/created-at (js/Date.)
                            :todos/done? false)])
  nil)

(defmethod do-request :GET:todos
  [_]
  (->> (d/q '[:find (pull ?e [:todos/id
                              :todos/description
                              :todos/priority
                              :todos/done?
                              :todos/created-at
                              :todos/todone-at])
              :where [?e :todos/id _]]
            (d/db conn))
       (map first)))

(defmethod do-request :DELETE:todos:id [{:keys [todo-id]}]
  (d/transact! conn [{:todos/id    todo-id
                      :todos/done? true
                      :todos/todone-at (js/Date.)}]))
