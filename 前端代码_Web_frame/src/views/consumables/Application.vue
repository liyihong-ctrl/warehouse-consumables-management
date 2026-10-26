<template>
  <LayoutComponent :active-menu="menuName" @curWarehouseChanged="curWarehouseChanged">
    <el-breadcrumb separator-class="el-icon-arrow-right">
      <el-breadcrumb-item v-for="(item,index) in breadcrumbs" :key="index">{{item}}</el-breadcrumb-item>
    </el-breadcrumb>
    <hr />
    <el-row class="operation-container" v-show="isShowMainContainer">
      <el-col class="search-container" :span="18">
        <el-input
          style="width: 300px; margin-right: 5px;"
          placeholder="请输入搜索内容"
          v-model="searchStr"
          clearable
        >
          <el-select
            style="width: 90px;"
            v-model="searchType"
            slot="prepend"
            placeholder="请选择"
            @change="searchTypeChanged"
          >
            <el-option label="申请名" value="1"></el-option>
            <el-option label="用户名" value="2"></el-option>
          </el-select>
        </el-input>
        <el-date-picker
          style="width: 300px"
          v-model="timeRange"
          type="daterange"
          align="right"
          unlink-panels
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          :picker-options="pickerOptions"
        ></el-date-picker>
        <el-button class="search-btn" type="primary" icon="el-icon-search" @click="doSearch"></el-button>
      </el-col>
      <el-col class="btn-group" :span="6">
        <el-button type="primary" plain @click="showApplicationDetailContainer">详情</el-button>
      </el-col>
    </el-row>
    <el-container class="consumables-grid-applications-container" v-show="isShowMainContainer">
      <el-main>
        <el-table
          ref="application-table"
          :data="applications"
          border
          stripe
          height="100%"
          highlight-current-row
          :header-cell-style="setTableHeaderStyle"
          @current-change="handleCurrentChange"
        >
          <el-table-column prop="name" label="申请名" width="160" align="center"></el-table-column>
          <el-table-column prop="userName" label="用户名" width="120" align="center"></el-table-column>
          <el-table-column prop="createTime" label="申请日期" width="150" align="center"></el-table-column>
          <el-table-column label="申请数" width="80" align="right">
            <template slot-scope="scope">
              <span>{{ scope.row.details.length}}</span>
            </template>
          </el-table-column>
          <el-table-column prop="approvalInstructions" label="申请说明"></el-table-column>
          <el-table-column label="审批状态" width="150" align="center">
            <template slot-scope="scope">
              <el-select
                v-model="scope.row.approvalStatus"
                placeholder="请选择"
                @change="updateApplicationStatus(scope.row)"
                :disabled="scope.row.approvalStatus === 4"
              >
                <el-option
                  v-for="status in ApprovalStatus"
                  :key="status.value"
                  :label="status.key"
                  :value="status.value"
                  :disabled="scope.row.approvalStatus >= status.value "
                ></el-option>
              </el-select>
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
    <el-container class="custom-el-container" v-if="isShowApplicationDetailContainer">
      <el-main class="custom-el-main">
        <div class="consumables-application-detail-container">
          <el-scrollbar style="height: 100%">
            <el-form
              id="application-detail-form"
              ref="application-detail-form"
              :model="applicationDetailModel"
              label-width="160px"
              status-icon
            >
              <el-form-item label="用户名：">
                <span>{{applicationDetailModel.userName}}</span>
              </el-form-item>
              <el-form-item label="申请名：">
                <span>{{applicationDetailModel.name}}</span>
              </el-form-item>
              <el-form-item label="备注：">
                <span>{{applicationDetailModel.remarks}}</span>
              </el-form-item>
              <el-form-item label="申请日期：">
                <span>{{applicationDetailModel.createTime}}</span>
              </el-form-item>
              <el-form-item label="申请状态：">
                <span>{{applicationDetailModel.approvalStatus}}</span>
              </el-form-item>
              <el-form-item label="消耗品：">
                <div style="height: 300px;">
                  <el-table
                    ref="add-consumable-table"
                    :data="applicationDetailModel.consumables"
                    border
                    stripe
                    height="100%"
                    :header-cell-style="setTableHeaderStyle"
                  >
                    <el-table-column
                      prop="code"
                      label="Code"
                      width="150"
                      align="center"
                      :show-overflow-tooltip="isShowOverflowTooltip"
                    ></el-table-column>
                    <el-table-column
                      prop="name"
                      label="名称"
                      width="200"
                      align="center"
                      :show-overflow-tooltip="isShowOverflowTooltip"
                    ></el-table-column>
                    <el-table-column
                      prop="typeName"
                      label="类型"
                      width="200"
                      align="center"
                      :show-overflow-tooltip="isShowOverflowTooltip"
                    ></el-table-column>
                    <el-table-column
                      prop="applicationCount"
                      label="申请数"
                      align="right"
                      :show-overflow-tooltip="isShowOverflowTooltip"
                    ></el-table-column>
                  </el-table>
                </div>
              </el-form-item>
              <el-form-item>
                <el-button @click="hideApplicationDetailContainer">返回</el-button>
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
import { HAS_PERMISSION } from "../../utils/permission";
import { formatTimeToStr } from "../../utils/date";
import {
  getApplicationsApi,
  viewApplicationDetailApi,
  doApprovalApi
} from "../../api/consumables/application";

export default {
  name: "c_application",
  components: {
    LayoutComponent
  },
  inject: ["reload"],
  mounted() {
    this.getInitData();
  },
  computed: {
    isShowMainContainer() {
      return !this.isShowApplicationDetailContainer;
    }
  },
  data: function() {
    return {
      menuName: "/consumables/application",
      breadcrumbs: ["申请管理"],
      isShowOverflowTooltip: true,
      ApprovalStatus: [
        { key: "未审核", value: 0 },
        { key: "审核中", value: 1 },
        { key: "未通过", value: 2 },
        { key: "审核通过", value: 3 },
        { key: "已完成", value: 4 }
      ],
      isShowApplicationDetailContainer: false,
      total: 0,
      page: {
        pageNum: 1,
        pageSize: 30,
        searchCondition: null
      },
      timeRange: "",
      pickerOptions: {
        shortcuts: [
          {
            text: "最近一周",
            onClick(picker) {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);
              picker.$emit("pick", [start, end]);
            }
          },
          {
            text: "最近一个月",
            onClick(picker) {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 30);
              picker.$emit("pick", [start, end]);
            }
          },
          {
            text: "最近三个月",
            onClick(picker) {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 90);
              picker.$emit("pick", [start, end]);
            }
          }
        ]
      },
      oldSearchCondition: {
        warehouseId: this.$store.state.curWarehouse.id,
        searchType: "",
        searchStr: "",
        startTime: "",
        endTime: ""
      },
      applications: [],
      selectCurrentApplication: null,
      searchType: "1",
      searchStr: "",
      applicationDetailModel: {
        id: "",
        name: "",
        remarks: "",
        userName: "",
        createTime: "",
        approvalStatus: 0,
        consumables: []
      }
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
      getApplicationsApi(that.page).then(res => {
        that.total = res.data.total;
        that.applications = res.data.list;
        if (that.applications && that.applications.length > 0) {
          that.$refs["application-table"].setCurrentRow(that.applications[0]);
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
      var searchCondition = {
        warehouseId: this.$store.state.curWarehouse.id,
        searchType: this.searchType,
        searchStr: this.searchStr,
        startTime: "",
        endTime: ""
      };

      if (this.timeRange) {
        searchCondition.startTime = formatTimeToStr(
          this.timeRange[0],
          "yyyy-MM-dd"
        );
        searchCondition.endTime = formatTimeToStr(
          this.timeRange[1],
          "yyyy-MM-dd"
        );
      }

      this.page.searchCondition = JSON.stringify(searchCondition);
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
      this.selectCurrentApplication = val;
    },
    approvalState(state) {
      switch (state) {
        case 0:
          return "未审核";
        case 1:
          return "审核中";
        case 2:
          return "未通过";
        case 3:
          return "审核通过";
        case 4:
          return "已完成";
      }
    },
    updateApplicationStatus(application) {
      this.$confirm(
        "此操作将永久更新名称为「" +
          application.name +
          "」的申请状态, 是否继续?",
        "修改审批状态",
        {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }
      )
        .then(() => {
          doApprovalApi({
            warehouseId: this.$store.state.curWarehouse.id,
            applicationId: application.id,
            approvalState: application.approvalStatus
          }).then(() => {
            Message.success("修改成功");
          });
        })
        .catch(() => {
          Message.info("取消更新");
        });
    },
    showApplicationDetailContainer() {
      if (!this.selectCurrentApplication) {
        return;
      }
      if (this.selectCurrentApplication.approvalStatus === 0) {
        viewApplicationDetailApi({
          warehouseId: this.$store.state.curWarehouse.id,
          applicationId: this.selectCurrentApplication.id
        }).then(() => {
          this.applications.forEach(item => {
            if (item.id === this.selectCurrentApplication.id) {
              item.approvalStatus = 1;
              return;
            }
          });
          this.applicationDetailModel.name = this.selectCurrentApplication.name;
          this.applicationDetailModel.userName = this.selectCurrentApplication.userName;
          this.applicationDetailModel.createTime = this.selectCurrentApplication.createTime;
          this.applicationDetailModel.remarks = this.selectCurrentApplication.remarks;
          this.applicationDetailModel.approvalStatus = this.approvalState(
            this.selectCurrentApplication.approvalStatus
          );
          this.selectCurrentApplication.details.forEach(item => {
            this.applicationDetailModel.consumables.push({
              id: item.consumableId,
              code: item.code,
              name: item.consumableName,
              applicationCount: item.applicateCount
            });
          });
          this.breadcrumbs.push("详情");
          this.isShowApplicationDetailContainer = true;
        });
      } else {
        this.applicationDetailModel.name = this.selectCurrentApplication.name;
        this.applicationDetailModel.userName = this.selectCurrentApplication.userName;
        this.applicationDetailModel.createTime = this.selectCurrentApplication.createTime;
        this.applicationDetailModel.remarks = this.selectCurrentApplication.remarks;
        this.applicationDetailModel.approvalStatus = this.approvalState(
          this.selectCurrentApplication.approvalStatus
        );
        this.selectCurrentApplication.details.forEach(item => {
          this.applicationDetailModel.consumables.push({
            id: item.consumableId,
            code: item.code,
            name: item.consumableName,
            typeName: item.typeName,
            applicationCount: item.applicateCount
          });
        });
        this.breadcrumbs.push("详情");
        this.isShowApplicationDetailContainer = true;
      }
    },
    hideApplicationDetailContainer() {
      this.applicationDetailModel = {
        id: "",
        name: "",
        remarks: "",
        userName: "",
        createTime: "",
        approvalStatus: 0,
        consumables: []
      };
      this.breadcrumbs.pop();
      this.isShowApplicationDetailContainer = false;
    }
  }
};
</script>

<style lang="less">
.consumables-grid-applications-container {
  height: calc(100% - 100px);
}

.consumables-application-detail-container {
  height: 100%;
  overflow-x: hidden;
  overflow-y: auto;

  #application-detail-form {
    width: 800px;
  }
}
</style>