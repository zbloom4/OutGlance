object false
node (:success) { true }
node (:info) { 'Appointment deleted!' }
child :data do
  child @client do
    attributes :user_id, :first, :last, :created_at
    child @appointment do
      attributes :id, :note, :created_at, :hours
    end
  end
end