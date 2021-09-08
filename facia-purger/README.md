# facia-purger

facia-purger is a lambda for listening to events from the fapi/pressed S3 objects in order to purge fastly when an object is changed

# How to deploy

A team city project has been created called 'facia-purger-lambda'. This project currently defines a single build step that cleans, tests and deploys artifacts. In riff-raff select project `dotcom:lambda:facia-purger` to deploy.