(ns sequencer.template
  (:require
    [hiccup.core :refer :all]
    [hiccup.page :refer :all]
    [sequencer.uiparts :refer :all]))

(def min-bootstrap
  (list
    (include-js "//ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js")
    (include-js "/bootstrap/js/bootstrap.min.js")
    (include-css "/bootstrap/css/bootstrap.min.css")
    (include-js "/js/main.js")
    (include-css "/css/main.css")))

(defn application
  [title is-login & content]
  (html5 {:lang "en"}
         [:head
          [:meta {:charset "utf-8"}]
          [:meta {:http-equiv "X-UA-Compatible" :content "IE=edge"}]
          [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}]
          [:link {:rel "icon" :href "/img/favicon.ico"}]
          [:title title]
          min-bootstrap]
         [:body
          [:nav.navbar.navbar-inverse.navbar-fixed-top
           [:div.container-fluid
            [:div.navbar-header
             [:a.navbar-brand {:href "/"} "Robo-Sequencer App"]]
            [:div#navbar.navbar-collapse.collapse
             [:ul.nav.navbar-nav.navbar-right
              (get-menu is-login)]]]]
          [:div.container-fluid content]]))

(defn login-page
  [error-message]
  (application "Robo-Sequencer login" false (login-part error-message)))

(defn index-page
  [tasks]
  (application "Robo-Sequencer main" true index-static (tasks-accordion tasks)))

(defn log-page
  [tasks]
  (application "Robo-Sequencer log" true (log-content tasks)))