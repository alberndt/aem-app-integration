<template>
  <div class="integration-log">
    <h1>Integration Log</h1>
    <div class="integration-log__entries">
      <div v-for="(subEntry, index) in log.entries" :key="index">
        <LogEntryBlock :entry="subEntry"/>
      </div>
    </div>
  </div>
</template>

<script>
import LogEntryBlock from "./LogEntryBlock.vue";

export default {
  name: "IntegrationLog",
  components: {
    LogEntryBlock,
  },
  props: {
    log: Object,
  },
};
</script>

<style>
.integration-log__entries {
  border-right: 2px solid #666;
  border-bottom: 2px solid #666;
}


</style>