<template>
  <LayoutComponent
    :active-menu="menuName"
    :openedMenus="openedMenus"
    @curWarehouseChanged="curWarehouseChanged"
  >
    <el-breadcrumb separator-class="el-icon-arrow-right">
      <el-breadcrumb-item v-for="(item,index) in breadcrumbs" :key="index">{{item}}</el-breadcrumb-item>
    </el-breadcrumb>
    <hr />
    <el-row class="operation-container" v-show="isShowMainContainer">
      <el-col class="search-container" :span="8">
        <el-input placeholder="请输入搜索内容" v-model="searchStr" clearable style="max-width: 350px;">
          <el-select
            style="width: 90px;"
            v-model="searchType"
            slot="prepend"
            placeholder="请选择"
            @change="searchTypeChanged"
          >
            <el-option label="姓名" value="1"></el-option>
            <el-option label="账号" value="2"></el-option>
            <el-option label="角色" value="3"></el-option>
          </el-select>
        </el-input>
        <el-button class="search-btn" type="primary" icon="el-icon-search" @click="doSearch"></el-button>
      </el-col>
      <el-col class="btn-group" :span="16">
        <el-button
          type="primary"
          plain
          @click="resetPassword"
          v-if="hasPermission('c_user_update')"
          :disabled="!canResetPassword"
        >重置密码</el-button>
        <el-button
          type="danger"
          plain
          @click="deleteUser"
          v-if="hasPermission('c_user_delete')"
          :disabled="!canDelete"
        >删除</el-button>
        <el-button
          type="primary"
          plain
          @click="showUserDetailCntainer"
          v-if="hasPermission('c_user_select')"
        >详情</el-button>
        <el-button
          type="primary"
          plain
          @click="showEditUserContainer(false)"
          v-if="hasPermission('c_user_update')"
          :disabled="!canEdit"
        >编辑</el-button>
        <el-button
          type="primary"
          plain
          @click="showAddUserContainer"
          v-if="hasPermission('c_user_add')"
        >新增</el-button>
        <el-button type="info" plain @click="exportUsers" v-if="hasPermission('c_user_export')">导出</el-button>
        <div style="margin-left: 10px" v-if="hasPermission('c_user_import')">
          <ele-import
            :fields="fields"
            :filepath="filepath"
            :requestFn="importUsers"
            :rules="importUserRules"
            :tips="importTips"
            :title="importDialogTitle"
            :visible.sync="isShowImportDialog"
            @close="handleCloseImportUsersDialog"
            @finish="handleFinishImportUsersDialog"
          />
          <el-button type="info" plain @click="handleOpen">导入</el-button>
        </div>
      </el-col>
    </el-row>
    <el-container class="warehouse-grid-user-container" v-show="isShowMainContainer">
      <el-main>
        <el-table
          ref="user-table"
          :data="users"
          border
          stripe
          height="100%"
          highlight-current-row
          :header-cell-style="setTableHeaderStyle"
          @current-change="handleCurrentChange"
        >
          <el-table-column label="头像" width="65" align="center">
            <template slot-scope="scope">
              <el-avatar shape="square" fit="fill" :src="scope.row.avatar"></el-avatar>
            </template>
          </el-table-column>
          <el-table-column prop="name" label="姓名" width="150" align="center"></el-table-column>
          <el-table-column prop="account" label="账号" width="220" align="center"></el-table-column>
          <el-table-column label="初始密码" width="80">
            <template slot-scope="scope">
              <div class="password-container">
                <el-tooltip effect="light" content="显示密码" placement="bottom-start">
                  <i
                    class="el-icon-view view-password"
                    v-show="!scope.row.isShowPassword"
                    @click="showPassword(scope.row)"
                  ></i>
                </el-tooltip>
                <el-tooltip effect="light" content="隐藏密码" placement="bottom-start">
                  <span
                    class="password"
                    v-show="scope.row.isShowPassword"
                    @click="hidePassword(scope.row)"
                  >{{ scope.row.extension.initializedPassword }}</span>
                </el-tooltip>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="角色" width="180" align="center">
            <template slot-scope="scope">
              <span v-for="(role, index) in scope.row.roles" :key="role.id">
                {{ role.name }}
                <span v-if="index + 1 < scope.row.roles.length">、</span>
              </span>
            </template>
          </el-table-column>
          <el-table-column prop="registerTime" label="创建日期" sortable></el-table-column>
          <el-table-column label="状态" width="65" v-if="hasPermission('c_user_state_management')">
            <template slot-scope="scope">
              <el-tooltip :content="scope.row.isEnable ? '停用' : '启用'" placement="top">
                <el-switch v-model="scope.row.isEnable" @change="handleStatusChanged(scope.row)"></el-switch>
              </el-tooltip>
            </template>
          </el-table-column>
        </el-table>
      </el-main>
    </el-container>
    <div class="page-container" v-show="isShowMainContainer">
      <el-pagination
        background
        layout="total, sizes, prev, pager, next, jumper"
        :page-sizes="[5, 10, 30, 50, 100]"
        :page-size="page.pageSize"
        :total="total"
        :current-page="page.pageNum"
        @size-change="handlePageSizeChange"
        @current-change="handleCurrentPageChange"
      ></el-pagination>
    </div>
    <el-container class="custom-el-container" v-if="isShowUserDetailContainer">
      <el-main class="custom-el-main">
        <div class="warehouse-user-detail-container">
          <el-scrollbar style="height: 100%">
            <el-form id="user-detail-form" label-width="160px" :model="userDetailModel">
              <el-form-item label="角色：">
                <span v-for="(role, index) in userDetailModel.roles" :key="role.id">
                  {{ role.name }}
                  <span v-if="index + 1 < userDetailModel.roles.length">、</span>
                </span>
              </el-form-item>
              <el-form-item label="账号：">
                <span>{{ userDetailModel.account }}</span>
              </el-form-item>
              <el-form-item label="姓名：">
                <span>{{ userDetailModel.name }}</span>
              </el-form-item>
              <el-form-item label="头像：">
                <el-image
                  class="user-avatar"
                  :src="userDetailModel.avatar"
                  v-if="userDetailModel.avatar"
                ></el-image>
              </el-form-item>
              <el-form-item label="性别：">
                <span>{{ userDetailModel.gender }}</span>
              </el-form-item>
              <el-form-item label="邮箱：" prop="email" v-if="userDetailModel.email">
                <span>{{ userDetailModel.email }}</span>
              </el-form-item>
              <el-form-item label="电话：" prop="phone" v-if="userDetailModel.phone">
                <span>{{ userDetailModel.phone }}</span>
              </el-form-item>
              <el-form-item>
                <el-button @click="hideUserDetailContainer">返回</el-button>
                <el-button
                  type="primary"
                  @click="showEditUserContainer(true)"
                  v-if="hasPermission('c_user_update') && canEdit"
                >编辑</el-button>
              </el-form-item>
            </el-form>
          </el-scrollbar>
        </div>
      </el-main>
    </el-container>
    <el-container class="custom-el-container" v-if="isShowEditUserContainer">
      <el-main class="custom-el-main">
        <div class="warehouse-edit-user-container">
          <el-scrollbar style="height: 100%">
            <el-form
              id="edit-user-form"
              label-width="160px"
              ref="edit-user-form"
              status-icon
              :model="editUserModel"
              :rules="userRules"
            >
              <el-form-item label="角色：" prop="roleIds">
                <el-select
                  style="width: 100%;"
                  placeholder="请选择角色"
                  multiple
                  clearable
                  size="medium"
                  v-model="editUserModel.roleIds"
                >
                  <el-option
                    v-for="role in roles"
                    :key="role.id"
                    :label="role.name"
                    :value="role.id"
                  ></el-option>
                </el-select>
              </el-form-item>
              <el-form-item label="账号：" prop="account">
                <span>{{ editUserModel.account }}</span>
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
                <el-button type="primary" @click="submitEditUserForm('edit-user-form')">保存</el-button>
              </el-form-item>
            </el-form>
          </el-scrollbar>
        </div>
      </el-main>
    </el-container>
    <el-container class="custom-el-container" v-if="isShowAddUserContainer">
      <el-main class="custom-el-main">
        <div class="warehouse-add-user-container">
          <el-scrollbar style="height: 100%">
            <el-form
              id="add-user-form"
              label-width="160px"
              ref="add-user-form"
              status-icon
              :model="addUserModel"
              :rules="userRules"
            >
              <el-form-item>
                <el-alert type="warning" description="新增用户的密码系统自动生成" show-icon></el-alert>
              </el-form-item>
              <el-form-item label="角色：" prop="roleIds">
                <el-select
                  style="width: 100%;"
                  placeholder="请选择角色"
                  multiple
                  clearable
                  size="medium"
                  v-model="addUserModel.roleIds"
                >
                  <el-option
                    v-for="role in roles"
                    :key="role.id"
                    :label="role.name"
                    :value="role.id"
                  ></el-option>
                </el-select>
              </el-form-item>
              <el-form-item label="账号：" prop="account">
                <el-tooltip class="item" effect="light" content="提示：此账号一经设置不可修改！" placement="right">
                  <el-input placeholder="账号" v-model="addUserModel.account" clearable></el-input>
                </el-tooltip>
              </el-form-item>

              <el-form-item label="姓名：" prop="name">
                <el-input placeholder="姓名" v-model="addUserModel.name" clearable></el-input>
              </el-form-item>
              <el-form-item label="头像：">
                <el-upload
                  id="avatar-uploader"
                  action="http://127.0.0.1:8888/cUser/uploadAvatar"
                  :headers="{Authorization: this.$store.state.token}"
                  :show-file-list="false"
                  :on-success="handleAddAvatarSuccess"
                  :before-upload="beforeAvatarUpload"
                >
                  <img
                    class="avatar"
                    :size="60"
                    :src="addUserModel.avatar"
                    v-if="addUserModel.avatar"
                    :key="addUserModel.avatar"
                  />
                  <i class="el-icon-plus avatar-uploader-icon" v-else></i>
                </el-upload>
              </el-form-item>
              <el-form-item label="性别：" prop="gender">
                <el-radio-group v-model="addUserModel.gender">
                  <el-radio label="男">男</el-radio>
                  <el-radio label="女">女</el-radio>
                </el-radio-group>
              </el-form-item>
              <el-form-item label="邮箱：" prop="email">
                <el-input placeholder="邮箱" v-model="addUserModel.email" clearable></el-input>
              </el-form-item>
              <el-form-item label="电话：" prop="phone">
                <el-input placeholder="电话" v-model="addUserModel.phone" clearable></el-input>
              </el-form-item>
              <el-form-item>
                <el-button @click="hideAddUserContainer">取消</el-button>
                <el-button type="primary" @click="submitAddUserForm('add-user-form')">保存</el-button>
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
import { headerRowStyle } from "../../utils/layout";
import base from "../../utils/base";
import { HAS_PERMISSION } from "../../utils/permission";
import {
  validateEmail,
  validatePhone,
  validateImgFormat,
  validateImgSize
} from "../../utils/validator";
import {
  getUsersApi,
  getRolesApi,
  addUserApi,
  updateUserApi,
  updateUserStateApi,
  deleteUserApi,
  resetUserPasswordApi,
  importUsersApi
} from "../../api/consumables/user";

export default {
  name: "c_user",
  components: {
    LayoutComponent
  },
  inject: ["reload"],
  mounted() {
    this.getInitData();
  },
  computed: {
    isMyself() {
      return this.selectCurrentUser.id === this.$store.state.userInfo.id;
    },
    canResetPassword() {
      if (!this.selectCurrentUser) {
        return false;
      }
      if (this.selectCurrentUser.type !== "2") {
        return true;
      }
      return false;
    },
    canDelete() {
      if (!this.selectCurrentUser) {
        return false;
      }
      if (this.selectCurrentUser.type !== "2") {
        return true;
      }
      return false;
    },
    canEdit() {
      if (!this.selectCurrentUser) {
        return false;
      }
      if (this.selectCurrentUser.type !== "2") {
        return true;
      }
      return false;
    },
    isShowMainContainer() {
      return (
        !this.isShowUserDetailContainer &&
        !this.isShowEditUserContainer &&
        !this.isShowAddUserContainer
      );
    },
    canRightHandleUp() {
      if (
        !this.assignWarehouseModel.warehouseIds ||
        this.assignWarehouseModel.warehouseIds.length <= 1
      ) {
        return false;
      }
      if (
        !this.rightCheckedWraehouses ||
        this.rightCheckedWraehouses.length === 0
      ) {
        return false;
      }
      for (var i = 0; i < this.rightCheckedWraehouses.length; i++) {
        if (
          this.assignWarehouseModel.warehouseIds.indexOf(
            this.rightCheckedWraehouses[i]
          ) === 0
        ) {
          return false;
        }
      }
      return true;
    },
    canRightHandleDown() {
      if (
        !this.assignWarehouseModel.warehouseIds ||
        this.assignWarehouseModel.warehouseIds.length <= 1
      ) {
        return false;
      }
      if (
        !this.rightCheckedWraehouses ||
        this.rightCheckedWraehouses.length === 0
      ) {
        return false;
      }
      for (var i = 0; i < this.rightCheckedWraehouses.length; i++) {
        if (
          this.assignWarehouseModel.warehouseIds.indexOf(
            this.rightCheckedWraehouses[i]
          ) ===
          this.assignWarehouseModel.warehouseIds.length - 1
        ) {
          return false;
        }
      }
      return true;
    }
  },
  data: function() {
    var checkAccount = (rule, value, callback) => {
      if (!(validateEmail(value) || validatePhone(value))) {
        callback(new Error("请输入格式正确的手机号或邮箱"));
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
      menuName: "/consumables/user",
      openedMenus: ["multi-level-menu-user"],
      breadcrumbs: ["人员管理", "管理员"],
      isShowUserDetailContainer: false,
      isShowEditUserContainer: false,
      isShowAddUserContainer: false,
      total: 0,
      page: {
        pageNum: 1,
        pageSize: 30,
        searchCondition: null
      },
      users: [],
      selectCurrentUser: null,
      searchType: "1",
      searchStr: "",
      roles: [],
      userDetailModel: null,
      editUserModel: {
        warehouseId: "",
        id: "",
        account: "",
        name: "",
        gender: "",
        avatar: "",
        email: "",
        phone: "",
        roleIds: []
      },
      addUserModel: {
        warehouseId: "",
        account: "",
        name: "",
        gender: "",
        avatar: "",
        email: "",
        phone: "",
        roleIds: []
      },
      userRules: {
        roleIds: [
          { required: true, message: "请选择角色", trigger: "blur" },
          { required: true, message: "请选择角色", trigger: "change" }
        ],
        account: [
          { required: true, message: "请输入账号", trigger: "blur" },
          { min: 6, max: 50, message: "账号长度范围为6~50", trigger: "blur" },
          { min: 6, max: 50, message: "账号长度范围为6~50", trigger: "change" },
          { validator: checkAccount, trigger: "change" },
          { validator: checkAccount, trigger: "blur" }
        ],
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
      },
      assignWarehouseModel: {
        userId: "",
        account: "",
        name: "",
        warehouseIds: []
      },
      warehouses: [],
      rightCheckedWraehouses: [],
      importDialogTitle: "批量导入用户",
      importTips: [],
      fields: {
        name: "姓名",
        account: "账号",
        gender: "性别",
        email: "邮箱",
        phone: "电话",
        roleNames: "角色",
        isEnable: "是否启用"
      },
      importUserRules: {
        account: [
          { required: true, message: "账号不能为空" },
          { min: 6, max: 50, message: "账号长度范围为6~50" },
          { min: 6, max: 50, message: "账号长度范围为6~50" },
          { validator: checkAccount, trigger: "change" },
          { validator: checkAccount, trigger: "blur" }
        ],
        name: [{ required: true, message: "姓名不能为空" }],
        gender: [{ required: true, message: "性别不能为空" }],
        roleNames: [{ required: true, message: "角色不能为空" }]
      },
      filepath:
        "http://127.0.0.1:8888/cUser/template?warehouseId=" +
        this.$store.state.curWarehouse.id,
      isShowImportDialog: false
    };
  },
  methods: {
    getInitData() {
      if (!this.page.searchCondition) {
        this.page.searchCondition = JSON.stringify({
          warehouseId: this.$store.state.curWarehouse.id
        });
      }
      var that = this;
      getUsersApi(that.page).then(res => {
        that.total = res.data.total;
        that.users = res.data.list;
        if (that.users && that.users.length > 0) {
          that.$refs["user-table"].setCurrentRow(that.users[0]);
        }
      });
    },
    curWarehouseChanged() {
      this.reload();
      Message.success("切换成功");
    },
    hasPermission(curPermissionStr) {
      return HAS_PERMISSION(
        this.$store.state.userInfo.permissions,
        curPermissionStr
      );
    },
    setTableHeaderStyle({ row, column, rowIndex, columnIndex }) {
      return headerRowStyle({ row, column, rowIndex, columnIndex });
    },
    searchTypeChanged() {
      this.searchStr = "";
    },
    doSearch() {
      this.page.searchCondition = JSON.stringify({
        warehouseId: this.$store.state.curWarehouse.id,
        searchType: this.searchType,
        searchStr: this.searchStr
      });
      this.getInitData();
    },
    handlePageSizeChange(size) {
      this.page.pageSize = size;
      this.getInitData();
    },
    handleCurrentPageChange(currentPage) {
      this.page.pageNum = currentPage;
      this.getInitData();
    },
    handleCurrentChange(val) {
      this.selectCurrentUser = val;
    },
    showPassword(item) {
      this.$set(item, "isShowPassword", true);
    },
    hidePassword(item) {
      this.$set(item, "isShowPassword", false);
    },
    handleStatusChanged(item) {
      if (item.id === this.$store.state.userInfo.id) {
        Message.error("无法修改");
        item.isEnable = !item.isEnable;
        return;
      }
      updateUserStateApi({
        warehouseId: this.$store.state.curWarehouse.id,
        userId: item.id
      }).then(() => {
        if (item.isEnable) {
          Message.success("该用户已启用");
        } else {
          Message.success("该用户已停用");
        }
      });
    },
    resetPassword() {
      if (!this.selectCurrentUser) {
        return;
      }
      this.$confirm(
        "此操作将重置名称为「" +
          this.selectCurrentUser.name +
          "」的用户的密码, 是否继续?",
        "重置密码",
        {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }
      )
        .then(() => {
          resetUserPasswordApi({
            warehouseId: this.$store.state.curWarehouse.id,
            userId: this.selectCurrentUser.id
          }).then(() => {
            if (this.selectCurrentUser.id === this.$store.state.userInfo.id) {
              this.$store.commit("logout");
              this.$router.push("/");
              Message.success("重置成功，请重新登录");
            } else {
              Message.success("重置成功");
            }
          });
        })
        .catch(() => {
          Message.info("取消重置");
        });
    },
    deleteUser() {
      if (!this.selectCurrentUser) {
        return;
      }
      if (this.selectCurrentUser.id === this.$store.state.userInfo.id) {
        Message.error("无法删除");
        return;
      }
      this.$confirm(
        "此操作将永久删除名称为「" +
          this.selectCurrentUser.name +
          "」的用户, 是否继续?",
        "删除",
        {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }
      )
        .then(() => {
          deleteUserApi({
            warehouseId: this.$store.state.curWarehouse.id,
            userId: this.selectCurrentUser.id
          }).then(() => {
            Message.success("删除成功");
            this.total -= 1;
            this.users = this.users.filter(item => {
              return item.id !== this.selectCurrentUser.id;
            });
            if (this.users.length > 0) {
              this.$refs["user-table"].setCurrentRow(this.users[0]);
              this.selectCurrentUser = this.users[0];
            } else {
              this.$refs["user-table"].setCurrentRow(null);
              this.selectCurrentUser = null;
              this.doSearch();
            }
          });
        })
        .catch(() => {
          Message.info("取消删除");
        });
    },
    showUserDetailCntainer() {
      if (!this.selectCurrentUser) {
        return;
      }
      this.userDetailModel = this.selectCurrentUser;
      this.breadcrumbs.push("详情");
      this.isShowUserDetailContainer = true;
    },
    showEditUserContainer(isFromDetail) {
      var that = this;
      getRolesApi({
        warehouseId: that.$store.state.curWarehouse.id
      }).then(res => {
        that.roles = res.data;
        if (isFromDetail) {
          that.resetUserDetailModel();
          that.breadcrumbs.pop();
          that.isShowUserDetailContainer = false;
        } else if (!that.selectCurrentUser) {
          return;
        }
        that.editUserModel.id = that.selectCurrentUser.id;
        that.editUserModel.account = that.selectCurrentUser.account;
        that.editUserModel.name = that.selectCurrentUser.name;
        that.editUserModel.gender = that.selectCurrentUser.gender;
        that.editUserModel.avatar = that.selectCurrentUser.avatar;
        that.editUserModel.email = that.selectCurrentUser.email;
        that.editUserModel.phone = that.selectCurrentUser.phone;
        that.selectCurrentUser.roles.forEach(item => {
          that.editUserModel.roleIds.push(item.id);
        });
        that.breadcrumbs.push("编辑");
        that.isShowEditUserContainer = true;
      });
    },
    showAddUserContainer() {
      getRolesApi({
        warehouseId: this.$store.state.curWarehouse.id
      }).then(res => {
        if (res.data.length > 0) {
          this.roles = res.data;
          this.breadcrumbs.push("新增");
          this.isShowAddUserContainer = true;
        } else {
          Message.error("无法新增：请先设定角色信息");
        }
      });
    },
    hideUserDetailContainer() {
      this.resetUserDetailModel();
      this.breadcrumbs.pop();
      this.isShowUserDetailContainer = false;
    },
    hideEditUserContainer() {
      this.resetEditUserModel();
      this.$refs["edit-user-form"].resetFields();
      this.breadcrumbs.pop();
      this.isShowEditUserContainer = false;
    },
    hideAddUserContainer() {
      this.resetAddUserModel();
      this.$refs["add-user-form"].resetFields();
      this.breadcrumbs.pop();
      this.isShowAddUserContainer = false;
    },
    resetUserDetailModel() {
      this.userDetailModel = {
        id: "",
        account: "",
        name: "",
        gender: "",
        avatar: "",
        email: "",
        phone: "",
        roleIds: []
      };
    },
    resetEditUserModel() {
      this.editUserModel = {
        warehouseId: "",
        id: "",
        account: "",
        name: "",
        gender: "",
        avatar: "",
        email: "",
        phone: "",
        roleIds: []
      };
    },
    resetAddUserModel() {
      this.addUserModel = {
        warehouseId: "",
        account: "",
        name: "",
        gender: "",
        avatar: "",
        email: "",
        phone: "",
        roleIds: []
      };
    },
    beforeAvatarUpload(file) {
      if (!validateImgFormat(file.type)) {
        Message.error("上传头像图片格式错误");
        return false;
      }
      if (!validateImgSize(file.size)) {
        Message.error("上传头像图片大小不能超过2MB!");
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
    handleAddAvatarSuccess(res) {
      this.addUserModel.avatar = res.data.imgPath;
      this.loading.close();
    },
    submitEditUserForm(formName) {
      this.$refs[formName].validate(valid => {
        if (valid) {
          this.editUserModel.warehouseId = this.$store.state.curWarehouse.id;
          updateUserApi(this.editUserModel).then(res => {
            Message.success("保存成功");
            this.resetEditUserModel();
            this.$refs["edit-user-form"].resetFields();
            this.selectCurrentUser = Object.assign({}, res.data);
            this.$forceUpdate();
            this.breadcrumbs.pop();
            this.isShowEditUserContainer = false;
          });
        }
      });
    },
    submitAddUserForm(formName) {
      this.$refs[formName].validate(valid => {
        if (valid) {
          this.addUserModel.warehouseId = this.$store.state.curWarehouse.id;
          addUserApi(this.addUserModel).then(res => {
            Message.success("保存成功");
            this.resetAddUserModel();
            this.$refs["add-user-form"].resetFields();
            this.breadcrumbs.pop();
            this.isShowAddUserContainer = false;
            this.total += 1;
            this.users.unshift(res.data);
            this.$refs["user-table"].setCurrentRow(this.users[0]);
          });
        }
      });
    },
    exportUsers() {
      var url =
        base.url +
        "/cUser/exportUsers?warehouseId=" +
        this.$store.state.curWarehouse.id;
      window.open(url);
    },
    async importUsers(data) {
      await importUsersApi({
        warehouseId: this.$store.state.curWarehouse.id,
        excelData: JSON.stringify(data)
      }).then(res => {
        if (res.success) {
          return Promise.resolve();
        } else {
          return Promise.reject(res.data);
        }
      });
    },
    handleCloseImportUsersDialog() {},
    handleFinishImportUsersDialog() {
      this.doSearch();
    },
    handleOpen() {
      this.isShowImportDialog = true;
    }
  }
};
</script>

<style lang="less">
.warehouse-grid-user-container {
  height: calc(100% - 100px);

  .el-avatar > img {
    width: 100%;
  }

  .password-container {
    display: flex;
    flex-flow: row nowrap;
    justify-content: center;
    align-items: center;

    .view-password {
      font-size: 18px;
      cursor: pointer;

      &:hover {
        color: #e5e5e5;
      }
    }

    .password {
      color: #b7472a;
      cursor: pointer;
    }
  }

  .warehouse-count {
    color: #00a8cc;
    cursor: pointer;

    &:hover {
      color: #909399;
      text-decoration: underline;
    }
  }
}

.warehouse-assign-warehouse-container,
.warehouse-user-detail-container,
.warehouse-edit-user-container,
.warehouse-add-user-container {
  height: 100%;
  overflow-x: hidden;
  overflow-y: auto;

  #assign-warehouse-form,
  #user-detail-form,
  #edit-user-form,
  #add-user-form {
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

  #assign-warehouse-form {
    width: 800px;
    .el-transfer-panel__item {
      display: block;
    }
  }
}
</style>