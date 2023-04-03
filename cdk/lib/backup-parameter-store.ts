import type { GuStackProps } from '@guardian/cdk/lib/constructs/core';
import { GuStack } from '@guardian/cdk/lib/constructs/core';
import type { App } from 'aws-cdk-lib';
import { Runtime } from 'aws-cdk-lib/aws-lambda';
import { CfnInclude } from 'aws-cdk-lib/cloudformation-include';
import { GuLambdaFunction } from '@guardian/cdk/lib/constructs/lambda';

export class BackupParameterStore extends GuStack {
	constructor(scope: App, id: string, props: GuStackProps) {
		super(scope, id, props);

		const lambda = new GuLambdaFunction(this, 'backupParameterStore', {
			app: 'backup-parameter-store',
			fileName: 'backup-parameter-store.jar',
			runtime: Runtime.JAVA_11,
			handler: 'com.gu.backupparameterstore.Lambda',
			environment: {
				Stage: this.stage,
			},
			memorySize: 512,
		});
		this.overrideLogicalId(lambda, {
			logicalId: 'Lambda',
			reason: 'preserve existing triggers',
		});

		// new CfnInclude(this, 'newBackupParameterStoreId', {
		// 	templateFile: '../backup-parameter-store/cfn.yaml',
		// });
	}
}
