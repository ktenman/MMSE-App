import dayjs from 'dayjs/esm';

import { ITestEntity, NewTestEntity } from './test-entity.model';

export const sampleWithRequiredData: ITestEntity = {
  id: 74075,
  createdAt: dayjs('2023-06-30T17:53'),
};

export const sampleWithPartialData: ITestEntity = {
  id: 54335,
  createdAt: dayjs('2023-07-01T08:19'),
};

export const sampleWithFullData: ITestEntity = {
  id: 3900,
  createdAt: dayjs('2023-07-01T00:04'),
  updatedAt: dayjs('2023-06-30T14:00'),
  score: 14777,
};

export const sampleWithNewData: NewTestEntity = {
  createdAt: dayjs('2023-06-30T14:13'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
