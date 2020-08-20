<template>
  <div class="log-entry log-entry--unknown">
    <ToggleButton v-if="entry.entries"/>
    <div>{{ remaining }}</div>
  </div>
</template>

<script>
import ToggleButton from "./ToggleButton.vue";

export default {
  name: "LogEntry",
  components: {ToggleButton},
  props: {
    entry: Object,
  },
  computed: {
    remaining: function () {
      let r = {...this.entry};
      delete r.entries;
      return r;
    }
  }
};
</script>

<style>
</style>