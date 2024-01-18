#!/bin/sh

sh Step2-CreateKeystores.sh

## Step 1, Create Group Policy: Use the staticNetwork.json to create Network group policy
cd ../
./gradlew clean build
cd ./setup2-staticNetworkDeployment


sh ~/.corda/cli/corda-cli.sh package create-cpi --cpb ../workflows/build/libs/workflows-1.0-SNAPSHOT-package.cpb --group-policy ../workspace/GroupPolicy.json --cpi-name "MyCorDapp" --cpi-version "1.0.0.0-SNAPSHOT" --file ../workflows/build/MyCorDapp-1.0-SNAPSHOT.cpi --keystore ../workspace/signingkeys.pfx --storepass "keystore password" --key "my-signing-key"
sh ~/.corda/cli/corda-cli.sh package create-cpi --cpb ~/.corda/corda5/notaryServer/notary-plugin-non-validating-server-5.1.0.0-package.cpb --group-policy ../workspace/GroupPolicy.json --cpi-name "NotaryServer" --cpi-version "1.0.0.0-SNAPSHOT" --file ../workflows/build/NotaryServer-1.0-SNAPSHOT.cpi --keystore ../workspace/signingkeys.pfx --storepass "keystore password" --key "my-signing-key"