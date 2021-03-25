(ns front-battle-api.domain.user
  (:require [failjure.core :as f]
            [clojure.string :as str]))

(defn- validate-email [email]
  (if (and email (re-seq #"^([\w\.\-]+)@([\w\-]+)((\.(\w){2,3})+)$" email)) 
    email
    (f/fail [:email_is_invalid "Email is invalid"])))

(defn- validate-password [password]
  (if (and password (re-seq #"(?=.*\d)(?=.*[a-z])(?=.*[A-Z]).{8,}" password))
    password
    (f/fail [:passwor_in_invalid "Password is invalid. Should be at least 8 symbols: 1 digit, 1 lower and 1 upper"])))

(defn- validate-password-eq [password confirm-password]
  (if (= password confirm-password)
    password
    (f/fail [:passwords_dont_match "Passwords do not match"])))

(defn- validate-password-question [password-question]
  (if (str/blank? password-question)
    (f/fail [:password_question_is_invalid "Password question can't be empty"])
    password-question))

(defn create
  [email password confirm-password password-question]
  (f/attempt-all [email             (validate-email email)
                  password          (validate-password password)
                  _                 (validate-password-eq password confirm-password)
                  password-question (validate-password-question password-question)]
                 {:email email
                  :password password
                  :password_question password-question}))


(defn password-ok? [user password]
  (if (and (not (nil? user))
           (= (:password user) password))
    user
    (f/fail [:password_or_email_are_wrong "Password or email are wrong"])))


(defn- validate-password-question-eq [user-pq pq]
  (if (= user-pq pq)
    pq
    (f/fail [:password_questions_do_not_match "Password questions do not match"])))

(defn restore-password [user new-password new-confirm-password password-question]
  (f/attempt-all [_ (validate-password new-password)
                  _ (validate-password-eq new-password new-confirm-password)
                  _ (validate-password-question-eq (:password_question user) password-question)]
    {:password new-password}))