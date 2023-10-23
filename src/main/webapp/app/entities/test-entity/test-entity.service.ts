import axios from "axios";
import buildPaginationQueryOpts from "@/shared/sort/sorts";
import { ITestEntity } from "@/shared/model/test-entity.model";

const baseApiUrl = "api/test-entities";

export default class TestEntityService {
  public find(id: number): Promise<ITestEntity> {
    return axios.get<ITestEntity>(`${baseApiUrl}/${id}`).then(res => res.data);
  }

  public retrieve(paginationQuery?: any): Promise<any> {
    const url = `${baseApiUrl}?${buildPaginationQueryOpts(paginationQuery)}`;
    return axios.get<any>(url);
  }

  public delete(id: number): Promise<any> {
    return axios.delete<any>(`${baseApiUrl}/${id}`);
  }

  public create(entity: ITestEntity): Promise<ITestEntity> {
    return axios.post<ITestEntity>(baseApiUrl, entity).then(res => res.data);
  }

  public update(entity: ITestEntity): Promise<ITestEntity> {
    return axios.put<ITestEntity>(`${baseApiUrl}/${entity.id}`, entity).then(res => res.data);
  }

  public partialUpdate(entity: ITestEntity): Promise<ITestEntity> {
    return axios.patch<ITestEntity>(`${baseApiUrl}/${entity.id}`, entity).then(res => res.data);
  }
}
