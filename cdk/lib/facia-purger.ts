import type { GuStackProps } from '@guardian/cdk/lib/constructs/core';
import { GuStack } from '@guardian/cdk/lib/constructs/core';
import { GuLambdaFunction } from '@guardian/cdk/lib/constructs/lambda';
import type { App } from 'aws-cdk-lib';
import { CfnParameter } from 'aws-cdk-lib';
import { Runtime } from 'aws-cdk-lib/aws-lambda';
import { Topic } from 'aws-cdk-lib/aws-sns';
import { EmailSubscription } from 'aws-cdk-lib/aws-sns-subscriptions';

export class FaciaPurger extends GuStack {
	constructor(scope: App, id: string, props: GuStackProps) {
		super(scope, id, props);

		const fastlyServiceId = new CfnParameter(this, 'FastlyServiceId', {
			type: 'String',
			description: 'Id of service to purge',
		});
		const fastlyAPIKey = new CfnParameter(this, 'FastlyAPIKey', {
			type: 'String',
			description:
				'API key with purge writes for service with id FastlyServiceId',
			noEcho: true,
		});
		const emailRecipient = new CfnParameter(this, 'EmailRecipient', {
			type: 'String',
			description: 'The email address to send alarm notifications to',
		});

		const notificationTopic = new Topic(this, 'NotificationTopic');
		notificationTopic.addSubscription(
			new EmailSubscription(emailRecipient.valueAsString),
		);

		const lambda = new GuLambdaFunction(this, 'faciaPurgerLambda', {
			app: 'facia-purger',
			fileName: 'facia-purger.jar',
			runtime: Runtime.JAVA_11,
			handler: 'com.gu.purge.facia.Lambda',
			environment: {
				FastlyServiceId: fastlyServiceId.valueAsString,
				FastlyAPIKey: fastlyAPIKey.valueAsString,
				Stage: this.stage,
			},
			memorySize: 512,
			errorPercentageMonitoring: {
				toleratedErrorPercentage: 1,
				numberOfMinutesAboveThresholdBeforeAlarm: 5,
				snsTopicName: notificationTopic.topicName,
			},
		});

		this.overrideLogicalId(lambda, {
			logicalId: 'Lambda',
			reason: 'preserve existing triggers',
		});
	}
}
