import { computed, defineComponent, inject, ref, Ref } from "vue";
import { useI18n } from "vue-i18n";
import { useRoute, useRouter } from "vue-router";
import { useVuelidate } from "@vuelidate/core";

import { useDateFormat, useValidation } from "@/shared/composables";
import { useAlertService } from "@/shared/alert/alert.service";

import UserService from "@/entities/user/user.service";
import { ITestEntity, TestEntity } from "@/shared/model/test-entity.model";
import TestEntityService from "./test-entity.service";

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: "TestEntityUpdate",
  setup() {
    const testEntityService = inject("testEntityService", () => new TestEntityService());
    const alertService = inject("alertService", () => useAlertService(), true);

    const testEntity: Ref<ITestEntity> = ref(new TestEntity());
    const userService = inject("userService", () => new UserService());
    const users: Ref<Array<any>> = ref([]);
    const isSaving = ref(false);
    const currentLanguage = inject("currentLanguage", () => computed(() => navigator.language ?? "en"), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveTestEntity = async (testEntityId) => {
      try {
        const res = await testEntityService().find(testEntityId);
        res.createdAt = new Date(res.createdAt);
        res.updatedAt = new Date(res.updatedAt);
        testEntity.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    const initRelationships = async () => {
      try {
        const res = await userService().retrieve();
        users.value = res.data;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.testEntityId) {
      retrieveTestEntity(route.params.testEntityId);
    }

    initRelationships();

    const { t: t$ } = useI18n();
    const validations = useValidation();
    const validationRules = {
      createdAt: {
        required: validations.required(t$("entity.validation.required").toString())
      },
      updatedAt: {},
      score: {},
      user: {
        required: validations.required(t$("entity.validation.required").toString())
      }
    };
    const v$ = useVuelidate(validationRules, testEntity as any);
    v$.value.$validate();

    return {
      testEntity,
      previousState,
      isSaving,
      currentLanguage,
      users,
      v$,
      ...useDateFormat({ entityRef: testEntity }),
      t$,
      save: async () => {
        isSaving.value = true;
        try {
          let response;
          if (testEntity.value.id) {
            response = await testEntityService().update(testEntity.value);
            alertService.showInfo(t$("mmseApp.testEntity.updated", { param: response.id }));
          } else {
            response = await testEntityService().create(testEntity.value);
            alertService.showSuccess(t$("mmseApp.testEntity.created", { param: response.id }).toString());
          }
          previousState();
        } catch (error) {
          alertService.showHttpError(error.response);
        } finally {
          isSaving.value = false;
        }
      }
    };
  }
});
