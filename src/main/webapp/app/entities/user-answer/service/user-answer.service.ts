import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { map } from 'rxjs/operators';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IUserAnswer, NewUserAnswer } from '../user-answer.model';

export type PartialUpdateUserAnswer = Partial<IUserAnswer> & Pick<IUserAnswer, 'id'>;

type RestOf<T extends IUserAnswer | NewUserAnswer> = Omit<T, 'createdAt' | 'updatedAt'> & {
  createdAt?: string | null;
  updatedAt?: string | null;
};

export type RestUserAnswer = RestOf<IUserAnswer>;

export type NewRestUserAnswer = RestOf<NewUserAnswer>;

export type PartialUpdateRestUserAnswer = RestOf<PartialUpdateUserAnswer>;

export type EntityResponseType = HttpResponse<IUserAnswer>;
export type EntityArrayResponseType = HttpResponse<IUserAnswer[]>;

@Injectable({ providedIn: 'root' })
export class UserAnswerService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/user-answers');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(userAnswer: NewUserAnswer): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(userAnswer);
    return this.http
      .post<RestUserAnswer>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(userAnswer: IUserAnswer): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(userAnswer);
    return this.http
      .put<RestUserAnswer>(`${this.resourceUrl}/${this.getUserAnswerIdentifier(userAnswer)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(userAnswer: PartialUpdateUserAnswer): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(userAnswer);
    return this.http
      .patch<RestUserAnswer>(`${this.resourceUrl}/${this.getUserAnswerIdentifier(userAnswer)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestUserAnswer>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestUserAnswer[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getUserAnswerIdentifier(userAnswer: Pick<IUserAnswer, 'id'>): number {
    return userAnswer.id;
  }

  compareUserAnswer(o1: Pick<IUserAnswer, 'id'> | null, o2: Pick<IUserAnswer, 'id'> | null): boolean {
    return o1 && o2 ? this.getUserAnswerIdentifier(o1) === this.getUserAnswerIdentifier(o2) : o1 === o2;
  }

  addUserAnswerToCollectionIfMissing<Type extends Pick<IUserAnswer, 'id'>>(
    userAnswerCollection: Type[],
    ...userAnswersToCheck: (Type | null | undefined)[]
  ): Type[] {
    const userAnswers: Type[] = userAnswersToCheck.filter(isPresent);
    if (userAnswers.length > 0) {
      const userAnswerCollectionIdentifiers = userAnswerCollection.map(userAnswerItem => this.getUserAnswerIdentifier(userAnswerItem)!);
      const userAnswersToAdd = userAnswers.filter(userAnswerItem => {
        const userAnswerIdentifier = this.getUserAnswerIdentifier(userAnswerItem);
        if (userAnswerCollectionIdentifiers.includes(userAnswerIdentifier)) {
          return false;
        }
        userAnswerCollectionIdentifiers.push(userAnswerIdentifier);
        return true;
      });
      return [...userAnswersToAdd, ...userAnswerCollection];
    }
    return userAnswerCollection;
  }

  protected convertDateFromClient<T extends IUserAnswer | NewUserAnswer | PartialUpdateUserAnswer>(userAnswer: T): RestOf<T> {
    return {
      ...userAnswer,
      createdAt: userAnswer.createdAt?.toJSON() ?? null,
      updatedAt: userAnswer.updatedAt?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restUserAnswer: RestUserAnswer): IUserAnswer {
    return {
      ...restUserAnswer,
      createdAt: restUserAnswer.createdAt ? dayjs(restUserAnswer.createdAt) : undefined,
      updatedAt: restUserAnswer.updatedAt ? dayjs(restUserAnswer.updatedAt) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestUserAnswer>): HttpResponse<IUserAnswer> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestUserAnswer[]>): HttpResponse<IUserAnswer[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
