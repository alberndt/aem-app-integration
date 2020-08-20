<template>
  <ResourceLogEntry v-if="entry.type == 'resource'" :entry="entry"/>
  <TaskLogEntry v-else-if="entry.type == 'task'" :entry="entry"/>
  <UnknownLogEntry v-else :entry="entry"/>
</template>

<script>
import ResourceLogEntry from "./ResourceLogEntry.vue";
import TaskLogEntry from "./TaskLogEntry.vue";
import UnknownLogEntry from "./UnknownLogEntry.vue";

export default {
  name: "LogEntry",
  components: {
    ResourceLogEntry, TaskLogEntry, UnknownLogEntry
  },
  props: {
    entry: Object,
  }
};
</script>


<style>
.log-entry {
  display: flex;
}
</style>