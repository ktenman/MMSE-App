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
      recordingTimer
    ] = [
      inject<LoginService>("loginService"),
      inject<ComputedRef<boolean>>("authenticated"),
      inject<ComputedRef<string>>("currentUsername"),
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
      ref(null)
    ];

    const questionService = new QuestionService();

    const openLogin = () => loginService.openLogin();

    const startRecording = async () => {
      if (!isRecording.value) {
        try {
          stream.value = await navigator.mediaDevices.getUserMedia({ audio: true });
          audioContext.value = new AudioContext();
          recorder.value = new MediaRecorder(stream.value);

          const audioChunks: Blob[] = [];
          recorder.value.ondataavailable = (event) => {
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
      if (typeof answerText === "string") {
        return new Answer(answerText, questionId);
      } else if (Array.isArray(answerText) && answerText.length > 0) {
        // Filter out null values before joining
        const filteredAnswerText = answerText.filter(item => item !== null);
        return new Answer(filteredAnswerText.join(","), questionId);
      } else {
        // Handle the case where answerText is null or an empty array
        // You might want to return a default Answer or throw an error
        return new Answer("", questionId); // or throw new Error("answerText is null or empty");
      }
    };

    const submitAnswer = async () => {
      // Start loading
      loading.value = true;

      if (question.value) {
        let answer: IAnswer;

        switch (question.value.questionType) {
          case QuestionType.MULTIPLE_CHOICE:
          case QuestionType.TEXT_INPUT:
            if (selectedAnswer.value) {
              answer = createAnswer(selectedAnswer.value, question.value.questionId as QuestionId);
            }
            break;
          case QuestionType.SUBTRACTION_TASK:
            if (selectedAnswers.value.every(answer => answer !== null && answer.toString() !== '')) {
              answer = createAnswer(selectedAnswers.value, question.value.questionId as QuestionId);
            }
            break;
          case QuestionType.VOICE_INPUT:
            if (lastRecordedAudioUrl.value) {
              answer = createAnswer(lastRecordedAudioFileName.value, question.value.questionId as QuestionId);
            }
            break;
          default:
            loading.value = false;
            return;
        }

        if (answer) {
          try {
            const response = await questionService.submitAnswer(answer);

            if (typeof response === 'string') {
              quizEndMessage.value = response;
              question.value = null;
            } else {
              question.value = response;
              selectedAnswers.value = [];
              selectedAnswer.value = null;
              lastRecordedAudioUrl.value = null;
            }
          } catch (error) {
            console.error('Error submitting answer:', error);
          } finally {
            loading.value = false;
          }
        } else {
          loading.value = false;
        }
      } else {
        loading.value = false;
      }
    };

    const retakeTest = async () => {
      const response = await questionService.retakeTest();

      if (typeof response === "string") {
        quizEndMessage.value = response;
      } else {
        question.value = response;
        quizEndMessage.value = null; // clear end message
        selectedAnswers.value = []; // clear selected answer, initialize to empty array instead of null
      }
    };

    const loadQuestion = async () => {
      const response = await questionService.getQuestion();
      if (typeof response === "string") {
        quizEndMessage.value = response;
        question.value = null;
      } else {
        question.value = response;
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
          default:
            return true; // Disabled for any other case
        }
      }
      return true; // Disabled if there's no question
    };

    (async () => {
      watch(authenticated, async (newVal) => {
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
      recordingDuration
    };
  }
});
