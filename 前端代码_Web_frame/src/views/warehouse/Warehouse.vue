<template>
  <LayoutComponent :active-menu="menuName">
    <el-breadcrumb separator-class="el-icon-arrow-right">
      <el-breadcrumb-item v-for="(item,index) in breadcrumbs" :key="index">{{item}}</el-breadcrumb-item>
    </el-breadcrumb>
    <hr />
    <el-row class="operation-container" v-show="isShowMainContainer">
      <el-col class="search-container" :span="8">
        <el-input placeholder="请输入搜索内容" v-model="searchStr" clearable style="max-width: 350px;">
          <template slot="prepend">仓库名</template>
        </el-input>
        <el-button class="search-btn" type="primary" icon="el-icon-search" @click="doSearch"></el-button>
      </el-col>
      <el-col class="btn-group" :span="16">
        <el-button
          type="primary"
          plain
          @click="showAssignUserContainer(selectCurrentWarehouse)"
        >分配人员</el-button>
        <el-button type="danger" plain @click="deleteWarehouse">删除</el-button>
        <el-button type="primary" plain @click="showWarehouseDetailContainer">详情</el-button>
        <el-button type="primary" plain @click="showEditWarehouseContainer(false)">编辑</el-button>
        <el-button type="primary" plain @click="showAddWarehouseContainer">新增</el-button>
      </el-col>
    </el-row>
    <el-container class="warehouse-grid-warehouse-container" v-show="isShowMainContainer">
      <el-main>
        <el-table
          ref="warehouse-table"
          :data="warehouses"
          border
          stripe
          height="100%"
          highlight-current-row
          :header-cell-style="setTableHeaderStyle"
          @current-change="handleCurrentChange"
        >
          <el-table-column label="照片" width="62">
            <template slot-scope="scope">
              <el-avatar shape="square" fit="fill" :src="scope.row.pictureUrl"></el-avatar>
            </template>
          </el-table-column>
          <el-table-column
            label="编号"
            width="100"
            :show-overflow-tooltip="isShowOverflowTooltip"
            sortable
            prop="code"
          ></el-table-column>
          <el-table-column
            label="名称"
            width="150"
            :show-overflow-tooltip="isShowOverflowTooltip"
            prop="name"
          ></el-table-column>
          <el-table-column label="人员数" width="65">
            <template slot-scope="scope">
              <div class="custom-text-right">
                <span
                  class="user-count"
                  @click="showAssignUserContainer(scope.row)"
                >{{ scope.row.users.length }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建日期" width="135" sortable></el-table-column>
          <el-table-column label="简介" prop="summary"></el-table-column>
          <el-table-column label="状态" width="65">
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
    <el-container class="custom-el-container" v-if="isShowAssignUserContainer">
      <el-main class="custom-el-main">
        <div class="warehouse-assign-user-container">
          <el-scrollbar style="height: 100%">
            <el-form
              id="assign-user-form"
              label-width="160px"
              ref="assign-user-form"
              status-icon
              :model="assignUserModel"
            >
              <el-form-item>
                <el-alert type="warning" description="系统管理员默认管理所有仓库，所以当前页面仅显示可分配仓库管理员信息" show-icon></el-alert>
              </el-form-item>
              <el-form-item label="编号：">
                <span>{{ assignUserModel.code }}</span>
              </el-form-item>
              <el-form-item label="名称：">
                <span>{{ assignUserModel.name }}</span>
              </el-form-item>
              <el-form-item label="照片：">
                <el-image
                  class="warehouse-picture"
                  :src="assignUserModel.pictureUrl"
                  v-if="assignUserModel.pictureUrl"
                ></el-image>
              </el-form-item>
              <el-form-item label="简介：">
                <span>{{ assignUserModel.summary }}</span>
              </el-form-item>
              <el-form-item label="分配人员：" prop="userIds">
                <el-transfer
                  filterable
                  :filter-method="filterMethod"
                  filter-placeholder="请输入管理员名称"
                  :titles="['未分配', '已分配']"
                  targetOrder="push"
                  v-model="assignUserModel.userIds"
                  :data="users"
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
                <el-button @click="hideAssignUserContainer">返回</el-button>
                <el-button type="primary" @click="submitAssignUserForm('assign-user-form')">保存</el-button>
              </el-form-item>
            </el-form>
          </el-scrollbar>
        </div>
      </el-main>
    </el-container>
    <el-container class="custom-el-container" v-if="isShowWarehouseDetailContainer">
      <el-main class="custom-el-main">
        <div class="warehouse-warehouse-detail-container">
          <el-scrollbar style="height: 100%">
            <el-form id="warehouse-detail-form" label-width="160px" :model="warehouseDetailModel">
              <el-form-item label="编号：">
                <span>{{ warehouseDetailModel.code }}</span>
              </el-form-item>
              <el-form-item label="名称：">
                <span>{{ warehouseDetailModel.name }}</span>
              </el-form-item>
              <el-form-item label="照片：">
                <el-image
                  class="warehouse-picture"
                  :src="warehouseDetailModel.pictureUrl"
                  v-if="warehouseDetailModel.pictureUrl"
                ></el-image>
              </el-form-item>
              <el-form-item label="简介：">
                <span>{{ warehouseDetailModel.summary }}</span>
              </el-form-item>
              <el-form-item>
                <el-button @click="hideWarehouseDetailContainer">返回</el-button>
                <el-button type="primary" @click="showEditWarehouseContainer(true)">编辑</el-button>
              </el-form-item>
            </el-form>
          </el-scrollbar>
        </div>
      </el-main>
    </el-container>
    <el-container class="custom-el-container" v-if="isShowEditWarehouseContainer">
      <el-main class="custom-el-main">
        <div class="warehouse-edit-warehouse-container">
          <el-scrollbar style="height: 100%">
            <el-form
              id="edit-warehouse-form"
              label-width="160px"
              ref="edit-warehouse-form"
              status-icon
              :model="editWarehouseModel"
              :rules="warehouseRules"
            >
              <el-form-item label="编号：" prop="code">
                <el-input placeholder="编号" v-model="editWarehouseModel.code" clearable></el-input>
              </el-form-item>
              <el-form-item label="名称：" prop="name">
                <el-input placeholder="名称" v-model="editWarehouseModel.name" clearable></el-input>
              </el-form-item>
              <el-form-item label="照片：" prop="pictureUrl">
                <el-upload
                  id="avatar-uploader"
                  action="http://127.0.0.1:8888/wWarehouse/uploadImage"
                  :headers="{Authorization: this.$store.state.token}"
                  :show-file-list="false"
                  :on-success="handleEditAvatarSuccess"
                  :before-upload="beforeAvatarUpload"
                >
                  <img
                    class="avatar"
                    :size="60"
                    :src="editWarehouseModel.pictureUrl"
                    v-if="editWarehouseModel.pictureUrl"
                    :key="editWarehouseModel.pictureUrl"
                  />
                  <i class="el-icon-plus avatar-uploader-icon" v-else></i>
                </el-upload>
              </el-form-item>
              <el-form-item label="简介：" prop="summary">
                <el-input
                  type="textarea"
                  :autosize="{ minRows: 4, maxRows: 6}"
                  placeholder="请输入简介"
                  v-model="editWarehouseModel.summary"
                ></el-input>
              </el-form-item>
              <el-form-item>
                <el-button @click="hideEditWarehouseContainer">取消</el-button>
                <el-button type="primary" @click="submitEditWarehouseForm('edit-warehouse-form')">保存</el-button>
              </el-form-item>
            </el-form>
          </el-scrollbar>
        </div>
      </el-main>
    </el-container>
    <el-container class="custom-el-container" v-if="isShowAddWarehouseContainer">
      <el-main class="custom-el-main">
        <div class="warehouse-add-warehouse-container">
          <el-scrollbar style="height: 100%">
            <el-form
              id="add-warehouse-form"
              label-width="160px"
              ref="add-warehouse-form"
              status-icon
              :model="addWarehouseModel"
              :rules="warehouseRules"
            >
              <el-form-item label="编号：" prop="code">
                <el-input placeholder="编号" v-model="addWarehouseModel.code" clearable></el-input>
              </el-form-item>
              <el-form-item label="名称：" prop="name">
                <el-input placeholder="名称" v-model="addWarehouseModel.name" clearable></el-input>
              </el-form-item>
              <el-form-item label="照片：" prop="pictureUrl">
                <el-upload
                  id="avatar-uploader"
                  action="http://127.0.0.1:8888/wWarehouse/uploadImage"
                  :headers="{Authorization: this.$store.state.token}"
                  :show-file-list="false"
                  :on-success="handleAddAvatarSuccess"
                  :before-upload="beforeAvatarUpload"
                >
                  <img
                    class="avatar"
                    :size="60"
                    :src="addWarehouseModel.pictureUrl"
                    v-if="addWarehouseModel.pictureUrl"
                    :key="addWarehouseModel.pictureUrl"
                  />
                  <i class="el-icon-plus avatar-uploader-icon" v-else></i>
                </el-upload>
              </el-form-item>
              <el-form-item label="简介：" prop="summary">
                <el-input
                  type="textarea"
                  :autosize="{ minRows: 4, maxRows: 6}"
                  placeholder="请输入简介"
                  v-model="addWarehouseModel.summary"
                ></el-input>
              </el-form-item>
              <el-form-item>
                <el-button @click="hideAddWarehouseContainer">取消</el-button>
                <el-button type="primary" @click="submitAddWarehouseForm('add-warehouse-form')">保存</el-button>
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
  validateCode,
  validateImgFormat,
  validateImgSize
} from "../../utils/validator";
import {
  getAdministratorsApi,
  getWarehousesApi,
  addWarehouseApi,
  editWarehouseApi,
  editWarehouseStateApi,
  deleteWarehouseApi,
  updateAssignUsersApi
} from "../../api/warehouse/warehouse";

export default {
  name: "w_warehouse",
  components: {
    LayoutComponent
  },
  mounted() {
    this.getInitData();
  },
  computed: {
    isShowMainContainer() {
      return (
        !this.isShowAssignUserContainer &&
        !this.isShowWarehouseDetailContainer &&
        !this.isShowEditWarehouseContainer &&
        !this.isShowAddWarehouseContainer
      );
    },
    canRightHandleUp() {
      if (
        !this.assignUserModel.userIds ||
        this.assignUserModel.userIds.length <= 1
      ) {
        return false;
      }
      if (!this.rightCheckedUsers || this.rightCheckedUsers.length === 0) {
        return false;
      }
      for (var i = 0; i < this.rightCheckedUsers.length; i++) {
        if (
          this.assignUserModel.userIds.indexOf(this.rightCheckedUsers[i]) === 0
        ) {
          return false;
        }
      }
      return true;
    },
    canRightHandleDown() {
      if (
        !this.assignUserModel.userIds ||
        this.assignUserModel.userIds.length <= 1
      ) {
        return false;
      }
      if (!this.rightCheckedUsers || this.rightCheckedUsers.length === 0) {
        return false;
      }
      for (var i = 0; i < this.rightCheckedUsers.length; i++) {
        if (
          this.assignUserModel.userIds.indexOf(this.rightCheckedUsers[i]) ===
          this.assignUserModel.userIds.length - 1
        ) {
          return false;
        }
      }
      return true;
    }
  },
  data: function() {
    var checkCode = (rule, value, callback) => {
      if (value && !validateCode(value)) {
        callback(new Error("请输入格式正确的编号(1-20个字母、数字)"));
      } else {
        callback();
      }
    };
    return {
      menuName: "/warehouse/warehouse",
      breadcrumbs: ["仓库管理"],
      isShowOverflowTooltip: true,
      isShowAssignUserContainer: false,
      isShowWarehouseDetailContainer: false,
      isShowEditWarehouseContainer: false,
      isShowAddWarehouseContainer: false,
      total: 0,
      page: {
        pageNum: 1,
        pageSize: 30,
        searchCondition: null
      },
      warehouses: [],
      selectCurrentWarehouse: null,
      searchStr: "",
      assignUserModel: {
        warehouseId: "",
        code: "",
        name: "",
        userIds: []
      },
      users: [],
      rightCheckedUsers: [],
      warehouseDetailModel: {
        id: "",
        code: "",
        name: "",
        pictureUrl: "",
        summary: ""
      },
      addWarehouseModel: {
        code: "",
        name: "",
        pictureUrl: "",
        summary: ""
      },
      editWarehouseModel: {
        id: "",
        code: "",
        name: "",
        pictureUrl: "",
        summary: ""
      },
      warehouseRules: {
        code: [
          { required: true, message: "请输入仓库编号", trigger: "blur" },
          { required: true, message: "请输入仓库编号", trigger: "change" },
          { validator: checkCode, trigger: "change" },
          { validator: checkCode, trigger: "blur" }
        ],
        name: [
          { required: true, message: "请输入仓库名称", trigger: "blur" },
          { required: true, message: "请输入仓库名称", trigger: "change" },
          {
            min: 1,
            max: 20,
            message: "仓库名称长度范围为1~20",
            trigger: "blur"
          },
          {
            min: 1,
            max: 20,
            message: "仓库名称长度范围为1~20",
            trigger: "change"
          }
        ],
        pictureUrl: [
          { required: true, message: "请上传仓库名照片", trigger: "blur" },
          { required: true, message: "请上传仓库名照片", trigger: "change" }
        ],
        summary: [
          {
            min: 0,
            max: 200,
            message: "仓库简介长度范围为0~200",
            trigger: "blur"
          },
          {
            min: 0,
            max: 200,
            message: "仓库简介长度范围为0~200",
            trigger: "change"
          }
        ]
      }
    };
  },
  methods: {
    getInitData() {
      getWarehousesApi(this.page).then(res => {
        this.total = res.data.total;
        this.warehouses = res.data.list;
        if (this.warehouses && this.warehouses.length > 0) {
          this.$refs["warehouse-table"].setCurrentRow(this.warehouses[0]);
        }
      });
    },
    setTableHeaderStyle({ row, column, rowIndex, columnIndex }) {
      return headerRowStyle({ row, column, rowIndex, columnIndex });
    },
    doSearch() {
      this.page.searchCondition = JSON.stringify({
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
      this.selectCurrentWarehouse = val;
    },
    handleStatusChanged(item) {
      item.isEnable = !item.isEnable;
      editWarehouseStateApi({
        warehouseId: item.id
      }).then(res => {
        if (res.success) {
          item.isEnable = !item.isEnable;
          if (item.isEnable) {
            Message.success("该仓库已启用");
          } else {
            Message.success("该仓库已停用");
          }
        }
      });
    },
    deleteWarehouse() {
      if (!this.selectCurrentWarehouse) {
        return;
      }
      this.$confirm(
        "此操作将永久删除编号为「" +
          this.selectCurrentWarehouse.code +
          "」的仓库, 是否继续?",
        "删除",
        {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }
      )
        .then(() => {
          deleteWarehouseApi({
            warehouseId: this.selectCurrentWarehouse.id
          }).then(() => {
            Message.success("删除成功");
            this.total -= 1;
            this.warehouses = this.warehouses.filter(item => {
              return item.id !== this.selectCurrentWarehouse.id;
            });
            if (this.warehouses.length > 0) {
              this.$refs["warehouse-table"].setCurrentRow(this.warehouses[0]);
              this.selectCurrentWarehouse = this.warehouses[0];
            } else {
              this.$refs["warehouse-table"].setCurrentRow(null);
              this.selectCurrentWarehouse = null;
              this.doSearch();
            }
          });
        })
        .catch(() => {
          Message.info("取消删除");
        });
    },
    showAssignUserContainer(item) {
      if (!item) {
        return;
      }
      getAdministratorsApi().then(res => {
        if (!res.data || res.data.length === 0) {
          Message.error("无法分配：暂无仓库管理员");
          return;
        }
        var userIds = [];
        item.users.forEach(item => {
          if (item.type === "2") {
            userIds.push(item.id);
          }
        });
        this.assignUserModel = {
          warehouseId: item.id,
          code: item.code,
          name: item.name,
          pictureUrl: item.pictureUrl,
          summary: item.summary,
          userIds: userIds
        };
        res.data.forEach(item => {
          this.users.push({
            key: item.id,
            label: item.name
          });
        });
        this.breadcrumbs.push("分配人员");
        this.isShowAssignUserContainer = true;
      });
    },
    showWarehouseDetailContainer() {
      if (!this.selectCurrentWarehouse) {
        return;
      }
      this.warehouseDetailModel = {
        id: this.selectCurrentWarehouse.id,
        code: this.selectCurrentWarehouse.code,
        name: this.selectCurrentWarehouse.name,
        pictureUrl: this.selectCurrentWarehouse.pictureUrl,
        summary: this.selectCurrentWarehouse.summary
      };
      this.breadcrumbs.push("详情");
      this.isShowWarehouseDetailContainer = true;
    },
    showEditWarehouseContainer(isFromDetail) {
      if (isFromDetail) {
        this.resetWarehouseDetailModel();
        this.breadcrumbs.pop();
        this.isShowWarehouseDetailContainer = false;
      } else if (!this.selectCurrentWarehouse) {
        return;
      }
      this.editWarehouseModel = {
        id: this.selectCurrentWarehouse.id,
        code: this.selectCurrentWarehouse.code,
        name: this.selectCurrentWarehouse.name,
        pictureUrl: this.selectCurrentWarehouse.pictureUrl,
        summary: this.selectCurrentWarehouse.summary
      };
      this.breadcrumbs.push("编辑");
      this.isShowEditWarehouseContainer = true;
    },
    showAddWarehouseContainer() {
      this.breadcrumbs.push("新增");
      this.isShowAddWarehouseContainer = true;
    },
    hideAssignUserContainer() {
      this.assignUserModel = {
        warehouseId: "",
        code: "",
        name: "",
        userIds: []
      };
      this.rightCheckedUsers = [];
      this.users = [];
      this.breadcrumbs.pop();
      this.isShowAssignUserContainer = false;
    },
    hideWarehouseDetailContainer() {
      this.resetWarehouseDetailModel();
      this.breadcrumbs.pop();
      this.isShowWarehouseDetailContainer = false;
    },
    hideEditWarehouseContainer() {
      this.resetEditWarehouseModel();
      this.$refs["edit-warehouse-form"].resetFields();
      this.breadcrumbs.pop();
      this.isShowEditWarehouseContainer = false;
    },
    hideAddWarehouseContainer() {
      this.resetAddWarehouseModel();
      this.$refs["add-warehouse-form"].resetFields();
      this.breadcrumbs.pop();
      this.isShowAddWarehouseContainer = false;
    },
    resetWarehouseDetailModel() {
      this.warehouseDetailModel = {
        code: "",
        name: "",
        pictureUrl: "",
        summary: ""
      };
    },
    resetEditWarehouseModel() {
      this.editWarehouseModel = {
        id: "",
        code: "",
        name: "",
        pictureUrl: "",
        summary: ""
      };
    },
    resetAddWarehouseModel() {
      this.addWarehouseModel = {
        code: "",
        name: "",
        pictureUrl: "",
        summary: ""
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
      this.editWarehouseModel.pictureUrl = res.data.imgPath;
      this.loading.close();
    },
    handleAddAvatarSuccess(res) {
      this.addWarehouseModel.pictureUrl = res.data.imgPath;
      this.loading.close();
    },
    submitAssignUserForm(formName) {
      this.$refs[formName].validate(valid => {
        if (valid) {
          updateAssignUsersApi({
            warehouseId: this.assignUserModel.warehouseId,
            userIds: this.assignUserModel.userIds
          }).then(res => {
            Message.success("保存成功");
            this.selectCurrentWarehouse.users = res.data.users;
            this.hideAssignUserContainer();
          });
        }
      });
    },
    submitEditWarehouseForm(formName) {
      this.$refs[formName].validate(valid => {
        if (valid) {
          editWarehouseApi(this.editWarehouseModel).then(res => {
            Message.success("保存成功");
            this.resetEditWarehouseModel();
            this.$refs["edit-warehouse-form"].resetFields();
            this.breadcrumbs.pop();
            this.isShowEditWarehouseContainer = false;
            this.selectCurrentWarehouse.code = res.data.code;
            this.selectCurrentWarehouse.name = res.data.name;
            this.selectCurrentWarehouse.pictureUrl = res.data.pictureUrl;
            this.selectCurrentWarehouse.summary = res.data.summary;
          });
        }
      });
    },
    submitAddWarehouseForm(formName) {
      this.$refs[formName].validate(valid => {
        if (valid) {
          addWarehouseApi(this.addWarehouseModel).then(res => {
            Message.success("保存成功");
            this.total += 1;
            this.resetAddWarehouseModel();
            this.$refs["add-warehouse-form"].resetFields();
            this.breadcrumbs.pop();
            this.isShowAddWarehouseContainer = false;
            this.warehouses.unshift(res.data);
            this.$refs["warehouse-table"].setCurrentRow(this.warehouses[0]);
          });
        }
      });
    },
    filterMethod(query, item) {
      return item.label.indexOf(query) > -1;
    },
    rightCheckChange(checkedWraehouses) {
      this.rightCheckedUsers = checkedWraehouses;
    },
    rightHandUp() {
      let that = this;
      if (!that.canRightHandleUp) {
        return;
      }
      if (that.rightCheckedUsers.length > 1) {
        Message.warning("仅支持单条数据上下移动");
        return;
      }
      that.rightCheckedUsers.forEach(item => {
        let index = -1;
        that.assignUserModel.userIds.find((val, indexs) => {
          if (val === item) {
            index = indexs;
            return;
          }
        });
        that.assignUserModel.userIds.splice(index, 1);
        that.assignUserModel.userIds.splice(index - 1, 0, item);
      });
    },
    rightHandDown() {
      let that = this;
      if (!that.canRightHandleDown) {
        return;
      }
      if (that.rightCheckedUsers.length > 1) {
        Message.warning("仅支持单条数据上下移动");
        return;
      }
      that.rightCheckedUsers.forEach(item => {
        let index = -1;
        that.assignUserModel.userIds.find((val, indexs) => {
          if (val === item) {
            index = indexs;
            return;
          }
        });
        that.assignUserModel.userIds.splice(index, 1);
        that.assignUserModel.userIds.splice(index + 1, 0, item);
      });
    }
  }
};
</script>

<style lang="less">
.warehouse-grid-warehouse-container {
  height: calc(100% - 100px);

  .el-avatar > img {
    width: 100%;
  }

  .user-count {
    color: #00a8cc;
    cursor: pointer;

    &:hover {
      color: #909399;
      text-decoration: underline;
    }
  }
}

.warehouse-warehouse-detail-container,
.warehouse-edit-warehouse-container,
.warehouse-add-warehouse-container {
  height: 100%;
  overflow-x: hidden;
  overflow-y: auto;

  .warehouse-picture {
    width: 178px;
    height: 178px;
  }

  #warehouse-detail-form,
  #edit-warehouse-form,
  #add-warehouse-form {
    width: 500px;

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

.warehouse-assign-user-container {
  height: 100%;
  overflow-x: hidden;
  overflow-y: auto;

  #assign-user-form {
    width: 800px;

    .warehouse-picture {
      width: 178px;
      height: 178px;
    }

    .el-transfer-panel__item {
      display: block;
    }
  }
}
</style>