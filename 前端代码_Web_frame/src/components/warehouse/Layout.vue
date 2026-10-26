<template>
  <el-container class="custom-home-container">
    <el-header class="custom-header-container">
      <el-row :gutter="20">
        <el-col :span="8">
          <div class="custom-header-left">
            <el-avatar class="custom-logo" :src="this.$store.state.defaultLogo"></el-avatar>
            <h1 class="custom-title">仓库管理系统</h1>
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
                class="system-switching"
                placeholder="切换当前系统"
                v-model="curSystem"
                @change="handlecurcurSystemChange"
              >
                <el-option
                  v-for="item in systems"
                  :key="item.key"
                  :label="item.value"
                  :value="item.key"
                ></el-option>
              </el-select>
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
        >
          <el-menu-item index="/warehouse/home">
            <i class="el-icon-s-home"></i>
            <span slot="title">首 页</span>
          </el-menu-item>
          <el-menu-item index="/warehouse/user">
            <i class="el-icon-s-custom"></i>
            <span slot="title">人员管理</span>
          </el-menu-item>
          <el-menu-item index="/warehouse/warehouse">
            <i class="el-icon-s-management"></i>
            <span slot="title">仓库管理</span>
          </el-menu-item>
          <el-menu-item index="/warehouse/userInfo">
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
import { logoutApi, switchSystemApi } from "../../api/login";

export default {
  name: "LayoutComponent",
  props: {
    activeMenu: String
  },
  data: function() {
    return {
      systems: [
        { key: "1", value: "消耗品管理系统" },
        { key: "2", value: "消耗品申请系统" },
        { key: "3", value: "仓库管理系统" }
      ],
      curSystem: "3"
    };
  },
  computed: {},
  methods: {
    handlecurcurSystemChange() {
      switchSystemApi().then(res => {
        this.$store.commit("updateWarehouses", res.data);
        switch (this.curSystem) {
          case "1":
            if (res.data && res.data.length > 0) {
              this.$store.commit(
                "initCurWarehouse",
                this.$store.state.userInfo.warehouses[0]
              );
              this.$router.push("/consumables/home");
            } else {
              this.curSystem = "3";
              Message.error("切换错误：暂无仓库");
            }
            break;
          case "2":
            this.$router.push("/application/application");
            break;
          case "3":
            break;
        }
      });
    },
    settingItemClick(command) {
      switch (command) {
        case "showUserInfo":
          this.$router.push("/warehouse/userInfo");
          break;
        case "showUpdatePassword":
          this.$router.push("/warehouse/userPassword");
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