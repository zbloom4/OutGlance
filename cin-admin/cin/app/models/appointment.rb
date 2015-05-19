class Appointment < ActiveRecord::Base
  belongs_to :client
  has_many :locations
end
