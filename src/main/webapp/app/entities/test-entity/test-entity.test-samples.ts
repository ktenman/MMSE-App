import dayjs from 'dayjs/esm';

import { ITestEntity, NewTestEntity } from './test-entity.model';

export const sampleWithRequiredData: ITestEntity = {
  id: 48916,
  createdAt: dayjs('2023-07-01T01:33'),
};

export const sampleWithPartialData: ITestEntity = {
  id: 79401,
  createdAt: dayjs('2023-07-01T06:00'),
};

export const sampleWithFullData: ITestEntity = {
  id: 44651,
  createdAt: dayjs('2023-07-01T05:20'),
  updatedAt: dayjs('2023-06-30T21:14'),
  score: 34215,
};

export const sampleWithNewData: NewTestEntity = {
  createdAt: dayjs('2023-07-01T00:11'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
