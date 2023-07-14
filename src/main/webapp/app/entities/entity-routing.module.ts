import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'test-entity',
        data: { pageTitle: 'mmseApp.testEntity.home.title' },
        loadChildren: () => import('./test-entity/test-entity.routes'),
      },
      {
        path: 'user-answer',
        data: { pageTitle: 'mmseApp.userAnswer.home.title' },
        loadChildren: () => import('./user-answer/user-answer.routes'),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
