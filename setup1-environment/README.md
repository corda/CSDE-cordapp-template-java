# This folder consists of two parts:

The source code of the Corda flow management tool.
A Docker Compose file to start three services:
* Corda Combined Worker
* Postgres Database
* A user-friendly Corda flow management tool
T
o build and deploy the entire setup, run the following shell script:

```
sh CordaDeployment.sh
```
After running the script, you should be able to access:

* Corda Swagger API at: https://localhost:8888/api/v1/swagger#/
* The Flow management tool at: http://localhost:5000/

The Flow management tool looks like the following, allowing you to trigger and query Corda Flow with ease.

![image(5)](https://github.com/parisyup/FlowManagementUI/assets/66366646/c65195a6-0a70-4354-804e-37884f657746)



![image(6)](https://github.com/parisyup/FlowManagementUI/assets/66366646/13e979b0-f76e-4f2c-9d55-81be8880890b)


