import { QuestionId } from '@/shared/model/enumerations/question-id.model';

export interface IAnswer {
  answerText?: string;
  questionId?: QuestionId;
}

export class Answer implements IAnswer {
  constructor(public answerText?: string, public questionId?: QuestionId) {
  }
}
