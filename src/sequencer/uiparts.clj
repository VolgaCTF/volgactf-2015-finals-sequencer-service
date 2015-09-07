(ns sequencer.uiparts
  (:require
    [clj-time.coerce :as c]
    [clj-time.format :as f])
  (:import (org.bson.types ObjectId)))

(defn collapsed-panel
  [id content]
  [:div.collapse {:id id}
   [:div.well content]])

(defn collapse-button
  [id content]
  [:button.btn.btn-primary {:type "button" :data-toggle "collapse"
                            :data-target (str "#" id) :aria-exapanded "false"
                            :aria-controls id} content])

(defn log-panel
  [{^ObjectId id :_id name :name username :username}]
  [:div.well
   "User "
   (hiccup.core/h username)
   " set a task - "
   (hiccup.core/h name)
   " at "
   (f/unparse (f/formatter-local "hh:mm") (c/from-long (.getTime id)))])

(defn task-panel
  [{^ObjectId id :_id name :name result :result com :comment}]
  (list
    [:div.panel.panel-default
     [:div.panel-heading {:role "tab"}
      [:h4.panel-title
       [:a {:href (str "#" id) :aria-controls (str id) :id (str "heading" id)
            :data-toggle "collapse" :role "button" :data-parrent "#tasks"}
        (hiccup.core/h name)]]]
     [:div.panel-collapse.collapse {:role "tabpanel" :id (str id) :aria-labelledby (str "heading" id)}
      [:div.panel-body
       (list
         "Result:"
         (hiccup.core/h result)
         [:br]
         "Comment:"
         (hiccup.core/h com)
         [:br])]]]))

(defn tasks-accordion
  [tasks]
  (list
    [:div.row
     [:div.col-lg-12
      [:h2 "Completed tasks"]
      [:div#tasks.panel-group {:role "tablist" :aria-multiselectable "true"}
       (for [t tasks] (task-panel t))]]]))

(defn example-layout
  [name func data comm]
  (list [:p
        (str "name;" name)
        [:br]
        (str "func;" func)
        [:br]
        (str "data;" data)
        [:br]
        (str "comment;" comm)]))

(def help-content
  (list
    (collapse-button "example1" "Count nucleotides")
    (collapse-button "example2" "DNA to RNA")
    (collapse-button "example3" "Reverse complement DNA string")
    (collapse-button "example4" "DNA Hamming distance")
    (collapsed-panel "example1" (example-layout "example1" "count-nucleo"
                                                "{:s1 ACAAGATGCCATTGTCCCCCGGCCTCCTG}" "whatever"))
    (collapsed-panel "example2" (example-layout "example2" "dna-to-rna"
                                                "{:s1 ACAAGATGCCATTGTCCCCCGGCCTCCTG}" "whatever"))
    (collapsed-panel "example3" (example-layout "example3" "reverse-complement-dna"
                                                "{:s1 ACAAGATGCCATTGTCCCCCGGCCTCCTG}" "whatever"))
    (collapsed-panel "example4" (example-layout "example4" "hamming"
                                                "{:s1 AGAACAT :s2 ACAAGAT}" "whatever"))))

(def task-submit-form
  [:form {:action "/" :method "POST" :enctype "multipart/form-data" }
   [:div.form-group
    [:label {:for "task-file"} "Send a task file for calculation"]
    [:input#task-file {:type "file" :name "task"}]
    [:p.help-block "Warning: A file content must be in predefined format!"]]
   [:button.btn.btn-default {:type "submit"} "Send task"]])

(defn get-menu
  [is-login]
  (if is-login
    (list
      [:li
       [:a {:href "/log"} "All submited tasks log"]]
      [:li
       [:a {:href "/logout"} "Logout"]])
    (list)))


(def index-static
  (list
    [:div.panel
     [:h1 "Robo-Sequencer App"]
     [:p "Robo sequence allow you to solve some bioinformatics task.
   To use it you need to post a file called task.rbs with your task data structure."
      [:br]
      "Here are some examples:"
      [:br]
      help-content]]
    [:div.row
     [:div.col-md-6.col-md-offset-3 task-submit-form]])
  )

(defn login-part
  [message]
  [:div.row
   (list
     (if message
       [:div.col-md-6.col-md-offset-3
        [:div.alert.alert-danger {:role "alert"}
         message]])
   [:div.col-md-6.col-md-offset-3
    [:div.panel.panel-login
     [:div.panel-heading
      [:div.row
       [:div.col-xs-6
        [:a#login-form-link.active {:href "#"} "Login"]]
       [:div.col-xs-6
        [:a#register-form-link {:href "#"} "Register"]]]
      [:hr]]
     [:div.panel-body
      [:div.row
       [:div.col-lg-12
        [:form#login-form {:action "/do-login" :method "post" :role "form" :style "display:block;"}
         [:div.form-group
          [:input#username.form-control {:type "text" :name "username" :tabindex "1" :placeholder "Username" }]]
         [:div.form-group
          [:input#password.form-control {:type "password" :name "password" :tabindex "2" :placeholder "Password" }]]
         [:div.form-group
          [:div.row
           [:div.col-sm-6.col-sm-offset-3
            [:input#login-submit.form-control.btn.btn-login
             {:type "Submit" :name "login-submit" :tabindex "3" :value "Login"}]]]]]
        [:form#register-form {:action "/do-register" :method "post" :role "form" :style "display:none;"}
         [:div.form-group
          [:input#username.form-control {:type "text" :name "username" :tabindex "1" :placeholder "Username" }]]
         [:div.form-group
          [:input#password.form-control {:type "password" :name "password" :tabindex "2" :placeholder "Password" }]]
         [:div.form-group
          [:div.row
           [:div.col-sm-6.col-sm-offset-3
            [:input#register-submit.form-control.btn.btn-login
             {:type "Submit" :name "register-submit" :tabindex "3" :value "Register"}]]]]]]]]]])])

(defn log-content
  [tasks]
  [:div.row
   [:div.col-lg-12
    (for [t tasks] (log-panel t))]])
