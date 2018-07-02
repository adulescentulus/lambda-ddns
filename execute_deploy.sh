#!/bin/bash -e
cd cfn
chmod +x prepare_template.sh
./prepare_template.sh
aws cloudformation package --template aws-resources.yml --s3-bucket nc-projects-infrabucket --output-template template-export.yml
aws cloudformation deploy  --template-file=template-export.yml --stack-name="${STACK_NAME}" --parameter-overrides ParamLambdaSecret=${STACK_PARAM_PASS} --capabilities=CAPABILITY_NAMED_IAM
