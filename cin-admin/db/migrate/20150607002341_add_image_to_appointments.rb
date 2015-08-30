class AddImageToAppointments < ActiveRecord::Migration
  def change
    add_column :appointments, :image, :string
  end
end
