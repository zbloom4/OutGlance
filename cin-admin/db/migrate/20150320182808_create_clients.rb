class CreateClients < ActiveRecord::Migration
  def change
    create_table :clients do |t|
      t.string :first
      t.string :last
      t.references :user, index: true

      t.timestamps
    end
  end
end
