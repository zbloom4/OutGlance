class LocationsController < ApplicationController
  acts_as_token_authentication_handler_for User
  skip_before_filter :verify_authenticity_token,
                    :if => Proc.new { |c| c.request.format == 'application/json' }
  
  #before_filter :authenticate_user!

  def index
    @client = Client.find params[:client_id]
    @appointment = @client.appointments.find params[:app_id]
    @locations = @appointment.locations
  end

  def create
    @client = Client.find params[:client_id]
    @appointment = @client.appointments.find params[:app_id]
    @location = @appointment.locations.build(location_params)

    
    if @location.save
      @location
    else
      render :status => :unprocessable_entity,
             :json => { :success => false,
                        :info => @location.errors.full_messages,
                        :data => {} }
    end
  end

  private
    def location_params
      params.require(:location).permit(:latitude, :longitude, :app_id, :created_at)
    end
end