object false
node (:success) { true }
node (:info) { 'User selected!' }
child :data do
    child @user do
        attributes :id, :first, :last, :email, :hours
    end
end