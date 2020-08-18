<template>
  <div id="app">
    <p>log: {{ log }}</p>
    <img alt="Vue logo" src="./assets/logo.png"/>
    <HelloWorld msg="Welcome to Your Vue.js App by"/>
  </div>
</template>

<script>
import HelloWorld from "./components/HelloWorld.vue";

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
  },
  components: {
    HelloWorld,
  },
};
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
}
</style>
