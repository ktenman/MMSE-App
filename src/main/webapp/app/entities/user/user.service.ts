import axios from 'axios';

const baseApiUrl = 'api/users';

export default class UserService {
  public retrieve(): Promise<any> {
    return axios.get<any>(baseApiUrl);
  }
}
