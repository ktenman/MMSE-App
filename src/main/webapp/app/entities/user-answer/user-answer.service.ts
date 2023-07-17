import axios from 'axios';
import buildPaginationQueryOpts from '@/shared/sort/sorts';
import {IUserAnswer} from '@/shared/model/user-answer.model';

const baseApiUrl = 'api/user-answers';

export default class UserAnswerService {
  public find(id: number): Promise<IUserAnswer> {
    return axios.get<IUserAnswer>(`${baseApiUrl}/${id}`).then(res => res.data);
  }

  public retrieve(paginationQuery?: any): Promise<any> {
    const url = `${baseApiUrl}?${buildPaginationQueryOpts(paginationQuery)}`;
    return axios.get<any>(url);
  }

  public delete(id: number): Promise<any> {
    return axios.delete<any>(`${baseApiUrl}/${id}`);
  }

  public create(entity: IUserAnswer): Promise<IUserAnswer> {
    return axios.post<IUserAnswer>(baseApiUrl, entity).then(res => res.data);
  }

  public update(entity: IUserAnswer): Promise<IUserAnswer> {
    return axios.put<IUserAnswer>(`${baseApiUrl}/${entity.id}`, entity).then(res => res.data);
  }

  public partialUpdate(entity: IUserAnswer): Promise<IUserAnswer> {
    return axios.patch<IUserAnswer>(`${baseApiUrl}/${entity.id}`, entity).then(res => res.data);
  }
}
