import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IUserAnswer } from '../user-answer.model';
import { UserAnswerService } from '../service/user-answer.service';

export const userAnswerResolve = (route: ActivatedRouteSnapshot): Observable<null | IUserAnswer> => {
  const id = route.params['id'];
  if (id) {
    return inject(UserAnswerService)
      .find(id)
      .pipe(
        mergeMap((userAnswer: HttpResponse<IUserAnswer>) => {
          if (userAnswer.body) {
            return of(userAnswer.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        })
      );
  }
  return of(null);
};

export default userAnswerResolve;
