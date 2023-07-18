import {ComputedRef, defineComponent, inject, ref} from 'vue';
import {useI18n} from 'vue-i18n';

import LoginService from '@/account/login.service';
import QuizService from '@/quiz/quiz.service'; // import QuizService

export default defineComponent({
  compatConfig: {MODE: 3},
  setup() {
    const loginService = inject<LoginService>('loginService');
    const quizService = inject('quizService', () => new QuizService()); // inject QuizService
    const authenticated = inject<ComputedRef<boolean>>('authenticated');
    const username = inject<ComputedRef<string>>('currentUsername');

    const question = ref(''); // store the current question

    const openLogin = () => {
      loginService.openLogin();
    };

    // fetch the first question and store it in the question ref
    const startQuiz = async () => {
      try {
        const res = await quizService.getNextQuestion();
        question.value = res.data.question;
      } catch (error) {
        console.log('Error', error);
      }
    };

    return {
      authenticated,
      username,
      question,
      openLogin,
      startQuiz,
      t$: useI18n().t,
    };
  },
});
