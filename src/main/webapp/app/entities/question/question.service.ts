import axios from 'axios';
import { IQuestion } from '@/shared/model/question.model';
import { IAnswer } from '@/shared/model/answer.model';
import { QuestionId } from '@/shared/model/enumerations/question-id.model';
import { IPatientProfile } from '@/shared/model/patient-profile.model';
import { IOrientationToPlaceQuestion } from '@/shared/model/orientation-to-place-question.model';

export default class QuestionService {
  public getQuestion(): Promise<IQuestion> {
    return axios.get<IQuestion>('api/question').then(res => res.data);
  }

  public submitAnswer(answer: IAnswer): Promise<void> {
    return axios.post<void>('/api/answer', answer);
  }

  public sendAudioToServer(audioBlob: Blob, questionId: QuestionId | undefined): Promise<void> {
    const formData = new FormData();
    formData.append('audio', audioBlob, 'recording.webm');
    formData.append('questionId', questionId as string);

    return axios
      .post('/api/upload-audio', formData)
      .then(() => {
        console.log('Audio uploaded successfully');
      })
      .catch(error => {
        console.error('Error uploading audio:', error);
      });
  }

  public getLastRecordedAudio(questionId: QuestionId): Promise<{ data: Blob; fileName: string }> {
    return axios
      .get('/api/last-recorded-audio', {
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

  public async saveOrientationToPlaceAnswers(patientProfileId: number, answers: IOrientationToPlaceQuestion[]): Promise<void> {
    try {
      await axios.post(`/api/save-orientation-to-place-answers/${patientProfileId}`, answers);
    } catch (error) {
      throw new Error('Error saving orientation to place answers');
    }
  }

}
