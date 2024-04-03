import { QuestionId } from '@/shared/model/enumerations/question-id.model';

export interface IQuestion {
  questionId?: QuestionId | null;
  questionText?: string;
  answerOptions?: string[] | null;
  image?: string | null;
}

export class Question implements IQuestion {
  constructor(
    public questionId?: QuestionId | null,
    public questionText?: string,
    public answerOptions?: string[] | null,
    public image?: string | null
  ) {
  }
}
