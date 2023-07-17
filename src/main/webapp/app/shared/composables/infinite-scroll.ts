import { ref } from 'vue';
import { useIntersectionObserver } from '@vueuse/core';

export function useInfiniteScroll(callback: () => void) {
  const infiniteScrollEl = ref<HTMLElement>(null);

  const intersectionObserver = useIntersectionObserver(
    infiniteScrollEl,
    intersection => {
      if (intersection[0].isIntersecting) {
        callback();
      }
    },
    {
      threshold: 0.5,
      immediate: false,
    }
  );

  return { infiniteScrollEl, intersectionObserver };
}
