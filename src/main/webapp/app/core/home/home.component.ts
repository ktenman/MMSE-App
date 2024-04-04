import { ComputedRef, defineComponent, inject, onBeforeMount, onMounted, ref, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import LoginService from '@/account/login.service';
import { IQuestion } from '@/shared/model/question.model';
import QuestionService from '@/entities/question/question.service';
import { Answer, IAnswer } from '@/shared/model/answer.model';
import { QuestionId } from '@/shared/model/enumerations/question-id.model';
import { QuestionType } from '@/shared/model/enumerations/question-type.model';
import { IPatientProfile, PatientProfile } from '@/shared/model/patient-profile.model';
import { IOrientationToPlaceQuestion } from '@/shared/model/orientation-to-place-question.model';
import { QuizState } from '@/shared/model/enumerations/quiz-state.mode';
import { ITestEntity, TestEntity } from '@/shared/model/test-entity.model';
import DrawingCanvas from '@/core/home/drawing-canvas.vue';
import { useRouter } from 'vue-router';
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome';

const QUIZ_PROGRESS = 'quizProgress';

export default defineComponent({
  name: 'Home',
  components: {
    FontAwesomeIcon,
    DrawingCanvas
  },
  computed: {
    QuestionType() {
      return QuestionType;
    },
    QuizState() {
      return QuizState;
    }
  },
  compatConfig: { MODE: 3 },
  setup() {
    const errorMessage = ref<string | null>(null);
    const orientationToPlaceQuestions = ref<IOrientationToPlaceQuestion[]>([]);
    const loginService = inject<LoginService>('loginService');
    const authenticated = inject<ComputedRef<boolean>>('authenticated');
    const username = inject<ComputedRef<string>>('currentUsername');
    const question = ref<IQuestion | null>(null);
    const selectedAnswer = ref<string | null>(null);
    const selectedAnswers = ref<Array<number | null>>([]);
    const quizEndMessage = ref<string | null>(null);
    const loading = ref(false);
    const isRecording = ref(false);
    const audioContext = ref(null);
    const recorder = ref(null);
    const stream = ref(null);
    const lastRecordedAudioUrl = ref<string | null>(null);
    const lastRecordedAudioFileName = ref<string | null>(null);
    const audioPlayer = ref<HTMLAudioElement | null>(null);
    const recordingDuration = ref(0);
    const recordingTimer = ref(null);
    const isPaperPickedUp = ref(false);
    const isPaperFolded = ref(false);
    const isPaperOnFloor = ref(false);
    const noAnimation = ref(false);
    const patientProfile = ref<IPatientProfile>(new PatientProfile());
    const quizState = ref(QuizState.PATIENT_INFO);
    const testEntity = ref<ITestEntity>(new TestEntity());
    const questionService = new QuestionService();
    const drawingFileName = ref<string | null>(null);
    const router = useRouter();
    const testLink = ref('');

    const closeErrorMessage = () => {
      errorMessage.value = null;
    };

    const updateDrawingFileName = (fileName: string) => {
      drawingFileName.value = fileName;
    };

    const navigateBack = () => {
      switch (quizState.value) {
        case QuizState.ORIENTATION_ANSWERS:
          quizState.value = QuizState.ORIENTATION_QUESTIONS;
          break;
        case QuizState.ORIENTATION_QUESTIONS:
          quizState.value = QuizState.PATIENT_INFO;
          break;
        default:
          break;
      }
      saveQuizProgress();
    };

    const copyTestLink = () => {
      navigator.clipboard.writeText(testLink.value)
        .then(() => {
          console.log('Test link copied to clipboard');
        })
        .catch((error) => {
          console.error('Failed to copy test link:', error);
        });
    };

    const loadOrientationToPlaceQuestions = async () => {
      try {
        if (patientProfile.value.id && patientProfile.value.id > 0) {
          orientationToPlaceQuestions.value = await questionService.getOrientationToPlaceQuestionsByPatientProfileId(
            patientProfile.value.id
          );
        } else {
          orientationToPlaceQuestions.value = await questionService.getOrientationToPlaceQuestions();
        }
      } catch (error) {
        console.error('Error loading orientation to place questions:', error);
      }
    };

    const retakeQuiz = () => {
      localStorage.removeItem(QUIZ_PROGRESS);
      quizState.value = QuizState.PATIENT_INFO;
      patientProfile.value = new PatientProfile();
      question.value = null;
      selectedAnswer.value = null;
      selectedAnswers.value = [];
      quizEndMessage.value = null;
      isPaperFolded.value = false;
      isPaperOnFloor.value = false;
      isPaperPickedUp.value = false;
    };

    const persistAnswer = (question: IOrientationToPlaceQuestion) => {
      if (question.correctAnswer && question.correctAnswer.length >= 3) {
        questionService.saveOrientationToPlaceCorrectAnswers(patientProfile.value.id, orientationToPlaceQuestions.value);
      }
    };

    const saveOrientationToPlaceCorrectAnswers = async () => {
      try {
        const response = await questionService.saveOrientationToPlaceCorrectAnswers(
          patientProfile.value.id, orientationToPlaceQuestions.value);
        orientationToPlaceQuestions.value = response;
        console.log('Orientation to place correct answers saved successfully');
        quizState.value = QuizState.ORIENTATION_ANSWERS;
        saveQuizProgress();
      } catch (error) {
        console.error('Error saving orientation to place correct answers:', error);
      }
    };

    const saveOrientationToPlaceAnswerOptions = async () => {
      try {
        const response = await questionService.saveOrientationToPlaceAnswerOptions(
          patientProfile.value.id, orientationToPlaceQuestions.value);
        testEntity.value = response;
        console.log('Orientation to place answer options saved successfully');
        quizState.value = QuizState.SHOW_TEST_LINK;
        const baseUrl = window.location.origin;
        testLink.value = baseUrl + router.resolve({
          name: 'TestView',
          params: { testEntityHash: testEntity.value.hash }
        }).href;
        await loadQuestion();
        saveQuizProgress();
      } catch (error) {
        errorMessage.value = error?.response?.data ? error.response.data.detail : 'An unexpected error occurred.';
      }
    };

    const startQuiz = async () => {
      if (!patientProfile.value.patientId || !patientProfile.value.name) {
        console.error('Patient ID and name are required to start the quiz');
        return;
      }

      try {
        const response = await questionService.startQuiz(patientProfile.value);
        patientProfile.value = response;
        loadOrientationToPlaceQuestions().then(response => {
          quizState.value = QuizState.ORIENTATION_QUESTIONS;
          saveQuizProgress();
        });
      } catch (error) {
        console.error('Error starting quiz:', error);
      }
    };

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
            questionService.sendAudioToServer(audioBlob, question.value?.questionId, testEntity.value.id)
              .then((fileName: string) => {
                audioChunks.length = 0;
                lastRecordedAudioFileName.value = fileName;
                loadLastRecordedAudio();
              });
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
          const {
            data,
            fileName
          } = await questionService.getLastRecordedAudio(question.value.questionId, testEntity.value.id);
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
          await questionService.submitAnswer(answer, testEntity.value.id);
          if (question.value.questionType === QuestionType.DRAG_AND_DROP) {
            isPaperFolded.value = false;
            isPaperOnFloor.value = false;
            isPaperPickedUp.value = false;
          }
          await loadQuestion(); // Load the next question after submitting the answer
          if (quizEndMessage.value) {
            quizState.value = QuizState.FINISHED;
          }
        } catch (error) {
          errorMessage.value = error?.response?.data ? error.response.data.detail : 'An unexpected error occurred.';
        } finally {
          loading.value = false;
        }
      } else {
        // If there's no question, stop loading
        loading.value = false;
      }
      saveQuizProgress();
    };

    const loadQuestion = async () => {
      if (patientProfile.value.id) {
        const response = await questionService.getQuestion(testEntity.value.id);
        if (typeof response === 'string') {
          quizEndMessage.value = response;
          question.value = null;
          quizState.value = QuizState.FINISHED;
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

    const saveQuizProgress = () => {
      const quizProgress = {
        quizState: quizState.value,
        patientProfile: patientProfile.value,
        question: question.value,
        selectedAnswer: selectedAnswer.value,
        selectedAnswers: selectedAnswers.value,
        isPaperFolded: isPaperFolded.value,
        isPaperOnFloor: isPaperOnFloor.value,
        isPaperPickedUp: isPaperPickedUp.value,
        quizEndMessage: quizEndMessage.value,
        testEntity: testEntity.value
      };
      localStorage.setItem(QUIZ_PROGRESS, JSON.stringify(quizProgress));
    };

    const loadQuizProgress = () => {
      const savedProgress = localStorage.getItem(QUIZ_PROGRESS);
      if (savedProgress) {
        const quizProgress = JSON.parse(savedProgress);
        quizState.value = quizProgress.quizState;
        patientProfile.value = quizProgress.patientProfile;
        question.value = quizProgress.question;
        selectedAnswer.value = quizProgress.selectedAnswer;
        selectedAnswers.value = quizProgress.selectedAnswers;
        isPaperFolded.value = quizProgress.isPaperFolded;
        isPaperOnFloor.value = quizProgress.isPaperOnFloor;
        isPaperPickedUp.value = quizProgress.isPaperPickedUp;
        quizEndMessage.value = quizProgress.quizEndMessage;
        testEntity.value = quizProgress.testEntity;
        if (!testLink.value) {
          const baseUrl = window.location.origin;
          testLink.value = baseUrl + router.resolve({
            name: 'TestView',
            params: { testEntityHash: testEntity.value.hash }
          }).href;
        }
      }
    };

    (async () => {
      watch(authenticated, async newVal => {
        if (newVal === true && patientProfile.value.id) {
          await loadQuestion();
        }
      });

      onBeforeMount(() => {
        loadQuizProgress();
      });

      onMounted(async () => {
        if (authenticated.value && quizState.value === QuizState.QUIZ) {
          await loadQuestion();
        }
        await loadOrientationToPlaceQuestions();
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
      noAnimation,
      patientProfile,
      startQuiz,
      orientationToPlaceQuestions,
      saveOrientationToPlaceCorrectAnswers,
      saveOrientationToPlaceAnswerOptions,
      quizState,
      retakeQuiz,
      persistAnswer,
      errorMessage,
      closeErrorMessage,
      testEntity,
      drawingFileName,
      updateDrawingFileName,
      navigateBack,
      testLink,
      copyTestLink
    };
  }
});
