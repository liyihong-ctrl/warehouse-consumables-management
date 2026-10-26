<template>
  <LayoutComponent :active-menu="menuName">
    <el-breadcrumb separator-class="el-icon-arrow-right">
      <el-breadcrumb-item>首页</el-breadcrumb-item>
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
import LayoutComponent from "@/components/warehouse/Layout.vue";
import CountTo from "vue-count-to";
import { getInitDataApi } from "../../api/warehouse/home";

export default {
  name: "w_home",
  components: {
    LayoutComponent,
    CountTo
  },
  mounted() {
    this.getInitData();
  },
  data: function() {
    return {
      menuName: "/warehouse/home",
      countInfos: [
        {
          name: "人员数",
          ref: "userCount",
          icon: "el-icon-s-custom",
          count: 0,
          pageInfo: "/warehouse/user",
          bg: "count-info-bg-user"
        },
        {
          name: "仓库数",
          ref: "warehouseCount",
          icon: "el-icon-s-management",
          count: 0,
          pageInfo: "/warehouse/warehouse",
          bg: "count-info-bg-warehouse"
        }
      ]
    };
  },
  methods: {
    getInitData() {
      getInitDataApi().then(res => {
        this.countInfos[0].count = res.data.userCount;
        this.countInfos[1].count = res.data.warehouseCount;
      });
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
            font-size: 30px;
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
          font-size: 25px;
        }
      }
    }

    .count-info-bg-user {
      background-color: #4f81c7;
    }

    .count-info-bg-warehouse {
      background-color: #4dd599;
    }
  }
}
</style>