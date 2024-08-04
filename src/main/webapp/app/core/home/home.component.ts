import { ComputedRef, defineComponent, inject, onBeforeMount, onMounted, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import LoginService from '@/account/login.service';
import HomeService from '@/core/home/home.service';
import { QuestionType } from '@/shared/model/enumerations/question-type.model';
import { IPatientProfile, PatientProfile } from '@/shared/model/patient-profile.model';
import { IOrientationToPlaceQuestion } from '@/shared/model/orientation-to-place-question.model';
import { QuizState } from '@/shared/model/enumerations/quiz-state.mode';
import { ITestEntity, TestEntity } from '@/shared/model/test-entity.model';
import { useRouter } from 'vue-router';
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome';

const QUIZ_PROGRESS = 'quizProgress';

export default defineComponent({
  name: 'Home',
  components: {
    FontAwesomeIcon
  },
  computed: {
    QuestionType() {
      return QuestionType;
    },
    QuizState() {
      return QuizState;
    },
  },
  compatConfig: { MODE: 3 },
  setup() {
    const errorMessage = ref<string | null>(null);
    const orientationToPlaceQuestions = ref<IOrientationToPlaceQuestion[]>([]);
    const loginService = inject<LoginService>('loginService');
    const authenticated = inject<ComputedRef<boolean>>('authenticated');
    const username = inject<ComputedRef<string>>('currentUsername');
    const loading = ref(false);
    const quizState = ref<QuizState>(QuizState.PATIENT_INFO);
    const patientProfile = ref<IPatientProfile>(new PatientProfile());
    const router = useRouter();
    const homeService = new HomeService();
    const testLink = ref('');
    const testEntity = ref<ITestEntity>(new TestEntity());

    const closeErrorMessage = () => {
      errorMessage.value = null;
    };

    const navigateBack = () => {
      switch (quizState.value) {
        // case QuizState.ORIENTATION_ANSWERS:
        //   quizState.value = QuizState.ORIENTATION_QUESTIONS;
        //   break;
        case QuizState.ORIENTATION_QUESTIONS:
          quizState.value = QuizState.PATIENT_INFO;
          break;
        default:
          break;
      }
      saveQuizProgress();
    };

    const copyTestLink = () => {
      navigator.clipboard
        .writeText(testLink.value)
        .then(() => {
          console.log('Test link copied to clipboard');
        })
        .catch(error => {
          console.error('Failed to copy test link:', error);
        });
    };

    const loadOrientationToPlaceQuestions = async () => {
      try {
        if (patientProfile.value.id && patientProfile.value.id > 0) {
          orientationToPlaceQuestions.value = await homeService.getOrientationToPlaceQuestionsByPatientProfileId(patientProfile.value.id);
        } else {
          orientationToPlaceQuestions.value = await homeService.getOrientationToPlaceQuestions();
        }
      } catch (error) {
        console.error('Error loading orientation to place questions:', error);
      }
    };

    const createNewQuiz = () => {
      localStorage.removeItem(QUIZ_PROGRESS);
      quizState.value = QuizState.PATIENT_INFO;
      patientProfile.value = new PatientProfile();
      testEntity.value = new TestEntity();
    };

    const persistAnswer = (question: IOrientationToPlaceQuestion) => {
      if (question.correctAnswer && question.correctAnswer.length >= 3) {
        // homeService.saveOrientationToPlaceCorrectAnswers(patientProfile.value.id, orientationToPlaceQuestions.value);
      }
    };

    const saveOrientationToPlaceCorrectAnswers = async () => {
      try {
        const response = await homeService.saveOrientationToPlaceCorrectAnswersV2(
          patientProfile.value.id,
          orientationToPlaceQuestions.value
        );

        // Ensure the response contains a hash
        if (response && response.hash) {
          testEntity.value = response;
          console.log('Orientation to place correct answers saved successfully');

          // Update the test link with the new hash
          const baseUrl = window.location.origin;
          const newTestLink = router.resolve({
            name: 'TestView',
            params: { testEntityHash: response.hash }
          }).href;

          testLink.value = `${baseUrl}${newTestLink}`;
          console.log('Updated test link:', testLink.value);

          quizState.value = QuizState.SHOW_TEST_LINK;
          saveQuizProgress();
        } else {
          throw new Error('Response does not contain a hash');
        }
      } catch (error) {
        console.error('Error saving orientation to place correct answers:', error);
      }
    };


    const saveOrientationToPlaceAnswerOptions = async () => {
      try {
        const response = await homeService.saveOrientationToPlaceAnswerOptions(patientProfile.value.id, orientationToPlaceQuestions.value);
        testEntity.value = response;
        console.log('Orientation to place answer options saved successfully');
        quizState.value = QuizState.SHOW_TEST_LINK;
        if (testEntity?.value?.hash) {
          const baseUrl = window.location.origin;
          testLink.value =
            baseUrl +
            router.resolve({
              name: 'TestView',
              params: { testEntityHash: testEntity.value.hash }
            }).href;
        }
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
        const response = await homeService.startQuiz(patientProfile.value);
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

    const startDragging = (event: DragEvent) => {
      event.dataTransfer?.setData('text/plain', 'paper');
    };

    const saveQuizProgress = () => {
      const quizProgress = {
        quizState: quizState.value,
        patientProfile: patientProfile.value,
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
        testEntity.value = quizProgress.testEntity;
        if (testEntity?.value?.hash) {
          const baseUrl = window.location.origin;
          testLink.value =
            baseUrl +
            router.resolve({
              name: 'TestView',
              params: { testEntityHash: testEntity.value.hash }
            }).href;
        }
      }
    };

    (async () => {
      onBeforeMount(() => {
        loadQuizProgress();
      });

      onMounted(async () => {
        await loadOrientationToPlaceQuestions();
      });
    })();

    return {
      authenticated,
      username,
      openLogin,
      t$: useI18n().t,
      loading,
      startDragging,
      patientProfile,
      startQuiz,
      orientationToPlaceQuestions,
      saveOrientationToPlaceCorrectAnswers,
      saveOrientationToPlaceAnswerOptions,
      quizState,
      createNewQuiz,
      persistAnswer,
      errorMessage,
      closeErrorMessage,
      testEntity,
      navigateBack,
      testLink,
      copyTestLink
    };
  },
});
