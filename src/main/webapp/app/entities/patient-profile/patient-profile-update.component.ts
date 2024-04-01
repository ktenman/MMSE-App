import { computed, defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import PatientProfileService from './patient-profile.service';
import { useDateFormat, useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import { type IPatientProfile, PatientProfile } from '@/shared/model/patient-profile.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'PatientProfileUpdate',
  setup() {
    const patientProfileService = inject('patientProfileService', () => new PatientProfileService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const patientProfile: Ref<IPatientProfile> = ref(new PatientProfile());
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrievePatientProfile = async patientProfileId => {
      try {
        const res = await patientProfileService().find(patientProfileId);
        res.createdAt = new Date(res.createdAt);
        res.updatedAt = new Date(res.updatedAt);
        patientProfile.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.patientProfileId) {
      retrievePatientProfile(route.params.patientProfileId);
    }

    const initRelationships = () => {
    };

    initRelationships();

    const { t: t$ } = useI18n();
    const validations = useValidation();
    const validationRules = {
      patientId: {
        required: validations.required(t$('entity.validation.required').toString())
      },
      name: {
        required: validations.required(t$('entity.validation.required').toString())
      },
      createdAt: {
        required: validations.required(t$('entity.validation.required').toString())
      },
      updatedAt: {
        required: validations.required(t$('entity.validation.required').toString())
      },
      testEntities: {}
    };
    const v$ = useVuelidate(validationRules, patientProfile as any);
    v$.value.$validate();

    return {
      patientProfileService,
      alertService,
      patientProfile,
      previousState,
      isSaving,
      currentLanguage,
      v$,
      ...useDateFormat({ entityRef: patientProfile }),
      t$
    };
  },
  created(): void {
  },
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.patientProfile.id) {
        this.patientProfileService()
          .update(this.patientProfile)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('mmseApp.patientProfile.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.patientProfileService()
          .create(this.patientProfile)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('mmseApp.patientProfile.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    }
  }
});
