object false
node (:success) { true }
node (:info) { 'Appointment selected!' }
child :data do
  child @client do
    node(:data) { @data }
    attributes :id, :user_id, :first, :last, :created_at
    child @appointment do
      attributes :id, :note, :beginning, :end, :clockIn, :clockOut, :hours
    end
  end
end