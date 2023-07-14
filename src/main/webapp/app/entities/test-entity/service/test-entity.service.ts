import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { map } from 'rxjs/operators';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ITestEntity, NewTestEntity } from '../test-entity.model';

export type PartialUpdateTestEntity = Partial<ITestEntity> & Pick<ITestEntity, 'id'>;

type RestOf<T extends ITestEntity | NewTestEntity> = Omit<T, 'createdAt' | 'updatedAt'> & {
  createdAt?: string | null;
  updatedAt?: string | null;
};

export type RestTestEntity = RestOf<ITestEntity>;

export type NewRestTestEntity = RestOf<NewTestEntity>;

export type PartialUpdateRestTestEntity = RestOf<PartialUpdateTestEntity>;

export type EntityResponseType = HttpResponse<ITestEntity>;
export type EntityArrayResponseType = HttpResponse<ITestEntity[]>;

@Injectable({ providedIn: 'root' })
export class TestEntityService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/test-entities');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(testEntity: NewTestEntity): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(testEntity);
    return this.http
      .post<RestTestEntity>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(testEntity: ITestEntity): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(testEntity);
    return this.http
      .put<RestTestEntity>(`${this.resourceUrl}/${this.getTestEntityIdentifier(testEntity)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(testEntity: PartialUpdateTestEntity): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(testEntity);
    return this.http
      .patch<RestTestEntity>(`${this.resourceUrl}/${this.getTestEntityIdentifier(testEntity)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestTestEntity>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestTestEntity[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getTestEntityIdentifier(testEntity: Pick<ITestEntity, 'id'>): number {
    return testEntity.id;
  }

  compareTestEntity(o1: Pick<ITestEntity, 'id'> | null, o2: Pick<ITestEntity, 'id'> | null): boolean {
    return o1 && o2 ? this.getTestEntityIdentifier(o1) === this.getTestEntityIdentifier(o2) : o1 === o2;
  }

  addTestEntityToCollectionIfMissing<Type extends Pick<ITestEntity, 'id'>>(
    testEntityCollection: Type[],
    ...testEntitiesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const testEntities: Type[] = testEntitiesToCheck.filter(isPresent);
    if (testEntities.length > 0) {
      const testEntityCollectionIdentifiers = testEntityCollection.map(testEntityItem => this.getTestEntityIdentifier(testEntityItem)!);
      const testEntitiesToAdd = testEntities.filter(testEntityItem => {
        const testEntityIdentifier = this.getTestEntityIdentifier(testEntityItem);
        if (testEntityCollectionIdentifiers.includes(testEntityIdentifier)) {
          return false;
        }
        testEntityCollectionIdentifiers.push(testEntityIdentifier);
        return true;
      });
      return [...testEntitiesToAdd, ...testEntityCollection];
    }
    return testEntityCollection;
  }

  protected convertDateFromClient<T extends ITestEntity | NewTestEntity | PartialUpdateTestEntity>(testEntity: T): RestOf<T> {
    return {
      ...testEntity,
      createdAt: testEntity.createdAt?.toJSON() ?? null,
      updatedAt: testEntity.updatedAt?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restTestEntity: RestTestEntity): ITestEntity {
    return {
      ...restTestEntity,
      createdAt: restTestEntity.createdAt ? dayjs(restTestEntity.createdAt) : undefined,
      updatedAt: restTestEntity.updatedAt ? dayjs(restTestEntity.updatedAt) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestTestEntity>): HttpResponse<ITestEntity> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestTestEntity[]>): HttpResponse<ITestEntity[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
