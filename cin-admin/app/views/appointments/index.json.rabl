object false
node (:success) { true }
node (:info) { 'Appointments' }
child :data do
  child @client do
  	attributes :id, :user_id, :first, :last, :created_at
  	child @appointments.each do
      attributes :id, :note, :beginning, :end
      child @location do
      	attributes :id, :latitude, :longitude
      end
    end
  end
end