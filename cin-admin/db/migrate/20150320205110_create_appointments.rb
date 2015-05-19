class CreateAppointments < ActiveRecord::Migration
  def change
    create_table :appointments do |t|
      t.string :note
      t.references :client, index: true

      t.timestamps
    end
  end
end
