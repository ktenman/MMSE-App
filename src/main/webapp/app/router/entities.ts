import { Authority } from '@/shared/security/authority';
/* tslint:disable */
// prettier-ignore
const Entities = () => import('@/entities/entities.vue');

const TestEntity = () => import('@/entities/test-entity/test-entity.vue');
const TestEntityUpdate = () => import('@/entities/test-entity/test-entity-update.vue');
const TestEntityDetails = () => import('@/entities/test-entity/test-entity-details.vue');

const UserAnswer = () => import('@/entities/user-answer/user-answer.vue');
const UserAnswerUpdate = () => import('@/entities/user-answer/user-answer-update.vue');
const UserAnswerDetails = () => import('@/entities/user-answer/user-answer-details.vue');

const PatientProfile = () => import('@/entities/patient-profile/patient-profile.vue');
const PatientProfileUpdate = () => import('@/entities/patient-profile/patient-profile-update.vue');
const PatientProfileDetails = () => import('@/entities/patient-profile/patient-profile-details.vue');
const TestComponent = () => import('@/core/home/test.vue');

// jhipster-needle-add-entity-to-router-import - JHipster will import entities to the router here

export default {
  path: '/',
  component: Entities,
  children: [
    {
      path: 'test-entity',
      name: 'TestEntity',
      component: TestEntity,
      meta: { authorities: [Authority.USER] }
    },
    {
      path: 'test-entity/new',
      name: 'TestEntityCreate',
      component: TestEntityUpdate,
      meta: { authorities: [Authority.USER] }
    },
    {
      path: 'test-entity/:testEntityId/edit',
      name: 'TestEntityEdit',
      component: TestEntityUpdate,
      meta: { authorities: [Authority.USER] }
    },
    {
      path: 'test-entity/:testEntityId/view',
      name: 'TestEntityView',
      component: TestEntityDetails,
      meta: { authorities: [Authority.USER] }
    },
    {
      path: 'user-answer',
      name: 'UserAnswer',
      component: UserAnswer,
      meta: { authorities: [Authority.USER] }
    },
    {
      path: 'user-answer/new',
      name: 'UserAnswerCreate',
      component: UserAnswerUpdate,
      meta: { authorities: [Authority.USER] }
    },
    {
      path: 'user-answer/:userAnswerId/edit',
      name: 'UserAnswerEdit',
      component: UserAnswerUpdate,
      meta: { authorities: [Authority.USER] }
    },
    {
      path: 'user-answer/:userAnswerId/view',
      name: 'UserAnswerView',
      component: UserAnswerDetails,
      meta: { authorities: [Authority.USER] }
    },
    {
      path: 'patient-profile',
      name: 'PatientProfile',
      component: PatientProfile,
      meta: { authorities: [Authority.USER] }
    },
    {
      path: 'patient-profile/new',
      name: 'PatientProfileCreate',
      component: PatientProfileUpdate,
      meta: { authorities: [Authority.USER] }
    },
    {
      path: 'patient-profile/:patientProfileId/edit',
      name: 'PatientProfileEdit',
      component: PatientProfileUpdate,
      meta: { authorities: [Authority.USER] }
    },
    {
      path: 'patient-profile/:patientProfileId/view',
      name: 'PatientProfileView',
      component: PatientProfileDetails,
      meta: { authorities: [Authority.USER] }
    },
    {
      path: 'q/:testEntityHash',
      name: 'TestView',
      component: TestComponent
    },
    // jhipster-needle-add-entity-to-router - JHipster will add entities to the router here
  ],
};
