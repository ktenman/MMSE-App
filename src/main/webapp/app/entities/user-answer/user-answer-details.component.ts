import { defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import UserAnswerService from './user-answer.service';
import { useDateFormat } from '@/shared/composables';
import { type IUserAnswer } from '@/shared/model/user-answer.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'UserAnswerDetails',
  setup() {
    const dateFormat = useDateFormat();
    const userAnswerService = inject('userAnswerService', () => new UserAnswerService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const userAnswer: Ref<IUserAnswer> = ref({});

    const retrieveUserAnswer = async userAnswerId => {
      try {
        const res = await userAnswerService().find(userAnswerId);
        userAnswer.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.userAnswerId) {
      retrieveUserAnswer(route.params.userAnswerId);
    }

    return {
      ...dateFormat,
      alertService,
      userAnswer,

      previousState,
      t$: useI18n().t,
    };
  },
});
