ActiveAdmin.register Appointment do
  actions :index, :show
  
  index do
    selectable_column
    column :id
    column :hours do |appointment|
      number_with_precision(appointment.hours/3600, precision: 3)
    end
    column :created_at
    column :client do |appointment|
      appointment.client.first + " " + appointment.client.last
    end
    actions
  end

  show do
  	panel "Appointments" do
      attributes_table_for appointment do
	  	row("ID") { appointment.id }
	  	row("Employee") { appointment.client.user.first + " " + appointment.client.user.last}
        row("Client") { appointment.client.first + " " + appointment.client.last }
        row("Date") { appointment.created_at }
        row("Duration") { number_with_precision(appointment.hours/3600, precision: 3) + " hours"}
        row("Note") { appointment.note}
        hash = Gmaps4rails.build_markers(appointment.locations) do |location, marker|
          marker.lat location.latitude
          marker.lng location.longitude
        end
        render 'gmaps4rails/gmaps4rails', :hash => hash
	  end
	end
  end

end