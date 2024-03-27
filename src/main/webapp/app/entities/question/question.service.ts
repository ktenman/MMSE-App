import axios from 'axios';
import { IQuestion } from '@/shared/model/question.model';
import { IAnswer } from '@/shared/model/answer.model';

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

  public sendAudioToServer(audioBlob: Blob) {
    const formData = new FormData();
    formData.append('audio', audioBlob, 'recording.webm');


    return axios.post('/api/upload-audio', formData)
      .then(response => {
        console.log('Audio uploaded successfully');
      })
      .catch(error => {
        console.error('Error uploading audio:', error);
      });
  }

}

