/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, MountingOptions } from '@vue/test-utils';
import sinon, { SinonStubbedInstance } from 'sinon';
import { RouteLocation } from 'vue-router';

import UserAnswerDetails from '../../../../../../main/webapp/app/entities/user-answer/user-answer-details.vue';
import UserAnswerService from '../../../../../../main/webapp/app/entities/user-answer/user-answer.service';
import AlertService from '../../../../../../main/webapp/app/shared/alert/alert.service';

type UserAnswerDetailsComponentType = InstanceType<typeof UserAnswerDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const userAnswerSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('UserAnswer Management Detail Component', () => {
    let userAnswerServiceStub: SinonStubbedInstance<UserAnswerService>;
    let mountOptions: MountingOptions<UserAnswerDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      userAnswerServiceStub = sinon.createStubInstance<UserAnswerService>(UserAnswerService);

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
          userAnswerService: () => userAnswerServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        userAnswerServiceStub.find.resolves(userAnswerSample);
        route = {
          params: {
            userAnswerId: '' + 123,
          },
        };
        const wrapper = shallowMount(UserAnswerDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.userAnswer).toMatchObject(userAnswerSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        userAnswerServiceStub.find.resolves(userAnswerSample);
        const wrapper = shallowMount(UserAnswerDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
