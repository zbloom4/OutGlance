object false
node (:success) { true }
node (:info) { 'Clients!' }
child :data do
  child @clients.each do
      attributes :id, :user_id, :first, :last, :created_at
  end
  if @appointments.length > 0
    child @appointments.each do
      attributes :id, :note, :created_at, :beginning, :end, :hours, :client_id
    end
  end
end
      #child client.appointments.each do |appointments|
	    #appointments.each do |appointment|
	      #attributes :id, :note, :created_at, :beginning, :end, :hours
	    #end
	  #end
	#end
  #end
    #clients.map do |client|
    #  child client.appointments.each do
    #    attributes :id, :note, :created_at, :beginning, :end, :hours
	#  end
	#end
#end