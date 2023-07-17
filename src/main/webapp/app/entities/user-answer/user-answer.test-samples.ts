import dayjs from 'dayjs/esm';

import { QuestionId } from 'app/entities/enumerations/question-id.model';

import { IUserAnswer, NewUserAnswer } from './user-answer.model';

export const sampleWithRequiredData: IUserAnswer = {
  id: 92878,
  createdAt: dayjs('2023-07-14T12:37'),
  questionId: 'FIRST',
};

export const sampleWithPartialData: IUserAnswer = {
  id: 87624,
  answerText: 'Skyway male',
  createdAt: dayjs('2023-07-14T06:52'),
  questionId: 'FIRST',
};

export const sampleWithFullData: IUserAnswer = {
  id: 52068,
  answerText: 'quickly Vanadium',
  createdAt: dayjs('2023-07-13T22:56'),
  updatedAt: dayjs('2023-07-13T20:42'),
  questionId: 'FIRST',
};

export const sampleWithNewData: NewUserAnswer = {
  createdAt: dayjs('2023-07-13T20:33'),
  questionId: 'FIRST',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
