<template>
  <el-container class="custom-home-container">
    <el-header class="custom-header-container">
      <el-row :gutter="20">
        <el-col :span="8">
          <div class="custom-header-left">
            <el-avatar class="custom-logo" :src="this.$store.state.defaultLogo"></el-avatar>
            <h1 class="custom-title">消耗品管理系统</h1>
          </div>
        </el-col>
        <el-col :span="16">
          <div class="custom-header-right">
            <div class="custom-userinfo">
              <i class="el-icon-user-solid custom-icon-userinfo"></i>
              <span class="custom-userinfo-name">{{ this.$store.state.userInfo.name }}</span>
            </div>
            <div class="custom-switching">
              <el-select
                class="warehouse-switching"
                placeholder="切换当前仓库"
                v-model="curWarehouseId"
                @change="handlecurWarehouseChange"
                :disabled="!(this.$store.state.userInfo.type === '1' || this.$store.state.userInfo.type === '2')"
              >
                <el-option
                  v-for="item in this.$store.state.userInfo.warehouses"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id"
                ></el-option>
              </el-select>
            </div>
            <div class="custom-warning">
              <el-badge
                :value="this.$store.state.alarmConsumables.length"
                v-if="isShowCurMenu('c_consumables_select')"
              >
                <i
                  class="el-icon-bell custom-icon-warning"
                  title="查看报警消耗品信息"
                  @click="showAlarmConsumables"
                ></i>
              </el-badge>
            </div>
            <div class="custom-setting">
              <el-dropdown @command="settingItemClick">
                <span class="el-dropdown-link" title="设置">
                  <i class="el-icon-s-tools custom-icon-setting el-icon--right"></i>
                </span>
                <el-dropdown-menu slot="dropdown">
                  <el-dropdown-item command="showUserInfo">
                    <i class="el-icon-info"></i>
                    个人信息
                  </el-dropdown-item>
                  <el-dropdown-item command="showUpdatePassword">
                    <i class="el-icon-key"></i>
                    修改密码
                  </el-dropdown-item>
                  <el-dropdown-item command="logout">
                    <i class="el-icon-switch-button"></i>
                    退出登录
                  </el-dropdown-item>
                </el-dropdown-menu>
              </el-dropdown>
            </div>
          </div>
        </el-col>
      </el-row>
    </el-header>
    <el-container class="custom-body-container">
      <el-aside class="custom-aside-container" style="width: 200px;">
        <el-menu
          :router="true"
          :default-active="activeMenu"
          background-color="#3c9d9b"
          text-color="aliceblue"
          active-text-color="aliceblue"
          :default-openeds="openedMenus"
        >
          <el-menu-item index="/consumables/home">
            <i class="el-icon-s-home"></i>
            <span slot="title">首页</span>
          </el-menu-item>
          <el-menu-item index="/consumables/permission">
            <i class="el-icon-unlock"></i>
            <span slot="title">系统权限</span>
          </el-menu-item>
          <el-menu-item index="/consumables/role" v-if="isShowCurMenu('c_role_select')">
            <i class="el-icon-s-custom"></i>
            <span slot="title">角色管理</span>
          </el-menu-item>
          <el-submenu index="multi-level-menu-user" v-if="isShowCurMenu('c_user_select')">
            <template slot="title">
              <i class="el-icon-menu"></i>人员管理
            </template>
            <el-menu-item index="/consumables/user">
              <i class="el-icon-user"></i>
              <span slot="title">管理员</span>
            </el-menu-item>
            <el-menu-item index="/consumables/applicationUser">
              <i class="el-icon-user-solid"></i>
              <span slot="title">申请人员</span>
            </el-menu-item>
          </el-submenu>
          <el-menu-item index="/consumables/type" v-if="isShowCurMenu('c_consumablesType_select')">
            <i class="el-icon-s-platform"></i>
            <span slot="title">类型管理</span>
          </el-menu-item>
          <el-menu-item
            index="/consumables/supplier"
            v-if="isShowCurMenu('c_supplier_select')"
          >
            <i class="el-icon-s-goods"></i>
            <span slot="title">供应商管理</span>
          </el-menu-item>
          <el-menu-item
            index="/consumables/referenceInformation"
            v-if="isShowCurMenu('c_referenceInformationTemplate_select')"
          >
            <i class="el-icon-printer"></i>
            <span slot="title">基准信息模板</span>
          </el-menu-item>
          <el-menu-item
            index="/consumables/consumables"
            v-if="isShowCurMenu('c_consumables_select')"
          >
            <i class="el-icon-s-order"></i>
            <span slot="title">消耗品管理</span>
          </el-menu-item>
          <el-menu-item
            index="/consumables/application"
            v-if="isShowCurMenu('c_application_select')"
          >
            <i class="el-icon-s-promotion"></i>
            <span slot="title">申请管理</span>
          </el-menu-item>
          <el-menu-item index="/consumables/operate" v-if="isShowCurMenu('c_userOperate_select')">
            <i class="el-icon-s-operation"></i>
            <span slot="title">操作履历</span>
          </el-menu-item>
          <el-menu-item index="/consumables/statistics" v-if="isShowCurMenu('c_statistics_select')">
            <i class="el-icon-s-data"></i>
            <span slot="title">统计信息</span>
          </el-menu-item>
          <el-menu-item index="/consumables/scrap">
            <i class="el-icon-warning-outline"></i>
            <span slot="title">报废管理</span>
          </el-menu-item>
          <el-menu-item index="/consumables/userInfo">
            <i class="el-icon-info"></i>
            <span slot="title">个人信息</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      <el-container>
        <el-main class="custom-main-container">
          <slot></slot>
        </el-main>
        <el-footer class="custom-footer-container">
          <p>Copyright © 2023. All rights reserved.</p>
        </el-footer>
      </el-container>
    </el-container>
  </el-container>
</template>

<script>
import { Message } from "element-ui";
import { logoutApi, getAlarmConsumablesApi } from "../../api/login";

export default {
  name: "LayoutComponent",
  props: {
    activeMenu: String,
    openedMenus: Array
  },
  mounted() {
    getAlarmConsumablesApi({
      warehouseId: this.curWarehouseId
    }).then(res => {
      this.$store.commit("initAlarmConsumables", res.data.alarmConsumables);
    });
  },
  data: function() {
    return {
      curWarehouseId: this.$store.state.curWarehouse.id
    };
  },
  computed: {},
  methods: {
    isShowCurMenu(permissionStr) {
      return this.$store.state.userInfo.permissions.find(item => {
        return item.str.indexOf(permissionStr) !== -1;
      });
    },
    handlecurWarehouseChange() {
      var curWarehouse = this.$store.state.userInfo.warehouses.find(item => {
        return item.id === this.curWarehouseId;
      });
      this.$store.commit("initCurWarehouse", curWarehouse);
      this.$emit("curWarehouseChanged");
    },
    showAlarmConsumables() {
      if (this.$route.path === "/consumables/consumables") {
        var params = {
          name: "c_consumables",
          isShowAlarmConsumables: true
        };
        this.$router.push({
          name: "refresh",
          params: params
        });
      } else {
        this.$router.push({
          name: "c_consumables",
          params: {
            isShowAlarmConsumables: true
          }
        });
      }
    },
    settingItemClick(command) {
      switch (command) {
        case "showUserInfo":
          this.$router.push("/consumables/userInfo");
          break;
        case "showUpdatePassword":
          this.$router.push("/consumables/userPassword");
          break;
        case "logout":
          logoutApi().then(res => {
            if (res.success) {
              this.$store.commit("logout");
              this.$router.push("/");
              Message.success("退出登录");
            } else {
              Message.error(res.data.msg);
            }
          });
          break;
      }
    }
  }
};
</script>

<style scoped lang="less">
</style>

