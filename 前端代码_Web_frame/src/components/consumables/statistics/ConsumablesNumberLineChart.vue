<template>
  <div>
    <consumable-search-modal
      :visible="consumableSearchModalVisible"
      @submit="updateSelectedConsumables"
      @cancel="closeConsumableSearchModal"
    ></consumable-search-modal>
    <el-form
      id="statistics-form"
      ref="statistics-form"
      :model="queryModel"
      :rules="queryFormRules"
      label-suffix="："
    >
      <el-form-item label="统计维度" prop="staticType">
        <el-select v-model="staticType" placeholder="请选择">
          <el-option label="出库数量" :value="CONSUMABLE_EXPORT"></el-option>
          <el-option label="在工减少" :value="CONSUMABLE_REDUCED_WORK"></el-option>
          <el-option label="入库数量" :value="CONSUMABLE_IMPORT"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="消耗品" required style="width: 900px;">
        <el-row>
          <el-tag
            v-for="consumable in selectedConsumables"
            :key="consumable.id"
            closable
            :disable-transitions="false"
            @close="cancelSelectConsumable(consumable)"
          >{{consumable.name}}</el-tag>
          <el-button class="button-new-tag" size="small" @click="showConsumableSearchModal">+ 添加</el-button>
          <div v-if="isConsumableError" class="el-form-item__error">请添加消耗品</div>
        </el-row>
      </el-form-item>
      <el-form-item label="日期范围" prop="timeRange">
        <el-date-picker
          style="width: 300px"
          type="daterange"
          unlink-panels
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          :picker-options="pickerOptions"
          value-format="yyyy-MM-dd"
          format="yyyy-MM-dd"
          v-model="timeRange"
        ></el-date-picker>
        <el-button class="search-btn" type="primary" icon="el-icon-search" @click="query"></el-button>
        <el-button type="info" plain style="float: right;" @click="exportStatisticsData" v-if="hasPermission('c_statistics_export')">导出</el-button>
      </el-form-item>
    </el-form>
    <el-row>
      <div id="main" style="width: 100%;height:400px;"></div>
    </el-row>
  </div>
</template>

<script>
import { Message } from "element-ui";
import ConsumableSearchModal from "../statistics/ConsumableSearchModal";
import echarts from "echarts";
import moment from "moment";
import {
  CONSUMABLE_EXPORT,
  CONSUMABLE_REDUCED_WORK,
  CONSUMABLE_IMPORT,
  COMPREHENSIVE_QUANTITY
} from "@/constants";
import { HAS_PERMISSION } from "../../../utils/permission";
import base from "../../../utils/base";
import { formatTimeToStr } from "../../../utils/date";
import { getConsumableStatisticsApi } from "../../../api/consumables/statistics";

export default {
  name: "c_statistics_line_chart",
  components: {
    ConsumableSearchModal
  },
  props: {
    chartName: {
      type: String,
      default: ""
    }
  },
  mounted() {
    this.chart = echarts.init(document.getElementById("main"));
    this.chart.setOption(this.option);
  },
  computed: {
    isShowMainContainer() {
      return true;
    },
    queryModel() {
      return {
        selectedConsumables: this.selectedConsumables,
        timeRange: this.timeRange,
        staticType: this.staticType
      };
    }
  },
  data() {
    const now = moment();
    const endTime = now.format("YYYY-MM-DD");
    const startTime = now.subtract(7, "days").format("YYYY-MM-DD");
    return {
      CONSUMABLE_EXPORT,
      CONSUMABLE_REDUCED_WORK,
      CONSUMABLE_IMPORT,
      COMPREHENSIVE_QUANTITY,
      menuName: "/consumables/statistics",
      breadcrumbs: ["统计信息"],
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
      selectedConsumables: [],
      consumableSearchModalVisible: false,
      timeRange: [startTime, endTime],
      staticType: CONSUMABLE_EXPORT,
      isConsumableError: false,
      queryFormRules: {
        timeRange: [{ required: true, message: "请选择日期范围" }],
        staticType: [{ required: true, message: "请选择统计维度" }]
      },
      option: {
        title: {
          text: this.chartName
        },
        tooltip: {
          trigger: "axis"
        },
        legend: {
          data: []
        },
        grid: {
          left: "3%",
          right: "4%",
          bottom: "3%",
          containLabel: true
        },
        toolbox: {
          feature: {
            saveAsImage: {}
          }
        },
        xAxis: {
          type: "category",
          boundaryGap: false,
          data: []
        },
        yAxis: {
          type: "value"
        },
        series: []
      },
      oldSearchCondition: {
        warehouseId: this.$store.state.curWarehouse.id,
        consumableIds: [],
        staticType: this.staticType,
        startTime: "",
        endTime: ""
      }
    };
  },
  methods: {
    hasPermission(curPermissionStr) {
      return HAS_PERMISSION(
        this.$store.state.userInfo.permissions,
        curPermissionStr
      );
    },
    validateComsubables() {
      this.isConsumableError =
        !this.selectedConsumables || !this.selectedConsumables.length;
    },
    query() {
      if (this.$refs["statistics-form"]) {
        this.validateComsubables();
        if (this.isConsumableError) {
          return;
        }
        this.$refs["statistics-form"].validate(ok => {
          if (ok) {
            var searchCondition = {
              warehouseId: this.$store.state.curWarehouse.id,
              consumableIds: this.selectedConsumables.map(d => d.id),
              staticType: this.staticType,
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

            this.oldSearchCondition = searchCondition;

            getConsumableStatisticsApi(searchCondition).then(
              ({ data = [] }) => {
                const legendData = [];
                let dateRange = [];
                this.option.series = data.map(
                  ({ consumableName, data: originDate = [] }, index) => {
                    if (!index) {
                      dateRange = originDate.map(d => d.date);
                    }
                    legendData.push(consumableName);
                    return {
                      name: consumableName,
                      type: "line",
                      stack: "总量",
                      data: originDate.map(d => d.value)
                    };
                  }
                );
                this.option.legend.data = legendData;
                this.option.xAxis.data = dateRange;
                this.chart.setOption(this.option);
              }
            );
          }
        });
      }
    },
    showConsumableSearchModal() {
      this.consumableSearchModalVisible = true;
    },
    closeConsumableSearchModal() {
      this.consumableSearchModalVisible = false;
    },
    updateSelectedConsumables(list) {
      this.consumableSearchModalVisible = false;
      this.selectedConsumables = list;
      this.validateComsubables();
    },
    cancelSelectConsumable(d) {
      for (let i = 0, len = this.selectedConsumables.length; i < len; i++) {
        const consumable = this.selectedConsumables[i];
        if (consumable.id === d.id) {
          this.selectedConsumables.splice(i, 1);

          this.validateComsubables();
          return;
        }
      }
    },
    exportStatisticsData() {
      if (
        !this.oldSearchCondition.consumableIds ||
        this.oldSearchCondition.consumableIds.length === 0
      ) {
        Message.error("导出错误：请选择消耗品");
        return;
      }
      if (
        !this.oldSearchCondition.startTime ||
        !this.oldSearchCondition.endTime
      ) {
        Message.error("导出错误：请选择日期范围");
        return;
      }
      var url =
        base.url +
        "/cStatistics/exportStatistics?warehouseId=" +
        this.$store.state.curWarehouse.id +
        "&staticType=" +
        this.staticType +
        "&consumableIds=" +
        JSON.stringify(this.oldSearchCondition.consumableIds) +
        "&startTime=" +
        this.oldSearchCondition.startTime +
        "&endTime=" +
        this.oldSearchCondition.endTime;
      window.open(url);
    }
  }
};
</script>

<style lang="less" scoped>
.el-tag {
  margin-right: 10px;
}
.row {
  padding-bottom: 10px;
}
.search-btn {
  margin-left: 10px;
}
</style>