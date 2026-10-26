import Vue from "vue";

Vue.config.productionTip = false;

import ElementUI from "element-ui";
Vue.use(ElementUI);

import animated from "animate.css";
Vue.use(animated);

import EleImport from 'babel-loader!vue-ele-import'
Vue.component(EleImport.name, EleImport);

import "element-ui/lib/theme-chalk/index.css";

import "normalize.css/normalize.css";
import "./assets/styles/global.css";
import "./assets/styles/layout.less";

import "./assets/iconfont/iconfont.css";

import App from "./App.vue";

import router from "./router";
import store from "./store/index";
import 'babel-polyfill';

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount("#app");
