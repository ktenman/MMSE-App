/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { SinonStubbedInstance } from 'sinon';
import { RouteLocation } from 'vue-router';

import dayjs from 'dayjs';
import { DATE_TIME_LONG_FORMAT } from '../../../../../../main/webapp/app/shared/composables/date-format';
import TestEntityUpdate from '../../../../../../main/webapp/app/entities/test-entity/test-entity-update.vue';
import TestEntityService from '../../../../../../main/webapp/app/entities/test-entity/test-entity.service';
import AlertService from '../../../../../../main/webapp/app/shared/alert/alert.service';

import UserService from '../../../../../../main/webapp/app/entities/user/user.service';

type TestEntityUpdateComponentType = InstanceType<typeof TestEntityUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock })
}));

const testEntitySample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<TestEntityUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('TestEntity Management Update Component', () => {
    let comp: TestEntityUpdateComponentType;
    let testEntityServiceStub: SinonStubbedInstance<TestEntityService>;

    beforeEach(() => {
      route = {};
      testEntityServiceStub = sinon.createStubInstance<TestEntityService>(TestEntityService);

      alertService = new AlertService({
        i18n: { t: vitest.fn() } as any,
        bvToast: {
          toast: vitest.fn()
        } as any,
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
          testEntityService: () => testEntityServiceStub,

          userService: () =>
            sinon.createStubInstance<UserService>(UserService, {
              retrieve: sinon.stub().resolves({})
            } as any),
        },
      };
    });

    afterEach(() => {
      vitest.resetAllMocks();
    });

    describe('load', () => {
      beforeEach(() => {
        const wrapper = shallowMount(TestEntityUpdate, { global: mountOptions });
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
        const wrapper = shallowMount(TestEntityUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.testEntity = testEntitySample;
        testEntityServiceStub.update.resolves(testEntitySample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(testEntityServiceStub.update.calledWith(testEntitySample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        testEntityServiceStub.create.resolves(entity);
        const wrapper = shallowMount(TestEntityUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.testEntity = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(testEntityServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        testEntityServiceStub.find.resolves(testEntitySample);
        testEntityServiceStub.retrieve.resolves([testEntitySample]);

        // WHEN
        route = {
          params: {
            testEntityId: '' + testEntitySample.id
          },
        };
        const wrapper = shallowMount(TestEntityUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.testEntity).toMatchObject(testEntitySample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        testEntityServiceStub.find.resolves(testEntitySample);
        const wrapper = shallowMount(TestEntityUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
