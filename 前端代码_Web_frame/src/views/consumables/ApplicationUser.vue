<template>
  <LayoutComponent
    :active-menu="menuName"
    :openedMenus="openedMenus"
    @curWarehouseChanged="curWarehouseChanged"
  >
    <el-breadcrumb separator-class="el-icon-arrow-right">
      <el-breadcrumb-item v-for="(item,index) in breadcrumbs" :key="index">{{item}}</el-breadcrumb-item>
    </el-breadcrumb>
    <hr />
    <el-row class="operation-container" v-show="isShowMainContainer">
      <el-col class="search-container" :span="8">
        <el-input placeholder="请输入搜索内容" v-model="searchStr" clearable style="max-width: 350px;">
          <el-select
            style="width: 90px;"
            v-model="searchType"
            slot="prepend"
            placeholder="请选择"
            @change="searchTypeChanged"
          >
            <el-option label="姓名" value="1"></el-option>
            <el-option label="账号" value="2"></el-option>
          </el-select>
        </el-input>
        <el-button class="search-btn" type="primary" icon="el-icon-search" @click="doSearch"></el-button>
      </el-col>
      <el-col class="btn-group" :span="16"></el-col>
    </el-row>
    <el-container class="warehouse-grid-user-container" v-show="isShowMainContainer">
      <el-main>
        <el-table
          ref="user-table"
          :data="users"
          border
          stripe
          height="100%"
          highlight-current-row
          :header-cell-style="setTableHeaderStyle"
          @current-change="handleCurrentChange"
        >
          <el-table-column label="头像" width="65" align="center">
            <template slot-scope="scope">
              <el-avatar shape="square" fit="fill" :src="scope.row.avatar"></el-avatar>
            </template>
          </el-table-column>
          <el-table-column prop="name" label="姓名" width="120" align="center"></el-table-column>
          <el-table-column prop="account" label="账号" width="220" align="center"></el-table-column>
          <el-table-column label="角色" width="80" align="center">
            <template slot-scope="scope">
              <span v-for="(role, index) in scope.row.roles" :key="role.id">
                {{ role.name }}
                <span v-if="index + 1 < scope.row.roles.length">、</span>
              </span>
            </template>
          </el-table-column>
          <el-table-column label="部门" align="center">
            <template slot-scope="scope">
              <span>{{ scope.row.extension.department }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="registerTime" label="创建日期" width="135" sortable></el-table-column>
          <el-table-column label="状态" width="65" v-if="hasPermission('c_user_state_management')">
            <template slot-scope="scope">
              <el-tooltip
                :content="scope.row.canApplication ? '不可申请' : '可申请'"
                placement="top"
              >
                <el-switch v-model="scope.row.canApplication" @change="handleStatusChanged(scope.row)"></el-switch>
              </el-tooltip>
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
  </LayoutComponent>
</template>

<script>
import { Message } from "element-ui";
import LayoutComponent from "@/components/consumables/Layout.vue";
import { headerRowStyle } from "../../utils/layout";
import { HAS_PERMISSION } from "../../utils/permission";
import {
  getApplicationUsersApi,
  updateUserCanApplicationStateApi
} from "../../api/consumables/user";

export default {
  name: "c_applicationUser",
  components: {
    LayoutComponent
  },
  inject: ["reload"],
  mounted() {
    this.getInitData();
  },
  computed: {
    isShowMainContainer() {
      return (
        !this.isShowUserDetailContainer &&
        !this.isShowEditUserContainer &&
        !this.isShowAddUserContainer
      );
    }
  },
  data: function() {
    return {
      menuName: "/consumables/applicationUser",
      openedMenus: ["multi-level-menu-user"],
      breadcrumbs: ["人员管理", "申请人员"],
      total: 0,
      page: {
        pageNum: 1,
        pageSize: 30,
        searchCondition: null
      },
      users: [],
      selectCurrentUser: null,
      searchType: "1",
      searchStr: ""
    };
  },
  methods: {
    getInitData() {
      if (!this.page.searchCondition) {
        this.page.searchCondition = JSON.stringify({
          warehouseId: this.$store.state.curWarehouse.id
        });
      }
      getApplicationUsersApi(this.page).then(res => {
        this.total = res.data.total;
        this.users = res.data.list;
        if (this.users && this.users.length > 0) {
          this.$refs["user-table"].setCurrentRow(this.users[0]);
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
      this.page.searchCondition = JSON.stringify({
        warehouseId: this.$store.state.curWarehouse.id,
        searchType: this.searchType,
        searchStr: this.searchStr
      });
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
      this.selectCurrentUser = val;
    },
    handleStatusChanged(user) {
      updateUserCanApplicationStateApi({
        warehouseId: this.$store.state.curWarehouse.id,
        userId: user.id
      }).then(res => {
        if (res.data) {
          Message.success("该用户已设置可以申请当前仓库消耗品");
        } else {
          Message.success("该用户已设置不可申请当前仓库消耗品");
        }
      });
    }
  }
};
</script>

<style lang="less">
.warehouse-grid-user-container {
  height: calc(100% - 100px);

  .el-avatar > img {
    width: 100%;
  }

  .password-container {
    display: flex;
    flex-flow: row nowrap;
    justify-content: center;
    align-items: center;

    .view-password {
      font-size: 18px;
      cursor: pointer;

      &:hover {
        color: #e5e5e5;
      }
    }

    .password {
      color: #b7472a;
      cursor: pointer;
    }
  }

  .warehouse-count {
    color: #00a8cc;
    cursor: pointer;

    &:hover {
      color: #909399;
      text-decoration: underline;
    }
  }
}

.warehouse-assign-warehouse-container,
.warehouse-user-detail-container,
.warehouse-edit-user-container,
.warehouse-add-user-container {
  height: 100%;
  overflow-x: hidden;
  overflow-y: auto;

  #assign-warehouse-form,
  #user-detail-form,
  #edit-user-form,
  #add-user-form {
    width: 500px;

    .user-avatar {
      width: 178px;
      height: 178px;
    }

    #avatar-uploader {
      .el-upload {
        border: 1px dashed #d9d9d9;
        border-radius: 6px;
        cursor: pointer;
        position: relative;
        overflow: hidden;

        &:hover {
          border-color: #409eff;
        }
      }

      .avatar-uploader-icon {
        font-size: 28px;
        color: #8c939d;
        width: 178px;
        height: 178px;
        line-height: 178px;
        text-align: center;
      }

      .avatar {
        width: 178px;
        height: 178px;
        display: block;
      }
    }
  }

  #assign-warehouse-form {
    width: 800px;
    .el-transfer-panel__item {
      display: block;
    }
  }
}
</style>