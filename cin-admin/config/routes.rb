Rails.application.routes.draw do
  devise_for :admin_users, ActiveAdmin::Devise.config
  ActiveAdmin.routes(self)

  devise_for :users

  get 'users' =>'cin/users#index' 

  devise_scope :user do
    post 'registrations' => 'registrations#create', :as => 'register'
    post 'sessions' => 'sessions#create', :as => 'login'
    delete 'sessions' => 'sessions#destroy', :as => 'logout'
    get 'sessions' => 'sessions#show'
  end
  
  get 'clients' => 'clients#index', :as => 'clients'
  post 'clients' => 'clients#create'
  delete 'clients/:id/destroy' => 'clients#destroy',  :as => 'destroy_client'
  get 'clients/:id/show' => 'clients#show', :as => 'show_client'
  post 'clients/:client_id/appointments/create' => 'appointments#create'
  get 'clients/:client_id/appointments/:id/show' => 'appointments#show'
  get 'clients/:client_id/appointments' => 'appointments#index', :as => 'appointments'
  post 'clients/:client_id/appointments/:app_id/locations/create' => 'locations#create'
  get 'clients/:client_id/appointments/:app_id/locations' => 'locations#index', :as => 'locations'
  post 'clients/:client_id/appointments/:id/update' => 'appointments#update'
  delete 'clients/:client_id/appointments/:app_id/delete' => 'appointments#delete'

  # The priority is based upon order of creation: first created -> highest priority.
  # See how all your routes lay out with "rake routes".

  # You can have the root of your site routed with "root"
  # root 'welcome#index'

  # Example of regular route:
  #   get 'products/:id' => 'catalog#view'

  # Example of named route that can be invoked with purchase_url(id: product.id)
  #   get 'products/:id/purchase' => 'catalog#purchase', as: :purchase

  # Example resource route (maps HTTP verbs to controller actions automatically):
  #   resources :products

  # Example resource route with options:
  #   resources :products do
  #     member do
  #       get 'short'
  #       post 'toggle'
  #     end
  #
  #     collection do
  #       get 'sold'
  #     end
  #   end

  # Example resource route with sub-resources:
  #   resources :products do
  #     resources :comments, :sales
  #     resource :seller
  #   end

  # Example resource route with more complex sub-resources:
  #   resources :products do
  #     resources :comments
  #     resources :sales do
  #       get 'recent', on: :collection
  #     end
  #   end

  # Example resource route with concerns:
  #   concern :toggleable do
  #     post 'toggle'
  #   end
  #   resources :posts, concerns: :toggleable
  #   resources :photos, concerns: :toggleable

  # Example resource route within a namespace:
  #   namespace :admin do
  #     # Directs /admin/products/* to Admin::ProductsController
  #     # (app/controllers/admin/products_controller.rb)
  #     resources :products
  #   end
end
