#!/bin/sh

sh Step3-buildCPIs.sh

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


upload_CPI(){
  local cpi_name="$1"
  local App_CPI_PATH="$2"
  local CPI_Status_PATH="$3"
  local currentCPIs="$4"

if [[  $currentCPIs == *"$cpi_name"* ]]; then
    echo "CPI found in the current cluster, force uploading."
    response=$(curl -s --insecure -u admin:admin -F upload=@$App_CPI_PATH $API_URL/maintenance/virtualnode/forcecpiupload/)
    App_CPI_ID=$(echo "$response" | jq '.id'| tr -d '"')
    echo "MyApp CPI_ID: $App_CPI_ID"
    buffer 30 "Uploading the CPI file"
    appResponse=$(curl -s --insecure -u admin:admin $API_URL/cpi/status/$App_CPI_ID)
    echo $appResponse > $CPI_Status_PATH

else
    echo "CPI not found in the current cluster."
    response=$(curl -s --insecure -u admin:admin -F upload=@$App_CPI_PATH $API_URL/cpi/)
    App_CPI_ID=$(echo "$response" | jq '.id'| tr -d '"')
    echo "MyApp CPI_ID: $App_CPI_ID"
    buffer 30 "Uploading the CPI file"
    appResponse=$(curl -s --insecure -u admin:admin $API_URL/cpi/status/$App_CPI_ID)
    echo $appResponse > $CPI_Status_PATH
fi


}


echo "---Set Env---"
RPC_HOST=localhost
RPC_PORT=8888
API_URL="https://$RPC_HOST:$RPC_PORT/api/v1"
echo "RPC_PORT:" $RPC_PORT
echo "API_URL:" $API_URL

echo "\n---Create and upload signing keys---"
#Upload the two pem files to the Corda cluster
echo "Uploading Gradle signing key\n"
curl --insecure -u admin:admin -X PUT -F alias="gradle-plugin-default-key" -F certificate=@../config/gradle-plugin-default-key.pem $API_URL/certificates/cluster/code-signer
echo "Uploading R3 app signing key\n"
curl --insecure -u admin:admin -X PUT -F alias="r3-ca-key" -F certificate=@../config/r3-ca-key.pem $API_URL/certificates/cluster/code-signer
echo "Uploading Your app signing key\n"
curl --insecure -u admin:admin -X PUT -F alias="my-signing-key" -F certificate=@../workspace/signingkey1.pem $API_URL/certificates/cluster/code-signer


cpi_response=$(curl -s --insecure -u admin:admin $API_URL/cpi/)
echo $cpi_response > ./currentCPI.json
cpi_names=$(echo "$cpi_response" | jq -r '.cpis[].id.cpiName')

currentCPIs=""
for element in "${cpi_names[@]}"; do
    currentCPIs="$currentCPIs $element"
done

App_CPI_PATH=../workflows/build/MyCorDapp-1.0-SNAPSHOT.cpi
APP_CPI_STATUS_PATH=./corDappCpiUploadStatus.json
upload_CPI "MyCorDapp" $App_CPI_PATH $APP_CPI_STATUS_PATH "$currentCPIs"

NOTARY_CPI_PATH=../workflows/build/NotaryServer-1.0-SNAPSHOT.cpi
APP_CPI_STATUS_PATH=./notaryCpiUploadStatus.json
upload_CPI "NotaryServer" $NOTARY_CPI_PATH $APP_CPI_STATUS_PATH "$currentCPIs"
