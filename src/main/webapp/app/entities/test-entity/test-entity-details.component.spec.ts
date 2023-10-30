/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import TestEntityDetails from './test-entity-details.vue';
import TestEntityService from './test-entity.service';
import AlertService from '@/shared/alert/alert.service';

type TestEntityDetailsComponentType = InstanceType<typeof TestEntityDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const testEntitySample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('TestEntity Management Detail Component', () => {
    let testEntityServiceStub: SinonStubbedInstance<TestEntityService>;
    let mountOptions: MountingOptions<TestEntityDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      testEntityServiceStub = sinon.createStubInstance<TestEntityService>(TestEntityService);

      alertService = new AlertService({
        i18n: { t: vitest.fn() } as any,
        bvToast: {
          toast: vitest.fn(),
        } as any,
      });

      mountOptions = {
        stubs: {
          'font-awesome-icon': true,
          'router-link': true,
        },
        provide: {
          alertService,
          testEntityService: () => testEntityServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        testEntityServiceStub.find.resolves(testEntitySample);
        route = {
          params: {
            testEntityId: '' + 123,
          },
        };
        const wrapper = shallowMount(TestEntityDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.testEntity).toMatchObject(testEntitySample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        testEntityServiceStub.find.resolves(testEntitySample);
        const wrapper = shallowMount(TestEntityDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
