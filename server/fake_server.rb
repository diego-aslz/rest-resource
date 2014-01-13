require 'sinatra'

get '/people/1.json' do
  content_type :json
  "person: {name: \"John\"}"
end

get '/people.json' do
  content_type :json
  if params[:name] == "John" && params[:test] == "true"
    "[{name: \"John\"}]"
  else
    "[{name: \"John\"},{name: \"Mary\"}]"
  end
end
