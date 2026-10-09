<template>
  <slot v-if="!errorMessage" />
  <el-result v-else icon="error" title="页面加载失败" :sub-title="errorMessage">
    <template #extra>
      <el-button type="primary" @click="reloadPage">重新加载页面</el-button>
    </template>
  </el-result>
</template>

<script setup lang="ts">
const errorMessage = ref("");
const route = useRoute();

watch(() => route.fullPath, () => {
  errorMessage.value = "";
});

onErrorCaptured((error) => {
  errorMessage.value = error instanceof Error ? error.message : String(error);
  ElMessage.error("页面发生错误，详细信息已显示在页面中");
  return false;
});

function reloadPage() {
  window.location.reload();
}
</script>

<style scoped>
.el-result {
  min-height: 320px;
}
</style>
