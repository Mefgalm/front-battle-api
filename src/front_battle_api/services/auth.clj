(ns front-battle-api.services.auth
  (:require [front-battle-api.db.user :as user-db]
            [front-battle-api.domain.user :as user-dom]
            [failjure.core :as f]
            [config.core :refer [env]]))

(defn uuid [] (str (java.util.UUID/randomUUID)))

(defn sign-up
  [email password confirm-password password-question]
  (f/attempt-all [_ (user-db/check-email (:db env) email)
                  result (user-dom/create email password confirm-password password-question)]
    (user-db/create-user! (:db env) result)))

(defn sign-in [email password]
  (f/attempt-all [user (user-db/get-by-email (:db env) email)
                  _ (user-dom/password-ok? user password)]
    (uuid)))

(defn restore-password [email new-password new-confirm-password password-question]
  (f/attempt-all [user (user-db/safe-by-email (:db env) email)
                  user-params (user-dom/restore-password user new-password new-confirm-password password-question)]
    (user-db/update-user! (:db env) (:id user) user-params)))

(defn get-user-by-id [id]
  (user-db/safe-by-id (:db env) id))