import { defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import PatientProfileService from './patient-profile.service';
import { useDateFormat } from '@/shared/composables';
import { type IPatientProfile } from '@/shared/model/patient-profile.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'PatientProfileDetails',
  setup() {
    const dateFormat = useDateFormat();
    const patientProfileService = inject('patientProfileService', () => new PatientProfileService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const patientProfile: Ref<IPatientProfile> = ref({});

    const retrievePatientProfile = async patientProfileId => {
      try {
        const res = await patientProfileService().find(patientProfileId);
        patientProfile.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.patientProfileId) {
      retrievePatientProfile(route.params.patientProfileId);
    }

    return {
      ...dateFormat,
      alertService,
      patientProfile,

      previousState,
      t$: useI18n().t
    };
  },
});
