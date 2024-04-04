import { computed, defineComponent, provide } from 'vue';
import { useI18n } from 'vue-i18n';
import Ribbon from '@/core/ribbon/ribbon.vue';
import JhiFooter from '@/core/jhi-footer/jhi-footer.vue';
import JhiNavbar from '@/core/jhi-navbar/jhi-navbar.vue';
import LoginForm from '@/account/login-form/login-form.vue';

import { useAlertService } from '@/shared/alert/alert.service';

import '@/shared/config/dayjs';
import { useRouter } from 'vue-router';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'App',
  components: {
    ribbon: Ribbon,
    'jhi-navbar': JhiNavbar,
    'login-form': LoginForm,
    'jhi-footer': JhiFooter
  },
  setup() {
    provide('alertService', useAlertService());
    const router = useRouter();

    const isTest = computed(() => {
      const path = router.currentRoute.value.path;
      const pattern = /^\/q\/[a-zA-Z]{4,8}$/; // This is the pattern for the test-quiz route. For example, /q/abcde or /q/aBcdefg
      return pattern.test(path);
    });

    return {
      t$: useI18n().t,
      isTest
    };
  },
});
