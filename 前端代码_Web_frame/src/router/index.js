import Vue from 'vue';
import VueRouter from 'vue-router';
import ApplicationRouter from '@/views/application/router';
import Login from '../views/Login.vue';

const originalPush = VueRouter.prototype.push;
VueRouter.prototype.push = function push(location) {
  return originalPush.call(this, location).catch(err => err)
};

Vue.use(VueRouter);

const routes = [
  {
    path: '/',
    name: 'login',
    component: Login
  },
  //#region 消耗品申请系统
  ...ApplicationRouter,
  //#endregion
  {
    path: '/application/userInfo',
    name: 'a_userInfo',
    component: () => import('../views/application/UserInfo.vue')
  },
  {
    path: '/application/userPassword',
    name: 'a_userPassword',
    component: () => import('../views/application/UserPassword.vue')
  },

  // 消耗品管理系统
  {
    path: '/consumables/home',
    name: 'c_home',
    component: () => import('../views/consumables/Home.vue')
  },
  {
    path: '/consumables/permission',
    name: 'c_permission',
    component: () => import('../views/consumables/Permission.vue')
  },
  {
    path: '/consumables/role',
    name: 'c_role',
    component: () => import('../views/consumables/Role.vue')
  },
  {
    path: '/consumables/user',
    name: 'c_user',
    component: () => import('../views/consumables/User.vue')
  },
  {
    path: '/consumables/applicationUser',
    name: 'c_applicationUser',
    component: () => import('../views/consumables/ApplicationUser.vue')
  },
  {
    path: '/consumables/type',
    name: 'c_type',
    component: () => import('../views/consumables/Type.vue')
  },
  {
    path: '/consumables/referenceInformation',
    name: 'c_referenceInformation',
    component: () => import('../views/consumables/ReferenceInformation.vue')
  },
  {
    path: '/consumables/consumables',
    name: 'c_consumables',
    component: () => import('../views/consumables/Consumables.vue')
  },
  {
    path: '/consumables/application',
    name: 'c_application',
    component: () => import('../views/consumables/Application.vue')
  },
  {
    path: '/consumables/operate',
    name: 'c_operate',
    component: () => import('../views/consumables/Operate.vue')
  },
  {
    path: '/consumables/statistics',
    name: 'c_statistics',
    component: () => import('../views/consumables/Statistics.vue')
  },
  {
    path: '/consumables/userInfo',
    name: 'c_userInfo',
    component: () => import('../views/consumables/UserInfo.vue')
  },
  {
    path: '/consumables/userPassword',
    name: 'c_userPassword',
    component: () => import('../views/consumables/UserPassword.vue')
  },
  // 供应商管理
  {
    path: '/consumables/supplier',
    name: 'c_supplier',
    component: () => import('../views/consumables/Supplier.vue')
  },
  // 报废管理
  {
    path: '/consumables/scrap',
    name: 'c_scrap',
    component: () => import('../views/consumables/Scrap.vue')
  },

  // 仓库管理系统
  {
    path: '/warehouse/home',
    name: 'w_home',
    component: () => import('../views/warehouse/Home.vue')
  },
  {
    path: '/warehouse/user',
    name: 'w_user',
    component: () => import('../views/warehouse/User.vue')
  },
  {
    path: '/warehouse/warehouse',
    name: 'w_warehouse',
    component: () => import('../views/warehouse/Warehouse.vue')
  },
  {
    path: '/warehouse/userInfo',
    name: 'w_userInfo',
    component: () => import('../views/warehouse/UserInfo.vue')
  },
  {
    path: '/warehouse/userPassword',
    name: 'w_userPassword',
    component: () => import('../views/warehouse/UserPassword.vue')
  },

  // common
  {
    path: '/common/refresh',
    name: 'refresh',
    component: () => import('../views/common/Refresh.vue')
  }
];

const router = new VueRouter({
  mode: 'hash',
  base: process.env.BASE_URL,
  routes
});

export default router;