import {ref, Ref} from 'vue';

export function usePagination(defaultItemsPerPage = 20) {
  const itemsPerPage = ref(defaultItemsPerPage);
  const page: Ref<number> = ref(1);

  return { itemsPerPage, page };
}
