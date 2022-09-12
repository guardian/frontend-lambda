import { App } from 'aws-cdk-lib';
import { Template } from 'aws-cdk-lib/assertions';
import { FaciaPurger } from './facia-purger';

describe('The FaciaPurger stack', () => {
	it('matches the snapshot', () => {
		const app = new App();
		const stack = new FaciaPurger(app, 'FaciaPurger', {
			stack: 'frontend',
			stage: 'TEST',
		});
		const template = Template.fromStack(stack);
		expect(template.toJSON()).toMatchSnapshot();
	});
});
