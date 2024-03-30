import { useAccountStore as useStore } from '@/shared/config/store/account-store';
import { useTranslationStore } from '@/shared/config/store/translation-store';

export type AccountStore = ReturnType<typeof useStore>;
export { useStore };

export type TranslationStore = ReturnType<typeof useTranslationStore>;
export { useTranslationStore };
