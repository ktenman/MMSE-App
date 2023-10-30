/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import dayjs from 'dayjs';
import UserAnswerUpdate from './user-answer-update.vue';
import UserAnswerService from './user-answer.service';
import { DATE_TIME_LONG_FORMAT } from '@/shared/composables/date-format';
import AlertService from '@/shared/alert/alert.service';

import TestEntityService from '@/entities/test-entity/test-entity.service';

type UserAnswerUpdateComponentType = InstanceType<typeof UserAnswerUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const userAnswerSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<UserAnswerUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('UserAnswer Management Update Component', () => {
    let comp: UserAnswerUpdateComponentType;
    let userAnswerServiceStub: SinonStubbedInstance<UserAnswerService>;

    beforeEach(() => {
      route = {};
      userAnswerServiceStub = sinon.createStubInstance<UserAnswerService>(UserAnswerService);
      userAnswerServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

      alertService = new AlertService({
        i18n: { t: vitest.fn() } as any,
        bvToast: {
          toast: vitest.fn(),
        } as any,
      });

      mountOptions = {
        stubs: {
          'font-awesome-icon': true,
          'b-input-group': true,
          'b-input-group-prepend': true,
          'b-form-datepicker': true,
          'b-form-input': true,
        },
        provide: {
          alertService,
          userAnswerService: () => userAnswerServiceStub,
          testEntityService: () =>
            sinon.createStubInstance<TestEntityService>(TestEntityService, {
              retrieve: sinon.stub().resolves({}),
            } as any),
        },
      };
    });

    afterEach(() => {
      vitest.resetAllMocks();
    });

    describe('load', () => {
      beforeEach(() => {
        const wrapper = shallowMount(UserAnswerUpdate, { global: mountOptions });
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
        const wrapper = shallowMount(UserAnswerUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.userAnswer = userAnswerSample;
        userAnswerServiceStub.update.resolves(userAnswerSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(userAnswerServiceStub.update.calledWith(userAnswerSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        userAnswerServiceStub.create.resolves(entity);
        const wrapper = shallowMount(UserAnswerUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.userAnswer = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(userAnswerServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        userAnswerServiceStub.find.resolves(userAnswerSample);
        userAnswerServiceStub.retrieve.resolves([userAnswerSample]);

        // WHEN
        route = {
          params: {
            userAnswerId: '' + userAnswerSample.id,
          },
        };
        const wrapper = shallowMount(UserAnswerUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.userAnswer).toMatchObject(userAnswerSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        userAnswerServiceStub.find.resolves(userAnswerSample);
        const wrapper = shallowMount(UserAnswerUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
