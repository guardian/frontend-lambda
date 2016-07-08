#!/usr/bin/env bash
#
# Builds and deploys the Lambda

# need to first create the lambda with something like this command
#aws lambda create-function --region eu-west-1 --function-name facia-purger --zip-file fileb://target/scala-2.11/facia-purger-assembly-1.0.jar --role arn:aws:iam::642631414762:role/facia-purge-lambda-role --handler com.gu.purge.facia.Lambda --runtime java8 --profile frontend --timeout 10 --memory-size 512

set -e

my_dir=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)

sbt assembly

jar_file=$(echo $my_dir/target/scala-2.11/facia-purger-assembly*.jar)

aws lambda update-function-code \
  --function-name facia-purger \
  --zip-file fileb://$jar_file \
  --profile frontend \
  --region eu-west-1
