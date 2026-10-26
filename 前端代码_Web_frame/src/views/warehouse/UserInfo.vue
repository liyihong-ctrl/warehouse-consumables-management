<template>
  <LayoutComponent :active-menu="menuName">
    <el-breadcrumb separator-class="el-icon-arrow-right">
      <el-breadcrumb-item v-for="(item,index) in breadcrumbs" :key="index">{{item}}</el-breadcrumb-item>
    </el-breadcrumb>
    <hr />
    <el-container class="custom-el-container" v-show="isShowUserInfoContainer">
      <el-main class="custom-el-main">
        <div class="warehouse-userinfo-container">
          <el-scrollbar style="height: 100%">
            <el-form id="userinfo-form" ref="userinfo-form" label-width="160px">
              <el-form-item label="角色：">
                <span>{{ userModel.typeName }}</span>
              </el-form-item>
              <el-form-item label="账号：">
                <span>{{ userModel.account }}</span>
              </el-form-item>
              <el-form-item label="姓名：">
                <span>{{ userModel.name }}</span>
              </el-form-item>
              <el-form-item label="头像：">
                <el-image class="user-avatar" :src="userModel.avatar" v-if="userModel.avatar"></el-image>
              </el-form-item>
              <el-form-item label="性别：">
                <span>{{ userModel.gender }}</span>
              </el-form-item>
              <el-form-item label="邮箱：" v-show="userModel.email">
                <span>{{ userModel.email }}</span>
              </el-form-item>
              <el-form-item label="电话：" v-show="userModel.phone">
                <span>{{ userModel.phone }}</span>
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
        <div class="warehouse-edit-userinfo-container">
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
                  <span>{{ editUserModel.typeName }}</span>
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
                  action="http://127.0.0.1:8888/wUser/uploadAvatar"
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
import LayoutComponent from "@/components/warehouse/Layout.vue";
import {
  validateEmail,
  validatePhone,
  validateImgFormat,
  validateImgSize
} from "../../utils/validator";
import { editUserInfoApi } from "../../api/warehouse/userinfo";

export default {
  name: "w_userInfo",
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
      menuName: "/warehouse/userInfo",
      breadcrumbs: ["个人信息"],
      isShowUserInfoContainer: true,
      isShowEditUserContainer: false,
      userType: [
        {
          key: "1",
          value: "系统管理员"
        },
        {
          key: "2",
          value: "仓库管理员"
        }
      ],
      userModel: {
        id: "",
        account: "",
        name: "",
        gender: "",
        avatar: "",
        email: "",
        phone: "",
        typeName: ""
      },
      editUserModel: {
        id: "",
        account: "",
        name: "",
        gender: "",
        avatar: "",
        email: "",
        phone: "",
        typeName: ""
      },
      userRules: {
        name: [
          { required: true, message: "请输入姓名", trigger: "blur" },
          { min: 1, max: 20, message: "姓名长度范围为1~20", trigger: "blur" },
          { min: 1, max: 20, message: "姓名长度范围为1~20", trigger: "change" }
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
        ]
      }
    };
  },
  methods: {
    genInitData() {
      this.userModel = {
        id: this.$store.state.userInfo.id,
        account: this.$store.state.userInfo.account,
        name: this.$store.state.userInfo.name,
        gender: this.$store.state.userInfo.gender,
        avatar: this.$store.state.userInfo.avatar,
        email: this.$store.state.userInfo.email,
        phone: this.$store.state.userInfo.phone,
        typeName: this.userType.find(item => {
          return item.key === this.$store.state.userInfo.type;
        }).value
      };
    },
    showEditUserContainer() {
      this.editUserModel = {
        id: this.$store.state.userInfo.id,
        account: this.$store.state.userInfo.account,
        name: this.$store.state.userInfo.name,
        gender: this.$store.state.userInfo.gender,
        avatar: this.$store.state.userInfo.avatar,
        email: this.$store.state.userInfo.email,
        phone: this.$store.state.userInfo.phone,
        typeName: this.userType.find(item => {
          return item.key === this.$store.state.userInfo.type;
        }).value
      };
      this.isShowUserInfoContainer = !this.isShowUserInfoContainer;
      this.isShowEditUserContainer = !this.isShowEditUserContainer;
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
.warehouse-userinfo-container,
.warehouse-edit-userinfo-container {
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