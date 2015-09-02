(ns sequencer.db
  (:require
    [monger.core :as mg]
    [monger.collection :as mc]
    [monger.operators :refer :all]
    [monger.query :as q]
    [sequencer.utils :as u]))

(defonce db
         (let [uri "mongodb://127.0.0.1/sequencer_db"
               {:keys [db]} (mg/connect-via-uri uri)]
           db))

(defmacro with-login-db
  [command & body]
  (conj body "sequencer-login" 'db command))

(defmacro with-task-db
  [command & body]
  (conj body "sequencer-task" 'db command))

(defn get-by-username
  [username]
   (u/transform (with-login-db mc/find-one-as-map {:username username})))

(defn create-user
  [user]
  (with-login-db mc/insert user ))

(defn put-task
  [task]
  (with-task-db mc/insert-and-return task))

(defn find-user-tasks
  [username]
  (with-task-db q/with-collection
                (q/find {:username username})
                (q/fields [:_id :name :result :comment])
                (q/limit 50)))

(defn find-all-tasks-order-by-time
  []
  (with-task-db q/with-collection
                   (q/find {})
                   (q/fields [:username :name :_id])
                     (q/sort (array-map :_id -1))
                     (q/limit 100)))
