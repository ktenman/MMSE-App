import { QuestionId } from '@/shared/model/enumerations/question-id.model';
import { QuestionType } from '@/shared/model/enumerations/question-type.model';

export interface IQuestion {
  questionId?: QuestionId | null;
  questionText?: string;
  answerOptions?: string[] | null;
  image?: string | null;
  questionType?: QuestionType | null;
  instructions?: string[] | null;
}

export class Question implements IQuestion {
  constructor(
    public questionId?: QuestionId | null,
    public questionText?: string,
    public answerOptions?: string[] | null,
    public image?: string | null,
    public questionType?: QuestionType | null,
    public instructions?: string[] | null
  ) {
  }
}
