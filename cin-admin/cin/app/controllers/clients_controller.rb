class ClientsController < ApplicationController
  acts_as_token_authentication_handler_for User
  skip_before_filter :verify_authenticity_token,
                    :if => Proc.new { |c| c.request.format == 'application/json' }

  #before_filter :authenticate_user!

  def index
    @clients = current_user.clients
  end

  def create
    @client = current_user.clients.build(client_params)
    @client.user = current_user

    if @client.save
      @client
    else
      render :status => :unprocessable_entity,
             :json => { :success => false,
                        :info => @client.errors.full_messages,
                        :data => {} }
    end
  end

  def show
    @client = Client.find(params[:id])
    @appointments = @client.appointments

    rescue ActiveRecord::RecordNotFound
    render :status => 404,
           :json => { :success => false,
                      :info => 'Not Found',
                      :data => {} }
  end

  def destroy
    @client = current_user.clients.find(params[:id])
    @client.destroy
  rescue ActiveRecord::RecordNotFound
    render :status => 404,
           :json => { :success => false,
                      :info => 'Not Found',
                      :data => {} }
  end

  private
    def client_params
      params.require(:client).permit(:first, :last)
    end
end