import type { GuStackProps } from '@guardian/cdk/lib/constructs/core';
import { GuStack } from '@guardian/cdk/lib/constructs/core';
import { GuLambdaFunction } from '@guardian/cdk/lib/constructs/lambda';
import type { App } from 'aws-cdk-lib';
import { Runtime } from 'aws-cdk-lib/aws-lambda';

export class FaciaPurger extends GuStack {
	constructor(scope: App, id: string, props: GuStackProps) {
		super(scope, id, props);

		new GuLambdaFunction(this, 'faciaPurgerLambda', {
			app: 'facia-purger',
			fileName: 'facia-purger.jar',
			runtime: Runtime.JAVA_11,
			handler: 'com.gu.purge.facia.Lambda',
		});
	}
}
