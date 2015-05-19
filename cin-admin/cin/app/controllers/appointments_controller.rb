class AppointmentsController < ApplicationController
  acts_as_token_authentication_handler_for User
  skip_before_filter :verify_authenticity_token,
                    :if => Proc.new { |c| c.request.format == 'application/json' }

  #before_filter :authenticate_user!

  def index
    @client = Client.find params[:client_id]
    @appointments = @client.appointments
  end

  def show
    @client = Client.find params[:client_id]
    @appointment = @client.appointments.find params[:id]
    @locations = @appointment.locations
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
    @appointment.hours = @locations.last.created_at - @locations.first.created_at
    @user.update_hours(@appointment.hours)
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
      params.require(:appointment).permit(:note, :client_id)
    end
end
