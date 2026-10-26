<template>
  <LayoutComponent :active-menu="menuName" @curWarehouseChanged="curWarehouseChanged">
    <el-breadcrumb separator-class="el-icon-arrow-right">
      <el-breadcrumb-item v-for="(item,index) in breadcrumbs" :key="index">{{item}}</el-breadcrumb-item>
    </el-breadcrumb>
    <hr />
    <el-row class="operation-container" v-show="isShowMainContainer">
      <el-col class="search-container" :span="8">
        <el-input placeholder="请输入根类型名搜索" v-model="searchStr" clearable style="max-width: 350px;">
          <template slot="prepend">类型名</template>
        </el-input>
        <el-button class="search-btn" type="primary" icon="el-icon-search" @click="doSearch"></el-button>
      </el-col>
      <el-col class="btn-group" :span="16">
        <el-tooltip effect="dark" content="展开" placement="top-start">
          <el-button
            type="primary"
            plain
            icon="el-icon-plus"
            @click="foldList = []"
            :disabled="foldList.length === 0"
          ></el-button>
        </el-tooltip>
        <el-tooltip effect="dark" content="收起" placement="top-start">
          <el-button
            type="primary"
            plain
            icon="el-icon-minus"
            @click="foldList = foldAllList"
            :disabled="foldList === foldAllList"
          ></el-button>
        </el-tooltip>
        <el-button
          type="danger"
          plain
          v-if="hasPermission('c_consumablesType_delete')"
          @click="deleteType"
        >删除</el-button>
        <el-button
          type="primary"
          plain
          v-if="hasPermission('c_consumablesType_update')"
          @click="showEditTypeDialog"
        >编辑</el-button>
        <el-dropdown
          class="custom-btn-dropdown"
          @command="showAddTypeContainer"
          v-if="hasPermission('c_consumablesType_add')"
        >
          <el-button type="primary" plain>
            新增
            <i class="el-icon-arrow-down el-icon--right"></i>
          </el-button>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item icon="el-icon-tickets" command="similar">同类</el-dropdown-item>
            <el-dropdown-item icon="el-icon-tickets" command="subclass">子类</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
        <el-button
          style="margin-left: 10px"
          type="info"
          plain
          @click="exportTypes"
          v-if="hasPermission('c_consumablesType_export')"
        >导出</el-button>
        <div style="margin-left: 10px" v-if="hasPermission('c_consumablesType_import') && false">
          <ele-import
            :fields="fields"
            :filepath="filepath"
            :requestFn="importTypes"
            :rules="importTypeRules"
            :tips="importTips"
            :title="importDialogTitle"
            :visible.sync="isShowImportDialog"
            @close="handleCloseimportTypesDialog"
            @finish="handleFinishimportTypesDialog"
          />
          <el-button type="info" plain @click="handleOpen">导入</el-button>
        </div>
      </el-col>
    </el-row>
    <el-container class="consumables-grid-types-container" v-show="isShowMainContainer">
      <el-main>
        <el-table
          ref="type-table"
          :data="types"
          border
          stripe
          height="100%"
          highlight-current-row
          :header-cell-style="setTableHeaderStyle"
          :row-style="toggleDisplayTr"
          @current-change="handleCurrentChange"
        >
          <el-table-column label="序号" width="55" type="index" align="center"></el-table-column>
          <el-table-column label="类型名称" width="350" align="left" show-overflow-tooltip>
            <template slot-scope="scope">
              <p :style="`margin-left: ${scope.row.__level * 20}px;margin-top:0;margin-bottom:0`">
                <i
                  class="permission_toggleFold"
                  :class="toggleFoldingClass(scope.row)"
                  @click="toggleFoldingStatus(scope.row)"
                ></i>
                {{scope.row.name}}
              </p>
            </template>
          </el-table-column>
          <el-table-column label="层级" width="50" align="center" prop="__level"></el-table-column>
          <el-table-column
            prop="curTypeConsumableCount"
            label="消耗品数(当前类型)"
            width="90"
            align="right"
          ></el-table-column>
          <el-table-column prop="totalAmount" label="合计" width="80" align="right"></el-table-column>
          <el-table-column label="创建日期" width="150" prop="createTime" align="center"></el-table-column>
          <el-table-column label="备注" prop="remarks" show-overflow-tooltip></el-table-column>
        </el-table>
        <el-dialog
          width="425px"
          :title="typeDialogTitle"
          :show-close="true"
          :modal-append-to-body="false"
          :close-on-click-modal="false"
          :close-on-press-escape="false"
          :visible.sync="isShowTypeDialog"
          @closed="hideTypeDialog"
        >
          <el-form
            id="type-form"
            label-width="95px"
            ref="type-form"
            status-icon
            :model="typeModel"
            :rules="typeRules"
          >
            <el-form-item label="类型名称：">
              <span v-if="typeModel.parent">{{ typeModel.parent.name }}</span>
              <span v-else>根类型</span>
            </el-form-item>
            <el-form-item label="类型名称：" prop="name">
              <el-input v-model="typeModel.name"></el-input>
            </el-form-item>
            <el-form-item label="备注：" prop="remarks">
              <el-input v-model="typeModel.remarks"></el-input>
            </el-form-item>
          </el-form>
          <div slot="footer" class="dialog-footer">
            <el-button @click="hideTypeDialog">取 消</el-button>
            <el-button type="primary" @click="submitTypeForm('type-form')">确 定</el-button>
          </div>
        </el-dialog>
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
  </LayoutComponent>
</template>

<script>
import { Message } from "element-ui";
import Vue from "vue";
import LayoutComponent from "@/components/consumables/Layout.vue";
import { headerRowStyle } from "../../utils/layout";
import base from "../../utils/base";
import { HAS_PERMISSION } from "../../utils/permission";
import {
  getTypesApi,
  updateTypeApi,
  addTypeApi,
  deleteTypeApi,
  importTypesApi
} from "../../api/consumables/type";

export default {
  name: "c_type",
  components: {
    LayoutComponent
  },
  inject: ["reload"],
  mounted() {
    this.getInitData();
  },
  computed: {
    isShowMainContainer() {
      return true;
    },
    /*********************************
     ** Fn: foldAllList
     ** Intro: 记录属性结构的首层节点
     ** Author: zyx
     *********************************/
    foldAllList() {
      return this.types.map(x => x.__identity);
    }
  },
  data: function() {
    return {
      menuName: "/consumables/type",
      breadcrumbs: ["类型管理"],
      isShowRoleDetailContainer: false,
      isShowEditRoleContainer: false,
      isShowTypeDialog: false,
      total: 0,
      page: {
        pageNum: 1,
        pageSize: 30,
        searchCondition: null
      },
      types: [],
      typesOrigin: [],
      selectCurrentType: null,
      searchType: "1",
      searchStr: "",
      typeModel: {
        warehouseId: "",
        addType: "",
        parent: null,
        name: "",
        remarks: ""
      },
      typeRules: {
        name: [
          { required: true, message: "类型名称不允许为空", trigger: "blur" },
          {
            min: 1,
            max: 20,
            message: "类型名称长度必须在1~20之间",
            trigger: "blur"
          },
          {
            min: 1,
            max: 20,
            message: "类型名称长度必须在1~20之间",
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
      foldList: [],
      dialogState: 1,
      typeDialogTitle: "",
      importDialogTitle: "批量导入类型",
      importTips: [],
      fields: {
        name1: "类型名称1",
        name2: "类型名称2",
        name3: "类型名称3",
        name4: "类型名称4",
        name5: "类型名称5",
        name6: "类型名称6",
        name7: "类型名称7",
        name8: "类型名称8",
        name9: "类型名称9",
        name10: "类型名称10",
        remarks: "备注"
      },
      importTypeRules: {
        remarks: [
          {
            min: 0,
            max: 50,
            message: "备注长度必须在0~50之间"
          }
        ]
      },
      filepath:
        "http://127.0.0.1:8888/cType/template?warehouseId=" +
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
      getTypesApi(this.page).then(res => {
        this.foldList = [];
        this.total = res.data.total;
        this.typesOrigin = res.data.list;
        this.types = this.formatConversion([], this.typesOrigin);
        if (this.types && this.types.length > 0) {
          this.$refs["type-table"].setCurrentRow(this.types[0]);
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
      this.selectCurrentType = val;
    },
    /*********************************
     ** Fn: toggleFoldingStatus
     ** Intro: 切换展开 还是折叠
     ** @params: params 当前点击行的数据
     ** Author: zyx
     *********************************/
    toggleFoldingStatus(params) {
      this.foldList.includes(params.__identity)
        ? this.foldList.splice(this.foldList.indexOf(params.__identity), 1)
        : this.foldList.push(params.__identity);
    },
    /*********************************
     ** Fn: toggleFoldingClass
     ** Intro: 如果子集长度为0，则不返回字体图标。
     ** Intro: 如果子集长度为不为0，根据foldList是否存在当前节点的标识返回相应的折叠或展开图标
     ** Intro: 关于class说明：permission_placeholder返回一个占位符，具体查看class
     ** @params: params 当前行的数据对象
     ** Author: zyx
     *********************************/
    toggleFoldingClass(params) {
      return params.children.length === 0
        ? "permission_placeholder"
        : this.foldList.indexOf(params.__identity) === -1
        ? "iconfont icon-minus-square-o"
        : "iconfont icon-plussquareo";
    },
    /*********************************
     ** Fn: toggleDisplayTr
     ** Intro: 该方法会对每一行数据都做判断 如果foldList 列表中的元素 也存在与当前行的 __family列表中  则该行不展示
     ** @params:
     ** Author: zyx
     *********************************/
    toggleDisplayTr({ row }) {
      for (let i = 0; i < this.foldList.length; i++) {
        let item = this.foldList[i];
        // 如果foldList中元素存在于 row.__family中，则该行隐藏。  如果该行的自身标识等于隐藏元素，则代表该元素就是折叠点
        if (row.__family.includes(item) && row.__identity !== item) {
          return { display: "none" };
        }
      }
      return "";
    },
    /*********************************
     ** Fn: formatConversion
     ** Intro: 将树形接口数据扁平化
     ** @params: parent 为当前累计的数组  也是最后返回的数组
     ** @params: children 为当前节点仍需继续扁平子节点的数据
     ** @params: index 默认等于0， 用于在递归中进行累计叠加 用于层级标识
     ** @params: family 装有当前包含元素自身的所有父级 身份标识
     ** @params: elderIdentity 父级的  唯一身份标识
     ** Author: zyx
     *********************************/
    formatConversion(
      parent,
      children,
      index = 0,
      family = [],
      elderIdentity = "x"
    ) {
      // children如果长度等于0，则代表已经到了最低层
      // let page = (this.startPage - 1) * 10
      if (children.length > 0) {
        children.map((x, i) => {
          // 设置 __level 标志位 用于展示区分层级
          Vue.set(x, "__level", index);
          // 设置 __family 为家族关系 为所有父级，包含本身在内
          Vue.set(x, "__family", [...family, elderIdentity + "_" + i]);
          // 本身的唯一标识  可以理解为个人的身份证咯 一定唯一。
          Vue.set(x, "__identity", elderIdentity + "_" + i);
          parent.push(x);
          // 如果仍有子集，则进行递归
          if (x.children.length > 0)
            this.formatConversion(
              parent,
              x.children,
              index + 1,
              [...family, elderIdentity + "_" + i],
              elderIdentity + "_" + i
            );
        });
      }
      return parent;
    },
    deleteType() {
      if (!this.selectCurrentType) {
        return;
      }
      this.$confirm(
        "此操作将永久删除名称为「" +
          this.selectCurrentType.name +
          "」的消耗品类型, 是否继续?",
        "删除",
        {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }
      )
        .then(() => {
          deleteTypeApi({
            warehouseId: this.$store.state.curWarehouse.id,
            typeId: this.selectCurrentType.id
          }).then(() => {
            Message.success("删除成功");
            this.doSearch();
          });
        })
        .catch(() => {
          Message.info("取消删除");
        });
    },
    showEditTypeDialog() {
      if (!this.selectCurrentType) {
        return;
      }
      this.typeDialogTitle = "编辑类型";
      this.dialogState = 2;
      this.typeModel.parent = this.selectCurrentType.parentId
        ? this.types.find(item => {
            return item.id === this.selectCurrentType.parentId;
          })
        : null;
      this.typeModel.name = this.selectCurrentType.name;
      this.typeModel.remarks = this.selectCurrentType.remarks;
      this.isShowTypeDialog = true;
    },
    showAddTypeContainer(command) {
      this.typeDialogTitle = "新增类型";
      this.dialogState = 1;
      this.typeModel.addType = command;
      switch (command) {
        case "similar":
          if (this.selectCurrentType) {
            this.typeModel.parent = this.types.find(item => {
              return item.id === this.selectCurrentType.parentId;
            });
          }
          break;
        case "subclass":
          if (!this.selectCurrentType) {
            this.typeDialogTitle = "";
            Message.error("无法新增：暂无数据");
            return;
          } else {
            this.typeModel.parent = this.selectCurrentType;
          }
          break;
      }
      this.isShowTypeDialog = true;
    },
    getCurTypeParentName() {
      return this.types.find(item => {
        return item.id === this.selectCurrentType.parentId;
      }).name;
    },
    hideTypeDialog() {
      this.typeModel = {
        warehouseId: "",
        addType: "",
        parent: null,
        name: "",
        remarks: ""
      };
      this.$refs["type-form"].resetFields();
      this.isShowTypeDialog = false;
    },
    submitTypeForm(formName) {
      this.$refs[formName].validate(valid => {
        if (valid) {
          if (this.dialogState === 1) {
            var typeModel = {
              warehouseId: this.$store.state.curWarehouse.id,
              parentId: this.typeModel.parent ? this.typeModel.parent.id : null,
              name: this.typeModel.name,
              remarks: this.typeModel.remarks
            };
            addTypeApi(typeModel).then(() => {
              Message.success("保存成功");
              this.doSearch();
              this.hideTypeDialog();
            });
          } else {
            var editTypeModel = {
              id: this.selectCurrentType.id,
              warehouseId: this.$store.state.curWarehouse.id,
              parentId: this.typeModel.parent ? this.typeModel.parent.id : null,
              name: this.typeModel.name,
              remarks: this.typeModel.remarks
            };
            updateTypeApi(editTypeModel).then(() => {
              Message.success("保存成功");
              this.doSearch();
              this.hideTypeDialog();
            });
          }
        }
      });
    },
    exportTypes() {
      var url =
        base.url +
        "/cType/exportTypes?warehouseId=" +
        this.$store.state.curWarehouse.id;
      window.open(url);
    },
    async importTypes(data) {
      await importTypesApi({
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
    handleCloseimportTypesDialog() {},
    handleFinishimportTypesDialog() {
      this.doSearch();
    },
    handleOpen() {
      this.isShowImportDialog = true;
    }
  }
};
</script>

<style lang="less">
.consumables-grid-types-container {
  height: calc(100% - 100px);
}
</style>