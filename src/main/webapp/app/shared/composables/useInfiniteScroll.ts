import { onBeforeMount, onBeforeUnmount } from "vue";

export function useInfiniteScroll(checkScroll: () => void) {
  onBeforeMount(() => {
    window.addEventListener("scroll", checkScroll);
  });

  onBeforeUnmount(() => {
    window.removeEventListener("scroll", checkScroll);
  });
}
