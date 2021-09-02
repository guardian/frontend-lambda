# frontend-lambda

frontend-lambda contains various self-contained AWS lambda projects used by frontend

## How to deploy

This is a multi-project repo and contains more than one lambda. It's anticipated that not all lambdas will be written in the same language or deployed in the same way.

A team city project has been created called 'frontend-lambdas'. This project defines a build step per lambda.

Each lambda in this repo is responsible for defining its own build configuration, cloudformation and project settings/dependencies. 

For example, for facia-purger we have a build step that publishes to riff raff, defining the .jar and the cloudformation to be used during the build and subsequent deployment of artifacts. In riff-raff we can then select project `dotcom:lambda:facia-purger` for deployment.