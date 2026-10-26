<template>
  <LayoutComponent :active-menu="menuName" @curWarehouseChanged="curWarehouseChanged">
    <el-breadcrumb separator-class="el-icon-arrow-right">
      <el-breadcrumb-item v-for="(item,index) in breadcrumbs" :key="index">{{item}}</el-breadcrumb-item>
    </el-breadcrumb>
    <hr />
    <el-container class="custom-el-container">
      <el-main class="custom-el-main">
        <el-scrollbar style="height: 100%">
          <div class="notice-container">
            <el-alert type="warning" description="选中项表示您在消耗品管理系统中拥有的权限" show-icon></el-alert>
          </div>
          <el-timeline>
            <el-timeline-item
              v-for="(activity, index) in this.$store.state.systemPermissionsType"
              :key="index"
              :timestamp="activity.typeName"
              :icon="activity.icon"
              :type="activity.type"
              :color="activity.color"
              :size="activity.size"
              placement="top"
            >
              <el-card>
                <div
                  class="permission-list"
                  v-for="item in getPermissionList(activity.typeStr)"
                  :key="item.id"
                >
                  <el-checkbox
                    v-if="item.str.indexOf(activity.typeStr) !== -1"
                    :checked="hasPermission(item.id)"
                    border
                    disabled
                  >{{item.name}}</el-checkbox>
                </div>
              </el-card>
            </el-timeline-item>
          </el-timeline>
        </el-scrollbar>
      </el-main>
    </el-container>
  </LayoutComponent>
</template>

<script>
import { Message } from "element-ui";

import LayoutComponent from "@/components/consumables/Layout.vue";

export default {
  name: "c_permission",
  components: {
    LayoutComponent
  },
  data() {
    return {
      menuName: "/consumables/permission",
      breadcrumbs: ["系统权限"],
    };
  },
  methods: {
    getPermissionList(typeStr) {
      return this.$store.state.systemPermissions.filter(item => {
        return item.str.indexOf(typeStr) !== -1;
      });
    },
    hasPermission(permissionId) {
      return (
        this.$store.state.userInfo.permissions.find(item => {
          return item.id === permissionId;
        }) != null
      );
    },
    curWarehouseChanged() {
      Message.success("切换成功");
    }
  }
};
</script>

<style scoped lang="less">
.permission-list {
  float: left;
  padding: 0 15px 15px;
}

.notice-container {
  margin-bottom: 15px;
}
</style>