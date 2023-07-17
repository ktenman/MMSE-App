import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { UserAnswerComponent } from './list/user-answer.component';
import { UserAnswerDetailComponent } from './detail/user-answer-detail.component';
import { UserAnswerUpdateComponent } from './update/user-answer-update.component';
import UserAnswerResolve from './route/user-answer-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const userAnswerRoute: Routes = [
  {
    path: '',
    component: UserAnswerComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: UserAnswerDetailComponent,
    resolve: {
      userAnswer: UserAnswerResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: UserAnswerUpdateComponent,
    resolve: {
      userAnswer: UserAnswerResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: UserAnswerUpdateComponent,
    resolve: {
      userAnswer: UserAnswerResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default userAnswerRoute;
