import { defineComponent, provide } from 'vue';

import TestEntityService from './test-entity/test-entity.service';
import UserAnswerService from './user-answer/user-answer.service';
import PatientProfileService from './patient-profile/patient-profile.service';
import UserService from '@/entities/user/user.service';
// jhipster-needle-add-entity-service-to-entities-component-import - JHipster will import entities services here

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'Entities',
  setup() {
    provide('userService', () => new UserService());
    provide('testEntityService', () => new TestEntityService());
    provide('userAnswerService', () => new UserAnswerService());
    provide('patientProfileService', () => new PatientProfileService());
    // jhipster-needle-add-entity-service-to-entities-component - JHipster will import entities services here
  },
});
