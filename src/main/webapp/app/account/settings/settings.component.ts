import {computed, ComputedRef, defineComponent, inject, ref, Ref} from 'vue';
import {useI18n} from 'vue-i18n';
import languages from '@/shared/config/languages';
import {useVuelidate} from '@vuelidate/core';
import {email, maxLength, minLength, required} from '@vuelidate/validators';
import axios from 'axios';
import {EMAIL_ALREADY_USED_TYPE} from '@/constants';
import {useStore} from '@/store';

const validations = {
  settingsAccount: {
    firstName: {
      required,
      minLength: minLength(1),
      maxLength: maxLength(50),
    },
    lastName: {
      required,
      minLength: minLength(1),
      maxLength: maxLength(50),
    },
    email: {
      required,
      email,
      minLength: minLength(5),
      maxLength: maxLength(254),
    },
  },
};

async function saveAccount(settingsAccount: any) {
  try {
    await axios.post('api/account', settingsAccount);
    return { success: 'OK' };
  } catch (ex) {
    if (ex.response.status === 400 && ex.response.data.type === EMAIL_ALREADY_USED_TYPE) {
      return { errorEmailExists: 'ERROR' };
    }
    return { error: 'ERROR' };
  }
}

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'Settings',
  validations,
  setup() {
    const store = useStore();

    const success: Ref<string> = ref(null);
    const error: Ref<string> = ref(null);
    const errorEmailExists: Ref<string> = ref(null);

    const settingsAccount = computed(() => store.account);
    const username = inject<ComputedRef<string>>('currentUsername', () => computed(() => store.account?.login), true);

    return {
      success,
      error,
      errorEmailExists,
      settingsAccount,
      username,
      v$: useVuelidate(),
      languages: languages(),
      t$: useI18n().t,
    };
  },
  methods: {
    async save() {
      const result = await saveAccount(this.settingsAccount);
      this.success = result.success || null;
      this.error = result.error || null;
      this.errorEmailExists = result.errorEmailExists || null;
    },
  },
});
