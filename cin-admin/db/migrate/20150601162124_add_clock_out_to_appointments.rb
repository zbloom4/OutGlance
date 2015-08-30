class AddClockOutToAppointments < ActiveRecord::Migration
  def change
    add_column :appointments, :clockOut, :datetime
  end
end
