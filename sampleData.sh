#!/bin/bash
echo " make sure the server is running"
echo " Sending requests to localhost:8080/accounts"

for i in {1..400}
 do
  curl --request POST -sL -i -v --header "Content-Type: application/json" --url 'http://localhost:8080/customers' --data  '{
    "name": "I feel great today😛🧐",
    "email": "Tomorrow, I want to just sleep '"$i"' times "
   }'


done
