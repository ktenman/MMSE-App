import dayjs from 'dayjs/esm';
import { IUser } from 'app/entities/user/user.model';

export interface ITestEntity {
  id: number;
  createdAt?: dayjs.Dayjs | null;
  updatedAt?: dayjs.Dayjs | null;
  score?: number | null;
  user?: Pick<IUser, 'id' | 'login'> | null;
}

export type NewTestEntity = Omit<ITestEntity, 'id'> & { id: null };
