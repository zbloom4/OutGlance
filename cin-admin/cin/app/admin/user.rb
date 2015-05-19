ActiveAdmin.register User do
  actions :index, :show
  
  index do
    selectable_column
    column :id
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
        row("ID") { user.id }
        row("First") { user.first }
        row("Last") { user.last }
        row("Email") { user.email }
        row("Hours") { number_with_precision(user.hours, precision: 3)}
        row("Authentication Token") { user.authentication_token }
      end
    end
    panel "Appointments" do
      attributes_table_for user do
        user.clients.each do |client|
          client.appointments.each do |appointment|
            if (2.weeks.ago) < (appointment.created_at)
              table_for client do
                column("Client"){ client.first + " " + client.last }
                column("Date") { appointment.created_at }
                column("Hours") { number_with_precision(appointment.hours/3600, precision: 3)}
                column('Map') {link_to('Map', cin_appointment_path(appointment.id))}
              end
            end
          end
        end
      end
    end
  end

  filter :first
  filter :id
  filter :last
  filter :email
  filter :hours
  filter :created_at
  filter :updated_at

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