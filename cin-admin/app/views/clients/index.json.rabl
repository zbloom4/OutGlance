object false
node (:success) { true }
node (:info) { 'Clients!' }
child :data do
  child @clients do
    attributes :id, :user_id, :first, :last, :created_at
  end
end