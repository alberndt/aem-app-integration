<template>
  <div class="log-entry log-entry--details">
    <ToggleButton v-if="entry.entries"/>
    <div>{{ entry.status || "no status" }}</div>
    <div>{{ entry.message || "no message" }}</div>
  </div>
</template>

<script>
import ToggleButton from "./ToggleButton.vue";

export default {
  name: "LogEntry",
  components: {ToggleButton},
  props: {
    entry: Object,
  }
};
</script>

<style>
.log-entry--details {
  display: flex;
}

.log-entry--details > div {
  margin-right: calc(5px + 1vw);
}


</style>