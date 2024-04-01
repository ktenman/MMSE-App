/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import PatientProfileDetails from './patient-profile-details.vue';
import PatientProfileService from './patient-profile.service';
import AlertService from '@/shared/alert/alert.service';

type PatientProfileDetailsComponentType = InstanceType<typeof PatientProfileDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock })
}));

const patientProfileSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('PatientProfile Management Detail Component', () => {
    let patientProfileServiceStub: SinonStubbedInstance<PatientProfileService>;
    let mountOptions: MountingOptions<PatientProfileDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      patientProfileServiceStub = sinon.createStubInstance<PatientProfileService>(PatientProfileService);

      alertService = new AlertService({
        i18n: { t: vitest.fn() } as any,
        bvToast: {
          toast: vitest.fn()
        } as any
      });

      mountOptions = {
        stubs: {
          'font-awesome-icon': true,
          'router-link': true
        },
        provide: {
          alertService,
          patientProfileService: () => patientProfileServiceStub
        }
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        patientProfileServiceStub.find.resolves(patientProfileSample);
        route = {
          params: {
            patientProfileId: '' + 123
          }
        };
        const wrapper = shallowMount(PatientProfileDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.patientProfile).toMatchObject(patientProfileSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        patientProfileServiceStub.find.resolves(patientProfileSample);
        const wrapper = shallowMount(PatientProfileDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
