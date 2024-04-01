import { defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import TestEntityService from './test-entity.service';
import { useDateFormat } from '@/shared/composables';
import { type ITestEntity } from '@/shared/model/test-entity.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'TestEntityDetails',
  setup() {
    const dateFormat = useDateFormat();
    const testEntityService = inject('testEntityService', () => new TestEntityService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const testEntity: Ref<ITestEntity> = ref({});

    const retrieveTestEntity = async testEntityId => {
      try {
        const res = await testEntityService().find(testEntityId);
        testEntity.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.testEntityId) {
      retrieveTestEntity(route.params.testEntityId);
    }

    return {
      ...dateFormat,
      alertService,
      testEntity,

      previousState,
      t$: useI18n().t
    };
  },
});
