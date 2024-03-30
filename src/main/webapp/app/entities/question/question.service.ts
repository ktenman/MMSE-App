import axios from 'axios';
import { IQuestion } from '@/shared/model/question.model';
import { IAnswer } from '@/shared/model/answer.model';
import { QuestionId } from '@/shared/model/enumerations/question-id.model';

export default class QuestionService {
  public getQuestion(): Promise<IQuestion> {
    return axios.get<IQuestion>('api/question').then(res => res.data);
  }

  public submitAnswer(answer: IAnswer): Promise<IQuestion | string> {
    return axios.post<IQuestion | string>("/api/answer", answer).then(res => res.data);
  }

  public retakeTest(): Promise<IQuestion | string> {
    return axios.post<IQuestion | string>("/api/retake").then(res => res.data);
  }

  public sendAudioToServer(audioBlob: Blob, questionId: QuestionId | undefined): Promise<void> {
    const formData = new FormData();
    formData.append('audio', audioBlob, 'recording.webm');
    formData.append('questionId', questionId as string);

    return axios.post('/api/upload-audio', formData)
      .then(() => {
        console.log('Audio uploaded successfully');
      })
      .catch(error => {
        console.error('Error uploading audio:', error);
      });
  }

  public getLastRecordedAudio(questionId: QuestionId): Promise<{ data: Blob, fileName: string }> {
    return axios.get('/api/last-recorded-audio', {
      params: { questionId },
      responseType: 'blob'
    })
      .then(res => {
        const contentDisposition = res.headers['content-disposition'];
        const fileName = contentDisposition ? contentDisposition.split('filename=')[1]?.split(';')[0].replace(/"/g, '') : null;
        return { data: res.data, fileName };
      });
  }

}

