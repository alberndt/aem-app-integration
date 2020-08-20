<template>
  <div class="log-entry log-entry--task">
    <ToggleButton v-if="entry.entries"/>
    <div class="log-entry--task__name" :title="entry.humanReadableTaskName">{{ entry.taskName }}</div>
    <div>{{ entry.status }}</div>
    <div>{{ entry.msg }}</div>
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
.log-entry--task {
  display: flex;
}

.log-entry--task > div {
  margin-right: calc(5px + 1vw);
}

.log-entry--task__name {
  min-width: 20%;
}

</style>