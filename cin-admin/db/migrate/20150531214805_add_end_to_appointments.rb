class AddEndToAppointments < ActiveRecord::Migration
  def change
    add_column :appointments, :end, :datetime
  end
end
