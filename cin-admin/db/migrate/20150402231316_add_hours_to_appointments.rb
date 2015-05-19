class AddHoursToAppointments < ActiveRecord::Migration
  def change
    add_column :appointments, :hours, :float
  end
end
