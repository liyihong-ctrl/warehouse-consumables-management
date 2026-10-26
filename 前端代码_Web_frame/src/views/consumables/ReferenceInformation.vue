<template>
  <LayoutComponent :active-menu="menuName" @curWarehouseChanged="curWarehouseChanged">
    <el-breadcrumb separator-class="el-icon-arrow-right">
      <el-breadcrumb-item v-for="(item,index) in breadcrumbs" :key="index">{{item}}</el-breadcrumb-item>
    </el-breadcrumb>
    <hr />
    <el-row class="operation-container" v-show="isShowMainContainer">
      <el-col class="search-container" :span="8">
        <el-input placeholder="请输入搜索内容" v-model="searchStr" clearable style="max-width: 350px;">
          <template slot="prepend">模板名</template>
        </el-input>
        <el-button class="search-btn" type="primary" icon="el-icon-search" @click="doSearch"></el-button>
      </el-col>
      <el-col class="btn-group" :span="16">
        <el-button
          type="danger"
          plain
          @click="deteteTemplate"
          :disabled="!canDelete"
          v-if="hasPermission('c_referenceInformationTemplate_delete')"
        >删除</el-button>
        <el-button
          type="primary"
          plain
          @click="showTempalteDetailCntainer"
          v-if="hasPermission('c_referenceInformationTemplate_select')"
        >详情</el-button>
        <el-button
          type="primary"
          plain
          @click="showEditTempalteContainer(false)"
          :disabled="!canEdit"
          v-if="hasPermission('c_referenceInformationTemplate_update')"
        >编辑</el-button>
        <el-button
          type="primary"
          plain
          @click="showAddTempalteContainer"
          v-if="hasPermission('c_referenceInformationTemplate_add')"
        >新增</el-button>
        <el-button
          type="info"
          plain
          @click="exportTemplates"
          v-if="hasPermission('c_referenceInformationTemplate_select')"
        >导出</el-button>
        <div
          style="margin-left: 10px"
          v-if="hasPermission('c_referenceInformationTemplate_add')"
        >
          <ele-import
            :fields="fields"
            :filepath="filepath"
            :requestFn="importTemplates"
            :rules="importTemplateRules"
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
    <el-container class="consumables-grid-template-container" v-show="isShowMainContainer">
      <el-main>
        <el-table
          ref="template-table"
          :data="templates"
          border
          stripe
          height="100%"
          highlight-current-row
          :header-cell-style="setTableHeaderStyle"
          @current-change="handleCurrentChange"
        >
          <el-table-column label="名称" width="250" align="center">
            <template slot-scope="scope">
              <span :style="scope.row.isSystem ? 'color: #F56C6C;' : ''">{{ scope.row.name }}</span>
            </template>
          </el-table-column>
          <el-table-column label="基准信息数" width="100" align="right">
            <template slot-scope="scope">
              <span>{{scope.row.details.length}}</span>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建日期" width="135" align="center" sortable></el-table-column>
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
    <el-container class="custom-el-container" v-if="isShowTemplateDetailContainer">
      <el-main class="custom-el-main">
        <div class="consumables-template-detail-container">
          <el-scrollbar style="height: 100%">
            <el-form id="template-detail-form" label-width="160px" :model="templateDetailModel">
              <el-form-item label="名称：">
                <span>{{ templateDetailModel.name }}</span>
              </el-form-item>
              <el-form-item label="备注：">
                <span>{{ templateDetailModel.remarks }}</span>
              </el-form-item>
              <el-form-item
                v-for="(detail, index) in templateDetailModel.details"
                :label="'基准信息名 ' + (index+1)"
                :key="index"
              >
                <span>{{ detail.key }}</span>
              </el-form-item>
              <el-form-item>
                <el-button @click="hideTemplateDetailContainer">返回</el-button>
                <el-button type="primary" @click="showEditTempalteContainer(true)" v-if="canEdit">编辑</el-button>
              </el-form-item>
            </el-form>
          </el-scrollbar>
        </div>
      </el-main>
    </el-container>
    <el-container class="custom-el-container" v-if="isshowEditTempalteContainer">
      <el-main class="custom-el-main">
        <div class="consumables-edit-template-container">
          <el-scrollbar style="height: 100%">
            <el-form
              id="edit-template-form"
              label-width="160px"
              ref="edit-template-form"
              status-icon
              :model="editTemplateModel"
              :rules="templateRules"
            >
              <el-form-item label="名称：" prop="name">
                <el-input placeholder="名称" v-model="editTemplateModel.name" clearable></el-input>
              </el-form-item>
              <el-form-item label="备注：" prop="remarks">
                <el-input
                  type="textarea"
                  :autosize="{ minRows: 4, maxRows: 6}"
                  placeholder="请输入描述"
                  v-model="editTemplateModel.remarks"
                ></el-input>
              </el-form-item>
              <el-form-item
                v-for="(detail, index) in editTemplateModel.details"
                :label="'基准信息名 ' + (index+1)"
                :key="index"
              >
                <el-row :gutter="20">
                  <el-col :span="20" style="padding: 0;">
                    <el-input placeholder="名" v-model="detail.key"></el-input>
                  </el-col>
                  <el-col :span="2">
                    <el-button
                      type="danger"
                      icon="el-icon-delete"
                      size="mini"
                      circle
                      @click="removeReferenceInfoInput(editTemplateModel, detail)"
                    ></el-button>
                  </el-col>
                </el-row>
              </el-form-item>
              <el-form-item>
                <el-button @click="hideEditTemplateContainer">取消</el-button>
                <el-button type="primary" @click="addReferenceInfoInput(editTemplateModel)">添加基准信息</el-button>
                <el-button type="primary" @click="submitEditTemplateForm('edit-template-form')">保存</el-button>
              </el-form-item>
            </el-form>
          </el-scrollbar>
        </div>
      </el-main>
    </el-container>
    <el-container class="custom-el-container" v-if="isshowAddTempalteContainer">
      <el-main class="custom-el-main">
        <div class="consumables-add-template-container">
          <el-scrollbar style="height: 100%">
            <el-form
              id="add-template-form"
              label-width="160px"
              ref="add-template-form"
              status-icon
              :model="addTemplateModel"
              :rules="templateRules"
            >
              <el-form-item label="名称：" prop="name">
                <el-input placeholder="名称" v-model="addTemplateModel.name" clearable></el-input>
              </el-form-item>
              <el-form-item label="备注：" prop="remarks">
                <el-input
                  type="textarea"
                  :autosize="{ minRows: 4, maxRows: 6}"
                  placeholder="请输入描述"
                  v-model="addTemplateModel.remarks"
                ></el-input>
              </el-form-item>
              <el-form-item
                v-for="(detail, index) in addTemplateModel.details"
                :label="'基准信息名 ' + (index+1)"
                :key="index"
              >
                <el-row :gutter="20">
                  <el-col :span="20" style="padding: 0;">
                    <el-input placeholder="名" v-model="detail.key"></el-input>
                  </el-col>
                  <el-col :span="2">
                    <el-button
                      type="danger"
                      icon="el-icon-delete"
                      size="mini"
                      circle
                      @click="removeReferenceInfoInput(addTemplateModel, detail)"
                    ></el-button>
                  </el-col>
                </el-row>
              </el-form-item>
              <el-form-item>
                <el-button @click="hideAddTemplateContainer">取消</el-button>
                <el-button type="primary" @click="addReferenceInfoInput(addTemplateModel)">添加基准信息</el-button>
                <el-button type="primary" @click="submitAddTemplateForm('add-template-form')">保存</el-button>
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
import { HAS_PERMISSION } from "../../utils/permission";
import {
  getReferenceInformationTemplatesApi,
  addReferenceInformationTemplateApi,
  updateReferenceInformationTemplateApi,
  deleteReferenceInformationTemplateApi,
  importReferenceInformationTemplatesApi
} from "../../api/consumables/referenceInformation";

export default {
  name: "c_template",
  components: {
    LayoutComponent
  },
  inject: ["reload"],
  mounted() {
    this.getInitData();
  },
  computed: {
    isShowMainContainer() {
      return (
        !this.isShowTemplateDetailContainer &&
        !this.isshowEditTempalteContainer &&
        !this.isshowAddTempalteContainer
      );
    },
    canDelete() {
      if (!this.selectCurrentTemplate) {
        return false;
      }
      return !this.selectCurrentTemplate.isSystem;
    },
    canEdit() {
      if (!this.selectCurrentTemplate) {
        return false;
      }
      return !this.selectCurrentTemplate.isSystem;
    }
  },
  data: function() {
    return {
      menuName: "/consumables/referenceInformation",
      breadcrumbs: ["基准信息模板"],
      isShowTemplateDetailContainer: false,
      isshowEditTempalteContainer: false,
      isshowAddTempalteContainer: false,
      total: 0,
      page: {
        pageNum: 1,
        pageSize: 30,
        searchCondition: null
      },
      templates: [],
      selectCurrentTemplate: null,
      searchStr: "",
      templateDetailModel: null,
      editTemplateModel: {
        id: "",
        warehouseId: "",
        name: "",
        remarks: "",
        details: []
      },
      addTemplateModel: {
        warehouseId: "",
        name: "",
        remarks: "",
        details: []
      },
      templateRules: {
        name: [
          { required: true, message: "请输入名称", trigger: "blur" },
          { min: 1, max: 20, message: "名称长度范围为1~20", trigger: "blur" },
          { min: 1, max: 20, message: "名称长度范围为1~20", trigger: "change" }
        ],
        remarks: [
          { min: 0, max: 255, message: "备注长度范围为0~255", trigger: "blur" },
          {
            min: 0,
            max: 255,
            message: "备注长度范围为0~255",
            trigger: "change"
          }
        ]
      },
      importDialogTitle: "批量导入基准信息模板",
      importTips: [],
      fields: {
        name: "名称",
        remarks: "备注",
        referenceInformationKey1: "基准信息名1",
        referenceInformationKey2: "基准信息名2",
        referenceInformationKey3: "基准信息名3",
        referenceInformationKey4: "基准信息名4",
        referenceInformationKey5: "基准信息名5",
        referenceInformationKey6: "基准信息名6",
        referenceInformationKey7: "基准信息名7",
        referenceInformationKey8: "基准信息名8",
        referenceInformationKey9: "基准信息名9",
        referenceInformationKey10: "基准信息名10",
        referenceInformationKey11: "基准信息名11",
        referenceInformationKey12: "基准信息名12",
        referenceInformationKey13: "基准信息名13",
        referenceInformationKey14: "基准信息名14",
        referenceInformationKey15: "基准信息名15",
        referenceInformationKey16: "基准信息名16",
        referenceInformationKey17: "基准信息名17",
        referenceInformationKey18: "基准信息名18",
        referenceInformationKey19: "基准信息名19",
        referenceInformationKey20: "基准信息名20"
      },
      importTemplateRules: {
        name: [
          { required: true, message: "请输入名称", trigger: "blur" },
          { min: 1, max: 20, message: "名称长度范围为1~20", trigger: "blur" },
          { min: 1, max: 20, message: "名称长度范围为1~20", trigger: "change" }
        ],
        remarks: [
          { min: 0, max: 255, message: "备注长度范围为0~255", trigger: "blur" },
          {
            min: 0,
            max: 255,
            message: "备注长度范围为0~255",
            trigger: "change"
          }
        ]
      },
      filepath:
        "http://127.0.0.1:8888/cReferenceInformation/template?warehouseId=" +
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
      getReferenceInformationTemplatesApi(this.page).then(res => {
        this.total = res.data.total;
        this.templates = res.data.list;
        if (this.templates && this.templates.length > 0) {
          this.$refs["template-table"].setCurrentRow(this.templates[0]);
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
      this.selectCurrentTemplate = val;
    },
    deteteTemplate() {
      if (!this.selectCurrentTemplate) {
        return;
      }
      this.$confirm(
        "此操作将永久删除名称为「" +
          this.selectCurrentTemplate.name +
          "」的基准信息模板, 是否继续?",
        "删除",
        {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }
      )
        .then(() => {
          deleteReferenceInformationTemplateApi({
            warehouseId: this.$store.state.curWarehouse.id,
            id: this.selectCurrentTemplate.id
          }).then(() => {
            Message.success("删除成功");
            this.total -= 1;
            this.templates = this.templates.filter(item => {
              return item.id !== this.selectCurrentTemplate.id;
            });
            if (this.templates.length > 0) {
              this.$refs["template-table"].setCurrentRow(this.templates[0]);
              this.selectCurrentTemplate = this.templates[0];
            } else {
              this.$refs["template-table"].setCurrentRow(null);
              this.selectCurrentTemplate = null;
              this.doSearch();
            }
          });
        })
        .catch(() => {
          Message.info("取消删除");
        });
    },
    showTempalteDetailCntainer() {
      if (!this.selectCurrentTemplate) {
        return;
      }
      this.templateDetailModel = {
        id: this.selectCurrentTemplate.id,
        warehouseId: this.$store.state.curWarehouse.id,
        name: this.selectCurrentTemplate.name,
        remarks: this.selectCurrentTemplate.remarks,
        details: []
      };
      this.selectCurrentTemplate.details.forEach(item => {
        this.templateDetailModel.details.push({
          key: item.referenceKey
        });
      });
      this.breadcrumbs.push("详情");
      this.isShowTemplateDetailContainer = true;
    },
    showEditTempalteContainer(isFromDetail) {
      if (isFromDetail) {
        this.hideTemplateDetailContainer();
      } else if (!this.selectCurrentTemplate) {
        return;
      }
      this.editTemplateModel = {
        id: this.selectCurrentTemplate.id,
        warehouseId: this.$store.state.curWarehouse.id,
        name: this.selectCurrentTemplate.name,
        remarks: this.selectCurrentTemplate.remarks,
        details: []
      };
      this.selectCurrentTemplate.details.forEach(item => {
        this.editTemplateModel.details.push({
          key: item.referenceKey
        });
      });
      this.breadcrumbs.push("编辑");
      this.isshowEditTempalteContainer = true;
    },
    showAddTempalteContainer() {
      this.addReferenceInfoInput(this.addTemplateModel);
      this.breadcrumbs.push("新增");
      this.isshowAddTempalteContainer = true;
    },
    addReferenceInfoInput(tempalte) {
      tempalte.details.push({
        key: ""
      });
    },
    removeReferenceInfoInput(tempalte, item) {
      var index = tempalte.details.indexOf(item);
      if (index !== -1) {
        tempalte.details.splice(index, 1);
      }
    },
    hideTemplateDetailContainer() {
      this.templateDetailModel = null;
      this.breadcrumbs.pop();
      this.isShowTemplateDetailContainer = false;
    },
    hideEditTemplateContainer() {
      this.editTemplateModel = {
        id: "",
        warehouseId: "",
        name: "",
        remarks: "",
        details: []
      };
      this.$refs["edit-template-form"].resetFields();
      this.breadcrumbs.pop();
      this.isshowEditTempalteContainer = false;
    },
    hideAddTemplateContainer() {
      this.addTemplateModel = {
        warehouseId: "",
        name: "",
        remarks: "",
        details: []
      };
      this.$refs["add-template-form"].resetFields();
      this.breadcrumbs.pop();
      this.isshowAddTempalteContainer = false;
    },
    submitEditTemplateForm(formName) {
      this.$refs[formName].validate(valid => {
        if (valid) {
          var editTemplateModel = {
            id: this.editTemplateModel.id,
            warehouseId: this.$store.state.curWarehouse.id,
            name: this.editTemplateModel.name,
            remarks: this.editTemplateModel.remarks,
            details: { details: JSON.stringify(this.editTemplateModel.details) }
          };
          updateReferenceInformationTemplateApi(editTemplateModel).then(res => {
            Message.success("保存成功");
            this.hideEditTemplateContainer();
            this.templates.forEach(item => {
              if (item.id === res.data.id) {
                item.id = res.data.id;
                item.warehouseId = res.data.warehouseId;
                item.name = res.data.name;
                item.remarks = res.data.remarks;
                item.createTime = res.data.createTime;
                item.isDelete = res.data.isDelete;
                item.details = res.data.details;
                return;
              }
            });
          });
        }
      });
    },
    submitAddTemplateForm(formName) {
      this.$refs[formName].validate(valid => {
        if (valid) {
          var addTemplateModel = {
            warehouseId: this.$store.state.curWarehouse.id,
            name: this.addTemplateModel.name,
            remarks: this.addTemplateModel.remarks,
            details: { details: JSON.stringify(this.addTemplateModel.details) }
          };
          addReferenceInformationTemplateApi(addTemplateModel).then(res => {
            Message.success("保存成功");
            this.total += 1;
            this.templates.unshift(res.data);
            this.$refs["template-table"].setCurrentRow(this.templates[0]);
            this.hideAddTemplateContainer();
          });
        }
      });
    },
    exportTemplates() {
      var url =
        base.url +
        "/cReferenceInformation/exportReferenceInformationTemplates?warehouseId=" +
        this.$store.state.curWarehouse.id;
      window.open(url);
    },
    async importTemplates(data) {
      await importReferenceInformationTemplatesApi({
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

<style lang="less" scoped>
.consumables-grid-template-container {
  height: calc(100% - 100px);
}

.consumables-template-detail-container,
.consumables-edit-template-container,
.consumables-add-template-container {
  height: 100%;
  overflow-x: hidden;
  overflow-y: auto;

  #template-detail-form,
  #edit-template-form,
  #add-template-form {
    width: 500px;
  }
}
</style>