import Vue from 'vue'
import App from './App.vue'

Vue.config.productionTip = false

window.myapp = new Vue({
  render: h => h(App),
  data: function () {
    return {
      "url": "integration-log.json"
    }
  }
}).$mount('#app')
