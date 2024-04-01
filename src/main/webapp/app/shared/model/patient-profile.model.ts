import { type ITestEntity } from '@/shared/model/test-entity.model';

export interface IPatientProfile {
  id?: number;
  patientId?: string;
  name?: string;
  createdAt?: Date;
  updatedAt?: Date;
  testEntities?: ITestEntity[] | null;
}

export class PatientProfile implements IPatientProfile {
  constructor(
    public id?: number,
    public patientId?: string,
    public name?: string,
    public createdAt?: Date,
    public updatedAt?: Date,
    public testEntities?: ITestEntity[] | null
  ) {
  }
}
