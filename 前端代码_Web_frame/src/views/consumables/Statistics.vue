<template>
  <LayoutComponent :active-menu="menuName" @curWarehouseChanged="curWarehouseChanged">
    <el-breadcrumb separator-class="el-icon-arrow-right">
      <el-breadcrumb-item v-for="(item,index) in breadcrumbs" :key="index">{{item}}</el-breadcrumb-item>
    </el-breadcrumb>
    <hr />
    <consumables-number-line-chart></consumables-number-line-chart>
  </LayoutComponent>
</template>

<script>
import { Message } from "element-ui";
import LayoutComponent from "@/components/consumables/Layout";
import ConsumablesNumberLineChart from "../../components/consumables/statistics/ConsumablesNumberLineChart";

export default {
  name: "c_statistics",
  components: {
    LayoutComponent,
    ConsumablesNumberLineChart
  },
  inject: ["reload"],
  computed: {
    isShowMainContainer() {
      return true;
    }
  },
  data() {
    return {
      menuName: "/consumables/statistics",
      breadcrumbs: ["统计信息"]
    };
  },
  methods: {
    curWarehouseChanged() {
      this.reload();
      Message.success("切换成功");
    }
  }
};
</script>

<style lang="less" scoped>
.el-tag + .el-tag {
  margin-right: 10px;
}
</style>