import { QuestionId } from '@/shared/model/enumerations/question-id.model';

export interface IOrientationToPlaceQuestion {
  questionId?: QuestionId;
  questionText?: string;
  correctAnswer?: string;
  answerOptions?: string;
}

export class OrientationToPlaceQuestion implements IOrientationToPlaceQuestion {
  constructor(
    public questionId?: QuestionId,
    public questionText?: string,
    public correctAnswer?: string,
    public answerOptions?: string
  ) {
  }
}
