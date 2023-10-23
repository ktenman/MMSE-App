import { ComputedRef, defineComponent, inject, onMounted, ref, watch } from "vue";
import { useI18n } from "vue-i18n";
import LoginService from "@/account/login.service";
import { IQuestion } from "@/shared/model/question.model";
import QuestionService from "@/entities/question/question.service";
import { Answer, IAnswer } from "@/shared/model/answer.model";
import { QuestionId } from "@/shared/model/enumerations/question-id.model";
import { QuestionType } from "@/shared/model/enumerations/question-type.model";

export default defineComponent({
  compatConfig: { MODE: 3 },
  setup() {
    const [
      loginService,
      authenticated,
      username,
      question,
      selectedAnswer,
      selectedAnswers,
      quizEndMessage
    ] = [
      inject<LoginService>("loginService"),
      inject<ComputedRef<boolean>>("authenticated"),
      inject<ComputedRef<string>>("currentUsername"),
      ref<IQuestion | null>(null),
      ref<string | null>(null),
      ref<Array<number | null>>([]),
      ref<string | null>(null)
    ];

    const questionService = new QuestionService();

    const openLogin = () => loginService.openLogin();

    const createAnswer = (answerText: string | Array<number | null>, questionId: QuestionId): IAnswer => {
      if (typeof answerText === "string") {
        return new Answer(answerText, questionId);
      } else {
        return new Answer(answerText.join(","), questionId);
      }
    };

    const submitAnswer = async () => {
      if (question.value) {
        let answer: IAnswer;

        if (question.value.questionType === QuestionType.MULTIPLE_CHOICE && selectedAnswer.value ||
          question.value.questionType === QuestionType.TEXT_INPUT && selectedAnswers.value) {
          answer = createAnswer(selectedAnswer.value, question.value.questionId as QuestionId);
        } else if (question.value.questionType === QuestionType.SUBTRACTION_TASK && selectedAnswers.value) {
          answer = createAnswer(selectedAnswers.value, question.value.questionId as QuestionId);
        } else {
          // Neither condition met - possibly display an error message here
          return;
        }

        const response = await questionService.submitAnswer(answer);

        if (typeof response === "string") {
          quizEndMessage.value = response;
          question.value = null;
        } else {
          question.value = response;
          selectedAnswers.value = [];
          selectedAnswer.value = null;
        }
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
      retakeTest
    };
  }
});
