import axios from 'axios';
import { IQuestion } from '@/shared/model/question.model';
import { IAnswer } from '@/shared/model/answer.model';
import { QuestionId } from '@/shared/model/enumerations/question-id.model';
import { ITestEntity } from '@/shared/model/test-entity.model';
import { IFile } from '@/shared/model/file.model';

export default class TestService {
  public getQuestion(testEntityId: number): Promise<IQuestion> {
    return axios.get<IQuestion>(`/api/test/question/${testEntityId}`).then(res => res.data);
  }

  public submitAnswer(answer: IAnswer, testEntityId: number): Promise<void> {
    return axios.post<void>(`/api/test/answer/${testEntityId}`, answer);
  }

  public sendAudioToServer(audioBlob: Blob, questionId: QuestionId | undefined, testEntityId: number): Promise<string> {
    const formData = new FormData();
    formData.append('file', audioBlob, 'recording.webm');
    formData.append('questionId', questionId as string);
    return axios.post<IFile>(`/api/test/file/${testEntityId}`, formData).then(res => res.data.fileName);
  }

  public sendImageToServer(imageBlob: Blob, questionId: QuestionId, testEntityId: number): Promise<string> {
    const formData = new FormData();
    formData.append('file', imageBlob, 'drawing.png');
    formData.append('questionId', questionId as string);
    return axios.post<IFile>(`/api/test/file/${testEntityId}`, formData).then(res => res.data.fileName);
  }

  public getLastRecordedAudio(questionId: QuestionId, testEntityId: number): Promise<{ data: Blob; fileName: string }> {
    return axios
      .get(`/api/test/file/${testEntityId}`, {
        params: { questionId },
        responseType: 'blob'
      })
      .then(res => {
        const contentDisposition = res.headers['content-disposition'];
        const fileName = contentDisposition ? contentDisposition.split('filename=')[1]?.split(';')[0].replace(/"/g, '') : null;
        return { data: res.data, fileName };
      });
  }

  public getQuestionByTestEntityHash(testEntityHash: string): Promise<IQuestion> {
    return axios.get<IQuestion>(`/api/test/question/${testEntityHash}`).then(res => res.data);
  }

  public getTestByTestEntityHash(testEntityHash: string): Promise<ITestEntity> {
    return axios.get<ITestEntity>(`/api/test/test-entity/${testEntityHash}`).then(res => res.data);
  }

}
