import dayjs from 'dayjs/esm';
import { ITestEntity } from 'app/entities/test-entity/test-entity.model';
import { QuestionId } from 'app/entities/enumerations/question-id.model';

export interface IUserAnswer {
  id: number;
  answerText?: string | null;
  createdAt?: dayjs.Dayjs | null;
  updatedAt?: dayjs.Dayjs | null;
  questionId?: keyof typeof QuestionId | null;
  testEntity?: Pick<ITestEntity, 'id'> | null;
}

export type NewUserAnswer = Omit<IUserAnswer, 'id'> & { id: null };
