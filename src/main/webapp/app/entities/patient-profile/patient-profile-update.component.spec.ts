/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import dayjs from 'dayjs';
import PatientProfileUpdate from './patient-profile-update.vue';
import PatientProfileService from './patient-profile.service';
import { DATE_TIME_LONG_FORMAT } from '@/shared/composables/date-format';
import AlertService from '@/shared/alert/alert.service';

type PatientProfileUpdateComponentType = InstanceType<typeof PatientProfileUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock })
}));

const patientProfileSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<PatientProfileUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('PatientProfile Management Update Component', () => {
    let comp: PatientProfileUpdateComponentType;
    let patientProfileServiceStub: SinonStubbedInstance<PatientProfileService>;

    beforeEach(() => {
      route = {};
      patientProfileServiceStub = sinon.createStubInstance<PatientProfileService>(PatientProfileService);
      patientProfileServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

      alertService = new AlertService({
        i18n: { t: vitest.fn() } as any,
        bvToast: {
          toast: vitest.fn()
        } as any
      });

      mountOptions = {
        stubs: {
          'font-awesome-icon': true,
          'b-input-group': true,
          'b-input-group-prepend': true,
          'b-form-datepicker': true,
          'b-form-input': true
        },
        provide: {
          alertService,
          patientProfileService: () => patientProfileServiceStub
        }
      };
    });

    afterEach(() => {
      vitest.resetAllMocks();
    });

    describe('load', () => {
      beforeEach(() => {
        const wrapper = shallowMount(PatientProfileUpdate, { global: mountOptions });
        comp = wrapper.vm;
      });
      it('Should convert date from string', () => {
        // GIVEN
        const date = new Date('2019-10-15T11:42:02Z');

        // WHEN
        const convertedDate = comp.convertDateTimeFromServer(date);

        // THEN
        expect(convertedDate).toEqual(dayjs(date).format(DATE_TIME_LONG_FORMAT));
      });

      it('Should not convert date if date is not present', () => {
        expect(comp.convertDateTimeFromServer(null)).toBeNull();
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', async () => {
        // GIVEN
        const wrapper = shallowMount(PatientProfileUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.patientProfile = patientProfileSample;
        patientProfileServiceStub.update.resolves(patientProfileSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(patientProfileServiceStub.update.calledWith(patientProfileSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        patientProfileServiceStub.create.resolves(entity);
        const wrapper = shallowMount(PatientProfileUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.patientProfile = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(patientProfileServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        patientProfileServiceStub.find.resolves(patientProfileSample);
        patientProfileServiceStub.retrieve.resolves([patientProfileSample]);

        // WHEN
        route = {
          params: {
            patientProfileId: '' + patientProfileSample.id
          }
        };
        const wrapper = shallowMount(PatientProfileUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.patientProfile).toMatchObject(patientProfileSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        patientProfileServiceStub.find.resolves(patientProfileSample);
        const wrapper = shallowMount(PatientProfileUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
