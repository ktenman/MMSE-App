/* tslint:disable max-line-length */
import { vitest } from "vitest";
import { MountingOptions, shallowMount } from "@vue/test-utils";
import sinon, { SinonStubbedInstance } from "sinon";

import UserAnswer from "../../../../../../main/webapp/app/entities/user-answer/user-answer.vue";
import UserAnswerService from "../../../../../../main/webapp/app/entities/user-answer/user-answer.service";
import AlertService from "../../../../../../main/webapp/app/shared/alert/alert.service";

type UserAnswerComponentType = InstanceType<typeof UserAnswer>;

const bModalStub = {
  render: () => {
  },
  methods: {
    hide: () => {
    },
    show: () => {
    }
  }
};

describe("Component Tests", () => {
  let alertService: AlertService;

  describe("UserAnswer Management Component", () => {
    let userAnswerServiceStub: SinonStubbedInstance<UserAnswerService>;
    let mountOptions: MountingOptions<UserAnswerComponentType>["global"];

    beforeEach(() => {
      userAnswerServiceStub = sinon.createStubInstance<UserAnswerService>(UserAnswerService);
      userAnswerServiceStub.retrieve.resolves({ headers: {} });

      alertService = new AlertService({
        i18n: { t: vitest.fn() } as any,
        bvToast: {
          toast: vitest.fn()
        } as any
      });

      mountOptions = {
        stubs: {
          jhiItemCount: true,
          bPagination: true,
          bModal: bModalStub as any,
          "font-awesome-icon": true,
          "b-badge": true,
          "jhi-sort-indicator": true,
          "b-button": true,
          "router-link": true
        },
        directives: {
          "b-modal": {}
        },
        provide: {
          alertService,
          userAnswerService: () => userAnswerServiceStub
        }
      };
    });

    describe("Mount", () => {
      it("Should call load all on init", async () => {
        // GIVEN
        userAnswerServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        const wrapper = shallowMount(UserAnswer, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(userAnswerServiceStub.retrieve.calledOnce).toBeTruthy();
        expect(comp.userAnswers[0]).toEqual(expect.objectContaining({ id: 123 }));
      });

      it("should calculate the sort attribute for an id", async () => {
        // WHEN
        const wrapper = shallowMount(UserAnswer, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(userAnswerServiceStub.retrieve.lastCall.firstArg).toMatchObject({
          sort: ["id,asc"]
        });
      });
    });
    describe("Handles", () => {
      let comp: UserAnswerComponentType;

      beforeEach(async () => {
        const wrapper = shallowMount(UserAnswer, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();
        userAnswerServiceStub.retrieve.reset();
        userAnswerServiceStub.retrieve.resolves({ headers: {}, data: [] });
      });

      it("should load a page", async () => {
        // GIVEN
        userAnswerServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        comp.page = 2;
        await comp.$nextTick();

        // THEN
        expect(userAnswerServiceStub.retrieve.called).toBeTruthy();
        expect(comp.userAnswers[0]).toEqual(expect.objectContaining({ id: 123 }));
      });

      it("should re-initialize the page", async () => {
        // GIVEN
        comp.page = 2;
        await comp.$nextTick();
        userAnswerServiceStub.retrieve.reset();
        userAnswerServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        comp.clear();
        await comp.$nextTick();

        // THEN
        expect(comp.page).toEqual(1);
        expect(userAnswerServiceStub.retrieve.callCount).toEqual(1);
        expect(comp.userAnswers[0]).toEqual(expect.objectContaining({ id: 123 }));
      });

      it("should calculate the sort attribute for a non-id attribute", async () => {
        // WHEN
        comp.propOrder = "name";
        await comp.$nextTick();

        // THEN
        expect(userAnswerServiceStub.retrieve.lastCall.firstArg).toMatchObject({
          sort: ["name,asc", "id"]
        });
      });

      it("Should call delete service on confirmDelete", async () => {
        // GIVEN
        userAnswerServiceStub.delete.resolves({});

        // WHEN
        comp.prepareRemove({ id: 123 });

        comp.removeUserAnswer();
        await comp.$nextTick(); // clear components

        // THEN
        expect(userAnswerServiceStub.delete.called).toBeTruthy();

        // THEN
        await comp.$nextTick(); // handle component clear watch
        expect(userAnswerServiceStub.retrieve.callCount).toEqual(1);
      });
    });
  });
});
