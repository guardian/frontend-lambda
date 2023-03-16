import 'source-map-support/register';
import { App } from 'aws-cdk-lib';
import { FaciaPurger } from '../lib/facia-purger';
import {BackupParamaterStore} from "../lib/backup-paramater-store";

const app = new App();
new FaciaPurger(app, 'FaciaPurger-CODE', { stack: 'frontend', stage: 'CODE' });
new FaciaPurger(app, 'FaciaPurger-PROD', { stack: 'frontend', stage: 'PROD' });
new BackupParamaterStore(app, 'BackupParamaterStore-CODE', {
	stack: 'frontend',
	stage: 'CODE',
});
// new BackupParamaterStore(app, 'FaciaPurger-PROD', { stack: 'frontend', stage: 'PROD' });
