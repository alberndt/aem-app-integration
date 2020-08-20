<template>
  <ResourceLogEntry v-if="entry.type == 'resource'" :entry="entry"/>
  <TaskLogEntry v-else-if="entry.type == 'task'" :entry="entry"/>
  <div v-else>{{ remaining }}</div>
</template>

<script>
import ResourceLogEntry from "./ResourceLogEntry.vue";
import TaskLogEntry from "./TaskLogEntry.vue";

export default {
  name: "LogEntry",
  components: {
    ResourceLogEntry, TaskLogEntry
  },
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


<style scoped>

</style>