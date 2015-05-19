class Location < ActiveRecord::Base
  belongs_to :appointment

  def location
  	[:latitude, :longitude]
  end
end
