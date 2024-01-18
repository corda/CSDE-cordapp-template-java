#!/bin/sh

sh Step4-DeployCpis.sh

# Helper method
buffer(){
# Define the animation sequence
animation="/-\|"
local time=$1
local message=$2
# Function to clear the current line
clear_line() {
  printf "\r"
  printf "                                                                                             "
  printf "\r"
}

# Iterate through the animation sequence
for ((i=0; i<$time; i++)); do
  # Get the current animation character
  char="${animation:i%${#animation}:1}"

  # Print the animation character
  printf "$message... %s" "$char"

  # Wait for a short interval
  sleep 0.1

  # Clear the current line
  clear_line
done
}

echo "---Set Env---"
RPC_HOST=localhost
RPC_PORT=8888
API_URL="https://$RPC_HOST:$RPC_PORT/api/v1"
echo "RPC_PORT:" $RPC_PORT
echo "API_URL:" $API_URL

appCPIChecksum=$(jq -r ".cpiFileChecksum" ./corDappCpiUploadStatus.json)
echo "\nappCPIChecksum: "$appCPIChecksum
notaryCPIChecksum=$(jq -r ".cpiFileChecksum" ./notaryCpiUploadStatus.json)
echo "notaryCPIChecksum: "$notaryCPIChecksum


# Extract information about each person using jq
jq -c '.members[]' ./staticNetwork.json | while IFS= read -r item; do
  name=$(echo "$item" | jq -r '.name')
  echo "\nCreating vNode for: "$name
  serviceX500Name=$(echo "$item" | jq -r '.serviceX500Name')
  if [ "$serviceX500Name" != "null" ]; then
    requestbody=$(echo '{ "request": {"cpiFileChecksum": "'$notaryCPIChecksum'", "x500Name": "'${name}'"}}')
    response=$(curl -s --insecure -u admin:admin -d "${requestbody}" $API_URL/virtualnode)
    Notary_HOLDING_ID=$(echo "$response" | jq '.requestId'| tr -d '"')
    echo "Notary_HOLDING_ID: $Notary_HOLDING_ID"
    buffer 30 "Creating the VNode for $name"
    registrationBody='{"memberRegistrationRequest": {"context": {"corda.key.scheme": "CORDA.ECDSA.SECP256R1","corda.roles.0": "notary","corda.notary.service.name": "'$serviceX500Name'","corda.notary.service.flow.protocol.version.0": "1","corda.notary.service.flow.protocol.name": "com.r3.corda.notary.plugin.nonvalidating"}}}'
    response=$(curl -s --insecure -u admin:admin -d "$registrationBody" $API_URL/membership/$Notary_HOLDING_ID)

  else
    requestbody=$(echo '{ "request": {"cpiFileChecksum": "'$appCPIChecksum'", "x500Name": "'${name}'"}}')
    response=$(curl -s --insecure -u admin:admin -d "${requestbody}" $API_URL/virtualnode)
    vNode_HOLDING_ID=$(echo "$response" | jq '.requestId'| tr -d '"')
    echo "\nvNode_HOLDING_ID: $vNode_HOLDING_ID"
    buffer 30 "Creating the VNode for $name"
    registrationBody='{"memberRegistrationRequest":{"context":{"corda.key.scheme":"CORDA.ECDSA.SECP256R1"}}}'
    response=$(curl -s --insecure -u admin:admin -d "$registrationBody" $API_URL/membership/$vNode_HOLDING_ID)
  fi

done
