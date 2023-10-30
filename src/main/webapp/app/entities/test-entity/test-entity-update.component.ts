import { computed, defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import TestEntityService from './test-entity.service';
import { useDateFormat, useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import UserService from '@/entities/user/user.service';
import { type ITestEntity, TestEntity } from '@/shared/model/test-entity.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'TestEntityUpdate',
  setup() {
    const testEntityService = inject('testEntityService', () => new TestEntityService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const testEntity: Ref<ITestEntity> = ref(new TestEntity());
    const userService = inject('userService', () => new UserService());
    const users: Ref<Array<any>> = ref([]);
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveTestEntity = async testEntityId => {
      try {
        const res = await testEntityService().find(testEntityId);
        res.createdAt = new Date(res.createdAt);
        res.updatedAt = new Date(res.updatedAt);
        testEntity.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.testEntityId) {
      retrieveTestEntity(route.params.testEntityId);
    }

    const initRelationships = () => {
      userService()
        .retrieve()
        .then(res => {
          users.value = res.data;
        });
    };

    initRelationships();

    const { t: t$ } = useI18n();
    const validations = useValidation();
    const validationRules = {
      createdAt: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      updatedAt: {},
      score: {},
      user: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
    };
    const v$ = useVuelidate(validationRules, testEntity as any);
    v$.value.$validate();

    return {
      testEntityService,
      alertService,
      testEntity,
      previousState,
      isSaving,
      currentLanguage,
      users,
      v$,
      ...useDateFormat({ entityRef: testEntity }),
      t$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.testEntity.id) {
        this.testEntityService()
          .update(this.testEntity)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('mmseApp.testEntity.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.testEntityService()
          .create(this.testEntity)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('mmseApp.testEntity.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
