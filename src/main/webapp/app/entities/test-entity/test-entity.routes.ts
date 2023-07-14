import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { TestEntityComponent } from './list/test-entity.component';
import { TestEntityDetailComponent } from './detail/test-entity-detail.component';
import { TestEntityUpdateComponent } from './update/test-entity-update.component';
import TestEntityResolve from './route/test-entity-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const testEntityRoute: Routes = [
  {
    path: '',
    component: TestEntityComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: TestEntityDetailComponent,
    resolve: {
      testEntity: TestEntityResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: TestEntityUpdateComponent,
    resolve: {
      testEntity: TestEntityResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: TestEntityUpdateComponent,
    resolve: {
      testEntity: TestEntityResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default testEntityRoute;
