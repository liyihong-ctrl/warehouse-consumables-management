<template>
  <div>
    <el-breadcrumb separator-class="el-icon-arrow-right">
      <el-breadcrumb-item v-for="d in breadcrumbs" :key="d.key" :to="{ path: d.path }">{{d.label}}</el-breadcrumb-item>
    </el-breadcrumb>
    <el-container class="custom-el-container">
      <el-main class="custom-el-main">
        <div class="consumables-application-detail-container">
          <el-scrollbar style="height: 100%">
            <el-form
              id="application-detail-form"
              ref="application-detail-form"
              label-width="160px"
              status-icon
              label-suffix="："
              :model="applicationInfo"
              :rules="applicationInfoRules"
            >
              <el-form-item label="申请名" prop="name">
                <span v-if="readonly">{{applicationInfo.name}}</span>
                <el-input v-else placeholder="请输入申请名称" v-model="applicationInfo.name"></el-input>
              </el-form-item>
              <el-form-item label="备注">
                <span v-if="readonly">{{applicationInfo.remarks}}</span>
                <el-input
                  v-else
                  type="textarea"
                  :autosize="{ minRows: 4, maxRows: 6}"
                  placeholder="请输入描述"
                  v-model="applicationInfo.remarks"
                ></el-input>
              </el-form-item>
              <el-form-item label="仓库" prop="warehouseId">
                <span v-if="readonly">{{ warehouse ? warehouse.name : ""}}</span>
                <el-select
                  v-else
                  placeholder="请选择仓库"
                  filterable
                  v-model="applicationInfo.warehouseId"
                  @change="handleWarehouseChanged"
                >
                  <el-option
                    v-for="item in warehouses"
                    :key="item.id"
                    :label="item.name"
                    :value="item.id"
                  ></el-option>
                </el-select>
              </el-form-item>
              <el-form-item label="消耗品">
                <div v-if="readonly" class="consumable-wrap">
                  <el-table
                    :data="applicationInfo.consumables"
                    border
                    stripe
                    height="100%"
                    highlight-current-row
                  >
                    <el-table-column prop="code" label="Code" align="center"></el-table-column>
                    <el-table-column prop="name" label="名称" width="200" align="center"></el-table-column>
                    <el-table-column prop="typeName" label="类型" align="center"></el-table-column>
                    <el-table-column prop="applicationCount" label="申请数" width="120" align="right"></el-table-column>
                  </el-table>
                </div>
                <div v-else>
                  <div class="search-container">
                    <el-input
                      placeholder="请输入搜索内容"
                      v-model="consumableSearchParams.searchStr"
                      clearable
                      style="width:300px;margin-right: 5px;"
                    >
                      <el-select
                        style="width: 90px;"
                        v-model="consumableSearchParams.searchType"
                        slot="prepend"
                        placeholder="请选择"
                        @change="searchTypeChanged"
                      >
                        <el-option label="Code" value="1"></el-option>
                        <el-option label="名称" value="2"></el-option>
                      </el-select>
                    </el-input>
                    <el-button
                      class="search-btn"
                      type="primary"
                      icon="el-icon-search"
                      @click="fetchConsumables"
                    ></el-button>
                    <div
                      v-if="consumablesErrorMessage"
                      class="el-form-item__error consumables__error"
                    >{{consumablesErrorMessage}}</div>
                  </div>
                  <div style="height: 390px;">
                    <el-table
                      ref="consumableTable"
                      :data="consumables"
                      border
                      stripe
                      height="100%"
                      highlight-current-row
                      :header-cell-style="setTableHeaderStyle"
                      @select-all="handleConsumableSelectedAll"
                      @select="handleConsumableSelected"
                      @selection-change="handleConsumablesChanged"
                    >
                      <el-table-column type="selection" width="55" align="center"></el-table-column>
                      <el-table-column prop="code" label="Code" align="center"></el-table-column>
                      <el-table-column prop="name" label="名称" align="center"></el-table-column>
                      <el-table-column prop="typeName" label="类型" align="center"></el-table-column>
                      <el-table-column prop="count" label="库存" width="65" align="right"></el-table-column>
                      <el-table-column label="申请数" width="220" align="center">
                        <template slot-scope="scope">
                          <el-input-number
                            :min="0"
                            :max="scope.row.count"
                            v-model="scope.row.applicationCount"
                            :disabled="!selectedConsumableMap[scope.row.id]"
                            @change="handleConsumableNumberChanged(scope.row)"
                          ></el-input-number>
                        </template>
                      </el-table-column>
                    </el-table>
                  </div>
                  <div class="page-container" style="margin-top: 15px;">
                    <el-pagination
                      background
                      layout="total, sizes, prev, pager, next, jumper"
                      :page-sizes="[1, 5, 10, 30, 50, 100]"
                      :page-size="consumableSearchParams.pageSize"
                      :total="consumablesTotal"
                      :current-page="consumableSearchParams.pageNum"
                      @size-change="handleConsumablePageSizeChanged"
                      @current-change="handleCurrentConsumablePageChanged"
                    ></el-pagination>
                  </div>
                </div>
              </el-form-item>
              <el-form-item v-if="action==='detail'">
                <el-button @click="toApplicationList">返回</el-button>
                <el-button v-if="editAllowable" type="primary" @click="toEditApplication">修改</el-button>
              </el-form-item>
              <el-form-item v-else-if="step===1">
                <el-button @click="toApplicationList">返回</el-button>
                <el-button type="primary" @click="nextStep">下一步</el-button>
              </el-form-item>
              <el-form-item v-else>
                <el-button @click="backStep">取消</el-button>
                <el-button type="primary" @click="submit">提交</el-button>
              </el-form-item>
            </el-form>
          </el-scrollbar>
        </div>
      </el-main>
    </el-container>
  </div>
</template>

<script>
import { Message } from "element-ui";
import { headerRowStyle } from "../../utils/layout";
import {
  getWarehousesApi,
  getConsumablesApi,
  getApplicationApi,
  addApplicationApi,
  editApplicationApi
} from "@/api/application/application";
import { NO_PERIEW } from "@/constants";

const APPLICATION_LIST_NAV = {
  key: "list",
  label: "申请管理",
  path: "/application/application"
};

export default {
  mounted() {
    this.handleRouterChanged(this.$route.params);
  },
  data() {
    return {
      breadcrumbs: [APPLICATION_LIST_NAV],
      action: "detail",
      step: 1,
      applicationInfo: {
        name: "",
        remarks: "",
        warehouseId: "",
        consumables: []
      },
      warehouses: [],
      consumableSearchParams: {
        pageNum: 1,
        pageSize: 30,
        searchCondition: "",
        searchType: "1",
        searchStr: ""
      },
      consumablesTotal: 0,
      consumables: [],
      applicationInfoRules: {
        name: [
          { required: true, message: "请输入申请名" },
          { min: 1, max: 20, message: "申请名长度范围为1~20", trigger: "blur" },
          {
            min: 1,
            max: 20,
            message: "申请名长度范围为1~20",
            trigger: "change"
          }
        ],
        remarks: [
          {
            min: 0,
            max: 50,
            message: "申请备注长度范围为0~50",
            trigger: "blur"
          },
          {
            min: 0,
            max: 50,
            message: "申请备注长度范围为0~50",
            trigger: "change"
          }
        ],
        warehouseId: [{ required: true, message: "请选择仓库" }]
      },
      consumablesErrorMessage: ""
    };
  },
  computed: {
    warehouse() {
      const { applicationInfo: { warehouseId } = {}, warehouses } = this;
      if (warehouseId && warehouses.length) {
        return warehouses.find(d => d.id === warehouseId);
      }
      return undefined;
    },
    selectedConsumableMap() {
      const { applicationInfo: { consumables = [] } = {} } = this;
      return consumables.reduce((r, d) => {
        r[d.id] = d;
        return r;
      }, Object.create(null));
    },
    readonly() {
      return this.action === "detail" || this.step === 2;
    },
    editAllowable() {
      return (
        this.applicationInfo &&
        this.applicationInfo.approvalStatus === NO_PERIEW
      );
    }
  },
  watch: {
    $route(to) {
      this.handleRouterChanged(to.params);
    },
    consumables() {
      this.toggleConsumableSelection();
    }
  },
  methods: {
    setTableHeaderStyle({ row, column, rowIndex, columnIndex }) {
      return headerRowStyle({ row, column, rowIndex, columnIndex });
    },
    searchTypeChanged() {
      this.consumableSearchParams.searchStr = "";
    },
    fetchDetail(applicationId) {
      getApplicationApi({ applicationId })
        .then(
          ({ data: { id, name, remarks, warehouseId, details = [] } = {} }) => {
            this.applicationInfo = {
              id,
              name,
              remarks,
              warehouseId,
              consumables: details.map(d => ({
                id: d.consumableId,
                code: d.code,
                name: d.consumableName,
                typeName: d.typeName,
                applicationCount: d.applicateCount
              }))
            };

            this.fetchConsumables();
          }
        )
        .catch(() => Message.error("申请信息获取失败"));
    },
    fetchWareHouses() {
      getWarehousesApi()
        .then(({ data = [] }) => {
          if (data.length) {
            this.warehouses = data;
          } else {
            Message.error("无法申请：暂无仓库数据");
          }
        })
        .catch(() => Message.error("仓库信息获取失败"));
    },
    fetchConsumables() {
      const { applicationInfo: { warehouseId } = {}, action } = this;
      if (!warehouseId || ["edit", "create"].indexOf(action) === -1) {
        return;
      }

      this.consumableSearchParams.searchCondition = JSON.stringify({
        warehouseId,
        searchType: this.consumableSearchParams.searchType,
        searchStr: this.consumableSearchParams.searchStr
      });
      getConsumablesApi(this.consumableSearchParams).then(
        ({ data: { total, list = [] } = {} }) => {
          this.consumablesTotal = total;
          this.consumables = list.map(d => ({
            id: d.id,
            code: d.code,
            name: d.name,
            typeName: d.typeName,
            count: d.count,
            applicationCount: 0
          }));
          this.toggleConsumableSelection();
        }
      );
    },
    handleWarehouseChanged() {
      this.consumables = [];
      this.fetchConsumables();
    },
    toEditApplication() {
      this.$router.push(`/application/application/edit/${this.id}`);
      this.action = "edit";
    },
    toApplicationDetail() {
      this.$router.push(`/application/application/detail/${this.id}`);
      this.action = "detail";
    },
    toApplicationList() {
      this.$router.push("/application/application");
    },
    nextStep() {
      this.handleConsumablesChanged();
      this.$refs["application-detail-form"].validate(ok => {
        if (ok && !this.consumablesErrorMessage) {
          // 清除申请数为0的数据
          this.applicationInfo.consumables = this.applicationInfo.consumables.filter(
            item => {
              return item.applicationCount !== 0;
            }
          );
          this.step = 2;
        }
      });
    },
    handleRouterChanged(params) {
      this.id = params.id;
      this.action = params.action && params.action.toLowerCase();
      this.setp = 1;
      this.breadcrumbs = [APPLICATION_LIST_NAV];
      if (this.action === "detail") {
        this.fetchDetail(this.id);
        this.breadcrumbs.push({
          key: this.action,
          label: "详情"
        });
      } else if (this.action === "edit") {
        this.fetchDetail(this.id);
        this.fetchConsumables();
        this.breadcrumbs.push({
          key: this.action,
          label: "修改"
        });
      } else if (this.action === "create") {
        this.breadcrumbs.push({
          key: this.action,
          label: "申请"
        });
      }
      this.fetchWareHouses();
    },
    backStep() {
      this.step = 1;
      this.$nextTick(() => this.toggleConsumableSelection());
    },
    submit() {
      const { applicationInfo = {} } = this;
      const params = {
        ...applicationInfo,
        consumables: {
          consumables: JSON.stringify(applicationInfo.consumables)
        }
      };
      let handleApi;
      let successMessage;
      if (this.action === "create") {
        handleApi = addApplicationApi;
        successMessage = "申请成功";
      } else if (this.action === "edit") {
        handleApi = editApplicationApi;
        successMessage = "操作成功";
      }
      if (handleApi) {
        handleApi(params).then(() => {
          Message.success(successMessage);
          this.$router.push("/application/application");
        });
      }
    },
    handleConsumableNumberChanged(consumable) {
      const { applicationInfo: { consumables = [] } = {} } = this;
      for (let i = 0, len = consumables.length; i < len; i++) {
        const d = consumables[i];
        if (d.id === consumable.id) {
          d.applicationCount = consumable.applicationCount;
        }
      }

      this.handleConsumablesChanged();
    },
    handleConsumablesChanged() {
      const { applicationInfo: { consumables = [] } = {} } = this;
      if (!consumables.length) {
        this.consumablesErrorMessage = "请选择消耗品";
      } else if (!consumables.some(d => d.applicationCount)) {
        this.consumablesErrorMessage = "请填写数量";
      } else {
        this.consumablesErrorMessage = "";
      }
    },
    handleConsumableSelected(selection, row) {
      const { applicationInfo: { consumables = [] } = {} } = this;
      for (let i = 0, len = consumables.length; i < len; i++) {
        const consumable = consumables[i];
        if (consumable.id === row.id) {
          row.applicationCount = 0;
          consumables.splice(i, 1);
          return;
        }
      }
      consumables.push(row);
    },
    handleConsumableSelectedAll(selection) {
      this.applicationInfo.consumables = selection.slice();
    },
    handleConsumablePageSizeChanged(size) {
      this.consumableSearchParams.pageSize = size;
      this.fetchConsumables();
    },
    handleCurrentConsumablePageChanged(num) {
      this.consumableSearchParams.pageNum = num;
      this.fetchConsumables();
    },
    toggleConsumableSelection() {
      if (
        (this.action === "edit" || this.action === "create") &&
        this.$refs.consumableTable
      ) {
        this.$nextTick(() => {
          const { consumables = [] } = this;
          consumables.forEach(consumable => {
            const d = this.selectedConsumableMap[consumable.id];
            if (d) {
              consumable.applicationCount = d.applicationCount;
              this.$refs.consumableTable.toggleRowSelection(consumable, true);
            }
          });
        });
      }
    }
  }
};
</script>

<style lang="less" scoped>
.consumables-application-detail-container {
  height: 100%;
  overflow-x: hidden;
  overflow-y: auto;

  #application-detail-form {
    width: 900px;
  }
  .consumable-wrap {
    height: 300px;
  }

  .search-container {
    display: flex;
    flex-flow: row nowrap;
    align-items: center;

    margin-bottom: 15px;
    width: 400px;

    .search-btn {
      margin-left: 5px;
    }
  }

  .consumables__error {
    width: 120px;
    margin-left: 10px;
  }
}
</style>