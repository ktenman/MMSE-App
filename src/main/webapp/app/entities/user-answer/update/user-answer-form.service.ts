import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IUserAnswer, NewUserAnswer } from '../user-answer.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IUserAnswer for edit and NewUserAnswerFormGroupInput for create.
 */
type UserAnswerFormGroupInput = IUserAnswer | PartialWithRequiredKeyOf<NewUserAnswer>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IUserAnswer | NewUserAnswer> = Omit<T, 'createdAt' | 'updatedAt'> & {
  createdAt?: string | null;
  updatedAt?: string | null;
};

type UserAnswerFormRawValue = FormValueOf<IUserAnswer>;

type NewUserAnswerFormRawValue = FormValueOf<NewUserAnswer>;

type UserAnswerFormDefaults = Pick<NewUserAnswer, 'id' | 'createdAt' | 'updatedAt'>;

type UserAnswerFormGroupContent = {
  id: FormControl<UserAnswerFormRawValue['id'] | NewUserAnswer['id']>;
  answerText: FormControl<UserAnswerFormRawValue['answerText']>;
  createdAt: FormControl<UserAnswerFormRawValue['createdAt']>;
  updatedAt: FormControl<UserAnswerFormRawValue['updatedAt']>;
  questionId: FormControl<UserAnswerFormRawValue['questionId']>;
  testEntity: FormControl<UserAnswerFormRawValue['testEntity']>;
};

export type UserAnswerFormGroup = FormGroup<UserAnswerFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class UserAnswerFormService {
  createUserAnswerFormGroup(userAnswer: UserAnswerFormGroupInput = { id: null }): UserAnswerFormGroup {
    const userAnswerRawValue = this.convertUserAnswerToUserAnswerRawValue({
      ...this.getFormDefaults(),
      ...userAnswer,
    });
    return new FormGroup<UserAnswerFormGroupContent>({
      id: new FormControl(
        { value: userAnswerRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      answerText: new FormControl(userAnswerRawValue.answerText),
      createdAt: new FormControl(userAnswerRawValue.createdAt, {
        validators: [Validators.required],
      }),
      updatedAt: new FormControl(userAnswerRawValue.updatedAt),
      questionId: new FormControl(userAnswerRawValue.questionId, {
        validators: [Validators.required],
      }),
      testEntity: new FormControl(userAnswerRawValue.testEntity, {
        validators: [Validators.required],
      }),
    });
  }

  getUserAnswer(form: UserAnswerFormGroup): IUserAnswer | NewUserAnswer {
    return this.convertUserAnswerRawValueToUserAnswer(form.getRawValue() as UserAnswerFormRawValue | NewUserAnswerFormRawValue);
  }

  resetForm(form: UserAnswerFormGroup, userAnswer: UserAnswerFormGroupInput): void {
    const userAnswerRawValue = this.convertUserAnswerToUserAnswerRawValue({ ...this.getFormDefaults(), ...userAnswer });
    form.reset(
      {
        ...userAnswerRawValue,
        id: { value: userAnswerRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): UserAnswerFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      createdAt: currentTime,
      updatedAt: currentTime,
    };
  }

  private convertUserAnswerRawValueToUserAnswer(
    rawUserAnswer: UserAnswerFormRawValue | NewUserAnswerFormRawValue
  ): IUserAnswer | NewUserAnswer {
    return {
      ...rawUserAnswer,
      createdAt: dayjs(rawUserAnswer.createdAt, DATE_TIME_FORMAT),
      updatedAt: dayjs(rawUserAnswer.updatedAt, DATE_TIME_FORMAT),
    };
  }

  private convertUserAnswerToUserAnswerRawValue(
    userAnswer: IUserAnswer | (Partial<NewUserAnswer> & UserAnswerFormDefaults)
  ): UserAnswerFormRawValue | PartialWithRequiredKeyOf<NewUserAnswerFormRawValue> {
    return {
      ...userAnswer,
      createdAt: userAnswer.createdAt ? userAnswer.createdAt.format(DATE_TIME_FORMAT) : undefined,
      updatedAt: userAnswer.updatedAt ? userAnswer.updatedAt.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
