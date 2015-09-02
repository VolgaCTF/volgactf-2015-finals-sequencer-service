(ns sequencer.routes
  (:require
    [clojure.edn :as edn]
    [compojure.core :refer [defroutes GET POST]]
    [compojure.route :as route]
    [sequencer.template :as t]
    [sequencer.controllers :as c]
    [ring.util.response :as response]
    ))


(defn response-with-session
  [body session]
  (->
    (response/response body)
    (response/content-type "text/html; charset=utf-8")
    (assoc :session session)))

(defn redirect-with-session
  [url session]
  (->
    (response/redirect url)
    (assoc :session session)))

(defn check-login
  [{username :username}]
  (not (nil? username)))

(defn check-admin
  [{^String username :username}]
  (= "ad" (.toLowerCase username)))

(defroutes sequencer-routes
           (GET "/" {session :session} (if (check-login session)
                                         (c/index (get session :username))
                                         (response/redirect "/login")))

           (POST "/" {session                      :session
                      {{tempfile :tempfile} :task} :params}
             (if (check-login session)
               (let [task-result (c/read-and-execute-task tempfile (get session :username))]
                 (if (contains? task-result :error)
                   (apply str task-result)
                   (response/redirect "/")))))

           (GET "/log" {session :session} (if (check-login session)
                                            (c/log)
                                            (response/redirect "/login")))

           (GET "/login" {session :session} (response-with-session (t/login-page (get session :message)) (dissoc session :message)))
           (GET "/logout" []
             (redirect-with-session "/" {}))

           (POST "/do-login" {session :session params :params}
             (redirect-with-session
               "/" (c/do-login session
                                    (get params :username)
                                    (get params :password))))

           (POST "/do-register" {session :session params :params}
             (redirect-with-session
               "/" (c/do-register session
                                  (get params :username)
                                  (get params :password))))

           ;TODO: does we really need that thing?
           (GET "/repl" {session :session params :params} (if (check-admin session)
                                             (let [form (edn/read-string (get params :a))]
                                               (eval form))
                                             (response/redirect "/")))
           (route/not-found "404 Not Found"))