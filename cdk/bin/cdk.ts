import 'source-map-support/register';
import { App } from 'aws-cdk-lib';
import { BackupParameterStore } from '../lib/backup-parameter-store';
import { FaciaPurger } from '../lib/facia-purger';

const app = new App();
new FaciaPurger(app, 'FaciaPurger-CODE', { stack: 'frontend', stage: 'CODE' });
new FaciaPurger(app, 'FaciaPurger-PROD', { stack: 'frontend', stage: 'PROD' });
new BackupParameterStore(app, 'BackupParameterStore-CODE', {
	stack: 'frontend',
	stage: 'CODE',
});
// new BackupParameterStore(app, 'FaciaPurger-PROD', { stack: 'frontend', stage: 'PROD' });
