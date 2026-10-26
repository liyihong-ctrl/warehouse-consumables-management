<template>
  <LayoutComponent :active-menu="menuName">
    <el-breadcrumb separator-class="el-icon-arrow-right">
      <el-breadcrumb-item v-for="(item,index) in breadcrumbs" :key="index">{{item}}</el-breadcrumb-item>
    </el-breadcrumb>
    <hr />
    <el-container class="custom-el-container" v-show="isShowUserInfoContainer">
      <el-main class="custom-el-main">
        <div class="consumable-userinfo-container">
          <el-scrollbar style="height: 100%">
            <el-form
              id="userinfo-form"
              label-width="160px"
            >
              <el-form-item label="角色：">
                <span v-for="(item, index) in editUserModel.roleNames" :key="index">{{ item }}</span>
              </el-form-item>
              <el-form-item label="账号：">
                <span>{{ editUserModel.account }}</span>
              </el-form-item>
              <el-form-item label="姓名：">
                <span>{{ editUserModel.name }}</span>
              </el-form-item>
              <el-form-item label="头像：">
                <el-image
                  class="user-avatar"
                  :src="editUserModel.avatar"
                  v-if="editUserModel.avatar"
                ></el-image>
              </el-form-item>
              <el-form-item label="性别：">
                <span>{{ editUserModel.gender }}</span>
              </el-form-item>
              <el-form-item label="邮箱：" v-show="editUserModel.email">
                <span>{{ editUserModel.email }}</span>
              </el-form-item>
              <el-form-item label="电话：" v-show="editUserModel.phone">
                <span>{{ editUserModel.phone }}</span>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="showEditUserContainer()">编辑</el-button>
              </el-form-item>
            </el-form>
          </el-scrollbar>
        </div>
      </el-main>
    </el-container>
    <el-container class="custom-el-container" v-show="isShowEditUserContainer">
      <el-main class="custom-el-main">
        <div class="consumable-edit-userinfo-container">
          <el-scrollbar style="height: 100%">
            <el-form
              id="edit-userinfo-form"
              label-width="160px"
              ref="edit-userinfo-form"
              status-icon
              :model="editUserModel"
              :rules="userRules"
            >
              <el-form-item label="角色：">
                <el-tooltip effect="light" content="角色无法修改" placement="right">
                  <span v-for="(item, index) in editUserModel.roleNames" :key="index">{{ item }}</span>
                </el-tooltip>
              </el-form-item>
              <el-form-item label="账号：">
                <el-tooltip effect="light" content="账号无法修改" placement="right">
                  <span>{{ editUserModel.account }}</span>
                </el-tooltip>
              </el-form-item>
              <el-form-item label="姓名：" prop="name">
                <el-input placeholder="姓名" v-model="editUserModel.name" clearable></el-input>
              </el-form-item>
              <el-form-item label="头像：">
                <el-upload
                  id="avatar-uploader"
                  action="http://127.0.0.1:8888/cUser/uploadAvatar"
                  :headers="{Authorization: this.$store.state.token}"
                  :show-file-list="false"
                  :on-success="handleEditAvatarSuccess"
                  :before-upload="beforeAvatarUpload"
                >
                  <img
                    class="avatar"
                    :size="60"
                    :src="editUserModel.avatar"
                    v-if="editUserModel.avatar"
                    :key="editUserModel.avatar"
                  />
                  <i class="el-icon-plus avatar-uploader-icon" v-else></i>
                </el-upload>
              </el-form-item>
              <el-form-item label="性别：" prop="gender">
                <el-radio-group v-model="editUserModel.gender">
                  <el-radio label="男">男</el-radio>
                  <el-radio label="女">女</el-radio>
                </el-radio-group>
              </el-form-item>
              <el-form-item label="邮箱：" prop="email">
                <el-input placeholder="邮箱" v-model="editUserModel.email" clearable></el-input>
              </el-form-item>
              <el-form-item label="电话：" prop="phone">
                <el-input placeholder="电话" v-model="editUserModel.phone" clearable></el-input>
              </el-form-item>
              <el-form-item>
                <el-button @click="hideEditUserContainer">取消</el-button>
                <el-button type="primary" @click="submitEditUserForm('edit-userinfo-form')">保存</el-button>
              </el-form-item>
            </el-form>
          </el-scrollbar>
        </div>
      </el-main>
    </el-container>
  </LayoutComponent>
</template>

<script>
import { Message, Loading } from "element-ui";
import LayoutComponent from "@/components/consumables/Layout.vue";
import {
  validateEmail,
  validatePhone,
  validateImgFormat,
  validateImgSize
} from "../../utils/validator";
import { editUserInfoApi } from "../../api/consumables/userinfo";

export default {
  name: "c_userInfo",
  components: {
    LayoutComponent
  },
  mounted() {
    this.genInitData();
  },
  data: function() {
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
      menuName: "/consumables/userInfo",
      breadcrumbs: ["个人信息"],
      isShowUserInfoContainer: true,
      isShowEditUserContainer: false,
      editUserModel: {
        id: "",
        account: "",
        name: "",
        gender: "",
        avatar: "",
        email: "",
        phone: "",
        roleNames: []
      },
      userRules: {
        name: [
          { required: true, message: "请输入姓名", trigger: "blur" },
          { min: 1, max: 20, message: "姓名长度范围为1~20", trigger: "blur" },
          { min: 1, max: 20, message: "姓名长度范围为1~20", trigger: "change" }
        ],
        gender: [
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
        ]
      }
    };
  },
  methods: {
    genInitData() {
      var roleNames = [];
      this.$store.state.userInfo.roles.forEach((item, index) => {
        roleNames.push(item.name);
        if (this.$store.state.userInfo.roles.length - 1 > index) {
          roleNames.push("、");
        }
      });
      this.editUserModel.id = this.$store.state.userInfo.id;
      this.editUserModel.account = this.$store.state.userInfo.account;
      this.editUserModel.name = this.$store.state.userInfo.name;
      this.editUserModel.gender = this.$store.state.userInfo.gender;
      this.editUserModel.avatar = this.$store.state.userInfo.avatar;
      this.editUserModel.email = this.$store.state.userInfo.email;
      this.editUserModel.phone = this.$store.state.userInfo.phone;
      this.editUserModel.roleNames = roleNames;
    },
    showEditUserContainer() {
      this.isShowEditUserContainer = !this.isShowEditUserContainer;
      this.isShowUserInfoContainer = !this.isShowUserInfoContainer;
      this.breadcrumbs.push("编辑");
    },
    hideEditUserContainer() {
      this.genInitData();
      this.isShowEditUserContainer = !this.isShowEditUserContainer;
      this.isShowUserInfoContainer = !this.isShowUserInfoContainer;
      this.breadcrumbs.pop();
    },
    beforeAvatarUpload(file) {
      if (!validateImgFormat(file.type)) {
        Message.error("上传图片格式错误");
        return false;
      }
      if (!validateImgSize(file.size)) {
        Message.error("上传图片大小不能超过2MB!");
        return false;
      }
      this.loading = Loading.service({
        lock: true,
        text: "上传中....",
        background: "rgba(0, 0, 0, 0.7)"
      });
      return true;
    },
    handleEditAvatarSuccess(res) {
      this.editUserModel.avatar = res.data.imgPath;
      this.loading.close();
    },
    submitEditUserForm(formName) {
      this.$refs[formName].validate(valid => {
        if (valid) {
          var editUserModel = {
            id: this.editUserModel.id,
            name: this.editUserModel.name,
            gender: this.editUserModel.gender,
            avatar: this.editUserModel.avatar,
            email: this.editUserModel.email,
            phone: this.editUserModel.phone
          };
          editUserInfoApi(editUserModel).then(res => {
            Message.success("保存成功");
            this.$store.commit("initUserInfo", res.data);
            this.hideEditUserContainer();
          });
        }
      });
    }
  }
};
</script>

<style lang="less">
.consumable-userinfo-container,
.consumable-edit-userinfo-container {
  height: 100%;
  overflow-x: hidden;
  overflow-y: auto;

  #userinfo-form,
  #edit-userinfo-form {
    width: 500px;

    .user-avatar {
      width: 178px;
      height: 178px;
    }

    #avatar-uploader {
      .el-upload {
        border: 1px dashed #d9d9d9;
        border-radius: 6px;
        cursor: pointer;
        position: relative;
        overflow: hidden;

        &:hover {
          border-color: #409eff;
        }
      }

      .avatar-uploader-icon {
        font-size: 28px;
        color: #8c939d;
        width: 178px;
        height: 178px;
        line-height: 178px;
        text-align: center;
      }

      .avatar {
        width: 178px;
        height: 178px;
        display: block;
      }
    }
  }
}
</style>