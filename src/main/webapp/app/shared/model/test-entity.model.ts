import { type IUser } from '@/shared/model/user.model';
import { type IUserAnswer } from '@/shared/model/user-answer.model';
import { type IPatientProfile } from '@/shared/model/patient-profile.model';

export interface ITestEntity {
  id?: number;
  createdAt?: Date;
  updatedAt?: Date | null;
  score?: number | null;
  user?: IUser;
  userAnswers?: IUserAnswer[] | null;
  patientProfile?: IPatientProfile | null;
  hash?: string;
}

export class TestEntity implements ITestEntity {
  constructor(
    public id?: number,
    public createdAt?: Date,
    public updatedAt?: Date | null,
    public score?: number | null,
    public user?: IUser,
    public userAnswers?: IUserAnswer[] | null,
    public patientProfile?: IPatientProfile | null,
    public hash?: string
  ) {
  }
}
