import {ComputedRef, defineComponent, inject, onMounted, ref} from 'vue';
import {useI18n} from 'vue-i18n';
import LoginService from '@/account/login.service';
import {IQuestion} from "@/shared/model/question.model";
import QuestionService from "@/entities/question/question.service";

export default defineComponent({
  compatConfig: { MODE: 3 },
  setup() {
    const loginService = inject<LoginService>('loginService');
    const questionService = new QuestionService();
    const authenticated = inject<ComputedRef<boolean>>('authenticated');
    const username = inject<ComputedRef<string>>('currentUsername');
    const question = ref<IQuestion | null>(null);
    const selectedAnswer = ref(null); // New line here

    const openLogin = () => {
      loginService.openLogin();
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
      selectedAnswer, // New line here
    };
  },
});
