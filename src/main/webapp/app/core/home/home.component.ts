import { ComputedRef, defineComponent, inject, onMounted, ref, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import LoginService from '@/account/login.service';
import { IQuestion } from '@/shared/model/question.model';
import QuestionService from '@/entities/question/question.service';
import { Answer, IAnswer } from '@/shared/model/answer.model';
import { QuestionId } from '@/shared/model/enumerations/question-id.model';
import { QuestionType } from '@/shared/model/enumerations/question-type.model';

export default defineComponent({
  computed: {
    QuestionType() {
      return QuestionType;
    }
  },
  compatConfig: { MODE: 3 },
  setup() {
    const [
      loginService,
      authenticated,
      username,
      question,
      selectedAnswer,
      selectedAnswers,
      quizEndMessage,
      loading,
      isRecording,
      audioContext,
      recorder,
      stream,
      lastRecordedAudioUrl,
      lastRecordedAudioFileName,
      audioPlayer,
      recordingDuration,
      recordingTimer,
      isPaperPickedUp,
      isPaperFolded,
      isPaperOnFloor,
      noAnimation
    ] = [
      inject<LoginService>('loginService'),
      inject<ComputedRef<boolean>>('authenticated'),
      inject<ComputedRef<string>>('currentUsername'),
      ref<IQuestion | null>(null),
      ref<string | null>(null),
      ref<Array<number | null>>([]),
      ref<string | null>(null),
      ref(false),
      ref(false),
      ref(null),
      ref(null),
      ref(null),
      ref<string | null>(null),
      ref<string | null>(null),
      ref<HTMLAudioElement | null>(null),
      ref(0),
      ref(null),
      ref(false),
      ref(false),
      ref(false),
      ref(false)
    ];

    const questionService = new QuestionService();

    const openLogin = () => loginService.openLogin();

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
            await questionService.sendAudioToServer(audioBlob, question.value?.questionId);
            audioChunks.length = 0;
            await loadLastRecordedAudio();
          };

          recorder.value.start();
          isRecording.value = true;

          const timer = setInterval(() => {
            recordingDuration.value++;
          }, 1000);

          recordingTimer.value = timer;
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
        clearInterval(recordingTimer.value);
        recordingDuration.value = 0;
      }
    };

    const loadLastRecordedAudio = async () => {
      if (question.value?.questionId) {
        try {
          const { data, fileName } = await questionService.getLastRecordedAudio(question.value.questionId);
          lastRecordedAudioUrl.value = URL.createObjectURL(data);
          lastRecordedAudioFileName.value = fileName;
        } catch (error) {
          console.error('Error loading last recorded audio:', error);
        }
      }
    };

    const createAnswer = (answerText: string | Array<number | null>, questionId: QuestionId): IAnswer => {
      if (typeof answerText === 'string') {
        return new Answer(answerText, questionId);
      } else if (Array.isArray(answerText) && answerText.length > 0) {
        // Filter out null values before joining
        const filteredAnswerText = answerText.filter(item => item !== null);
        return new Answer(filteredAnswerText.join(','), questionId);
      } else {
        return new Answer('', questionId);
      }
    };

    const submitAnswer = async () => {
      loading.value = true;
      if (question.value) {
        let answer: IAnswer;

        if (question.value.questionType === QuestionType.MULTIPLE_CHOICE && selectedAnswer.value ||
          question.value.questionType === QuestionType.TEXT_INPUT && selectedAnswer.value) {
          answer = createAnswer(selectedAnswer.value, question.value.questionId as QuestionId);
        } else if (question.value.questionType === QuestionType.SUBTRACTION_TASK && selectedAnswers.value) {
          answer = createAnswer(selectedAnswers.value, question.value.questionId as QuestionId);
        } else if (question.value.questionType === QuestionType.VOICE_INPUT && lastRecordedAudioUrl.value) {
          answer = createAnswer(lastRecordedAudioFileName.value, question.value.questionId as QuestionId);
        } else if (question.value.questionType === QuestionType.DRAG_AND_DROP) {
          const actions = [
            isPaperPickedUp.value,
            isPaperFolded.value,
            isPaperOnFloor.value
          ];
          answer = createAnswer(actions.join(','), question.value.questionId as QuestionId);
        } else {
          loading.value = false;
          return;
        }

        try {
          await questionService.submitAnswer(answer);
          await loadQuestion(); // Load the next question after submitting the answer
        } catch (error) {
          console.error('Error submitting answer:', error);
          // Optionally set an error message to display to the user
        } finally {
          loading.value = false;
        }
      } else {
        // If there's no question, stop loading
        loading.value = false;
      }
    };

    const retakeTest = async () => {
      isPaperPickedUp.value = false;
      isPaperFolded.value = false;
      isPaperOnFloor.value = false;
      const response = await questionService.retakeTest();

      if (typeof response === 'string') {
        quizEndMessage.value = response;
      } else {
        question.value = response;
        quizEndMessage.value = null; // clear end message
        selectedAnswers.value = []; // clear selected answer, initialize to empty array instead of null
      }
    };

    const loadQuestion = async () => {
      const response = await questionService.getQuestion();
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
    };

    const isNextButtonDisabled = () => {
      if (question.value) {
        switch (question.value.questionType) {
          case QuestionType.MULTIPLE_CHOICE:
          case QuestionType.TEXT_INPUT:
            return !selectedAnswer.value; // Disabled if selectedAnswer is null or empty
          case QuestionType.SUBTRACTION_TASK:
            return selectedAnswers.value.some(answer => answer === null || answer === ''); // Disabled if any selectedAnswers element is null or empty
          case QuestionType.VOICE_INPUT:
            return !lastRecordedAudioUrl.value; // Disabled if lastRecordedAudioUrl is null or empty
          case QuestionType.DRAG_AND_DROP:
            return false; // Always enabled for drag and drop
          default:
            return true; // Disabled for any other case
        }
      }
      return true; // Disabled if there's no question
    };

    (async () => {
      watch(authenticated, async newVal => {
        if (newVal === true) {
          await loadQuestion();
        }
      });

      onMounted(async () => {
        if (authenticated.value) {
          await loadQuestion();
        }
      });
    })();

    return {
      authenticated,
      username,
      openLogin,
      t$: useI18n().t,
      question,
      selectedAnswer,
      selectedAnswers,
      submitAnswer,
      quizEndMessage,
      retakeTest,
      isNextButtonDisabled,
      loading,
      audioContext,
      recorder,
      stream,
      startRecording,
      stopRecording,
      isRecording,
      lastRecordedAudioUrl,
      lastRecordedAudioFileName,
      audioPlayer,
      loadLastRecordedAudio,
      recordingDuration,
      pickUpPaper,
      foldPaper,
      putPaperOnFloor,
      isPaperFolded,
      isPaperOnFloor,
      isPaperPickedUp,
      startDragging,
      noAnimation
    };
  }
});
