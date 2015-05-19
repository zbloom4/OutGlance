class User < ActiveRecord::Base
  acts_as_token_authenticatable
  # Include default devise modules. Others available are:
  # :confirmable, :lockable, :timeoutable and :omniauthable
  devise :database_authenticatable, :registerable,
         :recoverable, :rememberable, :trackable, :validatable

  has_many :clients

  def update_hours(hours)
  	total_hours = (self.hours * 3600) + hours
  	self.update_attributes(:hours => total_hours/3600)
  end
end