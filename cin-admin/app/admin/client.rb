ActiveAdmin.register Client do
  permit_params :first, :last, :user_id, :address, :image

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
    column :address
    actions
  end

  show do
    panel "Client Details" do
      attributes_table_for client do
        row("First Name") { client.first }
        row("Last Name") { client.last }
        row("Address") {client.address}
        row("Photo") {image_tag(client.image.url(:thumb))} 
        row("Employee") { client.user.first + " " + client.user.last }
      end
    end
    panel "Appointments" do
      table_for client do
        if (client.appointments.count > 0)
          table_for client.appointments do
            column("Employee"){ |appointment| if (appointment.beginning.to_date.cweek/2 ==  Date.today.cweek/2)
              client.user.first + " " + client.user.last
            end}
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
              appointment.note.encode('utf-8', 'iso-8859-1')
            end}
            column('Map') { |appointment| if (appointment.beginning.to_date.cweek/2 ==  Date.today.cweek/2)
             link_to('Map', cin_appointment_path(appointment.id))
            end}
          end
        end
      end
    end
  end

  form do |f|
    f.inputs "Client Details" do
      f.input :user_id, :label => 'Employee', :as => :select, :collection => User.all.map{|u| ["#{u.first} #{u.last}", u.id]}
      f.input :first
      f.input :last
      f.input :address
    end
    f.actions
  end

  filter :user, :label => 'Employee', :as => :select, :collection => User.all.map{|u| ["#{u.first} #{u.last}", u.id]}
 
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
