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
            <el-option label="用户名" value="1"></el-option>
            <el-option label="消耗品名" value="2"></el-option>
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
        <el-button
          type="info"
          plain
          @click="exportOpertionHistorys"
          v-if="hasPermission('c_userOperate_export')"
        >导出</el-button>
      </el-col>
    </el-row>
    <el-container class="consumables-grid-operation-history-container" v-show="isShowMainContainer">
      <el-main>
        <el-table
          ref="operation-history-table"
          :data="operationHistorys"
          border
          stripe
          height="100%"
          highlight-current-row
          :header-cell-style="setTableHeaderStyle"
          @current-change="handleCurrentChange"
        >
          <el-table-column type="expand">
            <template slot-scope="props">
              <el-row>
                <el-col :span="24">
                  <el-table style="width: 100%" :data="props.row.details" border stripe>
                    <el-table-column prop="consumableCode" label="Code" width="150" align="center"></el-table-column>
                    <el-table-column prop="consumableName" label="消耗品名称" width="160" align="center"></el-table-column>
                    <el-table-column prop="operateName" label="操作状态" width="120" align="center"></el-table-column>
                    <el-table-column label="操作数" width="80" align="right">
                      <template slot-scope="scope">
                        <span>{{ scope.row.operationCount}}</span>
                      </template>
                    </el-table-column>
                    <el-table-column prop="remarks" label="备注" align="center" min-width="340"></el-table-column>
                  </el-table>
                </el-col>
              </el-row>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="操作日期" width="150" align="center"></el-table-column>
          <el-table-column label="消耗品操作数" width="160" align="right">
            <template slot-scope="scope">
              <span>{{ scope.row.details.length}}</span>
            </template>
          </el-table-column>
          <el-table-column prop="userName" label="用户名" width="120" align="center"></el-table-column>
          <el-table-column prop="userAccount" label="用户账号" align="left"></el-table-column>
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
  </LayoutComponent>
</template>

<script>
import { Message } from "element-ui";
import LayoutComponent from "@/components/consumables/Layout.vue";
import { headerRowStyle } from "../../utils/layout";
import base from "../../utils/base";
import { HAS_PERMISSION } from "../../utils/permission";
import { formatTimeToStr } from "../../utils/date";
import { getOperationHistorysApi } from "../../api/consumables/operate";

export default {
  name: "c_operationHistory",
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
      menuName: "/consumables/operate",
      breadcrumbs: ["操作履历"],
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
      operationHistorys: [],
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
      getOperationHistorysApi(that.page).then(res => {
        that.total = res.data.total;
        that.operationHistorys = res.data.list;
        if (that.operationHistorys && that.operationHistorys.length > 0) {
          that.$refs["operation-history-table"].setCurrentRow(
            that.operationHistorys[0]
          );
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

      // 保存检索条件
      this.oldSearchCondition = searchCondition;

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
    exportOpertionHistorys() {
      var url =
        base.url +
        "/cOperationHistory/exportConsumables?warehouseId=" +
        this.$store.state.curWarehouse.id +
        "&searchType=" +
        this.oldSearchCondition.searchType +
        "&searchStr=" +
        this.oldSearchCondition.searchStr +
        "&startTime=" +
        this.oldSearchCondition.startTime +
        "&endTime=" +
        this.oldSearchCondition.endTime;
      window.open(url);
    }
  }
};
</script>

<style lang="less">
.consumables-grid-operation-history-container {
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