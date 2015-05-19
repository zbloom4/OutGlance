ActiveAdmin.register Client do
  
  actions :index, :show

  sidebar "CIN", only: [:index] do
    ul do
      li link_to "Employees", cin_users_path()
      li link_to "Clients", cin_clients_path()
      li link_to "Appointments", cin_appointments_path()
    end
  end

  show do
    panel "Client Details" do
      attributes_table_for client do
        row("ID") { client.id }
        row("First Name") { client.first }
        row("Last Name") { client.last }
        row("Employee") { client.user.first + " " + client.user.last }
      end
    end
    panel "Appointments" do
      attributes_table_for client do
        row("Employee") { client.user.first + " " + client.user.last }
        client.appointments.each do |appointment|
          if (2.weeks.ago) < (appointment.created_at)
            table_for client do
              column("Date") { appointment.created_at }
              column("Hours") { number_with_precision(appointment.hours/3600, precision: 3)}
              column('Map') {link_to('Map', cin_appointment_path(appointment.id))}
            end
          end
        end
      end
    end
  end

  filter :first, label: 'First Name'
  filter :last, label: 'Last Name'
 
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
