# Static Network Deployment Scripts 

This folder consists of the scripts to deploy a local static Corda network carrying the template chat app. You will find 6 scripts and one json file to begin with. 

```
.
├── Step1-CreateGroupPolicy.sh
├── Step2-CreateKeystores.sh
├── Step3-buildCPIs.sh
├── Step4-DeployCpis.sh
├── Step5-VNodesSetup.sh
├── cleanup.sh
└── staticNetwork.json
```
These scripts documented the exact steps to deploy a staci network on Corda. You can also use the same scripts to deploy a static network to a Corda cluster(non-combined worker Corda env). In order to run the deployment, you would only need to run the `Step5-VNodesSetup.sh` because it will automatically trigger the previous steps. 
```
sh Step5-VNodesSetup.sh
```
Note that, you would need `jq` tool installed. You can install it by: 
```
brew install jq
```
