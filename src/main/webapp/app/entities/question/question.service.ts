import axios from 'axios';
import {IQuestion} from '@/shared/model/question.model';

const baseApiUrl = 'api/question';

export default class QuestionService {
  public getQuestion(): Promise<IQuestion> {
    return axios.get<IQuestion>(baseApiUrl).then(res => res.data);
  }
}
