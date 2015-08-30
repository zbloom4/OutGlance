class AddBeginningToAppointments < ActiveRecord::Migration
  def change
    add_column :appointments, :beginning, :datetime
  end
end
