<template>
  <el-dialog title="选择消耗品" width="80%" :visible.sync="visible" :modal="false" @close="cancel">
    <el-row class="selected-consumables">
      <span>已选择：</span>
      <span v-if="selectedConsumables.length">
        <el-tag
          :key="consumable.id"
          v-for="consumable in selectedConsumables"
          closable
          :disable-transitions="false"
          @close="cancelSelectConsumable(consumable)"
        >{{consumable.name}}</el-tag>
      </span>
      <el-tag v-else type="info">请选择</el-tag>
    </el-row>
    <el-row class="operation-container">
      <el-col class="search-container" :span="24">
        <el-input
          placeholder="请输入搜索内容"
          v-model="searchStr"
          clearable
          style="width:300px;margin-right: 5px;"
        >
          <el-select
            style="width: 90px;"
            v-model="searchType"
            slot="prepend"
            placeholder="请选择"
            @change="searchTypeChanged"
          >
            <el-option label="Code" value="1"></el-option>
            <el-option label="名称" value="2"></el-option>
            <el-option label="类型名" value="3"></el-option>
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
    </el-row>
    <el-container class="consumables-grid-consumables-container">
      <el-main>
        <el-table
          ref="consumableTable"
          :data="consumables"
          border
          stripe
          height="300px"
          highlight-current-row
          :header-cell-style="setTableHeaderStyle"
          @current-change="handleCurrentChange"
          @select="handleConsumableSelected"
          @select-all="handleConsumableSelectedAll"
        >
          <el-table-column type="selection" width="55"></el-table-column>
          <el-table-column prop="code" label="Code" width="120" align="center"></el-table-column>
          <el-table-column prop="name" label="名称" width="120" align="center"></el-table-column>
          <el-table-column prop="typeName" label="类型" width="120" align="center"></el-table-column>
          <el-table-column prop="count" label="数量" width="70" align="right"></el-table-column>
          <el-table-column prop="createTime" label="创建日期" width="135" align="center"></el-table-column>
          <el-table-column prop="summary" label="简介" min-width="300"></el-table-column>
        </el-table>
        <div class="page-container" style="margin-top: 0px;">
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
      </el-main>
    </el-container>
    <span slot="footer">
      <el-button @click="cancel">取 消</el-button>
      <el-button type="primary" @click="submit">确 定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { Message } from "element-ui";
import { headerRowStyle } from "../../../utils/layout";
import { HAS_PERMISSION } from "../../../utils/permission";
import { formatTimeToStr } from "../../../utils/date";
import { getConsumablesApi } from "@/api/consumables/statistics";

export default {
  name: "c_consumables_component",
  inject: ["reload"],
  props: {
    visible: Boolean
  },
  mounted() {
    this.getInitData();
  },
  data() {
    return {
      menuName: "/consumables/consumables",
      breadcrumbs: ["消耗品管理"],

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
      total: 0,
      page: {
        pageNum: 1,
        pageSize: 30,
        searchCondition: null
      },
      batchDownloadConsumableBarcode: [],
      consumables: [],
      searchStr: "",
      searchType: "1",
      selectedConsumables: []
    };
  },
  computed: {
    selectedConsumableMap() {
      const { selectedConsumables = [] } = this;
      return selectedConsumables.reduce((r, d) => {
        r[d.id] = d;
        return r;
      }, Object.create(null));
    }
  },
  watch: {
    consumables() {
      this.toggleConsumableSelection();
    }
  },
  methods: {
    cancelSelectConsumable(consumable) {
      this.handleConsumableSelected(null, consumable);

      if (this.$refs.consumableTable) {
        this.consumables.forEach(d => {
          if (d.id === consumable.id) {
            this.$refs.consumableTable.toggleRowSelection(d, false);
          }
        });
      }
    },
    handleConsumableSelected(selection, row) {
      for (let i = 0, len = this.selectedConsumables.length; i < len; i++) {
        const consumable = this.selectedConsumables[i];
        if (consumable.id === row.id) {
          this.selectedConsumables.splice(i, 1);
          return;
        }
      }
      this.selectedConsumables.push(row);
    },
    handleConsumableSelectedAll(selection) {
      this.selectedConsumables = selection.slice();
    },
    toggleConsumableSelection() {
      if (this.$refs.consumableTable) {
        // 设置选择项状态
        this.$nextTick(() => {
          this.consumables.forEach(consumable => {
            if (this.selectedConsumableMap[consumable.id]) {
              consumable.applicationCount = this.selectedConsumableMap[
                consumable.id
              ].applicationCount;
              this.$refs.consumableTable.toggleRowSelection(consumable, true);
            }
          });
        });
      }
    },
    submit() {
      this.$emit("submit", this.selectedConsumables);
    },
    cancel() {
      this.$emit("cancel");
    },
    getInitData() {
      if (!this.page.searchCondition) {
        this.page.searchCondition = JSON.stringify({
          warehouseId: this.$store.state.curWarehouse.id
        });
      }
      var that = this;
      getConsumablesApi(this.page).then(res => {
        that.total = res.data.total;
        that.consumables = res.data.list;
      });
    },
    hasPermission(curPermissionStr) {
      return HAS_PERMISSION(
        this.$store.state.userInfo.permissions,
        curPermissionStr
      );
    },
    curWarehouseChanged() {
      this.reload();
      Message.success("切换成功");
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
      this.selectCurrentConsumable = val;
    }
  }
};
</script>

<style lang="less" scoped>
.el-tag + .el-tag {
  margin-left: 10px;
}
.selected-consumables {
  padding: 0 20px 10px 20px;
}

.consumables-grid-consumables-container {
  height: calc(100% - 135px);

  .consumable-count {
    color: #00a8cc;
    cursor: pointer;

    &:hover {
      color: #909399;
      text-decoration: underline;
    }
  }
}

.consumables-consumables-detail-container,
.consumables-edit-consumables-container,
.consumables-add-consumables-container {
  height: 100%;
  overflow-x: hidden;
  overflow-y: auto;

  #consumable-detail-form {
    width: 800px;
  }

  #edit-consumable-form-step-first,
  #add-consumable-form-step-first {
    width: 500px;
  }

  #edit-consumable-form-step-second,
  #add-consumable-form-step-second {
    width: 700px;
  }
}
</style>
