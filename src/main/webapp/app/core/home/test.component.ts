import { defineComponent, onMounted, ref, watch } from 'vue';
import { useRoute } from 'vue-router';
import { IQuestion, Question } from '@/shared/model/question.model';
import { Answer, IAnswer } from '@/shared/model/answer.model';
import { QuestionType } from '@/shared/model/enumerations/question-type.model';
import { ITestEntity, TestEntity } from '@/shared/model/test-entity.model';
import TestService from '@/core/home/test.service';

const TEST_PROGRESS = 'testProgress';

export default defineComponent({
  name: 'TestView',
  computed: {
    QuestionType() {
      return QuestionType;
    },
  },
  setup() {
    const route = useRoute();
    const testEntityHash = route.params.testEntityHash as string;
    const testService = new TestService();
    const question = ref<IQuestion>(new Question());
    const selectedAnswer = ref<string | null>(null);
    const selectedAnswers = ref<Array<number | null>>([]);
    const quizEndMessage = ref<string | null>(null);
    const loading = ref(false);
    const testEntity = ref<ITestEntity>(new TestEntity());
    const errorMessage = ref<string | null>(null);
    const isRecording = ref(false);
    const lastRecordedAudioUrl = ref<string | null>(null);
    const lastRecordedAudioFileName = ref<string | null>(null);
    const recordingDuration = ref(0);
    const isPaperPickedUp = ref(false);
    const isPaperFolded = ref(false);
    const isPaperOnFloor = ref(false);
    const noAnimation = ref(false);
    const recorder = ref<MediaRecorder | null>(null);
    const audioContext = ref(null);
    const stream = ref(null);

    const closeErrorMessage = () => {
      errorMessage.value = null;
    };

    const submitAnswer = async () => {
      loading.value = true;
      if (question.value) {
        let answer: IAnswer;

        if (question.value.questionType === QuestionType.MULTIPLE_CHOICE && selectedAnswer.value) {
          answer = new Answer(selectedAnswer.value, question.value.questionId);
        } else if (question.value.questionType === QuestionType.SUBTRACTION_TASK && selectedAnswers.value) {
          const filteredAnswerText = selectedAnswers.value.filter(item => item !== null);
          answer = new Answer(filteredAnswerText.join(','), question.value.questionId);
        } else if (question.value.questionType === QuestionType.TEXT_INPUT && selectedAnswer.value) {
          answer = new Answer(selectedAnswer.value, question.value.questionId);
        } else if (question.value.questionType === QuestionType.VOICE_INPUT && lastRecordedAudioFileName.value) {
          answer = new Answer(lastRecordedAudioFileName.value, question.value.questionId);
        } else if (question.value.questionType === QuestionType.DRAG_AND_DROP) {
          const actions = [isPaperPickedUp.value, isPaperFolded.value, isPaperOnFloor.value];
          answer = new Answer(actions.join(','), question.value.questionId);
        } else {
          loading.value = false;
          return;
        }

        try {
          await testService.submitAnswer(answer, testEntity.value.id);
          if (question.value.questionType === QuestionType.DRAG_AND_DROP) {
            isPaperFolded.value = false;
            isPaperOnFloor.value = false;
            isPaperPickedUp.value = false;
          }
          await loadQuestion();
          if (quizEndMessage.value) {
            // Handle end of quiz
          }
        } catch (error) {
          errorMessage.value = error?.response?.data?.detail || 'An unexpected error occurred.';
        } finally {
          loading.value = false;
        }
      } else {
        loading.value = false;
      }
      saveTestProgress();
    };

    const loadQuestion = async () => {
      try {
        if (!testEntity.value.id) {
          await testService.getTestByTestEntityHash(testEntityHash).then(res => (testEntity.value = res));
        }
        const response = await testService.getQuestionByTestEntityHash(testEntityHash);
        if (typeof response === 'string') {
          quizEndMessage.value = response;
          question.value = null;
        } else {
          question.value = response;
          selectedAnswers.value = [];
          selectedAnswer.value = null;
          if (question.value.questionType === QuestionType.VOICE_INPUT) {
            await loadLastRecordedAudio();
          } else {
            lastRecordedAudioUrl.value = null;
          }
        }
      } catch (error) {
        errorMessage.value = error?.response?.data?.detail || 'An unexpected error occurred.';
      }
    };

    const isNextButtonDisabled = () => {
      if (question.value) {
        switch (question.value.questionType) {
          case QuestionType.MULTIPLE_CHOICE:
          case QuestionType.TEXT_INPUT:
            return !selectedAnswer.value;
          case QuestionType.SUBTRACTION_TASK:
            return selectedAnswers.value.some(answer => {
              if (typeof answer === 'string') {
                return answer === '';
              }
              return answer === null;
            });
          case QuestionType.VOICE_INPUT:
            return !lastRecordedAudioUrl.value;
          case QuestionType.DRAG_AND_DROP:
            return false;
          default:
            return true;
        }
      }
      return true;
    };

    const saveTestProgress = () => {
      const testProgress = {
        question: question.value,
        selectedAnswer: selectedAnswer.value,
        selectedAnswers: selectedAnswers.value,
        quizEndMessage: quizEndMessage.value,
        testEntity: testEntity.value,
        isPaperFolded: isPaperFolded.value,
        isPaperOnFloor: isPaperOnFloor.value,
        isPaperPickedUp: isPaperPickedUp.value
      };
      localStorage.setItem(TEST_PROGRESS, JSON.stringify(testProgress));
    };

    const loadTestProgress = () => {
      const savedProgress = localStorage.getItem(TEST_PROGRESS);
      if (savedProgress) {
        const testProgress = JSON.parse(savedProgress);
        question.value = testProgress.question;
        selectedAnswer.value = testProgress.selectedAnswer;
        selectedAnswers.value = testProgress.selectedAnswers;
        quizEndMessage.value = testProgress.quizEndMessage;
        testEntity.value = testProgress.testEntity;
        isPaperFolded.value = testProgress.isPaperFolded;
        isPaperOnFloor.value = testProgress.isPaperOnFloor;
        isPaperPickedUp.value = testProgress.isPaperPickedUp;
      }
    };

    const startRecording = async () => {
      if (!isRecording.value) {
        try {
          stream.value = await navigator.mediaDevices.getUserMedia({ audio: true });
          audioContext.value = new AudioContext();
          recorder.value = new MediaRecorder(stream.value);

          const audioChunks: Blob[] = [];
          recorder.value.ondataavailable = event => {
            audioChunks.push(event.data);
          };

          recorder.value.onstop = async () => {
            const audioBlob = new Blob(audioChunks, { type: 'audio/webm' });
            testService.sendAudioToServer(audioBlob, question.value?.questionId, testEntity.value.id).then((fileName: string) => {
              audioChunks.length = 0;
              lastRecordedAudioFileName.value = fileName;
              loadLastRecordedAudio();
            });
          };

          recorder.value.start();
          isRecording.value = true;

          setInterval(() => {
            recordingDuration.value++;
          }, 1000);
        } catch (error) {
          console.error('Error starting recording:', error);
        }
      }
    };

    const stopRecording = () => {
      if (isRecording.value && recorder.value && recorder.value.state !== 'inactive') {
        recorder.value.stop();
        stream.value?.getTracks().forEach(track => track.stop());
        isRecording.value = false;
        clearInterval(recordingDuration.value);
        recordingDuration.value = 0;
      }
    };

    const loadLastRecordedAudio = async () => {
      if (question.value?.questionId) {
        try {
          const {
            data,
            fileName
          } = await testService.getLastRecordedAudio(question.value.questionId, testEntity.value.id);
          lastRecordedAudioUrl.value = URL.createObjectURL(data);
          lastRecordedAudioFileName.value = fileName;
        } catch (error) {
          console.error('Error loading last recorded audio:', error);
        }
      }
    };

    const pickUpPaper = () => {
      isPaperPickedUp.value = true;
    };

    const foldPaper = () => {
      isPaperFolded.value = true;
    };

    const putPaperOnFloor = (event: DragEvent) => {
      event.preventDefault();
      noAnimation.value = true;
      isPaperOnFloor.value = true;
    };

    const startDragging = (event: DragEvent) => {
      event.dataTransfer?.setData('text/plain', 'paper');
    };

    onMounted(async () => {
      await loadQuestion();
      loadTestProgress();
    });

    watch(question, () => {
      saveTestProgress();
    });

    return {
      question,
      selectedAnswer,
      selectedAnswers,
      submitAnswer,
      quizEndMessage,
      isNextButtonDisabled,
      loading,
      errorMessage,
      closeErrorMessage,
      isRecording,
      lastRecordedAudioUrl,
      recordingDuration,
      startRecording,
      stopRecording,
      pickUpPaper,
      foldPaper,
      putPaperOnFloor,
      isPaperFolded,
      isPaperOnFloor,
      isPaperPickedUp,
      startDragging,
      noAnimation
    };
  },
});
