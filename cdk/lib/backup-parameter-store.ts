import { GuScheduledLambda } from '@guardian/cdk';
import type { GuStackProps } from '@guardian/cdk/lib/constructs/core';
import { GuStack } from '@guardian/cdk/lib/constructs/core';
import type { App } from 'aws-cdk-lib';
import { CfnParameter, Duration } from 'aws-cdk-lib';
import { Schedule } from 'aws-cdk-lib/aws-events';
import { Runtime } from 'aws-cdk-lib/aws-lambda';
import { Topic } from 'aws-cdk-lib/aws-sns';
import { EmailSubscription } from 'aws-cdk-lib/aws-sns-subscriptions';

export class BackupParameterStore extends GuStack {
	constructor(scope: App, id: string, props: GuStackProps) {
		super(scope, id, props);

		const emailRecipient = new CfnParameter(this, 'EmailRecipient', {
			type: 'String',
			description: 'The email address to send alarm notifications to',
		});

		const notificationTopic = new Topic(this, 'NotificationTopic');
		notificationTopic.addSubscription(
			new EmailSubscription(emailRecipient.valueAsString),
		);

		const lambda = new GuScheduledLambda(this, 'backupParameterStore', {
			monitoringConfiguration: {
				snsTopicName: notificationTopic.topicName,
				toleratedErrorPercentage: 1,
				numberOfMinutesAboveThresholdBeforeAlarm: 1,
			},
			rules: [
				{
					schedule: Schedule.cron({
						minute: '0',
						hour: '0',
						month: '*',
						weekDay: '?',
						year: '*',
					}),
				},
			],
			app: 'backup-parameter-store',
			fileName: 'backup-parameter-store.jar',
			runtime: Runtime.JAVA_8,
			handler: 'com.gu.backupparameterstore.Lambda::handler',
			environment: {
				Stage: this.stage,
			},
			memorySize: 512,
			timeout: Duration.seconds(300),
		});
		this.overrideLogicalId(lambda, {
			logicalId: 'Lambda',
			reason: 'preserve existing triggers',
		});
	}
}
