<template>
  <div class="log-entry-block">
    <LogEntry :entry="entry"/>
    <div class="log-entry-block__sub-entries" v-for="(subEntry, index) in entry.entries" :key="index">
      <LogEntryBlock :entry="subEntry"/>
    </div>
  </div>
</template>

<script>
import LogEntry from "./LogEntry.vue";

export default {
  name: "LogEntryBlock",
  components: {
    LogEntry
  },
  props: {
    entry: Object,
  },
};
</script>


<style scoped>


details {
  margin: 1rem;
}

summary {
  font-weight: bold;
}
</style>