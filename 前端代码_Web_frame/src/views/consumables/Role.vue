<template>
  <LayoutComponent :active-menu="menuName" @curWarehouseChanged="curWarehouseChanged">
    <el-breadcrumb separator-class="el-icon-arrow-right">
      <el-breadcrumb-item v-for="(item,index) in breadcrumbs" :key="index">{{item}}</el-breadcrumb-item>
    </el-breadcrumb>
    <hr />
    <el-row class="operation-container" v-show="isShowMainContainer">
      <el-col class="search-container" :span="8">
        <el-input placeholder="请输入搜索内容" v-model="searchStr" clearable style="max-width: 350px;">
          <template slot="prepend">角色名</template>
        </el-input>
        <el-button class="search-btn" type="primary" icon="el-icon-search" @click="doSearch"></el-button>
      </el-col>
      <el-col class="btn-group" :span="16">
        <el-button type="danger" plain @click="deleteRole" v-if="hasPermission('c_role_delete')">删除</el-button>
        <el-button
          type="primary"
          plain
          @click="showRoleDetailCntainer"
          v-if="hasPermission('c_role_select')"
        >详情</el-button>
        <el-button
          type="primary"
          plain
          @click="showEditRoleContainer(false)"
          v-if="hasPermission('c_role_update')"
        >编辑</el-button>
        <el-button
          type="primary"
          plain
          @click="showAddRoleContainer"
          v-if="hasPermission('c_role_add')"
        >新增</el-button>
        <el-button type="info" plain @click="exportRoles" v-if="hasPermission('c_role_export')">导出</el-button>
        <div style="margin-left: 10px" v-if="hasPermission('c_role_import')">
          <ele-import
            :fields="fields"
            :filepath="filepath"
            :requestFn="importRoles"
            :rules="importRoleRules"
            :tips="importTips"
            :title="importDialogTitle"
            :visible.sync="isShowImportDialog"
            @close="handleCloseImportRolesDialog"
            @finish="handleFinishImportRolesDialog"
          />
          <el-button type="info" plain @click="handleOpen">导入</el-button>
        </div>
      </el-col>
    </el-row>
    <el-container class="consumables-grid-roles-container" v-show="isShowMainContainer">
      <el-main>
        <el-table
          ref="role-table"
          :data="roles"
          border
          stripe
          height="100%"
          highlight-current-row
          :header-cell-style="setTableHeaderStyle"
          @current-change="handleCurrentChange"
        >
          <el-table-column prop="name" label="角色名称" width="180" align="center"></el-table-column>
          <el-table-column prop="str" label="角色标识" width="180" align="center"></el-table-column>
          <el-table-column prop="createTime" label="创建日期" width="150" sortable align="center"></el-table-column>
          <el-table-column prop="remarks" label="备注"></el-table-column>
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
    <el-container class="custom-el-container" v-if="isShowRoleDetailContainer">
      <el-main class="custom-el-main">
        <div class="consumables-role-detail-container">
          <el-scrollbar style="height: 100%">
            <el-form id="role-detail-form" label-width="160px" :model="roleDetailModel">
              <el-form-item label="角色名称">
                <span>{{roleDetailModel.name}}</span>
              </el-form-item>
              <el-form-item label="角色标识">
                <span>{{roleDetailModel.str}}</span>
              </el-form-item>
              <el-form-item label="登录权限">
                <el-checkbox-group v-model="roleDetailModel.loginPermissions">
                  <el-checkbox
                    v-for="(item, index) in webPermissions.loginPermissions"
                    :label="item.key"
                    :key="index"
                    disabled
                  >{{item.value}}</el-checkbox>
                </el-checkbox-group>
              </el-form-item>
              <el-form-item label="角色管理权限">
                <el-checkbox-group
                  v-model="roleDetailModel.roleManagementPermissions"
                  @change="handlePermissionChanged"
                >
                  <el-checkbox
                    v-for="(item, index) in webPermissions.roleManagementPermissions"
                    :label="item.key"
                    :key="index"
                    disabled
                  >{{item.value}}</el-checkbox>
                </el-checkbox-group>
              </el-form-item>
              <el-form-item label="用户管理权限">
                <el-checkbox-group
                  v-model="roleDetailModel.userManagementPermissions"
                  @change="handlePermissionChanged"
                >
                  <el-checkbox
                    v-for="(item, index) in webPermissions.userManagementPermissions"
                    :label="item.key"
                    :key="index"
                    disabled
                  >{{item.value}}</el-checkbox>
                </el-checkbox-group>
              </el-form-item>
              <el-form-item label="类型管理权限">
                <el-checkbox-group
                  v-model="roleDetailModel.consumablesTypeManagementPermissions"
                  @change="handlePermissionChanged"
                >
                  <el-checkbox
                    v-for="(item, index) in webPermissions.consumablesTypeManagementPermissions"
                    :label="item.key"
                    :key="index"
                    disabled
                  >{{item.value}}</el-checkbox>
                </el-checkbox-group>
              </el-form-item>
              <el-form-item label="基准信息模板管理权限">
                <el-checkbox-group
                  v-model="roleDetailModel.referenctInformationTemplatePermissions"
                  @change="handlePermissionChanged"
                >
                  <el-checkbox
                    v-for="(item, index) in webPermissions.referenctInformationTemplatePermissions"
                    :label="item.key"
                    :key="index"
                    disabled
                  >{{item.value}}</el-checkbox>
                </el-checkbox-group>
              </el-form-item>
              <el-form-item label="消耗品管理权限">
                <el-checkbox-group
                  v-model="roleDetailModel.consumablesManagementPermissions"
                  @change="handlePermissionChanged"
                >
                  <el-checkbox
                    v-for="(item, index) in webPermissions.consumablesManagementPermissions"
                    :label="item.key"
                    :key="index"
                    disabled
                  >{{item.value}}</el-checkbox>
                </el-checkbox-group>
              </el-form-item>
              <el-form-item label="申请管理权限">
                <el-checkbox-group
                  v-model="roleDetailModel.applicationManagementPermissions"
                  @change="handlePermissionChanged"
                >
                  <el-checkbox
                    v-for="(item, index) in webPermissions.applicationManagementPermissions"
                    :label="item.key"
                    :key="index"
                    disabled
                  >{{item.value}}</el-checkbox>
                </el-checkbox-group>
              </el-form-item>
              <el-form-item label="操作履历管理权限">
                <el-checkbox-group
                  v-model="roleDetailModel.userOperateManagementPermissions"
                  @change="handlePermissionChanged"
                >
                  <el-checkbox
                    v-for="(item, index) in webPermissions.userOperateManagementPermissions"
                    :label="item.key"
                    :key="index"
                    disabled
                  >{{item.value}}</el-checkbox>
                </el-checkbox-group>
              </el-form-item>
              <el-form-item label="统计信息管理权限">
                <el-checkbox-group
                  v-model="roleDetailModel.statisticsManagementPermissions"
                  @change="handlePermissionChanged"
                >
                  <el-checkbox
                    v-for="(item, index) in webPermissions.statisticsManagementPermissions"
                    :label="item.key"
                    :key="index"
                    disabled
                  >{{item.value}}</el-checkbox>
                </el-checkbox-group>
              </el-form-item>
              <el-form-item label="备注" prop="remarks" v-if="roleDetailModel.remarks">
                <span>{{roleDetailModel.remarks}}</span>
              </el-form-item>
              <el-form-item>
                <el-button @click="hideRoleDetailContainer">返回</el-button>
                <el-button
                  type="primary"
                  @click="showEditRoleContainer(true)"
                  v-if="hasPermission('c_role_update')"
                >编辑</el-button>
              </el-form-item>
            </el-form>
          </el-scrollbar>
        </div>
      </el-main>
    </el-container>
    <el-container class="custom-el-container" v-if="isShowEditRoleContainer">
      <el-main class="custom-el-main">
        <div class="consumables-edit-role-container">
          <el-scrollbar style="height: 100%">
            <el-form
              id="edit-role-form"
              label-width="160px"
              ref="edit-role-form"
              status-icon
              :model="editRoleModel"
              :rules="roleRules"
            >
              <el-form-item label="角色名称" prop="name">
                <el-input placeholder="请输入角色名称" v-model="editRoleModel.name"></el-input>
              </el-form-item>
              <el-form-item label="角色标识" prop="str">
                <el-input placeholder="请输入角色标识(建议使用角色名称拼音)" v-model="editRoleModel.str"></el-input>
              </el-form-item>
              <el-form-item label="登录权限" prop="loginPermissions">
                <el-checkbox-group v-model="editRoleModel.loginPermissions">
                  <el-checkbox
                    v-for="(item, index) in webPermissions.loginPermissions"
                    :label="item.key"
                    :key="index"
                    disabled
                  >{{item.value}}</el-checkbox>
                </el-checkbox-group>
              </el-form-item>
              <el-form-item label="角色管理权限" prop="roleManagementPermissions">
                <el-checkbox-group
                  v-model="editRoleModel.roleManagementPermissions"
                  @change="handlePermissionChanged"
                >
                  <el-checkbox
                    v-for="(item, index) in webPermissions.roleManagementPermissions"
                    :label="item.key"
                    :key="index"
                  >{{item.value}}</el-checkbox>
                </el-checkbox-group>
              </el-form-item>
              <el-form-item label="用户管理权限" prop="userManagementPermissions">
                <el-checkbox-group
                  v-model="editRoleModel.userManagementPermissions"
                  @change="handlePermissionChanged"
                >
                  <el-checkbox
                    v-for="(item, index) in webPermissions.userManagementPermissions"
                    :label="item.key"
                    :key="index"
                  >{{item.value}}</el-checkbox>
                </el-checkbox-group>
              </el-form-item>
              <el-form-item label="类型管理权限" prop="consumablesTypeManagementPermissions">
                <el-checkbox-group
                  v-model="editRoleModel.consumablesTypeManagementPermissions"
                  @change="handlePermissionChanged"
                >
                  <el-checkbox
                    v-for="(item, index) in webPermissions.consumablesTypeManagementPermissions"
                    :label="item.key"
                    :key="index"
                  >{{item.value}}</el-checkbox>
                </el-checkbox-group>
              </el-form-item>
              <el-form-item label="基准信息模板管理权限" prop="referenctInformationTemplatePermissions">
                <el-checkbox-group
                  v-model="editRoleModel.referenctInformationTemplatePermissions"
                  @change="handlePermissionChanged"
                >
                  <el-checkbox
                    v-for="(item, index) in webPermissions.referenctInformationTemplatePermissions"
                    :label="item.key"
                    :key="index"
                  >{{item.value}}</el-checkbox>
                </el-checkbox-group>
              </el-form-item>
              <el-form-item label="消耗品管理权限" prop="consumablesManagementPermissions">
                <el-checkbox-group
                  v-model="editRoleModel.consumablesManagementPermissions"
                  @change="handlePermissionChanged"
                >
                  <el-checkbox
                    v-for="(item, index) in webPermissions.consumablesManagementPermissions"
                    :label="item.key"
                    :key="index"
                  >{{item.value}}</el-checkbox>
                </el-checkbox-group>
              </el-form-item>
              <el-form-item label="申请管理权限" prop="applicationManagementPermissions">
                <el-checkbox-group
                  v-model="editRoleModel.applicationManagementPermissions"
                  @change="handlePermissionChanged"
                >
                  <el-checkbox
                    v-for="(item, index) in webPermissions.applicationManagementPermissions"
                    :label="item.key"
                    :key="index"
                  >{{item.value}}</el-checkbox>
                </el-checkbox-group>
              </el-form-item>
              <el-form-item label="操作履历管理权限" prop="userOperateManagementPermissions">
                <el-checkbox-group
                  v-model="editRoleModel.userOperateManagementPermissions"
                  @change="handlePermissionChanged"
                >
                  <el-checkbox
                    v-for="(item, index) in webPermissions.userOperateManagementPermissions"
                    :label="item.key"
                    :key="index"
                  >{{item.value}}</el-checkbox>
                </el-checkbox-group>
              </el-form-item>
              <el-form-item label="统计信息管理权限" prop="statisticsManagementPermissions">
                <el-checkbox-group
                  v-model="editRoleModel.statisticsManagementPermissions"
                  @change="handlePermissionChanged"
                >
                  <el-checkbox
                    v-for="(item, index) in webPermissions.statisticsManagementPermissions"
                    :label="item.key"
                    :key="index"
                  >{{item.value}}</el-checkbox>
                </el-checkbox-group>
              </el-form-item>
              <el-form-item label="备注" prop="remarks">
                <el-input placeholder="请输入备注" v-model="editRoleModel.remarks"></el-input>
              </el-form-item>
              <el-form-item>
                <el-button @click="hideEditRoleContainer">取消</el-button>
                <el-button type="primary" @click="submitEditRoleForm('edit-role-form')">保存</el-button>
              </el-form-item>
            </el-form>
          </el-scrollbar>
        </div>
      </el-main>
    </el-container>
    <el-container class="custom-el-container" v-if="isShowAddRoleContainer">
      <el-main class="custom-el-main">
        <div class="consumables-add-role-container">
          <el-scrollbar style="height: 100%">
            <el-form
              id="add-role-form"
              label-width="160px"
              ref="add-role-form"
              status-icon
              :model="addRoleModel"
              :rules="roleRules"
            >
              <el-form-item label="角色名称" prop="name">
                <el-input placeholder="请输入角色名称" v-model="addRoleModel.name" clearable></el-input>
              </el-form-item>
              <el-form-item label="角色标识" prop="str">
                <el-input placeholder="请输入角色标识(建议使用角色名称拼音)" v-model="addRoleModel.str" clearable></el-input>
              </el-form-item>
              <el-form-item label="登录权限" prop="loginPermissions">
                <el-checkbox-group v-model="addRoleModel.loginPermissions">
                  <el-checkbox
                    v-for="(item, index) in webPermissions.loginPermissions"
                    :label="item.key"
                    :key="index"
                    checked
                    disabled
                  >{{item.value}}</el-checkbox>
                </el-checkbox-group>
              </el-form-item>
              <el-form-item label="角色管理权限" prop="roleManagementPermissions">
                <el-checkbox-group
                  v-model="addRoleModel.roleManagementPermissions"
                  @change="handlePermissionChanged"
                >
                  <el-checkbox
                    v-for="(item, index) in webPermissions.roleManagementPermissions"
                    :label="item.key"
                    :key="index"
                  >{{item.value}}</el-checkbox>
                </el-checkbox-group>
              </el-form-item>
              <el-form-item label="用户管理权限" prop="userManagementPermissions">
                <el-checkbox-group
                  v-model="addRoleModel.userManagementPermissions"
                  @change="handlePermissionChanged"
                >
                  <el-checkbox
                    v-for="(item, index) in webPermissions.userManagementPermissions"
                    :label="item.key"
                    :key="index"
                  >{{item.value}}</el-checkbox>
                </el-checkbox-group>
              </el-form-item>
              <el-form-item label="类型管理权限" prop="consumablesTypeManagementPermissions">
                <el-checkbox-group
                  v-model="addRoleModel.consumablesTypeManagementPermissions"
                  @change="handlePermissionChanged"
                >
                  <el-checkbox
                    v-for="(item, index) in webPermissions.consumablesTypeManagementPermissions"
                    :label="item.key"
                    :key="index"
                  >{{item.value}}</el-checkbox>
                </el-checkbox-group>
              </el-form-item>
              <el-form-item label="基准信息模板管理权限" prop="referenctInformationTemplatePermissions">
                <el-checkbox-group
                  v-model="addRoleModel.referenctInformationTemplatePermissions"
                  @change="handlePermissionChanged"
                >
                  <el-checkbox
                    v-for="(item, index) in webPermissions.referenctInformationTemplatePermissions"
                    :label="item.key"
                    :key="index"
                  >{{item.value}}</el-checkbox>
                </el-checkbox-group>
              </el-form-item>
              <el-form-item label="消耗品管理权限" prop="consumablesManagementPermissions">
                <el-checkbox-group
                  v-model="addRoleModel.consumablesManagementPermissions"
                  @change="handlePermissionChanged"
                >
                  <el-checkbox
                    v-for="(item, index) in webPermissions.consumablesManagementPermissions"
                    :label="item.key"
                    :key="index"
                  >{{item.value}}</el-checkbox>
                </el-checkbox-group>
              </el-form-item>
              <el-form-item label="申请管理权限" prop="applicationManagementPermissions">
                <el-checkbox-group
                  v-model="addRoleModel.applicationManagementPermissions"
                  @change="handlePermissionChanged"
                >
                  <el-checkbox
                    v-for="(item, index) in webPermissions.applicationManagementPermissions"
                    :label="item.key"
                    :key="index"
                  >{{item.value}}</el-checkbox>
                </el-checkbox-group>
              </el-form-item>
              <el-form-item label="操作履历管理权限" prop="userOperateManagementPermissions">
                <el-checkbox-group
                  v-model="addRoleModel.userOperateManagementPermissions"
                  @change="handlePermissionChanged"
                >
                  <el-checkbox
                    v-for="(item, index) in webPermissions.userOperateManagementPermissions"
                    :label="item.key"
                    :key="index"
                  >{{item.value}}</el-checkbox>
                </el-checkbox-group>
              </el-form-item>
              <el-form-item label="统计信息管理权限" prop="statisticsManagementPermissions">
                <el-checkbox-group
                  v-model="addRoleModel.statisticsManagementPermissions"
                  @change="handlePermissionChanged"
                >
                  <el-checkbox
                    v-for="(item, index) in webPermissions.statisticsManagementPermissions"
                    :label="item.key"
                    :key="index"
                  >{{item.value}}</el-checkbox>
                </el-checkbox-group>
              </el-form-item>
              <el-form-item label="备注" prop="remarks">
                <el-input placeholder="请输入备注" v-model="addRoleModel.remarks" clearable></el-input>
              </el-form-item>
              <el-form-item>
                <el-button @click="hideAddRoleContainer">取消</el-button>
                <el-button type="primary" @click="submitAddRoleForm('add-role-form')">保存</el-button>
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
import { headerRowStyle } from "../../utils/layout";
import base from "../../utils/base";
import { INIT_WEB_PERMISSIONS, HAS_PERMISSION } from "../../utils/permission";
import {
  getRolesApi,
  addRoleApi,
  editRoleApi,
  deleteRoleApi,
  importRolesApi
} from "../../api/consumables/role";

export default {
  name: "c_role",
  components: {
    LayoutComponent
  },
  inject: ["reload"],
  mounted() {
    this.getInitData();
    this.webPermissions = INIT_WEB_PERMISSIONS(
      this.$store.state.systemPermissions
    );
  },
  computed: {
    isShowMainContainer() {
      return (
        !this.isShowRoleDetailContainer &&
        !this.isShowEditRoleContainer &&
        !this.isShowAddRoleContainer
      );
    }
  },
  data: function() {
    return {
      menuName: "/consumables/role",
      breadcrumbs: ["角色管理"],
      isShowRoleDetailContainer: false,
      isShowEditRoleContainer: false,
      isShowAddRoleContainer: false,
      total: 0,
      page: {
        pageNum: 1,
        pageSize: 30,
        searchCondition: null
      },
      roles: [],
      selectCurrentRole: null,
      searchStr: "",
      webPermissions: {
        loginPermissions: [],
        roleManagementPermissions: [],
        userManagementPermissions: [],
        consumablesTypeManagementPermissions: [],
        consumablesManagementPermissions: [],
        applicationManagementPermissions: [],
        userOperateManagementPermissions: [],
        statisticsManagementPermissions: []
      },
      roleDetailModel: {
        name: "",
        str: "",
        loginPermissions: [],
        roleManagementPermissions: [],
        userManagementPermissions: [],
        consumablesTypeManagementPermissions: [],
        referenctInformationTemplatePermissions: [],
        consumablesManagementPermissions: [],
        applicationManagementPermissions: [],
        userOperateManagementPermissions: [],
        statisticsManagementPermissions: [],
        remarks: ""
      },
      editRoleModel: {
        id: "",
        name: "",
        str: "",
        loginPermissions: [],
        roleManagementPermissions: [],
        userManagementPermissions: [],
        consumablesTypeManagementPermissions: [],
        referenctInformationTemplatePermissions: [],
        consumablesManagementPermissions: [],
        applicationManagementPermissions: [],
        userOperateManagementPermissions: [],
        statisticsManagementPermissions: [],
        remarks: ""
      },
      addRoleModel: {
        name: "",
        str: "",
        loginPermissions: [],
        roleManagementPermissions: [],
        userManagementPermissions: [],
        consumablesTypeManagementPermissions: [],
        referenctInformationTemplatePermissions: [],
        consumablesManagementPermissions: [],
        applicationManagementPermissions: [],
        userOperateManagementPermissions: [],
        statisticsManagementPermissions: [],
        remarks: ""
      },
      roleRules: {
        name: [
          { required: true, message: "角色名称不允许为空", trigger: "blur" },
          {
            min: 1,
            max: 20,
            message: "角色名称长度必须在1~20之间",
            trigger: "blur"
          },
          {
            min: 1,
            max: 20,
            message: "角色名称长度必须在1~20之间",
            trigger: "change"
          }
        ],
        str: [
          { required: true, message: "角色标识不允许为空", trigger: "blur" },
          {
            min: 1,
            max: 20,
            message: "角色标识长度必须在1~20之间",
            trigger: "blur"
          },
          {
            min: 1,
            max: 20,
            message: "角色标识长度必须在1~20之间",
            trigger: "change"
          }
        ],
        remarks: [
          {
            min: 0,
            max: 50,
            message: "备注长度必须在0~50之间",
            trigger: "blur"
          },
          {
            min: 0,
            max: 50,
            message: "备注长度必须在0~50之间",
            trigger: "change"
          }
        ]
      },
      importDialogTitle: "批量导入角色",
      importTips: [
        "角色名称不能为空、角色标识不能为空",
        "登录权限必选、查看权限为其他权限的基础",
        '权限选择关键词汇"有"、"无"'
      ],
      fields: {
        name: "角色名称",
        str: "角色标识",
        remarks: "备注",

        c_login: "登录权限",

        c_role_add: "角色管理权限：添加",
        c_role_delete: "角色管理权限：删除",
        c_role_update: "角色管理权限：修改",
        c_role_select: "角色管理权限：查看",
        c_role_import: "角色管理权限：批量导入",
        c_role_export: "角色管理权限：批量导出",

        c_user_add: "用户管理权限：添加",
        c_user_delete: "用户管理权限：删除",
        c_user_update: "用户管理权限：修改",
        c_user_select: "用户管理权限：查看",
        c_user_state_management: "用户管理权限：状态管理",
        c_user_import: "用户管理权限：批量导入",
        c_user_export: "用户管理权限：批量导出",

        c_consumablesType_add: "类型管理权限：添加",
        c_consumablesType_delete: "类型管理权限：删除",
        c_consumablesType_update: "类型管理权限：修改",
        c_consumablesType_select: "类型管理权限：查看",
        c_consumablesType_state_management: "类型管理权限：状态管理",
        c_consumablesType_import: "类型管理权限：批量导入",
        c_consumablesType_export: "类型管理权限：批量导出",

        c_referenceInformationTemplate_add: "基准信息模板：添加",
        c_referenceInformationTemplate_delete: "基准信息模板：删除",
        c_referenceInformationTemplate_update: "基准信息模板：修改",
        c_referenceInformationTemplate_select: "基准信息模板：查看",

        c_consumables_add: "消耗品管理权限：添加",
        c_consumables_delete: "消耗品管理权限：删除",
        c_consumables_update: "消耗品管理权限：修改",
        c_consumables_select: "消耗品管理权限：查看",
        c_consumables_state_management: "消耗品管理权限：状态管理",
        c_consumables_inbound: "消耗品管理权限：入库",
        c_consumables_outbound: "消耗品管理权限：出库",
        c_consumables_import: "消耗品管理权限：批量导入",
        c_consumables_export: "消耗品管理权限：批量导出",

        c_application_select: "申请管理权限：查看",
        c_application_state_management: "申请管理权限：状态管理",

        c_userOperate_select: "操作履历管理权限：查看",
        c_userOperate_export: "操作履历管理权限：批量导出",

        c_statistics_select: "统计信息管理权限：查看",
        c_statistics_export: "统计信息管理权限：批量导出"
      },
      importRoleRules: {
        name: [
          { required: true, message: "角色名称不允许为空", trigger: "blur" },
          {
            min: 1,
            max: 20,
            message: "角色名称长度必须在1~20之间",
            trigger: "blur"
          },
          {
            min: 1,
            max: 20,
            message: "角色名称长度必须在1~20之间",
            trigger: "change"
          }
        ],
        str: [
          { required: true, message: "角色标识不允许为空", trigger: "blur" },
          {
            min: 1,
            max: 20,
            message: "角色标识长度必须在1~20之间",
            trigger: "blur"
          },
          {
            min: 1,
            max: 20,
            message: "角色标识长度必须在1~20之间",
            trigger: "change"
          }
        ]
      },
      filepath:
        "http://127.0.0.1:8888/cRole/template?warehouseId=" +
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
      getRolesApi(this.page).then(res => {
        that.total = res.data.total;
        that.roles = res.data.list;
        if (that.roles && that.roles.length > 0) {
          that.$refs["role-table"].setCurrentRow(that.roles[0]);
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
      this.selectCurrentRole = val;
    },
    deleteRole() {
      if (!this.selectCurrentRole) {
        return;
      }
      this.$confirm(
        "此操作将永久删除名称为「" +
          this.selectCurrentRole.name +
          "」的角色, 是否继续?",
        "删除",
        {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }
      )
        .then(() => {
          deleteRoleApi({
            warehouseId: this.$store.state.curWarehouse.id,
            roleId: this.selectCurrentRole.id
          }).then(() => {
            Message.success("删除成功");
            this.roles = this.roles.filter(item => {
              return item.id !== this.selectCurrentRole.id;
            });
            if (this.roles.length > 0) {
              this.$refs["role-table"].setCurrentRow(this.roles[0]);
              this.selectCurrentRole = this.roles[0];
            } else {
              this.$refs["role-table"].setCurrentRow(null);
              this.selectCurrentRole = null;
              this.doSearch();
            }
          });
        })
        .catch(() => {
          Message.info("取消删除");
        });
    },
    initRole(role) {
      var rolePermissionModel = INIT_WEB_PERMISSIONS(role.permissions);
      var roleModel = {
        id: role.id,
        name: role.name,
        str: role.str,
        loginPermissions: [],
        roleManagementPermissions: [],
        userManagementPermissions: [],
        consumablesTypeManagementPermissions: [],
        referenctInformationTemplatePermissions: [],
        consumablesManagementPermissions: [],
        applicationManagementPermissions: [],
        userOperateManagementPermissions: [],
        statisticsManagementPermissions: [],
        remarks: role.remarks
      };
      rolePermissionModel.loginPermissions.forEach(item => {
        roleModel.loginPermissions.push(item.key);
      });
      rolePermissionModel.roleManagementPermissions.forEach(item => {
        roleModel.roleManagementPermissions.push(item.key);
      });
      rolePermissionModel.userManagementPermissions.forEach(item => {
        roleModel.userManagementPermissions.push(item.key);
      });
      rolePermissionModel.consumablesTypeManagementPermissions.forEach(item => {
        roleModel.consumablesTypeManagementPermissions.push(item.key);
      });
      rolePermissionModel.referenctInformationTemplatePermissions.forEach(
        item => {
          roleModel.referenctInformationTemplatePermissions.push(item.key);
        }
      );
      rolePermissionModel.consumablesManagementPermissions.forEach(item => {
        roleModel.consumablesManagementPermissions.push(item.key);
      });
      rolePermissionModel.applicationManagementPermissions.forEach(item => {
        roleModel.applicationManagementPermissions.push(item.key);
      });
      rolePermissionModel.userOperateManagementPermissions.forEach(item => {
        roleModel.userOperateManagementPermissions.push(item.key);
      });
      rolePermissionModel.statisticsManagementPermissions.forEach(item => {
        roleModel.statisticsManagementPermissions.push(item.key);
      });
      return roleModel;
    },
    showRoleDetailCntainer() {
      if (!this.selectCurrentRole) {
        return;
      }
      this.roleDetailModel = this.initRole(this.selectCurrentRole);
      this.breadcrumbs.push("详情");
      this.isShowRoleDetailContainer = true;
    },
    showEditRoleContainer(isFromDetail) {
      if (isFromDetail) {
        this.hideRoleDetailContainer();
      } else if (!this.selectCurrentRole) {
        return;
      }
      this.editRoleModel = this.initRole(this.selectCurrentRole);
      this.breadcrumbs.push("编辑");
      this.isShowEditRoleContainer = true;
    },
    showAddRoleContainer() {
      this.breadcrumbs.push("新增");
      this.isShowAddRoleContainer = true;
    },
    handlePermissionChanged(val) {
      if (val.length === 0) {
        return;
      }
      if (
        val.find(item => {
          return item.indexOf("_select") !== -1;
        })
      ) {
        return;
      }
      var selectPermissionStr = "c_" + val[0].split("_")[1] + "_select";
      val.push(selectPermissionStr);
    },
    hideRoleDetailContainer() {
      this.resetRoleDetailModel();
      this.breadcrumbs.pop();
      this.isShowRoleDetailContainer = false;
    },
    hideEditRoleContainer() {
      this.resetEditRoleModel();
      this.$refs["edit-role-form"].resetFields();
      this.breadcrumbs.pop();
      this.isShowEditRoleContainer = false;
    },
    hideAddRoleContainer() {
      this.resetAddRoleModel();
      this.$refs["add-role-form"].resetFields();
      this.breadcrumbs.pop();
      this.isShowAddRoleContainer = false;
    },
    resetRoleDetailModel() {
      this.roleDetailModel = {
        id: "",
        name: "",
        str: "",
        loginPermissions: [],
        roleManagementPermissions: [],
        userManagementPermissions: [],
        consumablesTypeManagementPermissions: [],
        consumablesManagementPermissions: [],
        applicationManagementPermissions: [],
        userOperateManagementPermissions: [],
        statisticsManagementPermissions: [],
        remarks: ""
      };
    },
    resetEditRoleModel() {
      this.editRoleModel = {
        id: "",
        name: "",
        str: "",
        loginPermissions: [],
        roleManagementPermissions: [],
        userManagementPermissions: [],
        consumablesTypeManagementPermissions: [],
        consumablesManagementPermissions: [],
        applicationManagementPermissions: [],
        userOperateManagementPermissions: [],
        statisticsManagementPermissions: [],
        remarks: ""
      };
    },
    resetAddRoleModel() {
      this.addRoleModel = {
        name: "",
        str: "",
        loginPermissions: [],
        roleManagementPermissions: [],
        userManagementPermissions: [],
        consumablesTypeManagementPermissions: [],
        consumablesManagementPermissions: [],
        applicationManagementPermissions: [],
        userOperateManagementPermissions: [],
        statisticsManagementPermissions: [],
        remarks: ""
      };
    },
    mergePermissions(obj) {
      var permissions = obj.loginPermissions
        .concat(obj.roleManagementPermissions)
        .concat(obj.userManagementPermissions)
        .concat(obj.consumablesTypeManagementPermissions)
        .concat(obj.referenctInformationTemplatePermissions)
        .concat(obj.consumablesManagementPermissions)
        .concat(obj.applicationManagementPermissions)
        .concat(obj.userOperateManagementPermissions)
        .concat(obj.statisticsManagementPermissions);
      return permissions;
    },
    submitEditRoleForm(formName) {
      this.$refs[formName].validate(valid => {
        if (valid) {
          var role = {
            id: this.editRoleModel.id,
            warehouseId: this.$store.state.curWarehouse.id,
            name: this.editRoleModel.name,
            str: this.editRoleModel.str,
            remarks: this.editRoleModel.remarks,
            permissionStrs: this.mergePermissions(this.editRoleModel)
          };
          editRoleApi(role).then(res => {
            Message.success("保存成功");
            this.resetEditRoleModel();
            this.$refs["edit-role-form"].resetFields();
            this.roles.forEach(item => {
              if (item.id === res.data.id) {
                item.code = res.data.code;
                item.name = res.data.name;
                item.str = res.data.str;
                item.remarks = res.data.remarks;
                item.permissions = res.data.permissions;
              }
            });
            this.breadcrumbs.pop();
            this.isShowEditRoleContainer = false;
          });
        }
      });
    },
    submitAddRoleForm(formName) {
      this.$refs[formName].validate(valid => {
        if (valid) {
          var role = {
            warehouseId: this.$store.state.curWarehouse.id,
            name: this.addRoleModel.name,
            str: this.addRoleModel.str,
            remarks: this.addRoleModel.remarks,
            permissionStrs: this.mergePermissions(this.addRoleModel)
          };
          addRoleApi(role).then(res => {
            Message.success("保存成功");
            this.resetAddRoleModel();
            this.$refs["add-role-form"].resetFields();
            this.total += 1;
            this.roles.unshift(res.data);
            this.$refs["role-table"].setCurrentRow(this.roles[0]);
            this.breadcrumbs.pop();
            this.isShowAddRoleContainer = false;
          });
        }
      });
    },
    exportRoles() {
      var url =
        base.url +
        "/cRole/exportRoles?warehouseId=" +
        this.$store.state.curWarehouse.id;
      window.open(url);
    },
    async importRoles(data) {
      await importRolesApi({
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
    handleCloseImportRolesDialog() {},
    handleFinishImportRolesDialog() {
      this.doSearch();
    },
    handleOpen() {
      this.isShowImportDialog = true;
    }
  }
};
</script>

<style lang="less">
.consumables-grid-roles-container {
  height: calc(100% - 100px);
}

.consumables-role-detail-container,
.consumables-edit-role-container,
.consumables-add-role-container {
  height: 100%;
  overflow-x: hidden;
  overflow-y: auto;

  #role-detail-form,
  #edit-role-form,
  #add-role-form {
    width: 700px;
  }
}

.consumables-import-container {
  height: 100%;
  overflow-x: hidden;
  overflow-y: auto;

  #import-role-form {
    width: 700px;
  }
}
</style>