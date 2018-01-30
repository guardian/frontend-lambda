#!/usr/bin/env bash

set -e

PROJECT=dotcom:not-found

if [[ -z $BUILD_NUMBER ]]; then
  BUILD_NUMBER=0
fi

if [[ -z $BUILD_VCS_NUMBER ]]; then
  BUILD_NUMBER=unknown
fi

if [[ -z $BRANCH_NAME ]]; then
  BRANCH_NAME=unknown
fi


BUILD_START_DATE=$(date +"%Y-%m-%dT%H:%M:%S.000Z")

cat >build.json << EOF
{
   "projectName":"$PROJECT",
   "buildNumber":"$BUILD_NUMBER",
   "startTime":"$BUILD_START_DATE",
   "revision":"$BUILD_VCS_NUMBER",
   "vcsURL":"git@github.com:guardian/frontend-lambda.git",
   "branch":"$BRANCH_NAME"
}
EOF

cd lambda
npm install
zip -r moonLambda.zip render.js ../package.json ../node_modules/
cd ..
[ -d target ] && rm -rf target
mkdir target
cd target
mkdir -p moonLambda
mkdir -p cfn
cp ../lambda/moonLambda.zip ./moonLambda/moonLambda.zip
cp ../cloudformation.yml ./cfn

cp ../riff-raff.yaml .
cd ..

aws s3 cp --acl bucket-owner-full-control --region=eu-west-1 --recursive target s3://riffraff-artifact/$PROJECT/$BUILD_NUMBER
aws s3 cp --acl bucket-owner-full-control --region=eu-west-1 build.json s3://riffraff-builds/$PROJECT/$BUILD_NUMBER/build.json
