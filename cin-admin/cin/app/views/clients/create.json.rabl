object false
node (:success) { true }
node (:info) { 'Client created!' }
child :data do
  child @client do
      attributes :id, :user_id, :first, :last, :created_at
  end
end