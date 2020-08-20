<template>
  <div id="app">

    <h1>Log</h1>
    <LogEntryBlock :entry="log"/>
    <p>done</p>
    <p>log: {{ log }}</p>
  </div>
</template>

<script>
import LogEntryBlock from "./components/LogEntryBlock.vue";

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
    LogEntryBlock
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
