import {ComputedRef, defineComponent, inject, onMounted, ref} from 'vue';
import {useI18n} from 'vue-i18n';

import LoginService from '@/account/login.service';
import {IQuestion} from "@/shared/model/question.model";
import QuestionService from "@/entities/question/question.service";

export default defineComponent({
  compatConfig: {MODE: 3},
  setup() {
    const loginService = inject<LoginService>('loginService');
    const questionService = new QuestionService(); // add this line
    const authenticated = inject<ComputedRef<boolean>>('authenticated');
    const username = inject<ComputedRef<string>>('currentUsername');
    const question = ref<IQuestion | null>(null); // add this line
    const openLogin = () => {
      loginService.openLogin();
    };

    // add the following block
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
      question, // add this line
    };
  },
});
