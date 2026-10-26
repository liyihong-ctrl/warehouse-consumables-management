<template>
  <LayoutComponent :active-menu="menuName">
    <el-breadcrumb separator-class="el-icon-arrow-right">
      <el-breadcrumb-item v-for="(item,index) in breadcrumbs" :key="index">{{item}}</el-breadcrumb-item>
    </el-breadcrumb>
    <hr />
    <el-row class="operation-container" v-show="isShowMainContainer">
      <el-col class="search-container" :span="8">
        <el-input
          class="search-input"
          placeholder="请输入搜索内容"
          v-model="searchStr"
          clearable
          style="max-width: 350px;"
        >
          <el-select
            style="width: 90px;"
            v-model="searchType"
            slot="prepend"
            placeholder="请选择"
            @change="searchTypeChanged"
          >
            <el-option label="姓名" value="1"></el-option>
            <el-option label="账号" value="2"></el-option>
            <el-option label="类型" value="3"></el-option>
          </el-select>
        </el-input>
        <el-button class="search-btn" type="primary" icon="el-icon-search" @click="doSearch"></el-button>
      </el-col>
      <el-col class="btn-group" :span="16">
        <el-button
          type="primary"
          plain
          @click="showAssignWarehouseContainer(selectCurrentUser)"
          :disabled="!canAssignWarehouse"
        >分配仓库</el-button>
        <el-button type="primary" plain @click="resetPassword">重置密码</el-button>
        <el-button type="danger" plain @click="deleteUser" :disabled="!canDelete">删除</el-button>
        <el-button type="primary" plain @click="showUserDetailCntainer">详情</el-button>
        <el-button type="primary" plain @click="showEditUserContainer(false)">编辑</el-button>
        <el-button type="primary" plain @click="showAddUserContainer">新增</el-button>
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
          <el-table-column label="头像" width="62" align="center">
            <template slot-scope="scope">
              <el-avatar shape="square" fit="fill" :src="scope.row.avatar"></el-avatar>
            </template>
          </el-table-column>
          <el-table-column
            label="姓名"
            width="130"
            :show-overflow-tooltip="isShowOverflowTooltip"
            prop="name"
            align="center"
          ></el-table-column>
          <el-table-column
            label="账号"
            width="220"
            :show-overflow-tooltip="isShowOverflowTooltip"
            prop="account"
            align="center"
          ></el-table-column>
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
          <el-table-column label="仓库数" width="80" align="right">
            <template slot-scope="scope">
              <div class="custom-text-right">
                <span
                  class="warehouse-count"
                  @click="showAssignWarehouseContainer(scope.row)"
                >{{ renderTypeName(scope.row.type) === userType[2].value ? "━" : scope.row.warehouses.length }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="类型" width="120" align="center">
            <template slot-scope="scope">
              <span :class="renterTypeStyle(scope.row.type)">{{ renderTypeName(scope.row.type) }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="registerTime" label="创建日期" sortable></el-table-column>
          <el-table-column label="状态" width="65">
            <template slot-scope="scope">
              <el-tooltip :content="scope.row.isEnable ? '停用' : '启用'" placement="top">
                <el-switch v-model="scope.row.isEnable" @change="handleStatusChanged(scope.row)" :disabled="!canUpdateUserState(scope.row)"></el-switch>
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
    <el-container class="custom-el-container" v-if="isShowAssignWarehouseContainer">
      <el-main class="custom-el-main">
        <div class="warehouse-assign-warehouse-container">
          <el-scrollbar style="height: 100%">
            <el-form
              id="assign-warehouse-form"
              label-width="160px"
              ref="assign-warehouse-form"
              status-icon
              :model="assignWarehouseModel"
            >
              <el-form-item label="账号：">
                <span>{{ assignWarehouseModel.account }}</span>
              </el-form-item>
              <el-form-item label="姓名：">
                <span>{{ assignWarehouseModel.name }}</span>
              </el-form-item>
              <el-form-item label="分配仓库" prop="warehouseIds">
                <el-transfer
                  filterable
                  :filter-method="filterMethod"
                  filter-placeholder="请输入仓库名称"
                  :titles="['未分配', '已分配']"
                  targetOrder="push"
                  v-model="assignWarehouseModel.warehouseIds"
                  :data="warehouses"
                  @right-check-change="rightCheckChange"
                >
                  <el-button
                    class="transfer-footer"
                    slot="left-footer"
                    size="small"
                    :disabled="true"
                  >上移</el-button>
                  <el-button
                    class="transfer-footer"
                    slot="left-footer"
                    size="small"
                    :disabled="true"
                  >下移</el-button>
                  <el-button
                    class="transfer-footer"
                    slot="right-footer"
                    size="small"
                    @click="rightHandUp"
                    :disabled="!canRightHandleUp"
                  >上移</el-button>
                  <el-button
                    class="transfer-footer"
                    slot="right-footer"
                    size="small"
                    @click="rightHandDown"
                    :disabled="!canRightHandleDown"
                  >下移</el-button>
                </el-transfer>
              </el-form-item>
              <el-form-item>
                <el-button @click="hideAssignWarehouseContainer">返回</el-button>
                <el-button
                  type="primary"
                  @click="submitAssignWarehouseForm('assign-warehouse-form')"
                >保存</el-button>
              </el-form-item>
            </el-form>
          </el-scrollbar>
        </div>
      </el-main>
    </el-container>
    <el-container class="custom-el-container" v-if="isShowUserDetailContainer">
      <el-main class="custom-el-main">
        <div class="warehouse-user-detail-container">
          <el-scrollbar style="height: 100%">
            <el-form id="user-detail-form" label-width="160px" :model="userDetailModel">
              <el-form-item label="角色：">
                <span>{{ userDetailModel.typeName }}</span>
              </el-form-item>
              <el-form-item label="部门：" v-if="userDetailModel.typeName === userType[2].value">
                <span>{{ userDetailModel.department }}</span>
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
              <el-form-item label="邮箱：" v-show="userDetailModel.email">
                <span>{{ userDetailModel.email }}</span>
              </el-form-item>
              <el-form-item label="电话：" v-show="userDetailModel.phone">
                <span>{{ userDetailModel.phone }}</span>
              </el-form-item>
              <el-form-item>
                <el-button @click="hideUserDetailContainer">返回</el-button>
                <el-button type="primary" @click="showEditUserContainer(true)">编辑</el-button>
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
              <el-form-item label="角色：" prop="type">
                <el-select placeholder="请选择角色" v-model="editUserModel.type" :disabled="isMyself">
                  <el-option
                    v-for="type in userType"
                    :key="type.key"
                    :label="type.value"
                    :value="type.key"
                  ></el-option>
                </el-select>
              </el-form-item>
              <el-form-item
                label="部门："
                prop="department"
                v-if="editUserModel.type === userType[2].key"
              >
                <el-input v-model="editUserModel.department" placeholder="请输入部门"></el-input>
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
              <el-form-item label="角色：" prop="type">
                <el-select placeholder="请选择角色" v-model="addUserModel.type">
                  <el-option
                    v-for="type in userType"
                    :key="type.key"
                    :label="type.value"
                    :value="type.key"
                  ></el-option>
                </el-select>
              </el-form-item>
              <el-form-item
                label="部门："
                prop="department"
                v-if="addUserModel.type === userType[2].key"
              >
                <el-input v-model="addUserModel.department" placeholder="请输入部门"></el-input>
              </el-form-item>
              <el-form-item label="账号：" prop="account">
                <el-tooltip
                  class="item"
                  effect="light"
                  content="提示：此账号为仓库管理员登录账号，一旦设置不可修改！"
                  placement="right"
                >
                  <el-input placeholder="账号" v-model="addUserModel.account" clearable></el-input>
                </el-tooltip>
              </el-form-item>
              <el-form-item label="姓名：" prop="name">
                <el-input placeholder="姓名" v-model="addUserModel.name" clearable></el-input>
              </el-form-item>
              <el-form-item label="头像：">
                <el-upload
                  id="avatar-uploader"
                  action="http://127.0.0.1:8888/wUser/uploadAvatar"
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
import LayoutComponent from "@/components/warehouse/Layout.vue";
import { headerRowStyle } from "../../utils/layout";
import {
  validateEmail,
  validatePhone,
  validateImgFormat,
  validateImgSize
} from "../../utils/validator";
import {
  getAdministratorsApi,
  addAdministratorApi,
  editAdministratorApi,
  editAdministratorStateApi,
  deleteAdministratorApi,
  resetAdministratorPasswordApi,
  getAllWarehousesApi,
  updateAssignWarehouseApi
} from "../../api/warehouse/user";

export default {
  name: "w_user",
  components: {
    LayoutComponent
  },
  mounted() {
    this.getInitData();
  },
  computed: {
    canAssignWarehouse() {
      if (!this.selectCurrentUser) {
        return false;
      }
      // 系统管理员不可分配仓库
      if (this.selectCurrentUser.type === this.userType[0].key) {
        return false;
      }
      // 申请人员不可分配仓库
      if (this.selectCurrentUser.type === this.userType[2].key) {
        return false;
      }
      return true;
    },
    canDelete() {
      if (!this.selectCurrentUser) {
        return false;
      }
      return this.selectCurrentUser.id !== this.$store.state.userInfo.id;
    },
    isMyself() {
      return this.selectCurrentUser.id === this.$store.state.userInfo.id;
    },
    isShowMainContainer() {
      return (
        !this.isShowAssignWarehouseContainer &&
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
      menuName: "/warehouse/user",
      breadcrumbs: ["人员管理"],
      isShowOverflowTooltip: true,
      isShowAssignWarehouseContainer: false,
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
      userType: [
        {
          key: "1",
          value: "系统管理员",
          style: {
            "system-manager": true
          }
        },
        {
          key: "2",
          value: "仓库管理员",
          style: {
            "warehouse-manager": true
          }
        },
        {
          key: "8",
          value: "申请人员",
          style: {
            "application-user": true
          }
        }
      ],
      userDetailModel: {
        id: "",
        department: "",
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
        department: "",
        account: "",
        name: "",
        gender: "",
        avatar: "",
        email: "",
        phone: "",
        type: ""
      },
      addUserModel: {
        department: "",
        account: "",
        name: "",
        gender: "",
        avatar: "",
        email: "",
        phone: "",
        type: ""
      },
      userRules: {
        type: [
          { required: true, message: "请选择角色", trigger: "blur" },
          { required: true, message: "请选择角色", trigger: "change" }
        ],
        department: [
          { required: true, message: "请输入所属部门", trigger: "blur" },
          { min: 1, max: 99, message: "部门长度范围为1~99", trigger: "blur" },
          { min: 1, max: 99, message: "部门长度范围为1~99", trigger: "change" }
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
      rightCheckedWraehouses: []
    };
  },
  methods: {
    getInitData() {
      getAdministratorsApi(this.page).then(res => {
        this.total = res.data.total;
        this.users = res.data.list;
        if (this.users && this.users.length > 0) {
          this.$refs["user-table"].setCurrentRow(this.users[0]);
        }
      });
    },
    setTableHeaderStyle({ row, column, rowIndex, columnIndex }) {
      return headerRowStyle({ row, column, rowIndex, columnIndex });
    },
    renterTypeStyle(type) {
      return this.userType.find(item => {
        return item.key === type;
      }).style;
    },
    renderTypeName(type) {
      return this.userType.find(item => {
        return item.key === type;
      }).value;
    },
    canUpdateUserState(curUser) {
      return curUser.id !== this.$store.state.userInfo.id;
    },
    searchTypeChanged() {
      this.searchStr = "";
    },
    doSearch() {
      this.page.searchCondition = JSON.stringify({
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
      editAdministratorStateApi({
        userId: item.id
      }).then(() => {
        if (item.isEnable) {
          Message.success("该用户已启用");
        } else {
          Message.success("该用户已停用");
        }
      });
    },
    showAssignWarehouseContainer(item) {
      if (!item) {
        return;
      }
      if (item.type === this.userType[0].key) {
        Message.error("无法分配：系统管理员默认具备所有仓库操作权限");
        return;
      }
      if (item.type === this.userType[2].key) {
        Message.error("无法分配：申请人员不可分配仓库");
        return;
      }
      getAllWarehousesApi({
        userId: item.id
      }).then(res => {
        if (!res.data || res.data.length === 0) {
          Message.error("无法分配：暂无仓库");
          return;
        }
        var warehouseIds = [];
        res.data.assignedWarehouse.forEach(item => {
          warehouseIds.push(item.id);
        });
        this.assignWarehouseModel = {
          userId: item.id,
          account: item.account,
          name: item.name,
          warehouseIds: warehouseIds
        };
        res.data.warehouses.forEach(item => {
          this.warehouses.push({
            key: item.id,
            label: item.name
          });
        });
        this.breadcrumbs.push("分配仓库");
        this.isShowAssignWarehouseContainer = true;
      });
    },
    resetPassword() {
      if (!this.selectCurrentUser) {
        return;
      }
      this.$confirm(
        "此操作将重置名称为「" +
          this.selectCurrentUser.name +
          "」的密码, 是否继续?",
        "重置密码",
        {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }
      )
        .then(() => {
          resetAdministratorPasswordApi({
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
          deleteAdministratorApi({
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
      this.userDetailModel = {
        id: this.selectCurrentUser.id,
        department: this.selectCurrentUser.extension.department,
        account: this.selectCurrentUser.account,
        name: this.selectCurrentUser.name,
        gender: this.selectCurrentUser.gender,
        avatar: this.selectCurrentUser.avatar,
        email: this.selectCurrentUser.email,
        phone: this.selectCurrentUser.phone,
        typeName: this.userType.find(item => {
          return item.key === this.selectCurrentUser.type;
        }).value
      };
      this.breadcrumbs.push("详情");
      this.isShowUserDetailContainer = true;
    },
    showEditUserContainer(isFromDetail) {
      if (isFromDetail) {
        this.resetUserDetailModel();
        this.breadcrumbs.pop();
        this.isShowUserDetailContainer = false;
      } else if (!this.selectCurrentUser) {
        return;
      }
      this.editUserModel = {
        id: this.selectCurrentUser.id,
        department: this.selectCurrentUser.extension.department,
        account: this.selectCurrentUser.account,
        name: this.selectCurrentUser.name,
        gender: this.selectCurrentUser.gender,
        avatar: this.selectCurrentUser.avatar,
        email: this.selectCurrentUser.email,
        phone: this.selectCurrentUser.phone,
        type: this.selectCurrentUser.type
      };
      this.breadcrumbs.push("编辑");
      this.isShowEditUserContainer = true;
    },
    showAddUserContainer() {
      this.breadcrumbs.push("新增");
      this.isShowAddUserContainer = true;
    },
    hideAssignWarehouseContainer() {
      this.assignWarehouseModel = {
        userId: "",
        account: "",
        name: "",
        warehouseIds: []
      };
      this.rightCheckedWraehouses = [];
      this.warehouses = [];
      this.breadcrumbs.pop();
      this.isShowAssignWarehouseContainer = false;
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
        department: "",
        account: "",
        name: "",
        gender: "",
        avatar: "",
        email: "",
        phone: "",
        type: ""
      };
    },
    resetEditUserModel() {
      this.editUserModel = {
        id: "",
        department: "",
        account: "",
        name: "",
        gender: "",
        avatar: "",
        email: "",
        phone: "",
        type: ""
      };
    },
    resetAddUserModel() {
      this.addUserModel = {
        department: "",
        account: "",
        name: "",
        gender: "",
        avatar: "",
        email: "",
        phone: "",
        type: ""
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
    submitAssignWarehouseForm(formName) {
      this.$refs[formName].validate(valid => {
        if (valid) {
          updateAssignWarehouseApi(this.assignWarehouseModel).then(res => {
            Message.success("保存成功");
            this.assignWarehouseModel = {
              userId: "",
              account: "",
              name: "",
              warehouseIds: []
            };
            this.rightCheckedWraehouses = [];
            this.warehouses = [];
            this.breadcrumbs.pop();
            this.selectCurrentUser.warehouses = res.data;
            this.isShowAssignWarehouseContainer = false;
          });
        }
      });
    },
    submitAddUserForm(formName) {
      this.$refs[formName].validate(valid => {
        if (valid) {
          addAdministratorApi(this.addUserModel).then(res => {
            Message.success("保存成功");
            this.total += 1;
            this.resetAddUserModel();
            this.$refs["add-user-form"].resetFields();
            this.breadcrumbs.pop();
            this.isShowAddUserContainer = false;
            this.users.unshift(res.data);
            this.$refs["user-table"].setCurrentRow(this.users[0]);
          });
        }
      });
    },
    submitEditUserForm(formName) {
      this.$refs[formName].validate(valid => {
        if (valid) {
          editAdministratorApi(this.editUserModel).then(res => {
            Message.success("保存成功");
            this.resetEditUserModel();
            this.$refs["edit-user-form"].resetFields();
            this.breadcrumbs.pop();
            this.isShowEditUserContainer = false;
            this.selectCurrentUser.extension.department =
              res.data.extension.department;
            this.selectCurrentUser.name = res.data.name;
            this.selectCurrentUser.gender = res.data.gender;
            this.selectCurrentUser.avatar = res.data.avatar;
            this.selectCurrentUser.email = res.data.email;
            this.selectCurrentUser.phone = res.data.phone;
            this.selectCurrentUser.type = res.data.type;
            this.selectCurrentUser.warehouses = res.data.warehouses;
            if (this.isMyself) {
              this.$store.commit("initUserInfo", res.data);
            }
          });
        }
      });
    },
    filterMethod(query, item) {
      return item.label.indexOf(query) > -1;
    },
    rightCheckChange(checkedWraehouses) {
      this.rightCheckedWraehouses = checkedWraehouses;
    },
    rightHandUp() {
      let that = this;
      if (!that.canRightHandleUp) {
        return;
      }
      if (that.rightCheckedWraehouses.length > 1) {
        Message.warning("仅支持单条数据上下移动");
        return;
      }
      that.rightCheckedWraehouses.forEach(item => {
        let index = -1;
        that.assignWarehouseModel.warehouseIds.find((val, indexs) => {
          if (val === item) {
            index = indexs;
            return;
          }
        });
        that.assignWarehouseModel.warehouseIds.splice(index, 1);
        that.assignWarehouseModel.warehouseIds.splice(index - 1, 0, item);
      });
    },
    rightHandDown() {
      let that = this;
      if (!that.canRightHandleDown) {
        return;
      }
      if (that.rightCheckedWraehouses.length > 1) {
        Message.warning("仅支持单条数据上下移动");
        return;
      }
      that.rightCheckedWraehouses.forEach(item => {
        let index = -1;
        that.assignWarehouseModel.warehouseIds.find((val, indexs) => {
          if (val === item) {
            index = indexs;
            return;
          }
        });
        that.assignWarehouseModel.warehouseIds.splice(index, 1);
        that.assignWarehouseModel.warehouseIds.splice(index + 1, 0, item);
      });
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

  .system-manager {
    color: #f56c6c;
  }

  .warehouse-manager {
    color: #67c23a;
  }

  .application-user {
    color: #e6a23c;
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