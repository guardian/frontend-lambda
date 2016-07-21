[![Build Status](https://travis-ci.org/guardian/frontend-lambda.svg?branch=master)](https://travis-ci.org/guardian/frontend-lambda)

# facia-purger

facia-purger is a lambda for listening to events from the fapi/pressed S3 objects in order to purge fastly when an object is changed

## Tests

Note that in order to run the included integration test you will need to have valid AWS credentials configured in `.aws/credentials`.
