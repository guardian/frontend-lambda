import { App } from 'aws-cdk-lib';
import { Template } from 'aws-cdk-lib/assertions';
import { BackupParameterStore } from './backup-parameter-store';

describe('The Backup Parameter Store stack', () => {
	it('matches the snapshot', () => {
		const app = new App();
		const stack = new BackupParameterStore(app, 'BackupParameterStore', {
			stack: 'frontend',
			stage: 'TEST',
		});
		const template = Template.fromStack(stack);
		expect(template.toJSON()).toMatchSnapshot();
	});
});
