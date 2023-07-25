import axios from 'axios';
import {IQuestion} from '@/shared/model/question.model';
import {IAnswer} from "@/shared/model/answer.model";

const baseApiUrl = 'api/question';

export default class QuestionService {
  public getQuestion(): Promise<IQuestion> {
    return axios.get<IQuestion>(baseApiUrl).then(res => res.data);
  }

  public submitAnswer(answer: IAnswer): Promise<IQuestion | string> {
    return axios.post<IQuestion | string>('/api/answer', answer).then(res => res.data);
  }

}

