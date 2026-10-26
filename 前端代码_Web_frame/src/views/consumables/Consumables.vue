<template>
  <LayoutComponent :active-menu="menuName" @curWarehouseChanged="curWarehouseChanged">
    <el-breadcrumb separator-class="el-icon-arrow-right">
      <el-breadcrumb-item v-for="(item,index) in breadcrumbs" :key="index">{{item}}</el-breadcrumb-item>
    </el-breadcrumb>
    <hr />
    <el-row class="operation-container" v-show="isShowMainContainer">
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
        <el-checkbox
          v-model="isViewAlarmData"
          style="margin-left: 15px; color: #F56C6C;"
          @change="doSearch"
        >查看报警数据</el-checkbox>
      </el-col>
    </el-row>
    <el-row class="operation-container" v-show="isShowMainContainer" style="margin-top: 15px;">
      <el-col class="btn-group" :span="24">
        <el-button
          type="primary"
          plain
          @click="batchDownloadBarCode"
          v-if="hasPermission('c_consumables_select')"
        >导出条形码</el-button>
        <el-button
          type="danger"
          plain
          @click="deleteConsumables"
          v-if="hasPermission('c_consumables_delete')"
        >批量删除</el-button>
        <el-button
          type="danger"
          plain
          @click="deleteConsumable"
          v-if="hasPermission('c_consumables_delete')"
        >删除</el-button>
        <el-button
          type="primary"
          plain
          @click="showConsumableDetailContainer"
          v-if="hasPermission('c_consumables_select')"
        >详情</el-button>
        <el-button
          type="primary"
          plain
          @click="showConsumableInboundBatchDialog"
          v-if="hasPermission('c_consumables_select')"
        >查看入库批次</el-button>
        <el-button
          type="primary"
          plain
          @click="showEditConsumablesContainer(false)"
          v-if="hasPermission('c_consumables_update')"
        >编辑</el-button>
        <el-button
          type="primary"
          plain
          @click="showAddConsumablesContainer"
          v-if="hasPermission('c_consumables_add')"
        >新增</el-button>
        <el-button
          type="info"
          plain
          @click="exportConsumables"
          v-if="hasPermission('c_consumables_export')"
        >导出</el-button>
        <div style="margin-left: 10px" v-if="hasPermission('c_consumables_import')">
          <ele-import
            :fields="fields"
            :filepath="filepath"
            :requestFn="importRoles"
            :rules="importConsumableRules"
            :tips="importTips"
            :title="importDialogTitle"
            :visible.sync="isShowImportDialog"
            @close="handleCloseImportRolesDialog"
            @finish="handleFinishImportRolesDialog"
          />
          <el-button type="info" plain @click="handleOpen">导入</el-button>
        </div>
      </el-col>
    </el-row>
    <el-container
      class="consumables-grid-consumables-container"
      v-show="isShowMainContainer"
      style="max-width: 1680px;"
    >
      <el-main>
        <el-table
          ref="consumable-table"
          :data="consumables"
          border
          stripe
          height="100%"
          highlight-current-row
          :header-cell-style="setTableHeaderStyle"
          @current-change="handleCurrentChange"
          @selection-change="handleSelectionChange"
        >
          <el-table-column type="selection" width="55" align="center"></el-table-column>
          <el-table-column prop="code" label="Code" width="120"></el-table-column>
          <el-table-column prop="name" label="名称" width="120" align="center"></el-table-column>
          <el-table-column prop="typeName" label="类型" width="100" align="center"></el-table-column>
          <el-table-column label="位置" min-width="150" align="center">
            <template slot-scope="scope">
              <span style="color: #909399;">{{ scope.row.baseReferenceInfomations.location }}</span>
            </template>
          </el-table-column>
          <el-table-column label="条形码" min-width="235" align="center">
            <template slot-scope="scope">
              <img :id="'grid_' + scope.row.id" />
            </template>
          </el-table-column>
          <el-table-column prop="count" label="数量" width="80" align="right"></el-table-column>
          <el-table-column prop="price" label="未税单价" width="90" align="right"></el-table-column>
          <el-table-column prop="totalPrice" label="库存金额" width="100" align="right"></el-table-column>
          <el-table-column label="简介" min-width="250">
            <template slot-scope="scope">
              <span style="font-size: 10px;">{{ scope.row.summary }}</span>
            </template>
          </el-table-column>
          <el-table-column label="子类（小类）" width="200" align="center">
            <template slot-scope="scope">
              <span style="color: #909399;">{{ scope.row.baseReferenceInfomations.subCategory }}</span>
            </template>
          </el-table-column>
          <el-table-column label="适用工程" min-width="200" align="center">
            <template slot-scope="scope">
              <span
                style="color: #909399;"
              >{{ scope.row.baseReferenceInfomations.applicableProject0 }}</span>
            </template>
          </el-table-column>
          <el-table-column label="适用工程1" min-width="200" align="center">
            <template slot-scope="scope">
              <span
                style="color: #909399;"
              >{{ scope.row.baseReferenceInfomations.applicableProject1 }}</span>
            </template>
          </el-table-column>
          <el-table-column label="适用工程2" min-width="200" align="center">
            <template slot-scope="scope">
              <span
                style="color: #909399;"
              >{{ scope.row.baseReferenceInfomations.applicableProject2 }}</span>
            </template>
          </el-table-column>
          <el-table-column label="适用机种" min-width="200" align="center">
            <template slot-scope="scope">
              <span
                style="color: #909399;"
              >{{ scope.row.baseReferenceInfomations.applicableModels }}</span>
            </template>
          </el-table-column>
          <el-table-column label="子类1" min-width="200" align="center">
            <template slot-scope="scope">
              <span style="color: #909399;">{{ scope.row.baseReferenceInfomations.itemType1 }}</span>
            </template>
          </el-table-column>
          <el-table-column label="子类2" min-width="200" align="center">
            <template slot-scope="scope">
              <span style="color: #909399;">{{ scope.row.baseReferenceInfomations.itemType2 }}</span>
            </template>
          </el-table-column>
          <el-table-column label="单位" min-width="200" align="center">
            <template slot-scope="scope">
              <span style="color: #909399;">{{ scope.row.baseReferenceInfomations.unit }}</span>
            </template>
          </el-table-column>
          <el-table-column label="安全库存" min-width="200" align="center">
            <template slot-scope="scope">
              <span style="color: #909399;">{{ scope.row.baseReferenceInfomations.safetyStock }}</span>
            </template>
          </el-table-column>
          <el-table-column label="当前库存" min-width="200" align="center">
            <template slot-scope="scope">
              <span
                style="color: #909399;"
              >{{ scope.row.baseReferenceInfomations.currentInventory }}</span>
            </template>
          </el-table-column>
          <el-table-column label="前周出库" min-width="200" align="center">
            <template slot-scope="scope">
              <span
                style="color: #909399;"
              >{{ scope.row.baseReferenceInfomations.outOfStockLastWeek }}</span>
            </template>
          </el-table-column>
          <el-table-column label="前月出库" min-width="200" align="center">
            <template slot-scope="scope">
              <span style="color: #909399;">{{ scope.row.baseReferenceInfomations.outOfTheMonth }}</span>
            </template>
          </el-table-column>
          <el-table-column label="单价" min-width="200" align="center">
            <template slot-scope="scope">
              <span style="color: #909399;">{{ scope.row.baseReferenceInfomations.unitPrice }}</span>
            </template>
          </el-table-column>
          <el-table-column label="库存金额" min-width="200" align="center">
            <template slot-scope="scope">
              <span style="color: #909399;">{{ scope.row.baseReferenceInfomations.inventoryAmount }}</span>
            </template>
          </el-table-column>
          <el-table-column label="业体" min-width="200" align="center">
            <template slot-scope="scope">
              <span style="color: #909399;">{{ scope.row.baseReferenceInfomations.industoryBody }}</span>
            </template>
          </el-table-column>
          <el-table-column label="采购周期" min-width="200" align="center">
            <template slot-scope="scope">
              <span style="color: #909399;">{{ scope.row.baseReferenceInfomations.purchasingCycle }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建日期" width="135" align="center"></el-table-column>
          <el-table-column fixed="right" label="操作" width="90" align="center">
            <template slot-scope="scope">
              <el-button type="text" size="small" @click="inbound(scope.row)">入库</el-button>
              <el-button type="text" size="small" @click="outbound(scope.row)">出库</el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-dialog
          width="525px"
          title="入库批次"
          :show-close="true"
          :modal-append-to-body="false"
          :close-on-click-modal="false"
          :close-on-press-escape="false"
          :visible.sync="isShowConsumableInboundBatchDialog"
        >
          <el-table
            style="max-height: 500px;"
            border
            stripe
            height="100%"
            highlight-current-row
            :header-cell-style="setTableHeaderStyle"
            :data="consumabeInboundBatchs"
          >
            <el-table-column label="操作日期" width="160" align="center">
              <template slot-scope="scope">
                <span>{{scope.row.createTime}}</span>
              </template>
            </el-table-column>
            <el-table-column property="count" label="入库数量" width="150" align="right"></el-table-column>
            <el-table-column property="price" label="入库价格" align="right"></el-table-column>
          </el-table>
          <div slot="footer">
            <el-button type="primary" @click="isShowConsumableInboundBatchDialog = false">关闭</el-button>
          </div>
        </el-dialog>
        <el-dialog
          width="425px"
          title="消耗品入库"
          :show-close="true"
          :modal-append-to-body="false"
          :close-on-click-modal="false"
          :close-on-press-escape="false"
          :visible.sync="isShowInboundDialog"
          @closed="resetInboundModel('inbound-form')"
        >
          <el-form
            label-width="90px"
            ref="inbound-form"
            status-icon
            :model="inboundModel"
            :rules="inboundRules"
          >
            <el-form-item label="入库类型：">
              <el-select v-model="inboundModel.type" placeholder="请选择">
                <el-option
                  v-for="type in inboundTypes"
                  :key="type.value"
                  :label="type.key"
                  :value="type.value"
                ></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="入库数：" prop="inboundCount">
              <el-input-number :min="0" v-model="inboundModel.inboundCount"></el-input-number>
            </el-form-item>
            <el-form-item label="入库价格：" prop="inboundPrice">
              <el-input-number :min="0" v-model="inboundModel.inboundPrice"></el-input-number>
            </el-form-item>
            <el-form-item label="备注：" prop="remarks">
              <el-input placeholder="备注" v-model="inboundModel.remarks" clearable></el-input>
            </el-form-item>
          </el-form>
          <div slot="footer">
            <el-button @click="hideInboundDialog('inbound-form')">取 消</el-button>
            <el-button type="primary" @click="doInbound('inbound-form')">确 定</el-button>
          </div>
        </el-dialog>
        <el-dialog
          width="425px"
          title="消耗品出库"
          :show-close="true"
          :modal-append-to-body="false"
          :close-on-click-modal="false"
          :close-on-press-escape="false"
          :visible.sync="isShowOutboundDialog"
          @closed="resetOutboundModel('outbound-form')"
        >
          <el-form
            label-width="90px"
            ref="outbound-form"
            status-icon
            :model="outboundModel"
            :rules="outboundRules"
          >
            <el-form-item label="出库类型：">
              <el-select v-model="outboundModel.type" placeholder="请选择">
                <el-option
                  v-for="type in outboundTypes"
                  :key="type.value"
                  :label="type.key"
                  :value="type.value"
                ></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="出库数：" prop="outboundCount">
              <el-input-number :min="0" v-model="outboundModel.outboundCount"></el-input-number>
            </el-form-item>
            <el-form-item label="备注：" prop="remarks">
              <el-input placeholder="备注" v-model="outboundModel.remarks" clearable></el-input>
            </el-form-item>
          </el-form>
          <div slot="footer">
            <el-button @click="hideOutboundDialog('outbound-form')">取 消</el-button>
            <el-button type="primary" @click="doOutbound('outbound-form')">确 定</el-button>
          </div>
        </el-dialog>
        <div
          style="display: none;"
          v-for="(consumable, index) in batchDownloadConsumableBarcode"
          :key="index"
        >
          <img :id="'barcode_'+ consumable.id" :ref="consumable.id" />
        </div>
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
    <el-container class="custom-el-container" v-if="isShowConsumableDetailContainer">
      <el-main class="custom-el-main">
        <div class="consumables-consumables-detail-container">
          <el-scrollbar style="height: 100%">
            <el-form
              id="consumable-detail-form"
              label-width="160px"
              ref="consumable-detail-form"
              status-icon
              :model="consumableDetailModel"
              v-show="editConsumableStep === 1"
            >
              <el-form-item label="Code：">
                <span>{{consumableDetailModel.code}}</span>
              </el-form-item>
              <el-form-item label="名称：">
                <span>{{consumableDetailModel.name}}</span>
              </el-form-item>
              <el-form-item label="数量：">
                <span>{{consumableDetailModel.count}}</span>
              </el-form-item>
              <el-form-item label="安全在工数：">
                <span>{{consumableDetailModel.safetyCount}}</span>
              </el-form-item>
              <el-form-item label="未税单价：">
                <span>{{consumableDetailModel.price}}</span>
              </el-form-item>
              <el-form-item label="简介：">
                <span class="custom-text-breaks">{{consumableDetailModel.summary}}</span>
              </el-form-item>
              <el-form-item label="类型：" prop="typeId">
                <span class="custom-text-breaks">{{consumableDetailModel.typeName}}</span>
              </el-form-item>
              <el-form-item
                v-for="(referenceInformation, index) in consumableDetailModel.referenceInformations"
                :label="'基准信息 ' + (index+1)"
                :key="index"
              >
                <el-row :gutter="20">
                  <el-col :span="10" style="background-color: #F2F6FC;">
                    <span class="custom-text-breaks">{{ "名：" + referenceInformation.key}}</span>
                  </el-col>
                  <el-col :span="10" style="background-color: #F2F6FC;">
                    <span class="custom-text-breaks">{{ "值：" + referenceInformation.value}}</span>
                  </el-col>
                </el-row>
              </el-form-item>
              <el-form-item>
                <el-button @click="hideConsumableDetailContainer">返回</el-button>
                <el-button type="primary" @click="showEditConsumablesContainer(true)">编辑</el-button>
              </el-form-item>
            </el-form>
          </el-scrollbar>
        </div>
      </el-main>
    </el-container>
    <el-container class="custom-el-container" v-if="isShowEditConsumableContainer">
      <el-main class="custom-el-main">
        <div class="consumables-edit-consumables-container">
          <el-scrollbar style="height: 100%">
            <el-form
              id="edit-consumable-form-step-first"
              label-width="160px"
              ref="edit-consumable-form-step-first"
              status-icon
              :model="editConsumableModel"
              :rules="consumableRules"
              v-show="editConsumableStep === 1"
            >
              <el-form-item label="Code：">
                <el-tooltip class="item" effect="light" content="消耗品Code不可修改" placement="right">
                  <span>{{editConsumableModel.code}}</span>
                </el-tooltip>
              </el-form-item>
              <el-form-item label="名称：" prop="name">
                <el-input placeholder="名称" v-model="editConsumableModel.name" clearable></el-input>
              </el-form-item>
              <el-form-item label="数量：">
                <el-tooltip class="item" effect="light" content="消耗品数量不可修改" placement="right">
                  <span>{{editConsumableModel.count}}</span>
                </el-tooltip>
              </el-form-item>
              <el-form-item label="安全在工数：" prop="safetyCount">
                <el-input-number :min="0" v-model="editConsumableModel.safetyCount"></el-input-number>
              </el-form-item>
              <el-form-item label="价格：" prop="price">
                <el-input-number :min="0" v-model="editConsumableModel.price"></el-input-number>
              </el-form-item>
              <el-form-item label="简介" prop="summary">
                <el-input
                  type="textarea"
                  :autosize="{ minRows: 4, maxRows: 6}"
                  placeholder="请输入描述"
                  v-model="editConsumableModel.summary"
                ></el-input>
              </el-form-item>
              <el-form-item label="类型" prop="typeId">
                <el-cascader
                  :props="{ checkStrictly: true }"
                  :show-all-levels="false"
                  v-model="editConsumableModel.typeId"
                  :options="types"
                ></el-cascader>
              </el-form-item>
              <el-form-item>
                <el-button
                  @click="hideEditConsumableContainer('edit-consumable-form-step-first')"
                >取消</el-button>
                <el-button type="primary" @click="showAddReferenceInfoForm('edit')">填写基准信息</el-button>
                <el-button
                  type="primary"
                  @click="submitEditConsumableForm('edit-consumable-form-step-first', 1)"
                >保存</el-button>
              </el-form-item>
            </el-form>
            <el-form
              id="edit-consumable-form-step-second"
              label-width="160px"
              ref="edit-consumable-form-step-second"
              status-icon
              :model="editConsumableModel"
              :rules="consumableRules"
              v-show="editConsumableStep === 2"
            >
              <el-form-item
                v-for="(referenceInformation, index) in editConsumableModel.referenceInformations"
                :label="'基准信息 ' + (index+1)"
                :key="index"
              >
                <el-row :gutter="20">
                  <el-col :span="10">
                    <el-input placeholder="名" maxlength="255" v-model="referenceInformation.key"></el-input>
                  </el-col>
                  <el-col :span="10">
                    <el-input placeholder="值" maxlength="255" v-model="referenceInformation.value"></el-input>
                  </el-col>
                  <el-col :span="2">
                    <el-button
                      type="danger"
                      icon="el-icon-delete"
                      size="mini"
                      circle
                      @click="removeReferenceInfoInput('edit', referenceInformation)"
                    ></el-button>
                  </el-col>
                </el-row>
              </el-form-item>
              <el-form-item>
                <el-button @click="hideAddReferenceInfoForm('edit')">返回</el-button>
                <el-button type="primary" @click="addReferenceInfoInput('edit')">添加基准信息</el-button>
                <el-button type="warning" @click="showTempalteSelectDialog">引用模板</el-button>
                <el-button
                  type="primary"
                  @click="submitEditConsumableForm('edit-consumable-form-step-first', 2)"
                >保存</el-button>
              </el-form-item>
            </el-form>
            <el-dialog
              width="425px"
              title="基准信息模板选择"
              :show-close="true"
              :modal-append-to-body="false"
              :close-on-click-modal="false"
              :close-on-press-escape="false"
              :visible.sync="isShowTempateSelectDialog"
            >
              <el-form>
                <el-form-item label="模板" label-width="60">
                  <el-select
                    v-model="curSelectTemplates"
                    multiple
                    collapse-tags
                    style="margin-left: 20px;"
                    placeholder="请选择基准信息模板"
                  >
                    <el-option
                      v-for="item in templates"
                      :key="item.id"
                      :label="item.name"
                      :value="item.id"
                    ></el-option>
                  </el-select>
                </el-form-item>
              </el-form>
              <div slot="footer">
                <el-button @click="hideTempateSelectDialog">取 消</el-button>
                <el-button type="primary" @click="renderTemplate('edit')">确 定</el-button>
              </div>
            </el-dialog>
          </el-scrollbar>
        </div>
      </el-main>
    </el-container>
    <el-container class="custom-el-container" v-if="isShowAddConsumableContainer">
      <el-main class="custom-el-main">
        <div class="consumables-add-consumables-container">
          <el-scrollbar style="height: 100%">
            <el-form
              id="add-consumable-form-step-first"
              label-width="160px"
              ref="add-consumable-form-step-first"
              status-icon
              :model="addConsumableModel"
              :rules="consumableRules"
              v-show="addConsumableStep === 1"
            >
              <el-form-item label="Code：" prop="code">
                <el-input placeholder="Code" v-model="addConsumableModel.code" clearable></el-input>
              </el-form-item>
              <el-form-item label="名称：" prop="name">
                <el-input placeholder="名称" v-model="addConsumableModel.name" clearable></el-input>
              </el-form-item>
              <el-form-item label="数量：" prop="count">
                <el-input-number :min="0" v-model="addConsumableModel.count"></el-input-number>
              </el-form-item>
              <el-form-item label="安全在工数：" prop="safetyCount">
                <el-input-number :min="0" v-model="addConsumableModel.safetyCount"></el-input-number>
              </el-form-item>
              <el-form-item label="未税单价：" prop="price">
                <el-input-number :min="0" v-model="addConsumableModel.price"></el-input-number>
              </el-form-item>
              <el-form-item label="简介" prop="summary">
                <el-input
                  type="textarea"
                  :autosize="{ minRows: 4, maxRows: 6}"
                  placeholder="请输入描述"
                  v-model="addConsumableModel.summary"
                ></el-input>
              </el-form-item>
              <el-form-item label="类型" prop="typeId">
                <el-cascader
                  :props="{ checkStrictly: true }"
                  :show-all-levels="false"
                  v-model="addConsumableModel.typeId"
                  :options="types"
                ></el-cascader>
              </el-form-item>
              <el-form-item>
                <el-button @click="hideAddConsumableContainer('add-consumable-form-step-first')">取消</el-button>
                <el-button type="primary" @click="showAddReferenceInfoForm">填写基准信息</el-button>
                <el-button
                  type="primary"
                  @click="submitAddConsumableForm('add-consumable-form-step-first', 1)"
                >保存</el-button>
              </el-form-item>
            </el-form>
            <el-form
              id="add-consumable-form-step-second"
              label-width="160px"
              ref="add-consumable-form-step-second"
              status-icon
              :model="addConsumableModel"
              :rules="consumableRules"
              v-show="addConsumableStep === 2"
            >
              <el-form-item
                v-for="(referenceInformation, index) in addConsumableModel.referenceInformations"
                :label="'基准信息 ' + (index+1)"
                :key="index"
              >
                <el-row :gutter="20">
                  <el-col :span="10">
                    <el-input placeholder="名" maxlength="255" v-model="referenceInformation.key"></el-input>
                  </el-col>
                  <el-col :span="10">
                    <el-input placeholder="值" maxlength="255" v-model="referenceInformation.value"></el-input>
                  </el-col>
                  <el-col :span="2">
                    <el-button
                      type="danger"
                      icon="el-icon-delete"
                      size="mini"
                      circle
                      @click="removeReferenceInfoInput('add', referenceInformation)"
                    ></el-button>
                  </el-col>
                </el-row>
              </el-form-item>
              <el-form-item>
                <el-button @click="hideAddReferenceInfoForm('add')">返回</el-button>
                <el-button type="primary" @click="addReferenceInfoInput('add')">添加基准信息</el-button>
                <el-button type="warning" @click="showTempalteSelectDialog">引用模板</el-button>
                <el-button
                  type="primary"
                  @click="submitAddConsumableForm('add-consumable-form-step-first', 2)"
                >保存</el-button>
              </el-form-item>
            </el-form>
            <el-dialog
              width="425px"
              title="基准信息模板选择"
              :show-close="true"
              :modal-append-to-body="false"
              :close-on-click-modal="false"
              :close-on-press-escape="false"
              :visible.sync="isShowTempateSelectDialog"
            >
              <el-form>
                <el-form-item label="模板" label-width="60">
                  <el-select
                    v-model="curSelectTemplates"
                    multiple
                    collapse-tags
                    style="margin-left: 20px;"
                    placeholder="请选择基准信息模板"
                  >
                    <el-option
                      v-for="item in templates"
                      :key="item.id"
                      :label="item.name"
                      :value="item.id"
                    ></el-option>
                  </el-select>
                </el-form-item>
              </el-form>
              <div slot="footer">
                <el-button @click="hideTempateSelectDialog">取 消</el-button>
                <el-button type="primary" @click="renderTemplate('add')">确 定</el-button>
              </div>
            </el-dialog>
          </el-scrollbar>
        </div>
      </el-main>
    </el-container>
  </LayoutComponent>
</template>

<script>
import { Message } from "element-ui";
import LayoutComponent from "@/components/consumables/Layout.vue";
import JsBarcode from "jsbarcode";
import JSZip from "jszip";
import FileSaver from "file-saver";
import { headerRowStyle } from "../../utils/layout";
import base from "../../utils/base";
import { HAS_PERMISSION } from "../../utils/permission";
import { formatTimeToStr } from "../../utils/date";
import { validateCode } from "../../utils/validator";
import {
  getTypesApi,
  getReferenceInformationTemplates,
  getConsumablesApi,
  getAllConsumablesApi,
  deleteConsumablesApi,
  deleteConsumableApi,
  addConsumableApi,
  updateConsumableApi,
  outboundApi,
  inboundApi,
  importConsumablesApi
} from "../../api/consumables/consumables";

export default {
  name: "c_consumables",
  components: {
    LayoutComponent
  },
  inject: ["reload"],
  mounted() {
    if (this.$route.params.isShowAlarmConsumables) {
      this.isViewAlarmData = true;
      this.page.searchCondition = JSON.stringify({
        warehouseId: this.$store.state.curWarehouse.id,
        isViewAlarmData: this.isViewAlarmData
      });
    }
    this.getInitData();
  },
  computed: {
    isShowMainContainer() {
      return (
        !this.isShowConsumableDetailContainer &&
        !this.isShowEditConsumableContainer &&
        !this.isShowAddConsumableContainer
      );
    }
  },
  data: function() {
    var checkCode = (rule, value, callback) => {
      if (value && !validateCode(value)) {
        callback(new Error("请输入格式正确的编号(1-20个字母、数字)"));
      } else {
        callback();
      }
    };
    var checkCount = (rule, value, callback) => {
      if (value < 0 || 9999999 < value) {
        callback(new Error("数量范围为0~9999999"));
      } else {
        callback();
      }
    };
    // var checkSafetyCount = (rule, value, callback) => {
    //   if (value < 0 || 9999999 < value) {
    //     callback(new Error("安全在工数范围为0~9999999"));
    //   } else {
    //     callback();
    //   }
    // };
    var checkPrice = (rule, value, callback) => {
      if (value < 0 || 9999999.99 < value) {
        callback(new Error("未税单价范围为0~9999999"));
      } else {
        callback();
      }
    };
    var checkInOutboundCount = (rule, value, callback) => {
      if (value < 1 || 9999999 < value) {
        callback(new Error("数量范围为1~9999999"));
      } else {
        callback();
      }
    };
    return {
      menuName: "/consumables/consumables",
      breadcrumbs: ["消耗品管理"],
      inboundTypes: [
        { key: "A类型", value: 2 },
        { key: "B类型", value: 3 }
      ],
      outboundTypes: [
        { key: "正常出库", value: 0 },
        { key: "在工减少", value: 1 }
      ],
      isShowConsumableDetailContainer: false,
      isShowConsumableInboundBatchDialog: false,
      isShowEditConsumableContainer: false,
      isShowAddConsumableContainer: false,
      isShowTempateSelectDialog: false,
      total: 0,
      page: {
        pageNum: 1,
        pageSize: 30,
        searchCondition: null
      },
      consumables: [],
      selectCurrentConsumable: null,
      searchType: "1",
      searchStr: "",
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
        endTime: "",
        isViewAlarmData: false
      },
      isViewAlarmData: false,
      batchDownloadConsumableBarcode: [],
      batchDeleteConsumableIds: [],
      types: [],
      consumableDetailStep: 1,
      consumableDetailModel: null,
      consumabeInboundBatchs: [],
      editConsumableStep: 1,
      editConsumableModel: {
        id: "",
        warehouseId: "",
        typeId: [],
        code: "",
        name: "",
        count: 0,
        safetyCount: 0,
        price: 0,
        summary: "",
        referenceInformations: []
      },
      addConsumableStep: 1,
      addConsumableModel: {
        warehouseId: "",
        typeId: "",
        code: "",
        name: "",
        count: 0,
        safetyCount: 0,
        price: 0,
        summary: "",
        referenceInformations: []
      },
      consumableRules: {
        typeId: [
          { required: true, message: "请选择类型", trigger: "blur" },
          { required: true, message: "请选择类型", trigger: "change" }
        ],
        code: [
          { required: true, message: "请输入Code", trigger: "blur" },
          { min: 1, max: 20, message: "Code长度范围为1~20", trigger: "blur" },
          { min: 1, max: 20, message: "Code长度范围为1~20", trigger: "change" },
          { validator: checkCode, trigger: "change" },
          { validator: checkCode, trigger: "blur" }
        ],
        name: [
          { required: true, message: "请输入名称", trigger: "blur" },
          { min: 1, max: 50, message: "名称长度范围为1~50", trigger: "blur" },
          { min: 1, max: 50, message: "名称长度范围为1~50", trigger: "change" }
        ],
        count: [
          { validator: checkCount, trigger: "change" },
          { validator: checkCount, trigger: "blur" }
        ],
        safetyCount: [
          { validator: checkCount, trigger: "change" },
          { validator: checkCount, trigger: "blur" }
        ],
        price: [
          { validator: checkPrice, trigger: "change" },
          { validator: checkPrice, trigger: "blur" }
        ],
        summary: [
          {
            min: 0,
            max: 255,
            message: "简介长度范围为0~255",
            trigger: "blur"
          },
          {
            min: 0,
            max: 255,
            message: "简介长度范围为0~255",
            trigger: "change"
          }
        ]
      },
      templates: [],
      curSelectTemplates: [],
      isShowOutboundDialog: false,
      outboundModel: {
        id: "",
        warehouseId: "",
        type: 0,
        outboundCount: 0,
        remarks: ""
      },
      outboundRules: {
        outboundCount: [
          { validator: checkInOutboundCount, trigger: "change" },
          { validator: checkInOutboundCount, trigger: "blur" }
        ],
        remarks: [
          {
            min: 0,
            max: 50,
            message: "备注长度范围为0~50",
            trigger: "blur"
          },
          {
            min: 0,
            max: 50,
            message: "备注长度范围为0~50",
            trigger: "change"
          }
        ]
      },
      isShowInboundDialog: false,
      inboundModel: {
        id: "",
        warehouseId: "",
        type: 2,
        inboundCount: 0,
        inboundPrice: 0,
        remarks: ""
      },
      inboundRules: {
        inboundCount: [
          { validator: checkInOutboundCount, trigger: "change" },
          { validator: checkInOutboundCount, trigger: "blur" }
        ],
        inboundPrice: [
          { validator: checkPrice, trigger: "change" },
          { validator: checkPrice, trigger: "blur" }
        ],
        remarks: [
          {
            min: 0,
            max: 50,
            message: "备注长度范围为0~50",
            trigger: "blur"
          },
          {
            min: 0,
            max: 50,
            message: "备注长度范围为0~50",
            trigger: "change"
          }
        ]
      },
      importDialogTitle: "批量导入消耗品",
      importTips: [
        "Code格式为数字或字母1~20位",
        "基准信息数最大支持导入量为20组"
      ],
      fields: {
        code: "Code",
        name: "名称",
        typeName: "类型",
        count: "数量",
        safetyCount: "安全在工数",
        price: "未税单价",
        summary: "简介",
        referenceInformationKey1: "基准信息名1",
        referenceInformationValue1: "基准信息值1",
        referenceInformationKey2: "基准信息名2",
        referenceInformationValue2: "基准信息值2",
        referenceInformationKey3: "基准信息名3",
        referenceInformationValue3: "基准信息值3",
        referenceInformationKey4: "基准信息名4",
        referenceInformationValue4: "基准信息值4",
        referenceInformationKey5: "基准信息名5",
        referenceInformationValue5: "基准信息值5",
        referenceInformationKey6: "基准信息名6",
        referenceInformationValue6: "基准信息值6",
        referenceInformationKey7: "基准信息名7",
        referenceInformationValue7: "基准信息值7",
        referenceInformationKey8: "基准信息名8",
        referenceInformationValue8: "基准信息值8",
        referenceInformationKey9: "基准信息名9",
        referenceInformationValue9: "基准信息值9",
        referenceInformationKey10: "基准信息名10",
        referenceInformationValue10: "基准信息值10",
        referenceInformationKey11: "基准信息名11",
        referenceInformationValue11: "基准信息值11",
        referenceInformationKey12: "基准信息名12",
        referenceInformationValue12: "基准信息值12",
        referenceInformationKey13: "基准信息名13",
        referenceInformationValue13: "基准信息值13",
        referenceInformationKey14: "基准信息名14",
        referenceInformationValue14: "基准信息值14",
        referenceInformationKey15: "基准信息名15",
        referenceInformationValue15: "基准信息值15",
        referenceInformationKey16: "基准信息名16",
        referenceInformationValue16: "基准信息值16",
        referenceInformationKey17: "基准信息名17",
        referenceInformationValue17: "基准信息值17",
        referenceInformationKey18: "基准信息名18",
        referenceInformationValue18: "基准信息值18",
        referenceInformationKey19: "基准信息名19",
        referenceInformationValue19: "基准信息值19",
        referenceInformationKey20: "基准信息名20",
        referenceInformationValue20: "基准信息值20"
      },
      importConsumableRules: {
        code: [
          { required: true, message: "Code不能为空" },
          { validator: checkCode }
        ],
        name: [
          { required: true, message: "名称不能为空" },
          { min: 1, max: 50, message: "名称长度范围为1~50" }
        ],
        typeName: [
          { required: true, message: "类型不能为空" },
          { min: 1, max: 20, message: "类型长度必须在1~20之间" }
        ],
        // count: [
        //   { required: true, message: "数量不能为空" },
        //   { type: "number", message: "数量必须为数字值" },
        //   { validator: checkCount }
        // ],
        // safetyCount: [
        //   { required: true, message: "安全在工数不能为空" },
        //   { type: "number", message: "安全在工数必须为数字值" },
        //   { validator: checkSafetyCount }
        // ],
        // price: [
        //   { required: true, message: "价格不能为空" },
        //   { type: "number", message: "价格必须为数字值" },
        //   { validator: checkPrice }
        // ],
        summary: [
          {
            min: 0,
            max: 255,
            message: "简介长度范围为0~255"
          }
        ],
        referenceInformationKey1: [
          {
            min: 0,
            max: 255,
            message: "基准信息名1长度范围为0~255"
          }
        ],
        referenceInformationValue1: [
          {
            min: 0,
            max: 255,
            message: "基准信息值1长度范围为0~255"
          }
        ],
        referenceInformationKey2: [
          {
            min: 0,
            max: 255,
            message: "基准信息名2长度范围为0~255"
          }
        ],
        referenceInformationValue2: [
          {
            min: 0,
            max: 255,
            message: "基准信息值2长度范围为0~255"
          }
        ],
        referenceInformationKey3: [
          {
            min: 0,
            max: 255,
            message: "基准信息名3长度范围为0~255"
          }
        ],
        referenceInformationValue3: [
          {
            min: 0,
            max: 255,
            message: "基准信息值3长度范围为0~255"
          }
        ],
        referenceInformationKey4: [
          {
            min: 0,
            max: 255,
            message: "基准信息名4长度范围为0~255"
          }
        ],
        referenceInformationValue4: [
          {
            min: 0,
            max: 255,
            message: "基准信息值4长度范围为0~255"
          }
        ],
        referenceInformationKey5: [
          {
            min: 0,
            max: 255,
            message: "基准信息名5长度范围为0~255"
          }
        ],
        referenceInformationValue5: [
          {
            min: 0,
            max: 255,
            message: "基准信息值5长度范围为0~255"
          }
        ],
        referenceInformationKey6: [
          {
            min: 0,
            max: 255,
            message: "基准信息名6长度范围为0~255"
          }
        ],
        referenceInformationValue6: [
          {
            min: 0,
            max: 255,
            message: "基准信息值6长度范围为0~255"
          }
        ],
        referenceInformationKey7: [
          {
            min: 0,
            max: 255,
            message: "基准信息名7长度范围为0~255"
          }
        ],
        referenceInformationValue7: [
          {
            min: 0,
            max: 255,
            message: "基准信息值7长度范围为0~255"
          }
        ],
        referenceInformationKey8: [
          {
            min: 0,
            max: 255,
            message: "基准信息名8长度范围为0~255"
          }
        ],
        referenceInformationValue8: [
          {
            min: 0,
            max: 255,
            message: "基准信息值8长度范围为0~255"
          }
        ],
        referenceInformationKey9: [
          {
            min: 0,
            max: 255,
            message: "基准信息名9长度范围为0~255"
          }
        ],
        referenceInformationValue9: [
          {
            min: 0,
            max: 255,
            message: "基准信息值9长度范围为0~255"
          }
        ],
        referenceInformationKey10: [
          {
            min: 0,
            max: 255,
            message: "基准信息名10长度范围为0~255"
          }
        ],
        referenceInformationValue10: [
          {
            min: 0,
            max: 255,
            message: "基准信息值10长度范围为0~255"
          }
        ],
        referenceInformationKey11: [
          {
            min: 0,
            max: 255,
            message: "基准信息名11长度范围为0~255"
          }
        ],
        referenceInformationValue11: [
          {
            min: 0,
            max: 255,
            message: "基准信息值11长度范围为0~255"
          }
        ],
        referenceInformationKey12: [
          {
            min: 0,
            max: 255,
            message: "基准信息名12长度范围为0~255"
          }
        ],
        referenceInformationValue12: [
          {
            min: 0,
            max: 255,
            message: "基准信息值12长度范围为0~255"
          }
        ],
        referenceInformationKey13: [
          {
            min: 0,
            max: 255,
            message: "基准信息名13长度范围为0~255"
          }
        ],
        referenceInformationValue13: [
          {
            min: 0,
            max: 255,
            message: "基准信息值13长度范围为0~255"
          }
        ],
        referenceInformationKey14: [
          {
            min: 0,
            max: 255,
            message: "基准信息名14长度范围为0~255"
          }
        ],
        referenceInformationValue14: [
          {
            min: 0,
            max: 255,
            message: "基准信息值14长度范围为0~255"
          }
        ],
        referenceInformationKey15: [
          {
            min: 0,
            max: 255,
            message: "基准信息名15长度范围为0~255"
          }
        ],
        referenceInformationValue15: [
          {
            min: 0,
            max: 255,
            message: "基准信息值15长度范围为0~255"
          }
        ],
        referenceInformationKey16: [
          {
            min: 0,
            max: 255,
            message: "基准信息名16长度范围为0~255"
          }
        ],
        referenceInformationValue16: [
          {
            min: 0,
            max: 255,
            message: "基准信息值16长度范围为0~255"
          }
        ],
        referenceInformationKey17: [
          {
            min: 0,
            max: 255,
            message: "基准信息名17长度范围为0~255"
          }
        ],
        referenceInformationValue17: [
          {
            min: 0,
            max: 255,
            message: "基准信息值17长度范围为0~255"
          }
        ],
        referenceInformationKey18: [
          {
            min: 0,
            max: 255,
            message: "基准信息名18长度范围为0~255"
          }
        ],
        referenceInformationValue18: [
          {
            min: 0,
            max: 255,
            message: "基准信息值18长度范围为0~255"
          }
        ],
        referenceInformationKey19: [
          {
            min: 0,
            max: 255,
            message: "基准信息名19长度范围为0~255"
          }
        ],
        referenceInformationValue19: [
          {
            min: 0,
            max: 255,
            message: "基准信息值19长度范围为0~255"
          }
        ],
        referenceInformationKey20: [
          {
            min: 0,
            max: 255,
            message: "基准信息名20长度范围为0~255"
          }
        ],
        referenceInformationValue20: [
          {
            min: 0,
            max: 255,
            message: "基准信息值20长度范围为0~255"
          }
        ]
      },
      filepath:
        "http://127.0.0.1:8888/cConsumable/template?warehouseId=" +
        this.$store.state.curWarehouse.id,
      isShowImportDialog: false
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
      getConsumablesApi(this.page).then(res => {
        that.total = res.data.total;
        that.consumables = res.data.list;
        if (that.consumables && that.consumables.length > 0) {
          that.consumables.forEach(item => {
            item.baseReferenceInfomations = that.generateBaseReferenceInfomationTemplateData(
              item.referenceInformations
            );
          });
          that.$refs["consumable-table"].setCurrentRow(that.consumables[0]);
          that.$nextTick(() => {
            that.consumables.forEach(item => {
              JsBarcode("#grid_" + item.id, item.code, {
                format: "CODE128", //条形码的格式
                width: 1, //线宽
                height: 40, //条码高度
                displayValue: true, //是否在条形码下方显示文字
                textAlign: "center", //设置文本的水平对齐方式
                textPosition: "bottom", //设置文本的垂直位置
                textMargin: 2, //设置条形码和文本之间的间距
                fontSize: 10 //设置文本的大小
              });
            });
          });
        }
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
        endTime: "",
        isViewAlarmData: this.isViewAlarmData
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
    },
    handleSelectionChange(rows) {
      this.batchDeleteConsumableIds = [];
      if (rows && rows.length > 0) {
        rows.forEach(item => {
          this.batchDeleteConsumableIds.push(item.id);
        });
      }
    },
    batchDownloadBarCode() {
      // 根据检索信息全量检索出消耗品数据，生成条形码并打包
      getAllConsumablesApi(this.oldSearchCondition).then(res => {
        if (!res.data || res.data.length === 0) {
          Message.error("导出失败：未检索到消耗品");
          return;
        }

        this.batchDownloadConsumableBarcode = res.data;

        var blogTitle =
          this.$store.state.curWarehouse.name + "_" + "消耗品条形码";
        var zip = new JSZip();
        var imgs = zip.folder(blogTitle);

        this.$nextTick(() => {
          this.batchDownloadConsumableBarcode.forEach(item => {
            JsBarcode("#barcode_" + item.id, item.code, {
              format: "CODE128", //条形码的格式
              width: 1, //线宽
              height: 40, //条码高度
              displayValue: true, //是否在条形码下方显示文字
              textAlign: "center", //设置文本的水平对齐方式
              textPosition: "bottom", //设置文本的垂直位置
              textMargin: 2, //设置条形码和文本之间的间距
              fontSize: 10 //设置文本的大小
            });
          });
          res.data.forEach(item => {
            let ref = item.id;
            let barCodeImg = this.$refs[ref][0];
            let url = barCodeImg.src;
            imgs.file(
              this.$store.state.curWarehouse.name +
                "_" +
                item.typeName +
                "_" +
                item.name +
                ".png",
              url.substring(22),
              { base64: true }
            );
          });
          zip.generateAsync({ type: "blob" }).then(function(content) {
            FileSaver.saveAs(content, blogTitle + ".zip");
          });
          Message.success("导出成功");
        });
      });
    },
    deleteConsumables() {
      if (this.batchDeleteConsumableIds.length === 0) {
        Message.error("批量删除错误：请选择需要删除的消耗品！");
        return;
      }
      this.$confirm(
        "此操作将永久删除所选中的" +
          this.batchDeleteConsumableIds.length +
          "个消耗品, 是否继续?",
        "删除",
        {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }
      )
        .then(() => {
          deleteConsumablesApi({
            warehouseId: this.$store.state.curWarehouse.id,
            ids: this.batchDeleteConsumableIds
          }).then(() => {
            Message.success("删除成功");
            this.total -= this.batchDeleteConsumableIds.length;
            this.consumables = this.consumables.filter(item => {
              return !this.batchDeleteConsumableIds.find(cid => {
                return cid === item.id;
              });
            });
            this.batchDeleteConsumableIds = [];
            if (this.consumables.length > 0) {
              this.$refs["consumable-table"].setCurrentRow(this.consumables[0]);
              this.selectCurrentConsumable = this.consumables[0];
            } else {
              this.$refs["consumable-table"].setCurrentRow(null);
              this.selectCurrentConsumable = null;
              this.doSearch();
            }
          });
        })
        .catch(() => {
          Message.info("取消删除");
        });
    },
    deleteConsumable() {
      if (!this.selectCurrentConsumable) {
        return;
      }
      this.$confirm(
        "此操作将永久删除名称为「" +
          this.selectCurrentConsumable.name +
          "」的消耗品, 是否继续?",
        "删除",
        {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }
      )
        .then(() => {
          deleteConsumableApi({
            warehouseId: this.$store.state.curWarehouse.id,
            id: this.selectCurrentConsumable.id
          }).then(() => {
            Message.success("删除成功");
            this.total -= 1;
            this.batchDeleteConsumableIds = [];
            this.$refs["consumable-table"].toggleRowSelection([]);
            this.consumables = this.consumables.filter(item => {
              return item.id !== this.selectCurrentConsumable.id;
            });
            if (this.consumables.length > 0) {
              this.$refs["consumable-table"].setCurrentRow(this.consumables[0]);
              this.selectCurrentConsumable = this.consumables[0];
            } else {
              this.$refs["consumable-table"].setCurrentRow(null);
              this.selectCurrentConsumable = null;
              this.doSearch();
            }
          });
        })
        .catch(() => {
          Message.info("取消删除");
        });
    },
    formatTypes(type) {
      if (type.children.length === 0) {
        type.children = null;
        return;
      }
      type.children.forEach(item => {
        this.formatTypes(item);
      });
    },
    showConsumableDetailContainer() {
      if (!this.selectCurrentConsumable) {
        return;
      }
      this.consumableDetailModel = {
        id: this.selectCurrentConsumable.id,
        warehouseId: this.selectCurrentConsumable.warehouseId,
        typeId: this.selectCurrentConsumable.typeId,
        code: this.selectCurrentConsumable.code,
        name: this.selectCurrentConsumable.name,
        count: this.selectCurrentConsumable.count,
        safetyCount: this.selectCurrentConsumable.safetyCount,
        price: this.selectCurrentConsumable.price,
        summary: this.selectCurrentConsumable.summary,
        typeName: this.selectCurrentConsumable.typeName,
        referenceInformations: []
      };
      this.selectCurrentConsumable.referenceInformations.forEach(item => {
        this.consumableDetailModel.referenceInformations.push({
          key: item.referenceKey,
          value: item.referenceValue
        });
      });
      this.breadcrumbs.push("详情");
      this.isShowConsumableDetailContainer = true;
    },
    formatTimeToStr(time) {
      return formatTimeToStr(time);
    },
    showConsumableInboundBatchDialog() {
      this.consumabeInboundBatchs = this.selectCurrentConsumable.inboundBatchs;
      this.isShowConsumableInboundBatchDialog = true;
    },
    showEditConsumablesContainer(isFromDetail) {
      if (!isFromDetail && !this.selectCurrentConsumable) {
        return;
      }
      getTypesApi({
        warehouseId: this.$store.state.curWarehouse.id
      }).then(res => {
        if (res.data.length > 0) {
          this.types = res.data;
          this.types.forEach(item => {
            this.formatTypes(item);
          });
          this.editConsumableModel = {
            id: this.selectCurrentConsumable.id,
            warehouseId: this.selectCurrentConsumable.warehouseId,
            typeId: this.selectCurrentConsumable.typeId,
            code: this.selectCurrentConsumable.code,
            name: this.selectCurrentConsumable.name,
            count: this.selectCurrentConsumable.count,
            safetyCount: this.selectCurrentConsumable.safetyCount,
            price: this.selectCurrentConsumable.price,
            summary: this.selectCurrentConsumable.summary,
            referenceInformations: []
          };
          this.selectCurrentConsumable.referenceInformations.forEach(item => {
            this.editConsumableModel.referenceInformations.push({
              key: item.referenceKey,
              value: item.referenceValue
            });
          });
          if (isFromDetail) {
            this.hideConsumableDetailContainer();
          }
          this.breadcrumbs.push("编辑");
          this.isShowEditConsumableContainer = true;
        }
      });
    },
    showAddConsumablesContainer() {
      getTypesApi({
        warehouseId: this.$store.state.curWarehouse.id
      }).then(res => {
        if (res.data.length > 0) {
          this.types = res.data;
          this.types.forEach(item => {
            this.formatTypes(item);
          });
          this.breadcrumbs.push("新增");
          this.isShowAddConsumableContainer = true;
        } else {
          Message.error("无法新增：请先设定消耗品类型信息");
        }
      });
    },
    showAddReferenceInfoForm(type) {
      if (type === "edit") {
        if (this.editConsumableModel.referenceInformations.length === 0) {
          this.editConsumableModel.referenceInformations.push({
            key: "",
            value: ""
          });
        }
        this.breadcrumbs.push("填写基准信息");
        this.editConsumableStep = 2;
      } else {
        if (this.addConsumableModel.referenceInformations.length === 0) {
          this.addConsumableModel.referenceInformations.push({
            key: "",
            value: ""
          });
        }
        this.breadcrumbs.push("填写基准信息");
        this.addConsumableStep = 2;
      }
    },
    showTempalteSelectDialog() {
      getReferenceInformationTemplates({
        warehouseId: this.$store.state.curWarehouse.id
      }).then(res => {
        if (res.data.length === 0) {
          Message.error("暂无基准信息模板");
          return;
        }
        this.templates = res.data;
        this.isShowTempateSelectDialog = true;
      });
    },
    hideTempateSelectDialog() {
      this.templates = [];
      this.curSelectTemplates = [];
      this.isShowTempateSelectDialog = false;
    },
    renderTemplate(type) {
      if (this.curSelectTemplates.length === 0) {
        return;
      }
      this.curSelectTemplates.forEach(curTemplate => {
        if (type === "edit") {
          this.templates.forEach(template => {
            if (curTemplate === template.id) {
              if (template.details && template.details.length > 0) {
                template.details.forEach(detail => {
                  this.editConsumableModel.referenceInformations.push({
                    key: detail.referenceKey,
                    value: ""
                  });
                });
              }
            }
          });
        } else {
          this.templates.forEach(template => {
            if (curTemplate === template.id) {
              if (template.details && template.details.length > 0) {
                template.details.forEach(detail => {
                  this.addConsumableModel.referenceInformations.push({
                    key: detail.referenceKey,
                    value: ""
                  });
                });
              }
            }
          });
        }
      });
      this.hideTempateSelectDialog();
    },
    hideAddReferenceInfoForm(type) {
      this.breadcrumbs.pop();
      switch (type) {
        case "edit":
          this.editConsumableStep = 1;
          break;
        case "add":
          this.addConsumableStep = 1;
          break;
      }
    },
    addReferenceInfoInput(type) {
      switch (type) {
        case "edit":
          this.editConsumableModel.referenceInformations.push({
            key: "",
            value: ""
          });
          break;
        case "add":
          this.addConsumableModel.referenceInformations.push({
            key: "",
            value: ""
          });
          break;
      }
    },
    removeReferenceInfoInput(type, item) {
      var index;
      switch (type) {
        case "edit":
          index = this.editConsumableModel.referenceInformations.indexOf(item);
          if (index !== -1) {
            this.editConsumableModel.referenceInformations.splice(index, 1);
          }
          break;
        case "add":
          index = this.addConsumableModel.referenceInformations.indexOf(item);
          if (index !== -1) {
            this.addConsumableModel.referenceInformations.splice(index, 1);
          }
          break;
      }
    },
    hideConsumableDetailContainer() {
      this.consumableDetailModel = null;
      this.breadcrumbs.pop();
      this.isShowConsumableDetailContainer = false;
    },
    hideEditConsumableContainer(formName) {
      this.resetEditConsumableModel();
      this.$refs[formName].resetFields();
      this.breadcrumbs.pop();
      this.isShowEditConsumableContainer = false;
    },
    hideAddConsumableContainer(formName) {
      this.resetAddConsumableModel();
      this.$refs[formName].resetFields();
      this.breadcrumbs.pop();
      this.isShowAddConsumableContainer = false;
    },
    resetEditConsumableModel() {
      this.editConsumableModel = {
        id: "",
        warehouseId: "",
        typeId: "",
        code: "",
        name: "",
        count: "",
        safetyCount: "",
        price: "",
        summary: "",
        referenceInformations: []
      };
    },
    resetAddConsumableModel() {
      this.addConsumableModel = {
        warehouseId: "",
        typeId: "",
        code: "",
        name: "",
        count: "",
        safetyCount: "",
        price: "",
        summary: "",
        referenceInformations: []
      };
    },
    submitEditConsumableForm(formName, step) {
      this.$refs[formName].validate(valid => {
        if (valid) {
          var editConsumableModel = {
            id: this.editConsumableModel.id,
            warehouseId: this.$store.state.curWarehouse.id,
            typeId:
              typeof this.editConsumableModel.typeId === "string"
                ? this.editConsumableModel.typeId
                : this.editConsumableModel.typeId[
                    this.editConsumableModel.typeId.length - 1
                  ],
            code: this.editConsumableModel.code,
            name: this.editConsumableModel.name,
            count: this.editConsumableModel.count,
            safetyCount: this.editConsumableModel.safetyCount,
            price: this.editConsumableModel.price,
            summary: this.editConsumableModel.summary,
            referenceInformations: {
              referenceInformations: JSON.stringify(
                this.editConsumableModel.referenceInformations
              )
            }
          };
          updateConsumableApi(editConsumableModel).then(() => {
            Message.success("保存成功");
            if (step === 2) {
              this.hideAddReferenceInfoForm("edit");
            }
            this.hideEditConsumableContainer(formName);
            this.doSearch();
          });
        } else {
          if (step === 2) {
            this.breadcrumbs.pop();
            this.editConsumableStep = 1;
          }
        }
      });
    },
    submitAddConsumableForm(formName, step) {
      this.$refs[formName].validate(valid => {
        if (valid) {
          var addConsumableModel = {
            warehouseId: this.$store.state.curWarehouse.id,
            typeId: this.addConsumableModel.typeId[
              this.addConsumableModel.typeId.length - 1
            ],
            code: this.addConsumableModel.code,
            name: this.addConsumableModel.name,
            count: this.addConsumableModel.count,
            safetyCount: this.addConsumableModel.safetyCount,
            price: this.addConsumableModel.price,
            summary: this.addConsumableModel.summary,
            referenceInformations: {
              referenceInformations: JSON.stringify(
                this.addConsumableModel.referenceInformations
              )
            }
          };
          addConsumableApi(addConsumableModel).then(() => {
            Message.success("保存成功");
            if (step === 2) {
              this.hideAddReferenceInfoForm("add");
            }
            this.hideAddConsumableContainer(formName);
            this.doSearch();
          });
        } else {
          if (step === 2) {
            this.breadcrumbs.pop();
            this.addConsumableStep = 1;
          }
        }
      });
    },
    inbound(item) {
      this.inboundModel.id = item.id;
      this.inboundModel.warehouseId = item.warehouseId;
      this.inboundModel.type = 2;
      this.inboundModel.inboundCount = 0;
      this.inboundModel.inboundPrice = 0;
      this.isShowInboundDialog = true;
    },
    outbound(item) {
      if (item.count === 0) {
        Message.error("无法出库：当前消耗品数量为0");
        return;
      }
      this.outboundModel.id = item.id;
      this.outboundModel.warehouseId = item.warehouseId;
      this.outboundModel.outboundCount = 0;
      this.isShowOutboundDialog = true;
    },
    hideInboundDialog(formName) {
      this.inboundModel = {
        id: "",
        warehouseId: "",
        type: 2,
        inboundCount: 0,
        inboundPrice: 0,
        remarks: ""
      };
      this.$refs[formName].resetFields();
      this.isShowInboundDialog = false;
    },
    hideOutboundDialog(formName) {
      this.outboundModel = {
        id: "",
        warehouseId: "",
        type: 0,
        outboundCount: 0,
        remarks: ""
      };
      this.$refs[formName].resetFields();
      this.isShowOutboundDialog = false;
    },
    resetInboundModel(formName) {
      this.hideInboundDialog(formName);
    },
    resetOutboundModel(formName) {
      this.hideOutboundDialog(formName);
    },
    doInbound(formName) {
      this.$refs[formName].validate(valid => {
        if (valid) {
          inboundApi(this.inboundModel).then(res => {
            Message.success("入库成功");
            this.consumables.forEach(item => {
              if (item.id === this.inboundModel.id) {
                item.count += this.inboundModel.inboundCount;
                item.price = res.data.price;
                item.totalPrice = res.data.totalPrice;
                item.inboundBatchs = [];
                res.data.inboundBatchs.forEach(batch => {
                  item.inboundBatchs.push(batch);
                });
                return;
              }
            });
            this.hideInboundDialog(formName);
          });
        }
      });
    },
    doOutbound(formName) {
      this.$refs[formName].validate(valid => {
        if (valid) {
          outboundApi(this.outboundModel).then(res => {
            Message.success("出库成功");
            this.consumables.forEach(item => {
              if (item.id === this.outboundModel.id) {
                item.count -= this.outboundModel.outboundCount;
                item.totalPrice = res.data.totalPrice;
                item.inboundBatchs = [];
                res.data.inboundBatchs.forEach(batch => {
                  item.inboundBatchs.push(batch);
                });
                return;
              }
            });
            this.hideOutboundDialog(formName);
          });
        }
      });
    },
    exportConsumables() {
      var url =
        base.url +
        "/cConsumable/exportConsumables?warehouseId=" +
        this.$store.state.curWarehouse.id +
        "&searchType=" +
        this.oldSearchCondition.searchType +
        "&searchStr=" +
        this.oldSearchCondition.searchStr +
        "&startTime=" +
        this.oldSearchCondition.startTime +
        "&endTime=" +
        this.oldSearchCondition.endTime +
        "&isViewAlarmData=" +
        this.oldSearchCondition.isViewAlarmData;
      window.open(url);
    },
    async importRoles(data) {
      var errorDatas = {};
      data.forEach((item, index) => {
        var errorData = {};
        if (isNaN(item.count)) {
          var count = eval(item.count);
          if (isNaN(count)) {
            errorData.count = "数量错误";
          } else {
            item.count = count;
          }
        }
        if (isNaN(item.safetyCount)) {
          var safetyCount = eval(item.safetyCount);
          if (isNaN(safetyCount)) {
            errorData.safetyCount = "安全在工数错误";
          } else {
            item.safetyCount = safetyCount;
          }
        }
        if (errorData.count || errorData.safetyCount) {
          errorDatas[index] = errorData;
        }
      });
      if (errorDatas[0]) {
        return Promise.reject(errorDatas);
      }
      await importConsumablesApi({
        warehouseId: this.$store.state.curWarehouse.id,
        excelData: JSON.stringify(data)
      }).then(res => {
        if (res.success) {
          return Promise.resolve();
        } else {
          return Promise.reject(res.data);
        }
      });
    },
    handleCloseImportRolesDialog() {},
    handleFinishImportRolesDialog() {
      this.doSearch();
    },
    handleOpen() {
      this.isShowImportDialog = true;
    },
    // 生成标准基准信息模板对应列
    generateBaseReferenceInfomationTemplateData(referenceInformations) {
      var baseData = {
        subCategory: "",
        applicableProject0: "",
        applicableProject1: "",
        applicableProject2: "",
        applicableModels: "",
        itemType1: "",
        itemType2: "",
        location: "",
        unit: "",
        safetyStock: "",
        currentInventory: "",
        outOfStockLastWeek: "",
        outOfTheMonth: "",
        unitPrice: "",
        inventoryAmount: "",
        industoryBody: "",
        purchasingCycle: ""
      };

      if (!referenceInformations || referenceInformations.length === 0) {
        return baseData;
      }

      referenceInformations.forEach(item => {
        switch (item.referenceKey) {
          case "位置":
            baseData.location = item.referenceValue;
            break;
          case "子类（小类）":
            baseData.subCategory = item.referenceValue;
            break;
          case "适用工程":
            baseData.applicableProject0 = item.referenceValue;
            break;
          case "适用工程1":
            baseData.applicableProject1 = item.referenceValue;
            break;
          case "适用工程2":
            baseData.applicableProject2 = item.referenceValue;
            break;
          case "适用机种":
            baseData.applicableModels = item.referenceValue;
            break;
          case "子类1":
            baseData.itemType1 = item.referenceValue;
            break;
          case "子类2":
            baseData.itemType2 = item.referenceValue;
            break;
          case "单位":
            baseData.unit = item.referenceValue;
            break;
          case "安全库存":
            baseData.safetyStock = item.referenceValue;
            break;
          case "当前库存":
            baseData.currentInventory = item.referenceValue;
            break;
          case "前周出库":
            baseData.outOfStockLastWeek = item.referenceValue;
            break;
          case "前月出库":
            baseData.outOfTheMonth = item.referenceValue;
            break;
          case "单价":
            baseData.unitPrice = item.referenceValue;
            break;
          case "库存金额":
            baseData.inventoryAmount = item.referenceValue;
            break;
          case "业体":
            baseData.industoryBody = item.referenceValue;
            break;
          case "采购周期":
            baseData.purchasingCycle = item.referenceValue;
            break;
        }
      });

      return baseData;
    }
  }
};
</script>

<style lang="less">
.consumables-grid-consumables-container {
  height: calc(100% - 140px);

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