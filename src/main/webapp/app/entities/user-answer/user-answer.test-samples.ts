import dayjs from 'dayjs/esm';

import { QuestionId } from 'app/entities/enumerations/question-id.model';

import { IUserAnswer, NewUserAnswer } from './user-answer.model';

export const sampleWithRequiredData: IUserAnswer = {
  id: 30434,
  createdAt: dayjs('2023-07-14T12:37'),
  questionId: 'FIRST',
};

export const sampleWithPartialData: IUserAnswer = {
  id: 28712,
  createdAt: dayjs('2023-07-14T02:53'),
  updatedAt: dayjs('2023-07-14T11:21'),
  questionId: 'FIRST',
};

export const sampleWithFullData: IUserAnswer = {
  id: 3081,
  answerText: 'olive male',
  createdAt: dayjs('2023-07-14T06:52'),
  updatedAt: dayjs('2023-07-14T02:27'),
  questionId: 'FIRST',
};

export const sampleWithNewData: NewUserAnswer = {
  createdAt: dayjs('2023-07-14T01:23'),
  questionId: 'FIRST',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
