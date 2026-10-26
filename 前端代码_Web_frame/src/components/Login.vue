<template>
  <div id="component-login">
    <img class="bg" src="../assets/images/bg.jpg" />
    <el-container class="login-container">
      <el-header class="login-header">
        <el-row>
          <el-col :span="12">
            <div class="logo">
              <img src="../assets/images/clogo.png" />
            </div>
          </el-col>
          <el-col class="meun-container" :span="12">
            <el-menu class="menu-list" mode="horizontal" :default-active="activeIndex">
              <el-menu-item index="1">首页</el-menu-item>
            </el-menu>
          </el-col>
        </el-row>
      </el-header>
      <el-main class="login-main">
        <div class="panel-container">
          <div class="login-panel" v-show="currentPanel === 'login'">
            <div class="login-panel-title">
              <span class="secure-login">安全登录</span>
              <span style="font-size: 13px; font-style: italic;">
                或者
                <el-tooltip
                  class="item"
                  effect="dark"
                  v-model="isShowToolTip"
                  content="申请的账号仅用于登录消耗品申请系统用"
                  placement="top-start"
                >
                  <span class="free-account" @click="showRegisterPanel">申请账号</span>
                </el-tooltip>
              </span>
            </div>
            <el-form
              class="login-form"
              :model="loginModel"
              :rules="loginModelRules"
              ref="login-form"
              label-width="70px"
              status-icon
            >
              <el-form-item label="系统：" prop="target">
                <el-select v-model="loginModel.target" placeholder="请选择要登录的系统">
                  <el-option label="消耗品管理系统" value="1"></el-option>
                  <el-option label="消耗品申请系统" value="2"></el-option>
                  <el-option label="仓库管理系统" value="3"></el-option>
                </el-select>
              </el-form-item>
              <el-form-item label="账号：" prop="account">
                <el-input v-model="loginModel.account" placeholder="手机号或邮箱" clearable></el-input>
              </el-form-item>
              <el-form-item label="密码：" prop="password">
                <el-input v-model="loginModel.password" placeholder="请输入密码" show-password></el-input>
              </el-form-item>
              <el-form-item>
                <el-button @click="clearLoginForm">取消</el-button>
                <el-button class="pl-btn-login" type="primary" @click="login">登录</el-button>
              </el-form-item>
            </el-form>
          </div>
          <div class="register-panel" v-show="currentPanel === 'register'">
            <div class="register-panel-title">
              <span class="already-have-an-account">已有账号 ? </span>
              <span style="font-size: 13px; font-style: italic;">前往 
                <span class="go-to-login" @click="showLoginPanel">登录</span>
              </span>
            </div>
            <el-form
              class="register-form"
              :model="registerModel"
              :rules="registerModelRules"
              ref="register-form"
              label-width="70px"
              status-icon
            >
              <el-form-item label="姓名：" prop="name">
                <el-input v-model="registerModel.name" placeholder="真实姓名" clearable></el-input>
              </el-form-item>
              <el-form-item label="性别：" prop="gender">
                <el-radio-group v-model="registerModel.gender">
                  <el-radio label="男">男</el-radio>
                  <el-radio label="女">女</el-radio>
                </el-radio-group>
              </el-form-item>
              <el-form-item label="邮箱：" prop="email">
                <el-input v-model="registerModel.email" placeholder="请输入密码"></el-input>
              </el-form-item>
              <el-form-item label="手机号：" prop="phone">
                <el-input v-model="registerModel.phone" placeholder="请输入密码"></el-input>
              </el-form-item>
              <el-form-item label="账号：" prop="account">
                <el-input v-model="registerModel.account" placeholder="手机号或邮箱" clearable></el-input>
              </el-form-item>
              <el-form-item label="密码：" prop="password">
                <el-input v-model="registerModel.password" placeholder="请输入密码" show-password></el-input>
              </el-form-item>
              <el-form-item label="部门：" prop="department">
                <el-input v-model="registerModel.department" placeholder="请输入部门"></el-input>
              </el-form-item>
              <el-form-item>
                <el-button @click="cancelRegister">取消</el-button>
                <el-button class="pl-btn-login" type="primary" @click="register('register-form')">注册</el-button>
              </el-form-item>
            </el-form>
          </div>
        </div>
      </el-main>
      <el-footer class="login-footer">
        <p>Copyright © 2023. All rights reserved.</p>
      </el-footer>
    </el-container>
  </div>
</template>

<script>
import { Message } from "element-ui";

import {
  validateEmail,
  validatePhone,
  validatePassword
} from "../utils/validator";

import { registerApi } from "../api/login";

export default {
  name: "LoginComponent",
  props: [],
  data: function() {
    var checkAccount = (rule, value, callback) => {
      if (!(validateEmail(value) || validatePhone(value))) {
        callback(new Error("请输入格式正确的手机号或邮箱"));
      } else {
        callback();
      }
    };
    var checkPassword = (rule, value, callback) => {
      if (!validatePassword(value)) {
        callback(new Error("请输入格式正确的密码(6-20个字母、数字、下划线 )"));
      } else {
        callback();
      }
    };
    var checkEmail = (rule, value, callback) => {
      if (value && !validateEmail(value)) {
        callback(new Error("请输入格式正确的邮箱"));
      } else {
        callback();
      }
    };
    var checkPhone = (rule, value, callback) => {
      if (value && !validatePhone(value)) {
        callback(new Error("请输入格式正确的电话号码"));
      } else {
        callback();
      }
    };
    return {
      activeIndex: "1",
      isShowToolTip: true,
      currentPanel: "login",
      loginModel: {
        target: "",
        account: "",
        password: "",
        rememberMe: false,
        type: "WEB"
      },
      loginModelRules: {
        target: [
          { required: true, message: "请选择登录系统", trigger: "blur" },
          { required: true, message: "请选择登录系统", trigger: "change" }
        ],
        account: [
          { required: true, message: "请输入手机号或邮箱", trigger: "blur" },
          { validator: checkAccount, trigger: "change" },
          { validator: checkAccount, trigger: "blur" }
        ],
        password: [
          { required: true, message: "请输入密码", trigger: "blur" },
          { validator: checkPassword, trigger: "change" },
          { validator: checkPassword, trigger: "blur" }
        ]
      },
      registerModel: {
        name: "",
        gender: "",
        email: "",
        phone: "",
        account: "",
        password: "",
        department: ""
      },
      registerModelRules: {
        name: [
          { required: true, message: "请输入姓名", trigger: "blur" },
          { min: 2, max: 6, message: "姓名长度范围为2~6", trigger: "blur" },
          { min: 2, max: 6, message: "姓名长度范围为2~6", trigger: "change" }
        ],
        gender: [
          { required: true, message: "请选择性别", trigger: "blur" },
          { required: true, message: "请选择性别", trigger: "blur" },
          { required: true, message: "请选择性别", trigger: "change" }
        ],
        email: [
          { validator: checkEmail, trigger: "change" },
          { validator: checkEmail, trigger: "blur" }
        ],
        phone: [
          { validator: checkPhone, trigger: "change" },
          { validator: checkPhone, trigger: "blur" }
        ],
        account: [
          { required: true, message: "请输入手机号或邮箱", trigger: "blur" },
          { validator: checkAccount, trigger: "change" },
          { validator: checkAccount, trigger: "blur" }
        ],
        password: [
          { required: true, message: "请输入密码", trigger: "blur" },
          { validator: checkPassword, trigger: "change" },
          { validator: checkPassword, trigger: "blur" }
        ],
        department: [
          { required: true, message: "请输入所属部门", trigger: "blur" },
          { min: 1, max: 99, message: "部门长度范围为1~99", trigger: "blur" },
          { min: 1, max: 99, message: "部门长度范围为1~99", trigger: "change" }
        ]
      }
    };
  },
  mounted: function() {},
  watch: {},
  computed: {},
  methods: {
    showRegisterPanel() {
      this.currentPanel = "register";
      this.clearLoginForm();
    },
    showLoginPanel() {
      this.currentPanel = "login";
      this.clearRegisterForm();
    },
    clearLoginForm() {
      this.$refs["login-form"].resetFields();
    },
    clearRegisterForm() {
      this.$refs["register-form"].resetFields();
    },
    cancelRegister() {
      this.showLoginPanel();
    },
    login() {
      this.$refs["login-form"].validate(valid => {
        if (valid) {
          this.$emit("doLogin", this.loginModel);
        } else {
          return false;
        }
      });
    },
    register(formName) {
      this.$refs[formName].validate(valid => {
        if (valid) {
          registerApi(this.registerModel).then(() => {
        Message.success("注册成功");
        this.loginModel.target = "2";
        this.loginModel.account = this.registerModel.account;
        this.loginModel.password = this.registerModel.password;
        this.showLoginPanel();
      });
        } else {
          return false;
        }
      });
    }
  }
};
</script>

<style scoped lang="less">
#component-login {
  position: relative;
  height: 100%;
  overflow: hidden;
  background-color: #f5f5f5;
  
  .bg {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
  }

  .login-container {
    position: absolute;
    z-index: 2;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;

    .login-header {
      padding: 0 100px;
      height: 100px;
      background-color: #fff;
      opacity: 0.8;

      .logo {
        img {
          width: 300px;
          height: 60px;
        }
      }

      .meun-container {
        float: right;
        width: 500px;
        height: 60px;
        overflow: hidden;
      }
    }

    .login-main {
      display: flex;
      flex-flow: row nowrap;
      justify-content: flex-end;
      align-items: center;
      height: calc(100% - 120px);

      .panel-container {
        display: flex;
        flex-flow: row nowrap;
        justify-content: center;
        align-items: center;
        width: 600px;

        .login-panel {
          width: 400px;
          height: 330px;
          background-color: #f1f1f6;
          border-radius: 5px;

          .login-panel-title {
            padding: 0 15px;
            height: 50px;
            line-height: 50px;
            background-color: #8590a6;
            border-radius: 5px 5px 0 0;

            .secure-login {
              font-weight: 700;
            }

            .free-account {
              &:hover {
                text-decoration: underline;
                color: #fff;
                cursor: pointer;
              }
            }
          }

          .login-form {
            padding: 15px 15px;

            .remember-me {
              margin-right: 82px;
            }

            .forget-password {
              height: 30px;
              line-height: 30px;
              text-align: center;

              span {
                font-size: 12px;
                color: #175199;
                cursor: pointer;

                &:hover {
                  color: #8590a6;
                }
              }
            }
          }
        }
        
        .register-panel {
          width: 400px;
          height: 560px;
          background-color: #f1f1f6;
          border-radius: 5px;

          .register-panel-title {
            padding: 0 15px;
            height: 50px;
            line-height: 50px;
            background-color: #8590a6;
            border-radius: 5px 5px 0 0;

            .already-have-an-account {
              font-weight: 700;
            }

            .go-to-login {
              &:hover {
                text-decoration: underline;
                color: #fff;
                cursor: pointer;
              }
            }
          }

          .register-form {
            padding: 15px 15px;
          }
        }
      }
    }
    .login-footer {
      text-align: center;
      line-height: 40px;
      font-size: 12px;
      color: #f5f5f5;
    }
  }
}
</style>
