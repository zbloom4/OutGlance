class AddClockInToAppointments < ActiveRecord::Migration
  def change
    add_column :appointments, :clockIn, :datetime
  end
end
