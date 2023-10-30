import { defineComponent } from 'vue';
import { useI18n } from 'vue-i18n';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'MmseFooter',
  setup() {
    return {
      t$: useI18n().t,
    };
  },
});
