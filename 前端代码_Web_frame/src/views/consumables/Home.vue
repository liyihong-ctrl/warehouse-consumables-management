<template>
  <LayoutComponent :active-menu="menuName" @curWarehouseChanged="curWarehouseChanged">
    <el-breadcrumb separator-class="el-icon-arrow-right">
      <el-breadcrumb-item v-for="(item,index) in breadcrumbs" :key="index">{{item}}</el-breadcrumb-item>
    </el-breadcrumb>
    <hr />
    <el-container class="custom-el-container">
      <el-main class="custom-el-main">
        <el-scrollbar style="height: 100%">
          <div class="home">
            <el-row :gutter="10" class="count-info-container">
              <el-col
                :span="24 / countInfos.length"
                v-for="(info, index) in countInfos"
                :key="index"
              >
                <div
                  class="count-main"
                  :class="info.bg"
                  @click="handleCountInfoClick(info.pageInfo)"
                  v-if="hasPermission(info.roleStr)"
                >
                  <div class="left-icon-container">
                    <i :class="info.icon"></i>
                  </div>
                  <div class="right-info-container">
                    <span class="title">{{ info.name }}</span>
                    <CountTo class="count" :startVal="0" :endVal="info.count" :duration="2000"></CountTo>
                  </div>
                </div>
              </el-col>
            </el-row>
          </div>
        </el-scrollbar>
      </el-main>
    </el-container>
  </LayoutComponent>
</template>

<script>
import { Message } from "element-ui";

import LayoutComponent from "@/components/consumables/Layout.vue";
import CountTo from "vue-count-to";
import { HAS_PERMISSION } from "../../utils/permission";
import { getInitDataApi } from "../../api/consumables/home";

export default {
  name: "c_home",
  components: {
    LayoutComponent,
    CountTo
  },
  inject: ["reload"],
  mounted() {
    this.getInitData();
  },
  data: function() {
    return {
      menuName: "/consumables/home",
      breadcrumbs: ["首页"],
      countInfos: [
        {
          name: "角色数",
          ref: "roleCount",
          icon: "el-icon-s-custom",
          count: 0,
          pageInfo: "/consumables/role",
          bg: "count-info-bg-role",
          roleStr: "c_role_select"
        },
        {
          name: "人员数",
          ref: "userCount",
          icon: "el-icon-user",
          count: 0,
          pageInfo: "/consumables/user",
          bg: "count-info-bg-user",
          roleStr: "c_user_select"
        },
        {
          name: "类型数",
          ref: "typeCount",
          icon: "el-icon-s-platform",
          count: 0,
          pageInfo: "/consumables/type",
          bg: "count-info-bg-type",
          roleStr: "c_consumablesType_select"
        },
        {
          name: "基准信息模板数",
          ref: "templateCount",
          icon: "el-icon-printer",
          count: 0,
          pageInfo: "/consumables/referenceInformation",
          bg: "count-info-bg-template",
          roleStr: "c_referenceInformationTemplate_select"
        },
        {
          name: "消耗品数",
          ref: "consumableCount",
          icon: "el-icon-s-order",
          count: 0,
          pageInfo: "/consumables/consumables",
          bg: "count-info-bg-consumable",
          roleStr: "c_consumables_select"
        },
        {
          name: "申请数",
          ref: "applicationCount",
          icon: "el-icon-s-promotion",
          count: 0,
          pageInfo: "/consumables/application",
          bg: "count-info-bg-application",
          roleStr: "c_application_select"
        }
      ]
    };
  },
  methods: {
    getInitData() {
      getInitDataApi({
        warehouseId: this.$store.state.curWarehouse.id
      }).then(res => {
        this.countInfos[0].count = res.data.roleCount;
        this.countInfos[1].count = res.data.userCount;
        this.countInfos[2].count = res.data.typeCount;
        this.countInfos[3].count = res.data.templateCount;
        this.countInfos[4].count = res.data.consumableCount;
        this.countInfos[5].count = res.data.applicationCount;
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
    handleCountInfoClick(page) {
      this.$router.push(page);
    }
  }
};
</script>

<style scoped lang="less">
.home {
  height: 100%;
  overflow-x: hidden;
  overflow-y: auto;

  .count-info-container {
    margin-bottom: 20px;

    .count-main {
      display: flex;
      flex-flow: row nowrap;
      height: 80px;
      border-radius: 4px;
      cursor: pointer;

      &:hover {
        .right-info-container {
          .count {
            font-size: 25px;
          }
        }
      }

      .left-icon-container {
        display: flex;
        flex-flow: row nowrap;
        justify-content: center;
        align-items: center;
        height: 100%;
        width: 60px;
        font-size: 45px;
        color: aliceblue;
      }

      .right-info-container {
        flex: 1;
        display: flex;
        flex-flow: column nowrap;
        justify-content: center;
        align-items: center;
        height: 100%;
        color: aliceblue;

        .title {
          font-size: 12px;
        }

        .count {
          font-size: 20px;
        }
      }
    }

    .count-info-bg-role {
      background-color: #4dd599;
    }

    .count-info-bg-user {
      background-color: #4f81c7;
    }

    .count-info-bg-type {
      background-color: #160f30;
    }

    .count-info-bg-template {
      background-color: #eb7070;
    }

    .count-info-bg-consumable {
      background-color: #c2b0c9;
    }

    .count-info-bg-application {
      background-color: #00a79d;
    }
  }
}
</style>