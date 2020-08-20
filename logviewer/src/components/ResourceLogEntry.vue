<template>
  <div class="log-entry log-entry--resource">
    <div :title="entry.url">
      <span class="log-entry__name">{{ entry.name }}</span>
      <br/>
      <span class="log-entry__path">{{ entry.path }}</span>
    </div>
    <div>{{ entry.status }}</div>
    <div>{{ entry.loadStatus }}</div>
  </div>
</template>

<script>
export default {
  name: "LogEntry",
  props: {
    entry: Object,
  },
};
</script>

<style scoped>
.log-entry--resource {
  display: flex;
}

.log-entry__name {
  font-weight: bold;
}

.log-entry__path {
  color: #666;
  font-size: 85%;
  display: inline-block;
  height: 1.2em;
  max-width: 350px;
  overflow: auto;
}
</style>