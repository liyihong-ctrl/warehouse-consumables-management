<template>
  <div class="login">
    <LoginComponent @doLogin="doLogin" />
  </div>
</template>

<script>
import LoginComponent from "@/components/Login.vue";
import { Message } from "element-ui";
import { loginApi } from "../api/login";

export default {
  name: "login",
  components: {
    LoginComponent
  },
  // inject: ["initWebSocket"],
  data: function() {
    return {};
  },
  methods: {
    doLogin(data) {
      loginApi(data).then(res => {
        Message.success("登录成功");

        this.$store.commit("initToken", res.data.Authorization);
        this.$store.commit("initUserInfo", res.data.userInfo);
        this.$store.commit("initSystemPermissions", res.data.systemPermissions);
        this.$store.commit("initDefaultLogo", res.data.defaultLogo);
        this.$store.commit("initSystemPermissionsType");

        if (res.data.target === "consumables") {
          this.$store.commit(
            "initCurWarehouse",
            res.data.userInfo.warehouses[0]
          );
        }

        // this.initWebSocket();

        var targetSystemPath;
        switch (res.data.target) {
          case "consumables":
          case "warehouse":
            targetSystemPath = "/" + res.data.target + "/home";
            break;
          case "application":
            targetSystemPath = "/" + res.data.target + "/application";
            break;
        }

        this.$router.push(targetSystemPath);
      });
    }
  }
};
</script>

<style scoped lang="less">
.login {
  height: 100%;
}
</style>