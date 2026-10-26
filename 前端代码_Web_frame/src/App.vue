<template>
  <div id="app">
    <router-view v-if="isRouterAlive" />
  </div>
</template>

<script>
// import { Message } from "element-ui";

export default {
  name: "App",
  provide() {
    return {
      reload: this.reload,
      // initWebSocket: this.initWebSocket
    };
  },
  created() {
    // if (this.$store.state.userInfo && this.$store.state.userInfo.id) {
    //   this.initWebSocket();
    // }
  },
  data() {
    return {
      isRouterAlive: true,
      // ws: null, //建立的连接
      // lockReconnect: false, //是否真正建立连接
      // timeout: 10 * 1000, //10秒一次心跳
      // timeoutObj: null, //心跳心跳倒计时
      // serverTimeoutObj: null, //心跳倒计时
      // timeoutnum: null //断开 重连倒计时
    };
  },
  methods: {
    reload() {
      this.isRouterAlive = false;
      this.$nextTick(function() {
        this.isRouterAlive = true;
      });
    },
    // initWebSocket() {
    //   this.ws = new WebSocket(
    //     "ws://127.0.0.1:8888/imserver/" + this.$store.state.userInfo.id
    //   );
    //   this.ws.onopen = this.onopen;
    //   this.ws.onmessage = this.onmessage;
    //   this.ws.onclose = this.onclose;
    //   this.ws.onerror = this.onerror;
    // },
    // reconnect() {
    //   var that = this;
    //   if (that.lockReconnect) {
    //     return;
    //   }
    //   that.lockReconnect = true;
    //   that.timeoutnum && clearTimeout(that.timeoutnum);
    //   that.timeoutnum = setTimeout(function() {
    //     that.initWebSocket();
    //     that.lockReconnect = false;
    //   }, 5000);
    // },
    // reset() {
    //   clearTimeout(this.timeoutObj);
    //   clearTimeout(this.serverTimeoutObj);
    //   this.start();
    // },
    // start() {
    //   var self = this;
    //   if (!self.$store.state.userInfo || !self.$store.state.userInfo.id) {
    //     clearTimeout(this.timeoutObj);
    //     clearTimeout(this.serverTimeoutObj);
    //     return;
    //   }
    //   self.timeoutObj && clearTimeout(self.timeoutObj);
    //   self.serverTimeoutObj && clearTimeout(self.serverTimeoutObj);
    //   self.timeoutObj = setTimeout(function() {
    //     if (self.ws.readyState == 1) {
    //       if (self.$store.state.userInfo && self.$store.state.userInfo.id) {
    //         self.ws.send(
    //           JSON.stringify({
    //             toUserId: self.$store.state.userInfo.id,
    //             msg: "heartCheck"
    //           })
    //         );
    //       }
    //     } else {
    //       self.reconnect();
    //     }
    //     self.serverTimeoutObj = setTimeout(function() {
    //       self.ws.close();
    //     }, self.timeout);
    //   }, self.timeout);
    // },
    // onopen() {
    //   this.start();
    // },
    // onmessage(e) {
    //   var data = JSON.parse(e.data);
    //   switch (data.code) {
    //     case 1000:
    //     case 1001:
    //     case 1002:
    //       break;
    //     case 1100:
    //       Message.info(data.data.msg);
    //       break;
    //     case 1200:
    //     case 1300:
    //     case 1400:
    //       Message.info(data.data.msg);
    //       if (data.data.isForceOffline) {
    //         this.$store.commit("logout");
    //         this.$router.push("/");
    //       }
    //       break;
    //   }
    //   this.reset();
    // },
    // onclose() {},
    // onerror() {
    //   if (!this.$store.state.userInfo.id) {
    //     clearTimeout(this.timeoutObj);
    //     clearTimeout(this.serverTimeoutObj);
    //     return;
    //   }
    //   this.reconnect();
    // }
  }
};
</script>

<style lang="less">
#app {
  width: 100%;
  height: 100%;
  font-family: "Helvetica Neue", Helvetica, "PingFang SC", "Hiragino Sans GB",
    "Microsoft YaHei", "微软雅黑", Arial, sans-serif;
}
</style>