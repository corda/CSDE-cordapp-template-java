#!/bin/sh

sh Step1-CreateGroupPolicy.sh

## Step 1, Create Group Policy: Use the staticNetwork.json to create Network group policy
# Generate a signing key
keytool -genkeypair -alias "my-signing-key" -keystore ../workspace/signingkeys.pfx -storepass "keystore password" -dname "CN=CPI Example - My Signing Key, O=CorpOrgCorp, L=London, C=GB" -keyalg RSA -storetype pkcs12 -validity 4000

#Import two pem files to the signing key pfx file 
keytool -importcert -keystore ../workspace/signingkeys.pfx -storepass "keystore password" -noprompt -alias gradle-plugin-default-key -file ../config/gradle-plugin-default-key.pem
keytool -importcert -keystore ../workspace/signingkeys.pfx -storepass "keystore password" -noprompt -alias digicert-ca -file ../config/r3-ca-key.pem

# Export the pfx key to pem file 
keytool -exportcert -rfc -alias "my-signing-key" -keystore ../workspace/signingkeys.pfx -storepass "keystore password" -file ../workspace/signingkey1.pem
