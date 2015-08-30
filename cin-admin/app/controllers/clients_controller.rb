require 'base64'
require 'open-uri'
class ClientsController < ApplicationController
  acts_as_token_authentication_handler_for User
  skip_before_filter :verify_authenticity_token,
                    :if => Proc.new { |c| c.request.format == 'application/json' }

  #before_filter :authenticate_user!

  def index
    @clients = current_user.clients
    @appointments = Array.new
    @clients.each do |client|
      client.appointments.each do |appointment|
        @appointments.push(appointment)
      end
    end
  end

  def create
    if params[:client][:image]
      data = StringIO.new(Base64.decode64(params[:client][:image][:data]))
      data.class.class_eval { attr_accessor :original_filename, :content_type }
      data.original_filename = params[:client][:image][:filename]
      data.content_type = params[:client][:image][:content_type]
      params[:client][:image] = data
    end
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

  def update
    @client = Client.find params[:client_id]
    @user = @client.user
    if params[:client][:image]
      data = StringIO.new(Base64.decode64(params[:client][:image][:data]))
      data.class.class_eval { attr_accessor :original_filename, :content_type }
      data.original_filename = params[:client][:image][:filename]
      data.content_type = params[:client][:image][:content_type]
      params[:client][:image] = data
    end
     if @client.update(client_params)
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
    if (@client.image.url)
      @data = Base64.strict_encode64(open("#{Rails.root.to_s}/public/#{@client.image.url}", "r").read)
    end
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
      params.require(:client).permit(:first, :last, :client_id, :image, :content_type, :filename, :original_filename, :address)
    end
end