import {ComputedRef, defineComponent, inject, onMounted, ref} from 'vue';
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

    const openLogin = () => {
      loginService.openLogin();
    };

    const submitAnswer = async () => {
      if (selectedAnswer.value && question.value) {
        const answer: IAnswer = new Answer(selectedAnswer.value, question.value.questionId as QuestionId);

        const response = await questionService.submitAnswer(answer);

        if (typeof response === 'string') {
          // Handle quiz end, maybe show the score to user and navigate to another page?
        } else {
          // If the quiz is not ended, the response should contain the next question
          question.value = response;
          selectedAnswer.value = null; // reset the selected answer
        }
      }
    };

    onMounted(async () => {
      if (authenticated.value) {
        question.value = await questionService.getQuestion();
      }
    });

    return {
      authenticated,
      username,
      openLogin,
      t$: useI18n().t,
      question,
      selectedAnswer,
      submitAnswer, // New line here
    };
  },
});
