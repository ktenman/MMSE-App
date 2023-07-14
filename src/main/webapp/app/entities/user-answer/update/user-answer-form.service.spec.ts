import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../user-answer.test-samples';

import { UserAnswerFormService } from './user-answer-form.service';

describe('UserAnswer Form Service', () => {
  let service: UserAnswerFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(UserAnswerFormService);
  });

  describe('Service methods', () => {
    describe('createUserAnswerFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createUserAnswerFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            answerText: expect.any(Object),
            createdAt: expect.any(Object),
            updatedAt: expect.any(Object),
            questionId: expect.any(Object),
            testEntity: expect.any(Object),
          })
        );
      });

      it('passing IUserAnswer should create a new form with FormGroup', () => {
        const formGroup = service.createUserAnswerFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            answerText: expect.any(Object),
            createdAt: expect.any(Object),
            updatedAt: expect.any(Object),
            questionId: expect.any(Object),
            testEntity: expect.any(Object),
          })
        );
      });
    });

    describe('getUserAnswer', () => {
      it('should return NewUserAnswer for default UserAnswer initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createUserAnswerFormGroup(sampleWithNewData);

        const userAnswer = service.getUserAnswer(formGroup) as any;

        expect(userAnswer).toMatchObject(sampleWithNewData);
      });

      it('should return NewUserAnswer for empty UserAnswer initial value', () => {
        const formGroup = service.createUserAnswerFormGroup();

        const userAnswer = service.getUserAnswer(formGroup) as any;

        expect(userAnswer).toMatchObject({});
      });

      it('should return IUserAnswer', () => {
        const formGroup = service.createUserAnswerFormGroup(sampleWithRequiredData);

        const userAnswer = service.getUserAnswer(formGroup) as any;

        expect(userAnswer).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IUserAnswer should not enable id FormControl', () => {
        const formGroup = service.createUserAnswerFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewUserAnswer should disable id FormControl', () => {
        const formGroup = service.createUserAnswerFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
