import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ITestEntity } from '../test-entity.model';
import { TestEntityService } from '../service/test-entity.service';

export const testEntityResolve = (route: ActivatedRouteSnapshot): Observable<null | ITestEntity> => {
  const id = route.params['id'];
  if (id) {
    return inject(TestEntityService)
      .find(id)
      .pipe(
        mergeMap((testEntity: HttpResponse<ITestEntity>) => {
          if (testEntity.body) {
            return of(testEntity.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        })
      );
  }
  return of(null);
};

export default testEntityResolve;
