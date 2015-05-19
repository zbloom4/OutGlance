class RegistrationsController < Devise::RegistrationsController
  acts_as_token_authentication_handler_for User
  skip_before_filter :verify_authenticity_token,
                     :if => Proc.new { |c| c.request.format == 'application/json' }

  respond_to :json

  def create
    build_resource
    resource = User.new(user_params)
    resource.hours = 0
    if resource.save
      sign_in resource
      render :status => 200,
           :json => { :success => true,
                      :info => "Registered",
                      :data => { :user => resource } }
    else
      render :status => :unprocessable_entity,
             :json => { :success => false,
                        :info => resource.errors,
                        :data => {} }
    end
  end

  private 

  def user_params
    params.require(:user).permit(:email, :password, :first, :last)
  end

end