object false
node (:success) { true }
node (:info) { 'Client selected!' }
child :data do
    child @client do
      node(:data) { @data }
      attributes :id, :user_id, :first, :last, :created_at, :address
    end
end