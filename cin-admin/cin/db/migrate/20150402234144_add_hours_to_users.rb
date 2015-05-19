class AddHoursToUsers < ActiveRecord::Migration
  def change
    add_column :users, :hours, :float
  end
end
