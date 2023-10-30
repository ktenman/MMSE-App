import { computed, defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import UserAnswerService from './user-answer.service';
import { useDateFormat, useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import TestEntityService from '@/entities/test-entity/test-entity.service';
import { type ITestEntity } from '@/shared/model/test-entity.model';
import { type IUserAnswer, UserAnswer } from '@/shared/model/user-answer.model';
import { QuestionId } from '@/shared/model/enumerations/question-id.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'UserAnswerUpdate',
  setup() {
    const userAnswerService = inject('userAnswerService', () => new UserAnswerService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const userAnswer: Ref<IUserAnswer> = ref(new UserAnswer());

    const testEntityService = inject('testEntityService', () => new TestEntityService());

    const testEntities: Ref<ITestEntity[]> = ref([]);
    const questionIdValues: Ref<string[]> = ref(Object.keys(QuestionId));
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveUserAnswer = async userAnswerId => {
      try {
        const res = await userAnswerService().find(userAnswerId);
        res.createdAt = new Date(res.createdAt);
        res.updatedAt = new Date(res.updatedAt);
        userAnswer.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.userAnswerId) {
      retrieveUserAnswer(route.params.userAnswerId);
    }

    const initRelationships = () => {
      testEntityService()
        .retrieve()
        .then(res => {
          testEntities.value = res.data;
        });
    };

    initRelationships();

    const { t: t$ } = useI18n();
    const validations = useValidation();
    const validationRules = {
      answerText: {},
      createdAt: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      updatedAt: {},
      questionId: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      testEntity: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
    };
    const v$ = useVuelidate(validationRules, userAnswer as any);
    v$.value.$validate();

    return {
      userAnswerService,
      alertService,
      userAnswer,
      previousState,
      questionIdValues,
      isSaving,
      currentLanguage,
      testEntities,
      v$,
      ...useDateFormat({ entityRef: userAnswer }),
      t$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.userAnswer.id) {
        this.userAnswerService()
          .update(this.userAnswer)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('mmseApp.userAnswer.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.userAnswerService()
          .create(this.userAnswer)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('mmseApp.userAnswer.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
