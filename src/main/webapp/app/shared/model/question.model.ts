export interface IQuestion {
  questionId?: string;
  questionText?: string;
  answerOptions?: string[] | null;
  image?: string | null;
}

export class Question implements IQuestion {
  constructor(
    public questionId?: string,
    public questionText?: string,
    public answerOptions?: string[] | null,
    public image?: string | null
  ) {}
}
