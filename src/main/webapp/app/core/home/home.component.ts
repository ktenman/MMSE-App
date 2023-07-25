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
    const [
      loginService,
      authenticated,
      username,
      question,
      selectedAnswer,
      quizEndMessage
    ] = [
      inject<LoginService>('loginService'),
      inject<ComputedRef<boolean>>('authenticated'),
      inject<ComputedRef<string>>('currentUsername'),
      ref<IQuestion | null>(null),
      ref<string | null>(null),
      ref<string | null>(null)
    ];

    const questionService = new QuestionService();

    const openLogin = () => loginService.openLogin();

    const createAnswer = (selected: string, questionId: QuestionId): IAnswer =>
      new Answer(selected, questionId);

    const submitAnswer = async () => {
      if (selectedAnswer.value && question.value) {
        const answer = createAnswer(selectedAnswer.value, question.value.questionId as QuestionId);
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

    const retakeTest = async () => {
      const response = await questionService.retakeTest();

      if (typeof response === 'string') {
        quizEndMessage.value = response;
      } else {
        question.value = response;
        quizEndMessage.value = null; // clear end message
        selectedAnswer.value = null; // clear selected answer
      }
    };

    const loadQuestion = async () => {
      const response = await questionService.getQuestion();

      if (typeof response === 'string') {
        quizEndMessage.value = response;
        question.value = null;
      } else {
        question.value = response;
      }
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
      submitAnswer,
      quizEndMessage,
      retakeTest
    };
  },
});
