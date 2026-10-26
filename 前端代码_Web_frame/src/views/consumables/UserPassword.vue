<template>
  <LayoutComponent :active-menu="menuName">
    <el-breadcrumb separator-class="el-icon-arrow-right">
      <el-breadcrumb-item v-for="(item,index) in breadcrumbs" :key="index">{{item}}</el-breadcrumb-item>
    </el-breadcrumb>
    <hr />
    <el-container class="custom-el-container">
      <el-main class="custom-el-main">
        <div class="consumable-edit-userpassword-container">
          <el-scrollbar style="height: 100%">
            <el-form
              id="edit-password-form"
              label-width="80px"
              :model="editPassModel"
              :rules="editPassModelRules"
              ref="edit-password-form"
              status-iconv-if="true"
            >
              <el-form-item label="旧密码" prop="oldPass">
                <el-input
                  placeholder="请输入旧密码"
                  v-model="editPassModel.oldPass"
                  show-password
                  clearable
                ></el-input>
              </el-form-item>
              <el-form-item label="新密码" prop="newPass">
                <el-input
                  placeholder="请输入新密码"
                  v-model="editPassModel.newPass"
                  show-password
                  clearable
                ></el-input>
              </el-form-item>
              <el-form-item label="确认" prop="confirmPass">
                <el-input
                  placeholder="请输入确认密码"
                  v-model="editPassModel.confirmPass"
                  show-password
                  clearable
                ></el-input>
              </el-form-item>
              <el-form-item>
                <el-button @click="goBack">取消</el-button>
                <el-button type="primary" @click="submitEditPasswordForm('edit-password-form')">保存</el-button>
              </el-form-item>
            </el-form>
          </el-scrollbar>
        </div>
      </el-main>
    </el-container>
  </LayoutComponent>
</template>

<script>
import { Message } from "element-ui";
import LayoutComponent from "@/components/consumables/Layout.vue";
import { validatePassword } from "../../utils/validator";
import { editUserPassword } from "../../api/consumables/userinfo";

export default {
  name: "c_userPassword",
  components: {
    LayoutComponent
  },
  mounted() {
    this.genInitData();
  },
  data: function() {
    var checkPassword = (rule, value, callback) => {
      if (!validatePassword(value)) {
        callback(new Error("请输入格式正确的密码(6-20个字母、数字、下划线 )"));
      } else {
        callback();
      }
    };
    var checkPasswordConfirm = (rule, value, callback) => {
      if (this.editPassModel.newPass !== value) {
        callback(new Error("两次密码不一致"));
      } else {
        callback();
      }
    };
    return {
      menuName: "/warehouse/userPassword",
      breadcrumbs: ["个人信息", "修改密码"],
      editPassModel: {
        oldPass: "",
        newPass: "",
        confirmPass: ""
      },
      editPassModelRules: {
        oldPass: [
          { required: true, message: "请输入旧密码", trigger: "blur" },
          { validator: checkPassword, trigger: "change" },
          { validator: checkPassword, trigger: "blur" }
        ],
        newPass: [
          { required: true, message: "请输入新密码", trigger: "blur" },
          { validator: checkPassword, trigger: "change" },
          { validator: checkPassword, trigger: "blur" }
        ],
        confirmPass: [
          { required: true, message: "请输入确认密码", trigger: "blur" },
          { validator: checkPasswordConfirm, trigger: "change" },
          { validator: checkPasswordConfirm, trigger: "blur" }
        ]
      }
    };
  },
  methods: {
    genInitData() {},
    goBack() {
      this.$router.go(-1);
    },
    submitEditPasswordForm(formName) {
      var that = this;
      that.$refs[formName].validate(valid => {
        if (valid) {
          editUserPassword(that.editPassModel).then(() => {
            Message.success("修改成功，请重新登录！");
            that.$store.commit("logout");
            that.$router.push("/");
          });
        }
      });
    }
  }
};
</script>

<style lang="less">
.consumable-edit-userpassword-container {
  height: 100%;
  overflow-x: hidden;
  overflow-y: auto;

  #edit-password-form {
    width: 400px;
  }
}
</style>