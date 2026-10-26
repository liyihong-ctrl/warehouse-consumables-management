<template>
  <el-container class="custom-home-container">
    <el-header class="custom-header-container">
      <el-row :gutter="20">
        <el-col :span="8">
          <div class="custom-header-left">
            <el-avatar class="custom-logo" :src="this.$store.state.defaultLogo"></el-avatar>
            <h1 class="custom-title">消耗品申请系统</h1>
          </div>
        </el-col>
        <el-col :span="16">
          <div class="custom-header-right">
            <div class="custom-userinfo">
              <i class="el-icon-user-solid custom-icon-userinfo"></i>
              <span class="custom-userinfo-name">{{userInfo.name}}</span>
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
          router
          :default-active="activeMenu"
          background-color="#3c9d9b"
          text-color="aliceblue"
          active-text-color="aliceblue"
        >
          <el-menu-item index="/application/application">
            <i class="el-icon-s-custom"></i>
            <span slot="title">申请管理</span>
          </el-menu-item>
          <el-menu-item index="/application/userInfo">
            <i class="el-icon-info"></i>
            <span slot="title">个人信息</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      <el-container>
        <el-main class="custom-main-container">
          <router-view></router-view>
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
import { mapState } from "vuex";
import { logoutApi } from "../../api/login";

export default {
  name: "LayoutComponent",
  data() {
    return {};
  },
  computed: {
    ...mapState({
      userInfo: state => state.userInfo
    }),
    activeMenu() {
      const { name } = this.$route;
      switch (name) {
        case "list":
        case "action":
          return "/application/application";
        case "userInfo":
          return "/application/userInfo";
        default:
          return "";
      }
    }
  },
  methods: {
    settingItemClick(command) {
      switch (command) {
        case "showUserInfo":
          this.$router.push("/application/userInfo");
          break;
        case "showUpdatePassword":
          this.$router.push("/application/userPassword");
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
