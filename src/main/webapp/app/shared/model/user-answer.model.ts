import { ITestEntity } from '@/shared/model/test-entity.model';

import { QuestionId } from '@/shared/model/enumerations/question-id.model';
export interface IUserAnswer {
  id?: number;
  answerText?: string | null;
  createdAt?: Date;
  updatedAt?: Date | null;
  questionId?: keyof typeof QuestionId;
  testEntity?: ITestEntity;
}

export class UserAnswer implements IUserAnswer {
  constructor(
    public id?: number,
    public answerText?: string | null,
    public createdAt?: Date,
    public updatedAt?: Date | null,
    public questionId?: keyof typeof QuestionId,
    public testEntity?: ITestEntity
  ) {}
}
