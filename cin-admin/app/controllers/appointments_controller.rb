require 'base64'
require 'open-uri'
class AppointmentsController < ApplicationController
  acts_as_token_authentication_handler_for User
  skip_before_filter :verify_authenticity_token,
                    :if => Proc.new { |c| c.request.format == 'application/json' }

  #before_filter :authenticate_user!

  def index
    @client = Client.find params[:client_id]
    @appointments = Array.new
    @client.appointments.each do |appointment|
      if (appointment.beginning.to_date.month == Date.today.month)
        @appointments.push(appointment)
      end
    end
  end

  def show
    @client = Client.find params[:client_id]
    @appointment = @client.appointments.find params[:id]
    @locations = @appointment.locations
    if (@appointment.image.url)
      @data = Base64.strict_encode64(open("#{Rails.root.to_s}/public/#{@appointment.image.url}", "r").read)
    end
    @hash = Gmaps4rails.build_markers(@locations) do |location, marker|
      marker.lat location.latitude
      marker.lng location.longitude
    end
  end

  def edit
  end

  def create
     @client = Client.find params[:client_id]
     @appointment = @client.appointments.build(appointment_params)
     @appointment.hours = 0.0
     @locations = @appointment.locations

    if @appointment.save
    	@appointment
    else
      render :status => :unprocessable_entity,
             :json => { :success => false,
                        :info => @appointment.errors.full_messages,
                        :data => {} }
    end
  end

  def update
    @client = Client.find params[:client_id]
    @user = @client.user
    @appointment = @client.appointments.find params[:id]
    @locations = @appointment.locations
    #@appointment.hours = @appointment.image.created_at - @locations.first.created_at
    if (@appointment.locations.first)
      @appointment.clockIn = @locations.first.created_at
      @appointment.clockOut = DateTime.now
      #@user.update_hours(@appointment.hours)
      if params[:appointment][:image]
        data = StringIO.new(Base64.decode64(params[:appointment][:image][:data]))
        data.class.class_eval { attr_accessor :original_filename, :content_type }
        data.original_filename = params[:appointment][:image][:filename]
        data.content_type = params[:appointment][:image][:content_type]
        params[:appointment][:image] = data
      end
      @appointment.hours = Time.now - @locations.first.created_at
      @user.update_hours(@appointment.hours)
    end
    if @appointment.update(appointment_params)
    	@appointment
    else
      render :status => :unprocessable_entity,
             :json => { :success => false,
                        :info => @appointment.errors.full_messages,
                        :data => {} }
    end
  end
    
   def delete
    @client = current_user.clients.find(params[:client_id])
    @appointment = @client.appointments.find(params[:app_id])
    @appointment.destroy
  rescue ActiveRecord::RecordNotFound
    render :status => 404,
           :json => { :success => false,
                      :info => 'Not Found',
                      :data => {} }
  end

  private
    def appointment_params
      params.require(:appointment).permit(:note, :client_id, :image, :content_type, :filename, :original_filename, :beginning, :end)
    end
end
