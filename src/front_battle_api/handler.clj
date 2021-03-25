(ns front-battle-api.handler
  (:require [failjure.core :as f]
            [schema.core :as s]
            [compojure.api.sweet :refer :all]
            [ring.middleware.defaults :refer :all]
            [front-battle-api.services.auth :as auth-service]
            [ring.util.response :refer [response]]
            [ragtime.repl :as repl]
            [ragtime.jdbc :as jdbc]
            [config.core :refer [load-env env]]))


(defn server-response [result]
  (if (f/failed? result)
    (let [[code message] (f/message result)]
      {:code code
       :data nil
       :message message})
    {:code nil
     :data result
     :message nil}))

(defn handle [f req]
  (-> (f req)
      (server-response)
      (response)))

(defn sign-up [{:keys [email password confirm_password password_question]}]
  (auth-service/sign-up email password confirm_password password_question))

(defn get-user [id]
  (auth-service/get-user-by-id id))

(defn sign-in [{:keys [email password]}]
  (auth-service/sign-in email password))

(defn restore-password [{:keys [email new_password new_confirm_password password_question]}]
  (auth-service/restore-password email new_password new_confirm_password password_question))

(s/defschema SignUp
  {:email             s/Str
   :password          s/Str
   :confirm_password  s/Str
   :password_question s/Str})

(s/defschema SignIn
  {:email    s/Str
   :password s/Str})

(s/defschema RestorePassword
  {:email                s/Str
   :new_password         s/Str
   :new_confirm_password s/Str
   :password_question    s/Str})


(def config
  {:datastore  (jdbc/sql-database (:db env))
   :migrations (jdbc/load-resources "migrations")})

(def app
  (do 
    (load-env)
    (repl/migrate config)
    (api
     {:swagger {:ui       "/"
                :spec     "/swagger.json"
                :basePath "/battle"
                :data     {:info {:title "Front battle"}}}}

     (context "/auth/sign-up" []
       (resource
        {:tags ["auth"]
         :post {:summary    "Sign up"
                :parameters {:body-params SignUp}
                :handler    (fn [{body :body-params}]
                              (handle sign-up body))}}))

     (context "/auth/sign-in" []
       (resource
        {:tags ["auth"]
         :post {:summary    "Sign in"
                :parameters {:body-params SignIn}
                :handler    (fn [{body :body-params}]
                              (handle sign-in body))}}))
     (context "/auth/restore-password" []
       (resource
        {:tags ["auth"]
         :post {:summary    "Sign in"
                :parameters {:body-params RestorePassword}
                :handler    (fn [{body :body-params}]
                              (handle restore-password body))}}))
     (context "/users/:id" []
       :path-params [id :- s/Int]
       (resource
        {:tags ["users"]
         :get  {:summary "get user"
                :handler (fn [_]
                           (handle get-user id))}})))))