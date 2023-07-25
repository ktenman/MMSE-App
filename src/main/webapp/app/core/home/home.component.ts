import {ComputedRef, defineComponent, inject, onMounted, ref, watch} from 'vue';
import {useI18n} from 'vue-i18n';
import LoginService from '@/account/login.service';
import {IQuestion} from "@/shared/model/question.model";
import QuestionService from "@/entities/question/question.service";
import {Answer, IAnswer} from "@/shared/model/answer.model";
import {QuestionId} from "@/shared/model/enumerations/question-id.model";

export default defineComponent({
  compatConfig: { MODE: 3 },
  setup() {
    const loginService = inject<LoginService>('loginService');
    const questionService = new QuestionService();
    const authenticated = inject<ComputedRef<boolean>>('authenticated');
    const username = inject<ComputedRef<string>>('currentUsername');
    const question = ref<IQuestion | null>(null);
    const selectedAnswer = ref<string | null>(null);
    const quizEndMessage = ref<string | null>(null);

    const openLogin = () => {
      loginService.openLogin();
    };

    const submitAnswer = async () => {
      if (selectedAnswer.value && question.value) {
        const answer: IAnswer = new Answer(selectedAnswer.value, question.value.questionId as QuestionId);

        const response = await questionService.submitAnswer(answer);

        if (typeof response === 'string') {
          quizEndMessage.value = response;
          question.value = null;
        } else {
          question.value = response;
          selectedAnswer.value = null;
        }
      }
    };

    const loadQuestion = async () => {
      const response = await questionService.getQuestion();

      if (typeof response === 'string') {
        // If the quiz has ended, the response should contain the message.
        quizEndMessage.value = response;

        // Clear the question ref
        question.value = null;
      } else {
        // If the quiz is not ended, the response should contain the next question
        question.value = response;
      }
    };

    watch(authenticated, async (newVal, oldVal) => {
      if (newVal === true) {
        await loadQuestion();
      }
    });

    onMounted(async () => {
      if (authenticated.value) {
        await loadQuestion();
      }
    });

    return {
      authenticated,
      username,
      openLogin,
      t$: useI18n().t,
      question,
      selectedAnswer,
      submitAnswer,
      quizEndMessage,
    };
  },
});
