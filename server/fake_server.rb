require 'sinatra'
require 'json'

get '/people/1.json' do
  content_type :json
  {person: {name: "John"}}.to_json
end

get '/status_codes/:status.json' do
  content_type :json
  [
    params[:status].to_i,
    nil.to_json,
  ]
end

get '/people.json' do
  content_type :json
  if params[:name] == "John" && params[:test] == "true"
    [{name: "John"}].to_json
  else
    [{name: "John"},{name: "Mary"}].to_json
  end
end

get '/status_codes.json' do
  content_type :json
  [
    params[:status].to_i,
    [].to_json,
  ]
end

post '/people.json' do
  content_type :json

  pars = JSON.parse request.body.read
  pars["person"]['id'] = 1
  [201, pars.to_json]
end

post '/status_codes.json' do
  content_type :json

  pars = JSON.parse request.body.read
  [pars["status_code"]['status'].to_i, nil.to_json]
end

put '/people/1.json' do
  content_type :json

  [204, nil.to_json]
end
