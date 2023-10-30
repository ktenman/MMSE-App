import { IUser } from '@/shared/model/user.model';

export interface ITestEntity {
  id?: number;
  createdAt?: Date;
  updatedAt?: Date | null;
  score?: number | null;
  user?: IUser;
}

export class TestEntity implements ITestEntity {
  constructor(
    public id?: number,
    public createdAt?: Date,
    public updatedAt?: Date | null,
    public score?: number | null,
    public user?: IUser
  ) {}
}
