object false
node (:success) { true }
node (:info) { 'Client deleted!' }
child :data do
  child @client do
      attributes :user_id, :first, :last, :created_at
  end
end