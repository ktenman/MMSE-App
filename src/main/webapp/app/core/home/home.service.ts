import axios from 'axios';
import { QuestionId } from '@/shared/model/enumerations/question-id.model';
import { IPatientProfile } from '@/shared/model/patient-profile.model';
import { IOrientationToPlaceQuestion } from '@/shared/model/orientation-to-place-question.model';
import { ITestEntity } from '@/shared/model/test-entity.model';
import { IFile } from '@/shared/model/file.model';

export default class HomeService {
  public sendImageToServer(imageBlob: Blob, questionId: QuestionId, testEntityId: number): Promise<string> {
    const formData = new FormData();
    formData.append('file', imageBlob, 'drawing.png');
    formData.append('questionId', questionId as string);
    return axios.post<IFile>(`/api/file/${testEntityId}`, formData).then(res => res.data.fileName);
  }

  public startQuiz(patientProfile: IPatientProfile): Promise<IPatientProfile> {
    return axios.post<IPatientProfile>('/api/start', patientProfile).then(res => res.data);
  }

  public getOrientationToPlaceQuestions(): Promise<IOrientationToPlaceQuestion[]> {
    return axios.get<IOrientationToPlaceQuestion[]>('/api/orientation-to-place-questions').then(res => res.data);
  }

  public getOrientationToPlaceQuestionsByPatientProfileId(patientProfileId: number): Promise<IOrientationToPlaceQuestion[]> {
    return axios.get<IOrientationToPlaceQuestion[]>(`/api/orientation-to-place-questions/${patientProfileId}`).then(res => res.data);
  }

  public saveOrientationToPlaceCorrectAnswers(
    patientProfileId: number,
    answers: IOrientationToPlaceQuestion[]
  ): Promise<IOrientationToPlaceQuestion[]> {
    return axios
      .post<IOrientationToPlaceQuestion[]>(`/api/orientation-to-place/correct-answers/${patientProfileId}`, answers)
      .then(res => res.data);
  }

  public saveOrientationToPlaceAnswerOptions(patientProfileId: number, answers: IOrientationToPlaceQuestion[]): Promise<ITestEntity> {
    return axios.post<ITestEntity>(`/api/orientation-to-place/answer-options/${patientProfileId}`, answers).then(res => res.data);
  }
}
