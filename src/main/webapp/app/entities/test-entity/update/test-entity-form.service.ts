import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ITestEntity, NewTestEntity } from '../test-entity.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ITestEntity for edit and NewTestEntityFormGroupInput for create.
 */
type TestEntityFormGroupInput = ITestEntity | PartialWithRequiredKeyOf<NewTestEntity>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends ITestEntity | NewTestEntity> = Omit<T, 'createdAt' | 'updatedAt'> & {
  createdAt?: string | null;
  updatedAt?: string | null;
};

type TestEntityFormRawValue = FormValueOf<ITestEntity>;

type NewTestEntityFormRawValue = FormValueOf<NewTestEntity>;

type TestEntityFormDefaults = Pick<NewTestEntity, 'id' | 'createdAt' | 'updatedAt'>;

type TestEntityFormGroupContent = {
  id: FormControl<TestEntityFormRawValue['id'] | NewTestEntity['id']>;
  createdAt: FormControl<TestEntityFormRawValue['createdAt']>;
  updatedAt: FormControl<TestEntityFormRawValue['updatedAt']>;
  score: FormControl<TestEntityFormRawValue['score']>;
  user: FormControl<TestEntityFormRawValue['user']>;
};

export type TestEntityFormGroup = FormGroup<TestEntityFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class TestEntityFormService {
  createTestEntityFormGroup(testEntity: TestEntityFormGroupInput = { id: null }): TestEntityFormGroup {
    const testEntityRawValue = this.convertTestEntityToTestEntityRawValue({
      ...this.getFormDefaults(),
      ...testEntity,
    });
    return new FormGroup<TestEntityFormGroupContent>({
      id: new FormControl(
        { value: testEntityRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      createdAt: new FormControl(testEntityRawValue.createdAt, {
        validators: [Validators.required],
      }),
      updatedAt: new FormControl(testEntityRawValue.updatedAt),
      score: new FormControl(testEntityRawValue.score),
      user: new FormControl(testEntityRawValue.user, {
        validators: [Validators.required],
      }),
    });
  }

  getTestEntity(form: TestEntityFormGroup): ITestEntity | NewTestEntity {
    return this.convertTestEntityRawValueToTestEntity(form.getRawValue() as TestEntityFormRawValue | NewTestEntityFormRawValue);
  }

  resetForm(form: TestEntityFormGroup, testEntity: TestEntityFormGroupInput): void {
    const testEntityRawValue = this.convertTestEntityToTestEntityRawValue({ ...this.getFormDefaults(), ...testEntity });
    form.reset(
      {
        ...testEntityRawValue,
        id: { value: testEntityRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): TestEntityFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      createdAt: currentTime,
      updatedAt: currentTime,
    };
  }

  private convertTestEntityRawValueToTestEntity(
    rawTestEntity: TestEntityFormRawValue | NewTestEntityFormRawValue
  ): ITestEntity | NewTestEntity {
    return {
      ...rawTestEntity,
      createdAt: dayjs(rawTestEntity.createdAt, DATE_TIME_FORMAT),
      updatedAt: dayjs(rawTestEntity.updatedAt, DATE_TIME_FORMAT),
    };
  }

  private convertTestEntityToTestEntityRawValue(
    testEntity: ITestEntity | (Partial<NewTestEntity> & TestEntityFormDefaults)
  ): TestEntityFormRawValue | PartialWithRequiredKeyOf<NewTestEntityFormRawValue> {
    return {
      ...testEntity,
      createdAt: testEntity.createdAt ? testEntity.createdAt.format(DATE_TIME_FORMAT) : undefined,
      updatedAt: testEntity.updatedAt ? testEntity.updatedAt.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
