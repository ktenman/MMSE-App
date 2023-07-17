import dayjs from 'dayjs/esm';

import { ITestEntity, NewTestEntity } from './test-entity.model';

export const sampleWithRequiredData: ITestEntity = {
  id: 24273,
  createdAt: dayjs('2023-06-30T17:53'),
};

export const sampleWithPartialData: ITestEntity = {
  id: 17804,
  createdAt: dayjs('2023-07-01T08:19'),
  updatedAt: dayjs('2023-07-01T11:23'),
  score: 16745,
};

export const sampleWithFullData: ITestEntity = {
  id: 30479,
  createdAt: dayjs('2023-07-01T08:47'),
  updatedAt: dayjs('2023-06-30T14:13'),
  score: 21720,
};

export const sampleWithNewData: NewTestEntity = {
  createdAt: dayjs('2023-06-30T18:25'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
