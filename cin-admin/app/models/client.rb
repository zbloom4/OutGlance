class Client < ActiveRecord::Base
  belongs_to :user
  has_many :appointments
  mount_uploader :image, ImageUploader
end
