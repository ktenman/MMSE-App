import { Authority } from '@/shared/security/authority';

const MmseUserManagementComponent = () => import('@/admin/user-management/user-management.vue');
const MmseUserManagementViewComponent = () => import('@/admin/user-management/user-management-view.vue');
const MmseUserManagementEditComponent = () => import('@/admin/user-management/user-management-edit.vue');
const MmseDocsComponent = () => import('@/admin/docs/docs.vue');
const MmseConfigurationComponent = () => import('@/admin/configuration/configuration.vue');
const MmseHealthComponent = () => import('@/admin/health/health.vue');
const MmseLogsComponent = () => import('@/admin/logs/logs.vue');
const MmseMetricsComponent = () => import('@/admin/metrics/metrics.vue');

export default [
  {
    path: '/admin/user-management',
    name: 'MmseUser',
    component: MmseUserManagementComponent,
    meta: { authorities: [Authority.ADMIN] },
  },
  {
    path: '/admin/user-management/new',
    name: 'MmseUserCreate',
    component: MmseUserManagementEditComponent,
    meta: { authorities: [Authority.ADMIN] },
  },
  {
    path: '/admin/user-management/:userId/edit',
    name: 'MmseUserEdit',
    component: MmseUserManagementEditComponent,
    meta: { authorities: [Authority.ADMIN] },
  },
  {
    path: '/admin/user-management/:userId/view',
    name: 'MmseUserView',
    component: MmseUserManagementViewComponent,
    meta: { authorities: [Authority.ADMIN] },
  },
  {
    path: '/admin/docs',
    name: 'MmseDocsComponent',
    component: MmseDocsComponent,
    meta: { authorities: [Authority.ADMIN] },
  },
  {
    path: '/admin/health',
    name: 'MmseHealthComponent',
    component: MmseHealthComponent,
    meta: { authorities: [Authority.ADMIN] },
  },
  {
    path: '/admin/logs',
    name: 'MmseLogsComponent',
    component: MmseLogsComponent,
    meta: { authorities: [Authority.ADMIN] },
  },
  {
    path: '/admin/metrics',
    name: 'MmseMetricsComponent',
    component: MmseMetricsComponent,
    meta: { authorities: [Authority.ADMIN] },
  },
  {
    path: '/admin/configuration',
    name: 'MmseConfigurationComponent',
    component: MmseConfigurationComponent,
    meta: { authorities: [Authority.ADMIN] },
  },
];
