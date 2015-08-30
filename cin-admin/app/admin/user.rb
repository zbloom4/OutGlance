ActiveAdmin.register User do
  permit_params :first, :last, :email, :password, :hours

  sidebar "CIN", only: [:index] do
    ul do
      li link_to "Users",  cin_users_path()
      li link_to "Clients", cin_clients_path()
      li link_to "Appointments", cin_appointments_path()
    end
  end
  
  index do
    selectable_column
    column :first
    column :last
    column :email
    column :hours do |user| 
      number_with_precision(user.hours, precision: 3)
    end 
    actions
  end
  show do
    panel "Employee Details" do
      attributes_table_for user do
        row("First") { user.first }
        row("Last") { user.last }
        row("Email") { user.email }
        row("Hours") { number_with_precision(user.hours, precision: 3)}
        row("Authentication Token") { user.authentication_token }
      end
    end
#    panel "Appointments" do
#      attributes_table_for user do
#        user.clients.each do |client|
#          client.appointments.each do |appointment|
#            if (2.weeks.ago) < (appointment.created_at)
#              table_for client do
#                column("Client"){ client.first + " " + client.last }
#                column("Date") { appointment.created_at }
#                column("Hours") { number_with_precision(appointment.hours/3600, precision: 3)}
#                column('Map') {link_to('Map', cin_appointment_path(appointment.id))}
#              end
#            end
#          end
#        end
#      end
#    end
    @clients = user.clients
    @appointments = Array.new
    @clients.each do |client|
      @apps = client.appointments
      @apps.each do |appointment|
        @appointments.push(appointment)
      end
    end
    render partial: 'calendar/calendar', :locals => {:appointments => @appointments, :clients => @clients}

    @appointments = Array.new
    @clients.each do |client|
      client.appointments.each do |appointment|
      	if (appointment.beginning.to_date.cweek/2 == Date.today.cweek/2)
          @appointments.push(appointment)
        end
      end
    end

    panel "Appointments" do
      #user.clients.each do |client|
        #table_for client do
          #if (client.appointments.count > 0)
            table_for @appointments do
  	          column("Client"){ |appointment| appointment.client.first + " " + appointment.client.last }
  	          column("Beginning") { |appointment| if (appointment.beginning.to_date.cweek/2 ==  Date.today.cweek/2)
  	            appointment.beginning.strftime("%b %d, %Y - %r")
  	          end}
  	          column("End") { |appointment| if (appointment.beginning.to_date.cweek/2 ==  Date.today.cweek/2)
  	            appointment.end.strftime("%b %d, %Y - %r")
  	          end}
  	          column("Hours") { |appointment|  if (appointment.beginning.to_date.cweek/2 ==  Date.today.cweek/2)
  	            number_with_precision(appointment.hours/3600, precision: 3)
  	          end}
  	          column("Clocked In") { |appointment| if (appointment.beginning.to_date.cweek/2 ==  Date.today.cweek/2)
  	            if (appointment.clockIn)
  	              appointment.clockIn.strftime("%r")
  	            end  
  	          end}
  	          column("Clocked Out") { |appointment| if (appointment.beginning.to_date.cweek/2 ==  Date.today.cweek/2)
  	            if (appointment.clockOut)
  	              appointment.clockOut.strftime("%r")
  	            end  
  	          end}
  	          column("Signature") { |appointment|  if (appointment.beginning.to_date.cweek/2 ==  Date.today.cweek/2)
  	            image_tag(appointment.image.url(:thumb))
  	          end} 
  	          column("Daily Note") {|appointment|  if (appointment.beginning.to_date.cweek/2 ==  Date.today.cweek/2)
                if (appointment.note)
  	              appointment.note.encode('utf-8', 'iso-8859-1')
                end
  	          end}
  	          column('Map') { |appointment| if (appointment.beginning.to_date.cweek/2 ==  Date.today.cweek/2)
  	           link_to('Map', cin_appointment_path(appointment.id))
  	          end}
            end
          #end
        #end
      #end
    end
  end

  form do |f|
    f.inputs "User Details" do
      f.input :first
      f.input :last
      f.input :email
      if f.object.new_record?
        f.input :password
        f.input :hours, :as => :hidden, :input_html => {:value => 0.0}
      else
        f.input :hours
      end
    end
    f.actions
  end

  filter :clients, :label => 'Clients', :as => :select, :collection => Client.all.map{|c| ["#{c.first} #{c.last}", c.id]}

  csv do
    column :first
    column :last
    column("Hours") { number_with_precision(appointment.hours/3600, precision: 3)}
  end  

  # See permitted parameters documentation:
  # https://github.com/activeadmin/activeadmin/blob/master/docs/2-resource-customization.md#setting-up-strong-parameters
  #
  # permit_params :list, :of, :attributes, :on, :model
  #
  # or
  #
  # permit_params do
  #   permitted = [:permitted, :attributes]
  #   permitted << :other if resource.something?
  #   permitted
  # end
end