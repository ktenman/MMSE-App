import { defineComponent, inject, onMounted, ref, Ref, watch, watchEffect } from "vue";
import { useI18n } from "vue-i18n";
import { ITestEntity } from "@/shared/model/test-entity.model";
import useDataUtils from "@/shared/data/data-utils.service";
import { useDateFormat } from "@/shared/composables";
import TestEntityService from "./test-entity.service";
import { useAlertService } from "@/shared/alert/alert.service";
import { usePagination } from "@/shared/composables/pagination";
import { useSorting } from "@/shared/composables/sorting";
import { useInfiniteScroll } from "@/shared/composables/infinite-scroll";

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: "TestEntity",
  setup() {
    const { t: t$ } = useI18n();
    const dateFormat = useDateFormat();
    const dataUtils = useDataUtils();
    const testEntityService = inject("testEntityService", () => new TestEntityService());
    const alertService = inject("alertService", () => useAlertService(), true);

    const queryCount: Ref<number> = ref(null);
    const totalItems = ref(0);
    const links: Ref<any> = ref({});

    const testEntities: Ref<ITestEntity[]> = ref([]);

    const isFetching = ref(false);

    // Use the composables
    const pagination = usePagination();
    const sorting = useSorting();
    const infiniteScroll = useInfiniteScroll(() => {
      if (!isFetching.value) {
        pagination.page.value++;
      }
    });

    const clear = () => {
      pagination.page.value = 1;
      links.value = {};
      testEntities.value = [];
    };

    const retrieveTestEntitys = async () => {
      isFetching.value = true;
      try {
        const paginationQuery = {
          page: pagination.page.value - 1,
          size: pagination.itemsPerPage.value,
          sort: sorting.getSort()
        };
        const res = await testEntityService().retrieve(paginationQuery);
        totalItems.value = Number(res.headers["x-total-count"]);
        queryCount.value = totalItems.value;
        links.value = dataUtils.parseLinks(res.headers?.["link"]);
        testEntities.value.push(...(res.data ?? []));
      } catch (err) {
        alertService.showHttpError(err.response);
      } finally {
        isFetching.value = false;
      }
    };

    const handleSyncList = () => {
      clear();
    };

    onMounted(async () => {
      await retrieveTestEntitys();
    });

    const removeId: Ref<number> = ref(null);
    const removeEntity = ref<any>(null);
    const prepareRemove = (instance: ITestEntity) => {
      removeId.value = instance.id;
      removeEntity.value.show();
    };
    const closeDialog = () => {
      removeEntity.value.hide();
    };
    const removeTestEntity = async () => {
      try {
        await testEntityService().delete(removeId.value);
        const message = t$("mmseApp.testEntity.deleted", { param: removeId.value }).toString();
        alertService.showInfo(message, { variant: "danger" });
        removeId.value = null;
        clear();
        closeDialog();
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    // Whenever order changes, reset the pagination
    watch([sorting.propOrder, sorting.reverse], () => {
      clear();
    });

    // Whenever the data resets or page changes, switch to the new page.
    watch([testEntities, pagination.page], async ([data, page], [_prevData, prevPage]) => {
      if (data.length === 0 || page !== prevPage) {
        await retrieveTestEntitys();
      }
    });

    watchEffect(() => {
      if (links.value.next) {
        infiniteScroll.intersectionObserver.resume();
      } else if (infiniteScroll.intersectionObserver.isActive) {
        infiniteScroll.intersectionObserver.pause();
      }
    });

    return {
      testEntities,
      handleSyncList,
      isFetching,
      retrieveTestEntitys,
      clear,
      ...dateFormat,
      removeId,
      removeEntity,
      prepareRemove,
      closeDialog,
      removeTestEntity,
      totalItems,
      queryCount,
      ...pagination,
      ...sorting,
      ...infiniteScroll,
      t$,
      ...dataUtils
    };
  }
});
