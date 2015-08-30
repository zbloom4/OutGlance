class Appointment < ActiveRecord::Base
  belongs_to :client
  has_many :locations
  mount_uploader :image, ImageUploader
  extend SimpleCalendar
  has_calendar :attribute => :beginning
end
