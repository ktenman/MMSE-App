import axios from 'axios';
import { defineComponent, inject, ref, Ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useI18n } from 'vue-i18n';
import AccountService from '../account.service';
import LoginService from '@/account/login.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  setup() {
    const authenticationError: Ref<boolean> = ref(false);
    const login: Ref<string> = ref(null);
    const password: Ref<string> = ref(null);
    const rememberMe: Ref<boolean> = ref(false);
    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const accountService = inject<AccountService>('accountService');
    const loginService = inject<LoginService>('loginService');

    const doLogin = async () => {
      const data = { username: login.value, password: password.value, rememberMe: rememberMe.value };
      try {
        const result = await axios.post('api/authenticate', data);
        const bearerToken = result.headers.authorization;
        if (bearerToken && bearerToken.slice(0, 7) === 'Bearer ') {
          const jwt = bearerToken.slice(7, bearerToken.length);
          if (rememberMe.value) {
            localStorage.setItem('mmse-authenticationToken', jwt);
            sessionStorage.removeItem('mmse-authenticationToken');
          } else {
            sessionStorage.setItem('mmse-authenticationToken', jwt);
            localStorage.removeItem('mmse-authenticationToken');
          }
        }

        authenticationError.value = false;
        loginService.hideLogin();
        await accountService.retrieveAccount();
        if (route.path === '/forbidden') {
          previousState();
        }
      } catch (_error) {
        authenticationError.value = true;
      }
    };
    return {
      authenticationError,
      login,
      password,
      rememberMe,
      accountService,
      doLogin,
      t$: useI18n().t
    };
  },
});
