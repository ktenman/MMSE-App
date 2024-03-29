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
      stream
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

          recorder.value.onstop = () => {
            const audioBlob = new Blob(audioChunks, { type: 'audio/webm' });
            questionService.sendAudioToServer(audioBlob);
            audioChunks.length = 0;
          };

          recorder.value.start();
          isRecording.value = true;
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

        if (question.value.questionType === QuestionType.MULTIPLE_CHOICE && selectedAnswer.value ||
            question.value.questionType === QuestionType.TEXT_INPUT && selectedAnswers.value) {
          answer = createAnswer(selectedAnswer.value, question.value.questionId as QuestionId);
        } else if (question.value.questionType === QuestionType.SUBTRACTION_TASK && selectedAnswers.value) {
          answer = createAnswer(selectedAnswers.value, question.value.questionId as QuestionId);
        } else {
          // Neither condition met - possibly display an error message here
          // Stop loading and exit if no answer is provided
          loading.value = false;
          return;
        }

        try {
          const response = await questionService.submitAnswer(answer);

          if (typeof response === "string") {
            quizEndMessage.value = response;
            question.value = null;
          } else {
            question.value = response;
            selectedAnswers.value = [];
            selectedAnswer.value = null;
          }
        } catch (error) {
          // Handle any errors here
          console.error('Error submitting answer:', error);
          // Optionally set an error message to display to the user
        } finally {
          // Stop loading regardless of the outcome
          loading.value = false;
        }
      } else {
        // If there's no question, stop loading
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
      isRecording
    };
  }
});
