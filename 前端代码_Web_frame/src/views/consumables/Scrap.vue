<template>
  <LayoutComponent :active-menu="menuName" @curWarehouseChanged="curWarehouseChanged">
    <el-breadcrumb separator-class="el-icon-arrow-right">
      <el-breadcrumb-item v-for="(item,index) in breadcrumbs" :key="index">{{item}}</el-breadcrumb-item>
    </el-breadcrumb>
    <hr />
    <el-row class="operation-container" v-show="isShowMainContainer">
      <el-col class="search-container" :span="8">
        <el-input placeholder="请输入报废原因或经办人搜索" v-model="searchStr" clearable style="max-width: 350px;">
          <template slot="prepend">搜索</template>
        </el-input>
        <el-button class="search-btn" type="primary" icon="el-icon-search" @click="doSearch"></el-button>
      </el-col>
      <el-col class="btn-group" :span="16">
        <el-button type="danger" plain @click="deleteScrap">删除</el-button>
        <el-button type="primary" plain @click="showEditContainer">编辑</el-button>
        <el-button type="success" plain @click="approveScrap">审批通过</el-button>
        <el-button type="warning" plain @click="rejectScrap">驳回</el-button>
        <el-button type="primary" plain @click="showAddContainer">新增报废</el-button>
      </el-col>
    </el-row>
    <el-container class="consumables-grid-scrap-container" v-show="isShowMainContainer">
      <el-main>
        <el-table
          ref="scrap-table"
          :data="scraps"
          border
          stripe
          height="100%"
          highlight-current-row
          :header-cell-style="setTableHeaderStyle"
          @current-change="handleCurrentChange"
        >
          <el-table-column label="序号" width="55" type="index" align="center"></el-table-column>
          <el-table-column prop="consumableId" label="消耗品ID" width="200" align="center" show-overflow-tooltip></el-table-column>
          <el-table-column prop="quantity" label="报废数量" width="100" align="center"></el-table-column>
          <el-table-column prop="reason" label="报废原因" min-width="200" show-overflow-tooltip></el-table-column>
          <el-table-column prop="operator" label="经办人" width="120" align="center"></el-table-column>
          <el-table-column label="审批状态" width="100" align="center">
            <template slot-scope="scope">
              <el-tag :type="scope.row.status === 0 ? 'warning' : scope.row.status === 1 ? 'success' : 'danger'" size="small">
                {{ scope.row.status === 0 ? '待审批' : scope.row.status === 1 ? '已通过' : '已驳回' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建日期" width="150" sortable align="center"></el-table-column>
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

    <!-- 新增报废对话框 -->
    <el-dialog title="新增报废记录" :visible.sync="isShowAddDialog" width="500px" @close="resetAddForm">
      <el-form ref="add-form" :model="addModel" :rules="rules" label-width="100px" status-icon>
        <el-form-item label="消耗品ID" prop="consumableId">
          <el-input placeholder="请输入消耗品ID" v-model="addModel.consumableId"></el-input>
        </el-form-item>
        <el-form-item label="报废数量" prop="quantity">
          <el-input-number :min="1" v-model="addModel.quantity"></el-input-number>
        </el-form-item>
        <el-form-item label="报废原因" prop="reason">
          <el-input placeholder="请输入报废原因" v-model="addModel.reason" type="textarea" :rows="3" maxlength="500"></el-input>
        </el-form-item>
        <el-form-item label="经办人" prop="operator">
          <el-input placeholder="请输入经办人" v-model="addModel.operator" maxlength="100"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="isShowAddDialog = false">取消</el-button>
        <el-button type="primary" @click="submitAddForm">确定</el-button>
      </span>
    </el-dialog>

    <!-- 编辑报废对话框 -->
    <el-dialog title="编辑报废记录" :visible.sync="isShowEditDialog" width="500px" @close="resetEditForm">
      <el-form ref="edit-form" :model="editModel" :rules="rules" label-width="100px" status-icon>
        <el-form-item label="消耗品ID" prop="consumableId">
          <el-input placeholder="请输入消耗品ID" v-model="editModel.consumableId"></el-input>
        </el-form-item>
        <el-form-item label="报废数量" prop="quantity">
          <el-input-number :min="1" v-model="editModel.quantity"></el-input-number>
        </el-form-item>
        <el-form-item label="报废原因" prop="reason">
          <el-input placeholder="请输入报废原因" v-model="editModel.reason" type="textarea" :rows="3" maxlength="500"></el-input>
        </el-form-item>
        <el-form-item label="经办人" prop="operator">
          <el-input placeholder="请输入经办人" v-model="editModel.operator" maxlength="100"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="isShowEditDialog = false">取消</el-button>
        <el-button type="primary" @click="submitEditForm">确定</el-button>
      </span>
    </el-dialog>
  </LayoutComponent>
</template>

<script>
import LayoutComponent from "../../components/consumables/Layout";
import { headerRowStyle } from "../../utils/layout";
import {
  getScrapsApi,
  addScrapApi,
  updateScrapApi,
  deleteScrapApi,
  approveScrapApi,
  rejectScrapApi
} from "../../api/consumables/scrap";
import { Message } from "element-ui";

export default {
  name: "Scrap",
  components: {
    LayoutComponent
  },
  data() {
    return {
      menuName: "scrap",
      breadcrumbs: ["消耗品管理系统", "报废管理"],
      searchStr: "",
      scraps: [],
      page: {
        pageNum: 1,
        pageSize: 10
      },
      total: 0,
      selectCurrentScrap: null,

      isShowMainContainer: true,
      isShowAddDialog: false,
      isShowEditDialog: false,

      addModel: this.initModel(),
      editModel: this.initModel(),

      rules: {
        consumableId: [
          { required: true, message: "消耗品ID不能为空", trigger: "blur" }
        ],
        quantity: [
          { required: true, message: "报废数量不能为空", trigger: "blur" }
        ],
        reason: [
          { required: true, message: "报废原因不能为空", trigger: "blur" },
          { min: 1, max: 500, message: "原因长度范围为1~500", trigger: "blur" }
        ]
      }
    };
  },
  mounted() {
    this.getInitData();
  },
  methods: {
    getInitData() {
      if (!this.page.searchCondition) {
        this.page.searchCondition = JSON.stringify({
          warehouseId: this.$store.state.curWarehouse.id
        });
      }
      var that = this;
      getScrapsApi(this.page).then(res => {
        that.total = res.data.total;
        that.scraps = res.data.list;
        if (that.scraps && that.scraps.length > 0) {
          that.$refs["scrap-table"].setCurrentRow(that.scraps[0]);
        }
      });
    },
    curWarehouseChanged() {
      this.getInitData();
      Message.success("切换成功");
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
      this.selectCurrentScrap = val;
    },
    deleteScrap() {
      if (!this.selectCurrentScrap) { Message.warning("请先选择一条记录"); return; }
      this.$confirm("确定删除该报废记录?", "删除", {
        confirmButtonText: "确定", cancelButtonText: "取消", type: "warning"
      }).then(() => {
        deleteScrapApi({
          warehouseId: this.$store.state.curWarehouse.id,
          id: this.selectCurrentScrap.id
        }).then(() => {
          Message.success("删除成功");
          this.getInitData();
        });
      }).catch(() => { Message.info("取消删除"); });
    },
    showEditContainer() {
      if (!this.selectCurrentScrap) { Message.warning("请先选择一条记录"); return; }
      this.editModel = Object.assign({}, this.selectCurrentScrap);
      this.isShowEditDialog = true;
    },
    showAddContainer() {
      this.isShowAddDialog = true;
    },
    approveScrap() {
      if (!this.selectCurrentScrap) { Message.warning("请先选择一条记录"); return; }
      approveScrapApi({
        warehouseId: this.$store.state.curWarehouse.id,
        id: this.selectCurrentScrap.id
      }).then(() => {
        Message.success("审批通过");
        this.getInitData();
      });
    },
    rejectScrap() {
      if (!this.selectCurrentScrap) { Message.warning("请先选择一条记录"); return; }
      rejectScrapApi({
        warehouseId: this.$store.state.curWarehouse.id,
        id: this.selectCurrentScrap.id
      }).then(() => {
        Message.success("已驳回");
        this.getInitData();
      });
    },
    resetAddForm() {
      this.addModel = this.initModel();
      this.$refs["add-form"] && this.$refs["add-form"].resetFields();
    },
    resetEditForm() {
      this.editModel = this.initModel();
      this.$refs["edit-form"] && this.$refs["edit-form"].resetFields();
    },
    submitAddForm() {
      this.$refs["add-form"].validate(valid => {
        if (valid) {
          addScrapApi({
            warehouseId: this.$store.state.curWarehouse.id,
            consumableId: this.addModel.consumableId,
            quantity: this.addModel.quantity,
            reason: this.addModel.reason,
            operator: this.addModel.operator
          }).then(() => {
            Message.success("新增成功");
            this.isShowAddDialog = false;
            this.getInitData();
          });
        }
      });
    },
    submitEditForm() {
      this.$refs["edit-form"].validate(valid => {
        if (valid) {
          updateScrapApi({
            id: this.editModel.id,
            warehouseId: this.$store.state.curWarehouse.id,
            consumableId: this.editModel.consumableId,
            quantity: this.editModel.quantity,
            reason: this.editModel.reason,
            operator: this.editModel.operator
          }).then(() => {
            Message.success("保存成功");
            this.isShowEditDialog = false;
            this.getInitData();
          });
        }
      });
    },
    initModel() {
      return {
        consumableId: "",
        quantity: 1,
        reason: "",
        operator: ""
      };
    }
  }
};
</script>

<style lang="less">
.consumables-grid-scrap-container {
  height: calc(100% - 100px);
}
</style>
