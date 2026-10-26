<template>
  <div class="application-container">
    <el-breadcrumb separator-class="el-icon-arrow-right">
      <el-breadcrumb-item v-for="(item,index) in breadcrumbs" :key="index">{{item}}</el-breadcrumb-item>
    </el-breadcrumb>
    <hr />
    <el-row class="operation-container">
      <el-col class="search-container" :span="8">
        <el-input placeholder="请输入搜索内容" v-model="searchStr" clearable style="max-width: 350px;">
          <template slot="prepend">申请名</template>
        </el-input>
        <el-button class="search-btn" type="primary" icon="el-icon-search" @click="doSearch"></el-button>
      </el-col>
      <el-col class="btn-group" :span="16">
        <el-button type="primary" plain @click="toCreateApplication">申请</el-button>
      </el-col>
    </el-row>
    <el-container class="application-grid-application-container">
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
          <el-table-column label="序号" width="60" type="index" align="center"></el-table-column>
          <el-table-column prop="name" label="名称" width="200" align="center"></el-table-column>
          <el-table-column prop="createTime" label="申请日期" width="135" sortable align="center"></el-table-column>
          <el-table-column label="申请数" width="80" align="right">
            <template slot-scope="scope">
              <span>{{scope.row.details.length}}</span>
            </template>
          </el-table-column>
          <el-table-column prop="remarks" label="备注"></el-table-column>
          <el-table-column label="审批状态" width="120" :formatter="statusFormatter" align="center"></el-table-column>
          <el-table-column label="操作" width="100" align="center">
            <template slot-scope="scope">
              <el-button type="text" @click="toApplicationDetail(scope.row.id)">详情</el-button>
              <el-button
                type="text"
                @click="exportApplication(scope.row.id)"
                v-if="isShowExportBtn(scope.row.approvalStatus)"
              >打印</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-main>
    </el-container>
    <div class="page-container">
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
  </div>
</template>

<script>
import { Message } from "element-ui";
import { APPROVAL_STATUS, NO_PERIEW } from "@/constants";
import { headerRowStyle } from "../../utils/layout";
import base from "../../utils/base";
import {
  getApplicationsApi,
  canApplicationApi,
  canExportApi
} from "../../api/application/application";

export default {
  name: "a_application",
  mounted() {
    this.getInitData();
  },
  data: function() {
    return {
      NO_PERIEW,
      menuName: "/application/application",
      breadcrumbs: ["申请管理"],
      total: 0,
      page: {
        pageNum: 1,
        pageSize: 30,
        searchCondition: null
      },
      searchStr: "",
      applications: [],
      selectCurrentApplication: null
    };
  },
  methods: {
    statusFormatter: row => APPROVAL_STATUS[row.approvalStatus],
    getInitData() {
      if (!this.page.searchCondition) {
        this.page.searchCondition = JSON.stringify({
          warehouseId: this.$store.state.curWarehouse.id
        });
      }
      getApplicationsApi(this.page).then(res => {
        this.total = res.data.total;
        this.applications = res.data.list;
        if (this.applications && this.applications.length > 0) {
          this.$refs["application-table"].setCurrentRow(this.applications[0]);
        }
      });
    },
    setTableHeaderStyle({ row, column, rowIndex, columnIndex }) {
      return headerRowStyle({ row, column, rowIndex, columnIndex });
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
    isShowExportBtn(approvalStatus) {
      return approvalStatus == 3;
    },
    updateQuery(partialQuery = {}) {
      const query = {
        ...this.$route.query,
        ...partialQuery
      };
      this.$router.push({ query });
    },
    handlePageSizeChange(size) {
      this.page.pageSize = size;
      this.updateQuery({ pageSize: size });
      this.getInitData();
    },
    handleCurrentPageChange(currentPage) {
      this.page.pageNum = currentPage;
      this.updateQuery({ pageNum: currentPage });
      this.getInitData();
    },
    handleCurrentChange(val) {
      this.selectCurrentApplication = val;
    },
    toApplicationDetail(id) {
      if (id) {
        this.$router.push(`/application/application/detail/${id}`);
      }
    },
    toEditApplication(id) {
      if (id) {
        this.$router.push(`/application/application/edit/${id}`);
      }
    },
    toCreateApplication() {
      canApplicationApi().then(res => {
        if (res.data) {
          this.$router.push("/application/application/create");
        } else {
          Message.error("无法申请：暂无可申请仓库");
        }
      });
    },
    exportApplication(id) {
      if (!id) {
        return;
      }
      canExportApi({
        applicationId: id
      }).then(() => {
        const url = base.url + `/aApplication/export/pdf?applicationId=${id}`;
        window.open(url);
      });
    }
  }
};
</script>

<style lang="less" scoped>
.application-container {
  height: 100%;
}
.application-grid-application-container {
  height: calc(100% - 100px);
}
</style>