<template>
  <div id="app">
    <IntegrationLog v-for="(logRoot, index) in log" :key="index" :log="logRoot"/>
    <h3>log:</h3>
    <pre>{{ log }}</pre>
  </div>
</template>

<script>
import IntegrationLog from "./components/IntegrationLog.vue";

async function load(url) {
  console.log("load " + url);
  if (url) {
    return await fetch(url).then((response) => response.json());
  } else {
    return new Promise(function (resolve) {
      resolve({});
    });
  }
}

export default {
  name: "App",
  components: {
    IntegrationLog
  },
  props: {
    message: String,
  },
  created: function () {
    load(this.url).then((log) => (this.log = log));
  },
  data: function () {
    return {
      log: {},
    };
  },
  computed: {
    url: function () {
      return this.$root.url;
    },
  },
  watch: {
    url: function (val) {
      console.log("url changed to " + val);
      load(this.url).then((log) => (this.log = log));
    },
  }
};
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
  margin-top: 60px;
}
</style>
