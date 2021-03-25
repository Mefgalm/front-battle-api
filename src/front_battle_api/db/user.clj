(ns front-battle-api.db.user
  (:require [clojure.java.jdbc :as j]
            [honeysql.core :as sql]
            [failjure.core :as f]
            [honeysql.helpers :as helpers]))

(defn safe-get [entity]
  (if (nil? entity)
    (f/fail [:entity_not_found "Entity not found"])
    entity))

(defn get-by-email [config email]
  (let [sql {:select [:*]
             :from   [[:users :u]]
             :where  [:= :u.email email]}]
    (first (j/query config (sql/format sql)))))

(defn get-by-id [config id]
  (let [sql {:select [:*]
             :from   [[:users :u]]
             :where  [:= :u.id id]}]
    (first (j/query config (sql/format sql)))))

(defn safe-by-email [config email]
  (safe-get (get-by-email config email)))

(defn safe-by-id [config id]
  (f/attempt-all [user (safe-get (get-by-id config id))]
    (dissoc user :password :password_question)))

(defn check-email [config email]
  (if (nil? (get-by-email config email))
    email
    (f/fail [:email_alredy_exists "Email already exists"])))

(defn create-user! [config user-params]
  (f/try* (j/insert! config :users user-params)))

(defn update-user! [config id user-params]
 (f/attempt-all [_ (f/try* (j/update! config :users user-params ["id = ?" id]))
                 user (safe-by-id config id)]
   user))


