<template>
  <button class="btn btn-toggle" @click="toggle">+</button>
</template>

<script>
export default {
  name: "ToggleButton",
  methods: {
    toggle: function () {
      console.log("emit toggle");
      this.$parent.$parent.$emit("toggle");
    },
  },
};
</script>

<style>
.btn-toggle {
  height: 1rem;
  width: 1rem;
  margin-top: 0.17rem;
  margin-right: 0.5rem;
  border: solid 1px #ccc;
  padding: 0;
}
</style>