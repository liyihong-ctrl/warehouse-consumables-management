<template>
  <el-popover
    placement="top"
    width="160"
    v-model="visible"
    class="popover"
  >
    <p>确定删除此申请吗？</p>
    <div class="options">
      <el-button size="mini" type="text" @click="visible = false">取消</el-button>
      <el-button type="primary" size="mini" @click="handleOk">确定</el-button>
    </div>
    <el-button
      type="text"
      slot="reference"
      class="red"
    >
      删除
    </el-button>
  </el-popover>
</template>

<script>
export default {
    data() {
      return {
          visible: false
      }
    },
    methods: {
      handleOk() {
        this.visible = false
        this.$emit('ok')
      },
    },
}
</script>

<style lang="less" scoped>
.popover {
  margin-left: 10px;
}
.options {
  text-align: right;
  margin: 0
}
.red {
  color: #F56C6C;
}
</style>
