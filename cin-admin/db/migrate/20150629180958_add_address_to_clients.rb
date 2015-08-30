class AddAddressToClients < ActiveRecord::Migration
  def change
    add_column :clients, :address, :string
  end
end
