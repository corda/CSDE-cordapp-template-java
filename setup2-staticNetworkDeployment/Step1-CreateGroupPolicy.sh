#!/bin/sh

## Step 1, Create Group Policy: Use the staticNetwork.json to create Network group policy
rm -r ../workspace
mkdir ../workspace
sh ~/.corda/cli/corda-cli.sh mgm groupPolicy --file="./staticNetwork.json" > ../workspace/GroupPolicy.json