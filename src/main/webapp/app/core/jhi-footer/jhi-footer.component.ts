import {computed, defineComponent} from 'vue';
import {useI18n} from 'vue-i18n';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'MmseFooter',
  setup() {
    const { t } = useI18n();

    const copyright = computed(() => {
      const currentYear = new Date().getFullYear();
      return `${t('footer')} © ${currentYear}`;
    });

    return {
      t,
      copyright,
    };
  },
});
