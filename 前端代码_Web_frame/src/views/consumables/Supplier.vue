<template>
  <LayoutComponent :active-menu="menuName" @curWarehouseChanged="curWarehouseChanged">
    <el-breadcrumb separator-class="el-icon-arrow-right">
      <el-breadcrumb-item v-for="(item,index) in breadcrumbs" :key="index">{{item}}</el-breadcrumb-item>
    </el-breadcrumb>
    <hr />
    <el-row class="operation-container" v-show="isShowMainContainer">
      <el-col class="search-container" :span="8">
        <el-input placeholder="请输入供应商名称搜索" v-model="searchStr" clearable style="max-width: 350px;">
          <template slot="prepend">供应商名</template>
        </el-input>
        <el-button class="search-btn" type="primary" icon="el-icon-search" @click="doSearch"></el-button>
      </el-col>
      <el-col class="btn-group" :span="16">
        <el-button type="danger" plain @click="deleteSupplier">删除</el-button>
        <el-button type="primary" plain @click="showEditSupplierContainer">编辑</el-button>
        <el-button type="primary" plain @click="showAddSupplierContainer">新增</el-button>
      </el-col>
    </el-row>
    <el-container class="consumables-grid-supplier-container" v-show="isShowMainContainer">
      <el-main>
        <el-table
          ref="supplier-table"
          :data="suppliers"
          border
          stripe
          height="100%"
          highlight-current-row
          :header-cell-style="setTableHeaderStyle"
          @current-change="handleCurrentChange"
        >
          <el-table-column label="序号" width="55" type="index" align="center"></el-table-column>
          <el-table-column prop="name" label="供应商名称" width="180" align="center"></el-table-column>
          <el-table-column prop="contactPerson" label="联系人" width="120" align="center"></el-table-column>
          <el-table-column prop="contactPhone" label="联系电话" width="150" align="center"></el-table-column>
          <el-table-column prop="address" label="地址" min-width="200" show-overflow-tooltip></el-table-column>
          <el-table-column prop="remarks" label="备注" min-width="150" show-overflow-tooltip></el-table-column>
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

    <!-- 新增供应商对话框 -->
    <el-dialog title="新增供应商" :visible.sync="isShowAddDialog" width="500px" @close="resetAddForm">
      <el-form ref="add-form" :model="addModel" :rules="rules" label-width="100px" status-icon>
        <el-form-item label="供应商名称" prop="name">
          <el-input placeholder="请输入供应商名称" v-model="addModel.name" maxlength="50"></el-input>
        </el-form-item>
        <el-form-item label="联系人" prop="contactPerson">
          <el-input placeholder="请输入联系人" v-model="addModel.contactPerson" maxlength="20"></el-input>
        </el-form-item>
        <el-form-item label="联系电话" prop="contactPhone">
          <el-input placeholder="请输入联系电话" v-model="addModel.contactPhone" maxlength="20"></el-input>
        </el-form-item>
        <el-form-item label="地址" prop="address">
          <el-input placeholder="请输入地址" v-model="addModel.address" maxlength="255" type="textarea" :rows="2"></el-input>
        </el-form-item>
        <el-form-item label="备注" prop="remarks">
          <el-input placeholder="请输入备注" v-model="addModel.remarks" maxlength="255" type="textarea" :rows="2"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="isShowAddDialog = false">取消</el-button>
        <el-button type="primary" @click="submitAddForm">确定</el-button>
      </span>
    </el-dialog>

    <!-- 编辑供应商对话框 -->
    <el-dialog title="编辑供应商" :visible.sync="isShowEditDialog" width="500px" @close="resetEditForm">
      <el-form ref="edit-form" :model="editModel" :rules="rules" label-width="100px" status-icon>
        <el-form-item label="供应商名称" prop="name">
          <el-input placeholder="请输入供应商名称" v-model="editModel.name" maxlength="50"></el-input>
        </el-form-item>
        <el-form-item label="联系人" prop="contactPerson">
          <el-input placeholder="请输入联系人" v-model="editModel.contactPerson" maxlength="20"></el-input>
        </el-form-item>
        <el-form-item label="联系电话" prop="contactPhone">
          <el-input placeholder="请输入联系电话" v-model="editModel.contactPhone" maxlength="20"></el-input>
        </el-form-item>
        <el-form-item label="地址" prop="address">
          <el-input placeholder="请输入地址" v-model="editModel.address" maxlength="255" type="textarea" :rows="2"></el-input>
        </el-form-item>
        <el-form-item label="备注" prop="remarks">
          <el-input placeholder="请输入备注" v-model="editModel.remarks" maxlength="255" type="textarea" :rows="2"></el-input>
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
import { HAS_PERMISSION } from "../../utils/permission";
import {
  getSuppliersApi,
  addSupplierApi,
  updateSupplierApi,
  deleteSupplierApi
} from "../../api/consumables/supplier";
import { Message } from "element-ui";

export default {
  name: "Supplier",
  components: {
    LayoutComponent
  },
  data() {
    return {
      menuName: "supplier",
      breadcrumbs: ["消耗品管理系统", "供应商管理"],
      searchStr: "",
      suppliers: [],
      page: {
        pageNum: 1,
        pageSize: 10
      },
      total: 0,
      selectCurrentSupplier: null,

      isShowMainContainer: true,
      isShowAddDialog: false,
      isShowEditDialog: false,

      addModel: this.initAddModel(),
      editModel: this.initEditModel(),

      rules: {
        name: [
          { required: true, message: "供应商名称不能为空", trigger: "blur" },
          { min: 1, max: 50, message: "供应商名称长度范围为1~50", trigger: "blur" }
        ],
        contactPerson: [
          { min: 0, max: 20, message: "联系人长度范围为0~20", trigger: "blur" }
        ],
        contactPhone: [
          { min: 0, max: 20, message: "联系电话长度范围为0~20", trigger: "blur" }
        ],
        address: [
          { min: 0, max: 255, message: "地址长度范围为0~255", trigger: "blur" }
        ],
        remarks: [
          { min: 0, max: 255, message: "备注长度范围为0~255", trigger: "blur" }
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
      getSuppliersApi(this.page).then(res => {
        that.total = res.data.total;
        that.suppliers = res.data.list;
        if (that.suppliers && that.suppliers.length > 0) {
          that.$refs["supplier-table"].setCurrentRow(that.suppliers[0]);
        }
      });
    },
    curWarehouseChanged() {
      this.getInitData();
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
      this.selectCurrentSupplier = val;
    },
    deleteSupplier() {
      if (!this.selectCurrentSupplier) {
        Message.warning("请先选择一条记录");
        return;
      }
      this.$confirm(
        "此操作将永久删除供应商「" +
          this.selectCurrentSupplier.name +
          "」, 是否继续?",
        "删除",
        {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }
      )
        .then(() => {
          deleteSupplierApi({
            warehouseId: this.$store.state.curWarehouse.id,
            id: this.selectCurrentSupplier.id
          }).then(() => {
            Message.success("删除成功");
            this.suppliers = this.suppliers.filter(item => {
              return item.id !== this.selectCurrentSupplier.id;
            });
            if (this.suppliers.length > 0) {
              this.$refs["supplier-table"].setCurrentRow(this.suppliers[0]);
              this.selectCurrentSupplier = this.suppliers[0];
            } else {
              this.$refs["supplier-table"].setCurrentRow(null);
              this.selectCurrentSupplier = null;
              this.doSearch();
            }
          });
        })
        .catch(() => {
          Message.info("取消删除");
        });
    },
    showEditSupplierContainer() {
      if (!this.selectCurrentSupplier) {
        Message.warning("请先选择一条记录");
        return;
      }
      this.editModel.id = this.selectCurrentSupplier.id;
      this.editModel.name = this.selectCurrentSupplier.name;
      this.editModel.contactPerson = this.selectCurrentSupplier.contactPerson;
      this.editModel.contactPhone = this.selectCurrentSupplier.contactPhone;
      this.editModel.address = this.selectCurrentSupplier.address;
      this.editModel.remarks = this.selectCurrentSupplier.remarks;
      this.isShowEditDialog = true;
    },
    showAddSupplierContainer() {
      this.isShowAddDialog = true;
    },
    resetAddForm() {
      this.addModel = this.initAddModel();
      this.$refs["add-form"] && this.$refs["add-form"].resetFields();
    },
    resetEditForm() {
      this.editModel = this.initEditModel();
      this.$refs["edit-form"] && this.$refs["edit-form"].resetFields();
    },
    submitAddForm() {
      this.$refs["add-form"].validate(valid => {
        if (valid) {
          var model = {
            warehouseId: this.$store.state.curWarehouse.id,
            name: this.addModel.name,
            contactPerson: this.addModel.contactPerson,
            contactPhone: this.addModel.contactPhone,
            address: this.addModel.address,
            remarks: this.addModel.remarks
          };
          addSupplierApi(model).then(() => {
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
          var model = {
            warehouseId: this.$store.state.curWarehouse.id,
            id: this.editModel.id,
            name: this.editModel.name,
            contactPerson: this.editModel.contactPerson,
            contactPhone: this.editModel.contactPhone,
            address: this.editModel.address,
            remarks: this.editModel.remarks
          };
          updateSupplierApi(model).then(() => {
            Message.success("保存成功");
            this.isShowEditDialog = false;
            this.getInitData();
          });
        }
      });
    },
    initAddModel() {
      return {
        name: "",
        contactPerson: "",
        contactPhone: "",
        address: "",
        remarks: ""
      };
    },
    initEditModel() {
      return {
        id: "",
        name: "",
        contactPerson: "",
        contactPhone: "",
        address: "",
        remarks: ""
      };
    }
  }
};
</script>

<style lang="less">
.consumables-grid-supplier-container {
  height: calc(100% - 100px);
}
</style>
