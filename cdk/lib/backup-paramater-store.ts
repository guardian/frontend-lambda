import type { GuStackProps } from '@guardian/cdk/lib/constructs/core';
import { GuStack } from '@guardian/cdk/lib/constructs/core';
import { GuLambdaFunction } from '@guardian/cdk/lib/constructs/lambda';
import type { App } from 'aws-cdk-lib';
import { CfnInclude } from 'aws-cdk-lib/cloudformation-include';
import { Runtime } from 'aws-cdk-lib/aws-lambda';

export class BackupParamaterStore extends GuStack {
	constructor(scope: App, id: string, props: GuStackProps) {
		super(scope, id, props);

		new CfnInclude(this, 'newBackupParamaterStoreId', {
			templateFile: '../backup-parameter-store/cfn.yaml',
		});
	}
}
