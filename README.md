# frontend-lambda

frontend-lambda contains various self-contained AWS lambda projects used by frontend

## How to deploy

This is a multi-project repo and contains more than one lambda. It's anticipated that not all lambdas will be written in the same language or deployed in the same way.

Each lambda is responsible for its own deploy and will therefore define its own build configuration, cloudformation and project settings/dependencies. 
