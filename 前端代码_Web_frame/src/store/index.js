import Vue from 'vue';
import Vuex from 'vuex';
import createPersistedState from 'vuex-persistedstate';
import * as getters from './getters';
import * as actions from './actions';
import * as mutations from './mutations';

Vue.use(Vuex);

const state = {
  token: "",
  userInfo: null,
  systemPermissions: [],
  systemPermissionsType: [],
  defaultLogo: "",
  curWarehouse: "",
  alarmConsumables: []
};

const store = new Vuex.Store({
  plugins: [createPersistedState({
    storage: window.sessionStorage
  })],
  state,
  getters,
  actions,
  mutations
});

export default store;