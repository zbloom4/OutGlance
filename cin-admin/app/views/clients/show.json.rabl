object false
node (:success) { true }
node (:info) { 'Client selected!' }
child :data do
    child @client do
        attributes :id, :user_id, :first, :last, :created_at
  	    child @client.appointments.each do
          attributes :id, :note, :created_at
        end
    end
end