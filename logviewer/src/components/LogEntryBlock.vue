<template>
  <div class="log-entry-block">
    <div class="log-entry-block__line">
      <LogEntry :entry="entry" v-on:toggle="toggle"/>
    </div>
    <div v-if="!collapsed" class="log-entry-block__sub-entries">
      <LogEntryBlock v-for="(subEntry, index) in entry.entries"
                     :key="index" :entry="subEntry"/>
    </div>
  </div>
</template>

<script>
import LogEntry from "./LogEntry.vue";

export default {
  name: "LogEntryBlock",
  components: {
    LogEntry,
  },
  props: {
    entry: Object,
  },
  data: function () {
    return {
      collapsed: false
    };
  },
  // created() {
  //   this.$on("toggle", (m) => {
  //     console.log("receiv toggle " + m);
  //   });
  // },
  methods: {
    toggle: function () {
      console.log("toggle was received");
      this.collapsed = !this.collapsed;
    },
  },
};
</script>


<style>
.log-entry-block {
  border-top: 1px solid #666;
  border-left: 1px solid #666;
}

.log-entry-block__line {
  padding: 5px 10px 4px 10px;
}

.log-entry-block__sub-entries {
  padding-left: 30px;
}
</style>