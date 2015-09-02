(ns sequencer.controllers
  (:require
    [sequencer.db :as db]
    [sequencer.utils :refer [assuming]]
    [sequencer.taskparser :as parser]
    [sequencer.template :as t]
    [crypto.password.bcrypt :as password]))

(defn read-and-execute-task
  [file username]
  (let [res (try (->
                   (slurp file)
                   (parser/parse-strings)
                   (parser/execute-task))
                 (catch Exception e {:error (.getMessage e)}))]
    (if (not (contains? res :error))
      (->
        (assoc res :username username)
        (db/put-task))
      res)))

(defn index
  [username]
  (t/index-page (db/find-user-tasks username)))

(defn log
  []
  (t/log-page (db/find-all-tasks-order-by-time)))

(defn do-login
  [session user pwd]
  (let [^String user (.toLowerCase ^String user)
        db-user (db/get-by-username user)]
    (if (and (not (empty? db-user) )
             (password/check pwd (get db-user :password) ))
      (assoc session :username (get db-user :username))
      (assoc session :message "Wrong username or password"))))

(defn do-register
  [session user pwd]
  (let [^String lower-user (.toLowerCase ^String user)]
    (assuming [(empty? (db/get-by-username lower-user))
               "User already exists",
               (< 3 (.length ^String lower-user) 14)
               "Username length must be between 3-14 symbols",
               (= (clojure.string/trim lower-user)
                  (clojure.string/join (re-seq #"[A-Za-z0-9_]" (clojure.string/trim lower-user))))
               "Username should be alphanumeric"]
              (do
                (db/create-user {:username (clojure.string/trim lower-user)
                                 :password (password/encrypt pwd)})
                (assoc session :username lower-user))
              (assoc session :message why))))

