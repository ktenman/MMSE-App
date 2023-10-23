/* tslint:disable max-line-length */
import { vitest } from "vitest";
import { MountingOptions, shallowMount } from "@vue/test-utils";
import sinon, { SinonStubbedInstance } from "sinon";

import TestEntity from "../../../../../../main/webapp/app/entities/test-entity/test-entity.vue";
import TestEntityService from "../../../../../../main/webapp/app/entities/test-entity/test-entity.service";
import AlertService from "../../../../../../main/webapp/app/shared/alert/alert.service";

type TestEntityComponentType = InstanceType<typeof TestEntity>;

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

  describe("TestEntity Management Component", () => {
    let testEntityServiceStub: SinonStubbedInstance<TestEntityService>;
    let mountOptions: MountingOptions<TestEntityComponentType>["global"];

    beforeEach(() => {
      testEntityServiceStub = sinon.createStubInstance<TestEntityService>(TestEntityService);
      testEntityServiceStub.retrieve.resolves({ headers: {} });

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
          testEntityService: () => testEntityServiceStub
        }
      };
    });

    describe("Mount", () => {
      it("Should call load all on init", async () => {
        // GIVEN
        testEntityServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        const wrapper = shallowMount(TestEntity, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(testEntityServiceStub.retrieve.calledOnce).toBeTruthy();
        expect(comp.testEntities[0]).toEqual(expect.objectContaining({ id: 123 }));
      });

      it("should calculate the sort attribute for an id", async () => {
        // WHEN
        const wrapper = shallowMount(TestEntity, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(testEntityServiceStub.retrieve.lastCall.firstArg).toMatchObject({
          sort: ["id,asc"]
        });
      });
    });
    describe("Handles", () => {
      let comp: TestEntityComponentType;

      beforeEach(async () => {
        const wrapper = shallowMount(TestEntity, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();
        testEntityServiceStub.retrieve.reset();
        testEntityServiceStub.retrieve.resolves({ headers: {}, data: [] });
      });

      it("should load a page", async () => {
        // GIVEN
        testEntityServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        comp.page = 2;
        await comp.$nextTick();

        // THEN
        expect(testEntityServiceStub.retrieve.called).toBeTruthy();
        expect(comp.testEntities[0]).toEqual(expect.objectContaining({ id: 123 }));
      });

      it("should re-initialize the page", async () => {
        // GIVEN
        comp.page = 2;
        await comp.$nextTick();
        testEntityServiceStub.retrieve.reset();
        testEntityServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        comp.clear();
        await comp.$nextTick();

        // THEN
        expect(comp.page).toEqual(1);
        expect(testEntityServiceStub.retrieve.callCount).toEqual(1);
        expect(comp.testEntities[0]).toEqual(expect.objectContaining({ id: 123 }));
      });

      it("should calculate the sort attribute for a non-id attribute", async () => {
        // WHEN
        comp.propOrder = "name";
        await comp.$nextTick();

        // THEN
        expect(testEntityServiceStub.retrieve.lastCall.firstArg).toMatchObject({
          sort: ["name,asc", "id"]
        });
      });

      it("Should call delete service on confirmDelete", async () => {
        // GIVEN
        testEntityServiceStub.delete.resolves({});

        // WHEN
        comp.prepareRemove({ id: 123 });

        comp.removeTestEntity();
        await comp.$nextTick(); // clear components

        // THEN
        expect(testEntityServiceStub.delete.called).toBeTruthy();

        // THEN
        await comp.$nextTick(); // handle component clear watch
        expect(testEntityServiceStub.retrieve.callCount).toEqual(1);
      });
    });
  });
});
