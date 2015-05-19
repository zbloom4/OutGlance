object false
node (:success) { true }
node (:info) { 'Appointment created!' }
child :data do
  child @client do
    attributes :id, :user_id, :first, :last, :created_at
    child @appointment do
      attributes :id, :comment, :created_at
      child @location do
        attributes :id, :longitude, :latitude
      end  
    end
  end
end