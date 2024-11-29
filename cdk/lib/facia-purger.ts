import type { GuStackProps } from '@guardian/cdk/lib/constructs/core';
import { GuStack } from '@guardian/cdk/lib/constructs/core';
import { GuLambdaFunction } from '@guardian/cdk/lib/constructs/lambda';
import type { App } from 'aws-cdk-lib';
import { CfnParameter, Duration } from 'aws-cdk-lib';
import {
	Alarm,
	ComparisonOperator,
	TreatMissingData,
	Unit,
} from 'aws-cdk-lib/aws-cloudwatch';
import { SnsAction } from 'aws-cdk-lib/aws-cloudwatch-actions';
import { Runtime } from 'aws-cdk-lib/aws-lambda';
import { SnsDestination } from 'aws-cdk-lib/aws-lambda-destinations';
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

		const cachePurgedTopic = new Topic(this, 'CachePurgedTopic', {
			topicName: `facia-fastly-cache-purger-${this.stage}-decached`,
		});

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
			onSuccess: new SnsDestination(cachePurgedTopic),
		});

		this.overrideLogicalId(lambda, {
			logicalId: 'Lambda',
			reason: 'preserve existing triggers',
		});

		const invocationAlarm = new Alarm(this, 'invocationAlarm', {
			alarmDescription:
				'Notify if there are less than 5 invocations over last 10 minutes or there is insufficient data (i.e. no invocations)',
			comparisonOperator: ComparisonOperator.LESS_THAN_OR_EQUAL_TO_THRESHOLD,
			threshold: 5,
			evaluationPeriods: 1,
			actionsEnabled: true,
			treatMissingData: TreatMissingData.BREACHING,
			metric: lambda.metricInvocations({
				period: Duration.minutes(10),
				statistic: 'Sum',
				unit: Unit.COUNT,
			}),
		});

		invocationAlarm.addAlarmAction(new SnsAction(notificationTopic));
	}
}
