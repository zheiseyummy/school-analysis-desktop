<script setup lang="ts">
import { createLocalBackup, listLocalBackups, restoreLocalBackup } from "@/api/backup";

const backups = ref<{ fileName: string }[]>([]);
const loading = ref(false);

async function loadBackups() {
  loading.value = true;
  try {
    const { data } = await listLocalBackups();
    backups.value = (data ?? []).map((fileName: string) => ({ fileName }));
  } finally {
    loading.value = false;
  }
}

async function createBackup() {
  await createLocalBackup();
  ElMessage.success("数据库备份已创建");
  await loadBackups();
}

async function restoreBackup(fileName: string) {
  await ElMessageBox.confirm(
    `恢复“${fileName}”会覆盖当前本地数据库，是否继续？`,
    "确认恢复备份",
    { type: "warning", confirmButtonText: "确认恢复", cancelButtonText: "取消" }
  );
  await restoreLocalBackup(fileName);
  ElMessage.success("数据库已恢复，请刷新页面");
}

function displayName(fileName: string) {
  return fileName.replace(/^school_/, "").replace(/\.db$/, "").replace("_", " ");
}

onMounted(loadBackups);
</script>

<template>
  <div class="app-container">
    <el-card shadow="never">
      <template #header>
        <div class="card-header"><span>数据备份</span><span class="tip">自动保留最近 30 个本地 SQLite 备份</span></div>
      </template>
      <div class="toolbar">
        <el-button type="primary" :loading="loading" @click="createBackup"><i-ep-folder-add />立即备份</el-button>
        <el-button :loading="loading" @click="loadBackups"><i-ep-refresh />刷新列表</el-button>
        <span class="tip">备份文件仅保存在本机 data/backup 目录</span>
      </div>
      <el-table v-loading="loading" :data="backups" border>
        <el-table-column type="index" label="#" width="70" />
        <el-table-column label="备份时间"><template #default="scope">{{ displayName(scope.row.fileName) }}</template></el-table-column>
        <el-table-column label="文件名"><template #default="scope">{{ scope.row.fileName }}</template></el-table-column>
        <el-table-column label="操作" width="130"><template #default="scope"><el-button type="warning" link @click="restoreBackup(scope.row.fileName)"><i-ep-refresh-left />恢复</el-button></template></el-table-column>
        <template #empty><el-empty description="暂无备份，点击立即备份创建" /></template>
      </el-table>
    </el-card>
  </div>
</template>

<style scoped>
.card-header,.toolbar{display:flex;align-items:center;justify-content:space-between}.toolbar{justify-content:flex-start;gap:10px;margin-bottom:16px}.tip{color:var(--el-text-color-secondary);font-size:13px}
</style>
