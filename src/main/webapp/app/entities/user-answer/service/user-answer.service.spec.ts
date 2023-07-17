import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IUserAnswer } from '../user-answer.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../user-answer.test-samples';

import { UserAnswerService, RestUserAnswer } from './user-answer.service';

const requireRestSample: RestUserAnswer = {
  ...sampleWithRequiredData,
  createdAt: sampleWithRequiredData.createdAt?.toJSON(),
  updatedAt: sampleWithRequiredData.updatedAt?.toJSON(),
};

describe('UserAnswer Service', () => {
  let service: UserAnswerService;
  let httpMock: HttpTestingController;
  let expectedResult: IUserAnswer | IUserAnswer[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(UserAnswerService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a UserAnswer', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const userAnswer = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(userAnswer).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a UserAnswer', () => {
      const userAnswer = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(userAnswer).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a UserAnswer', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of UserAnswer', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a UserAnswer', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addUserAnswerToCollectionIfMissing', () => {
      it('should add a UserAnswer to an empty array', () => {
        const userAnswer: IUserAnswer = sampleWithRequiredData;
        expectedResult = service.addUserAnswerToCollectionIfMissing([], userAnswer);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(userAnswer);
      });

      it('should not add a UserAnswer to an array that contains it', () => {
        const userAnswer: IUserAnswer = sampleWithRequiredData;
        const userAnswerCollection: IUserAnswer[] = [
          {
            ...userAnswer,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addUserAnswerToCollectionIfMissing(userAnswerCollection, userAnswer);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a UserAnswer to an array that doesn't contain it", () => {
        const userAnswer: IUserAnswer = sampleWithRequiredData;
        const userAnswerCollection: IUserAnswer[] = [sampleWithPartialData];
        expectedResult = service.addUserAnswerToCollectionIfMissing(userAnswerCollection, userAnswer);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(userAnswer);
      });

      it('should add only unique UserAnswer to an array', () => {
        const userAnswerArray: IUserAnswer[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const userAnswerCollection: IUserAnswer[] = [sampleWithRequiredData];
        expectedResult = service.addUserAnswerToCollectionIfMissing(userAnswerCollection, ...userAnswerArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const userAnswer: IUserAnswer = sampleWithRequiredData;
        const userAnswer2: IUserAnswer = sampleWithPartialData;
        expectedResult = service.addUserAnswerToCollectionIfMissing([], userAnswer, userAnswer2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(userAnswer);
        expect(expectedResult).toContain(userAnswer2);
      });

      it('should accept null and undefined values', () => {
        const userAnswer: IUserAnswer = sampleWithRequiredData;
        expectedResult = service.addUserAnswerToCollectionIfMissing([], null, userAnswer, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(userAnswer);
      });

      it('should return initial array if no UserAnswer is added', () => {
        const userAnswerCollection: IUserAnswer[] = [sampleWithRequiredData];
        expectedResult = service.addUserAnswerToCollectionIfMissing(userAnswerCollection, undefined, null);
        expect(expectedResult).toEqual(userAnswerCollection);
      });
    });

    describe('compareUserAnswer', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareUserAnswer(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareUserAnswer(entity1, entity2);
        const compareResult2 = service.compareUserAnswer(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareUserAnswer(entity1, entity2);
        const compareResult2 = service.compareUserAnswer(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareUserAnswer(entity1, entity2);
        const compareResult2 = service.compareUserAnswer(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
