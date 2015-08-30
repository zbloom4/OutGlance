ActiveAdmin.register Appointment do
  permit_params :client, :beginning, :end, :hours, :client_id, :user_id, :image

  sidebar "CIN", only: [:index] do
    ul do
      li link_to "Users",  cin_users_path()
      li link_to "Clients", cin_clients_path()
      li link_to "Appointments", cin_appointments_path()
    end
  end
  
  index do
    selectable_column
    column :beginning do |appointment|
      if (appointment.beginning)
        appointment.beginning.strftime("%b %d, %Y - %r")
      end
    end  
    column :end do |appointment|
      if (appointment.end)
        appointment.end.strftime("%b %d, %Y - %r")
      end
    end  
    column :clockIn do |appointment|
      if (appointment.clockIn)
        appointment.clockIn.strftime("%b %d, %Y - %r")
      end
    end  
    column :clockOut do |appointment|
      if (appointment.clockOut)
        appointment.clockOut.strftime("%b %d, %Y - %r")
      end  
    end 
    column :hours do |appointment|
      number_with_precision(appointment.hours/3600, precision: 3)
    end 
    column :client do |appointment|
      appointment.client.first + " " + appointment.client.last
    end
    column :employee do |appointment|
      appointment.client.user.first + " " + appointment.client.user.last
    end
    column :note do |appointment|
      if (appointment.note)
        appointment.note.encode('utf-8', 'iso-8859-1')
      end
    end
    actions
  end

  filter :client, label: 'Client', :as => :select, :collection => Client.all.map{|c| ["#{c.first} #{c.last}", c.id]}
  filter :client_user_first, label: 'Employee', :as => :select, :collection => User.all.map{|u| ["#{u.first} #{u.last}", u.first]}
  filter :clockIn_null, label: 'Completed', :as => :select, :collection => [["Yes", false], ["No", true]]
  filter :beginning, label: 'Date'

  show do
  	panel "Appointments" do
      attributes_table_for appointment do
  	  	row("Employee") { appointment.client.user.first + " " + appointment.client.user.last}
        row("Client") { appointment.client.first + " " + appointment.client.last }
        row("Beginning") { if (appointment.beginning)
          appointment.beginning.strftime("%b %d, %Y - %r") 
        end}
        row("End") { if (appointment.end)
          appointment.end.strftime("%b %d, %Y - %r") 
        end}
        row("Clocked In") { if (appointment.clockIn)
          appointment.clockIn.strftime("%b %d, %Y - %r") 
          elsif (appointment.locations.first)
            appointment.locations.first.created_at.strftime("%b %d, %Y - %r")
        end}
        row("Clocked Out") { if (appointment.clockOut)
          appointment.clockOut.strftime("%b %d, %Y - %r") 
          #elsif (appointment.locations.last)
            #appointment.locations.last.created_at.strftime("%b %d, %Y - %r")
            #appointment.updated_at.strftime("%b %d, %Y - %r")
        end}
        row("Duration") { number_with_precision(appointment.hours/3600, precision: 3) + " hours"}
        row("Note") { appointment.note}
        row("Signature") {image_tag(appointment.image.url)} 
        @lastApp = appointment.locations.last
        hash = Gmaps4rails.build_markers(appointment.locations) do |location, marker|
          marker.lat location.latitude
          marker.lng location.longitude
          if (location == @lastApp)
            marker.picture({
              :url => "http://maps.google.com/mapfiles/ms/icons/green-dot.png", # up to you to pass the proper parameters in the url, I guess with a method from device
              :width   => 32,
              :height  => 32
            })
          else
             marker.picture({
              :url => "https://storage.googleapis.com/support-kms-prod/SNP_2752125_en_v0", # up to you to pass the proper parameters in the url, I guess with a method from device
              :width   => 8,
              :height  => 8
            })
          end
          marker.infowindow location.created_at.strftime("%b %d, %Y - %r")
        end
        render 'gmaps4rails/gmaps4rails', :hash => hash
	    end
	  end
  end

  form do |f|
    f.inputs "Appointment Details" do
      f.input :client_id, :label => 'Client', :as => :select, :collection => Client.all.map{|c| ["#{c.first} #{c.last}", c.id]}
      f.input :beginning, as: :date_time_picker, datepicker_options: { step: 15}
      f.input :end, as: :date_time_picker, datepicker_options: { step: 15}
      if f.object.new_record?
        f.input :hours, :as => :hidden, :input_html => {:value => 0.0}
      else
        f.input :hours
      end  
    end
    f.actions
  end
end