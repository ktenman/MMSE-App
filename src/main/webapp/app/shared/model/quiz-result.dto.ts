// src/shared/model/quiz-result.dto.ts
export interface IQuestionResultDTO {
  questionText: string;
  userAnswer: string;
  correctAnswer: string;
  correct: boolean;
  score: number;
  maxScore: number;
}

export class QuestionResultDTO implements IQuestionResultDTO {
  constructor(
    public questionText: string,
    public userAnswer: string,
    public correctAnswer: string,
    public correct: boolean,
    public score: number,
    public maxScore: number
  ) {
  }
}

export interface IQuizResultDTO {
  score: number;
  maxScore: number;
  questionResults: { [key: string]: IQuestionResultDTO };
  duration: number;
}

export class QuizResultDTO implements IQuizResultDTO {
  constructor(
    public score: number,
    public maxScore: number,
    public questionResults: { [key: string]: IQuestionResultDTO },
    public duration: number
  ) {
  }
}
