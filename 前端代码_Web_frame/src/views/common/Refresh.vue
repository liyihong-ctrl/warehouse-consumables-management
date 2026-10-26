<!-- 页面刷新用 -->
<template>
  <div></div>
</template>

<script>
export default {
  name: "refresh",
  beforeCreate() {
    this.$router.push({
      name: this.$route.params.name,
      params: {
        isShowAlarmConsumables: true
      }
    });
  }
};
</script>

<style>
</style>