import axios from 'axios';
import { IQuestion } from '@/shared/model/question.model';
import { IAnswer } from '@/shared/model/answer.model';
import { QuestionId } from '@/shared/model/enumerations/question-id.model';
import { IPatientProfile } from '@/shared/model/patient-profile.model';
import { IOrientationToPlaceQuestion } from '@/shared/model/orientation-to-place-question.model';
import { ITestEntity } from '@/shared/model/test-entity.model';

export default class QuestionService {
  public getQuestion(testEntityId: number): Promise<IQuestion> {
    return axios.get<IQuestion>(`api/question/${testEntityId}`).then(res => res.data);
  }

  public submitAnswer(answer: IAnswer, testEntityId: number): Promise<void> {
    return axios.post<void>(`/api/answer/${testEntityId}`, answer);
  }

  public sendAudioToServer(audioBlob: Blob, questionId: QuestionId | undefined, testEntityId: number): Promise<void> {
    const formData = new FormData();
    formData.append('audio', audioBlob, 'recording.webm');
    formData.append('questionId', questionId as string);

    return axios
      .post(`/api/upload-audio/${testEntityId}`, formData)
      .then(() => {
        console.log('Audio uploaded successfully');
      })
      .catch(error => {
        console.error('Error uploading audio:', error);
      });
  }

  public getLastRecordedAudio(questionId: QuestionId, testEntityId: number): Promise<{ data: Blob; fileName: string }> {
    return axios
      .get(`/api/last-recorded-audio/${testEntityId}`, {
        params: { questionId },
        responseType: 'blob'
      })
      .then(res => {
        const contentDisposition = res.headers['content-disposition'];
        const fileName = contentDisposition ? contentDisposition.split('filename=')[1]?.split(';')[0].replace(/"/g, '') : null;
        return { data: res.data, fileName };
      });
  }

  public startQuiz(patientProfile: IPatientProfile): Promise<IPatientProfile> {
    return axios.post<IPatientProfile>('/api/start', patientProfile).then(res => res.data);
  }

  public getOrientationToPlaceQuestions(): Promise<IOrientationToPlaceQuestion[]> {
    return axios.get<IOrientationToPlaceQuestion[]>('/api/orientation-to-place-questions')
      .then(res => res.data);
  }

  public getOrientationToPlaceQuestionsByPatientProfileId(patientProfileId: number): Promise<IOrientationToPlaceQuestion[]> {
    return axios.get<IOrientationToPlaceQuestion[]>(`/api/orientation-to-place-questions/${patientProfileId}`)
      .then(res => res.data);
  }

  public saveOrientationToPlaceAnswers(patientProfileId: number, answers: IOrientationToPlaceQuestion[]): Promise<ITestEntity> {
    return axios.post(`/api/save-orientation-to-place-answers/${patientProfileId}`, answers).then(res => res.data);
  }

}
