import { v4 as uuidv4 } from 'uuid';
import { QuestionId } from '@/shared/model/enumerations/question-id.model';

export interface IAnswer {
  answerText?: string;
  questionId?: QuestionId;
  idempotencyKey?: string;
}

export class Answer implements IAnswer {
  public idempotencyKey: string;

  constructor(public answerText?: string, public questionId?: QuestionId) {
    this.answerText = answerText;
    this.questionId = questionId;
    this.idempotencyKey = uuidv4();
  }
}
