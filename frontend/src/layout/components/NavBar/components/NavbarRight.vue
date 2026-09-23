<template>
  <div class="flex">
    <template v-if="device !== 'mobile'">
      <div class="setting-item" @click="toggle">
        <svg-icon
          :icon-class="isFullscreen ? 'fullscreen-exit' : 'fullscreen'"
        />
      </div>

      <el-tooltip content="布局大小" effect="dark" placement="bottom">
        <size-select class="setting-item" />
      </el-tooltip>

      <lang-select class="setting-item" />
    </template>

    <div class="local-user flex-center h100% px-14px">
      <el-icon class="mr-6px"><UserFilled /></el-icon>
      <span>本地用户</span>
    </div>

    <template v-if="defaultSettings.showSettings">
      <div class="setting-item" @click="settingStore.settingsVisible = true">
        <svg-icon icon-class="setting" />
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { useAppStore, useSettingsStore } from "@/store";
import defaultSettings from "@/settings";

const appStore = useAppStore();
const settingStore = useSettingsStore();
const device = computed(() => appStore.device);
const { isFullscreen, toggle } = useFullscreen();
</script>

<style lang="scss" scoped>
.setting-item,
.local-user {
  min-width: 40px;
  height: $navbar-height;
  line-height: $navbar-height;
  color: var(--el-text-color);
  text-align: center;
}

.setting-item {
  display: inline-block;
  cursor: pointer;

  &:hover {
    background: rgb(0 0 0 / 10%);
  }
}

.layout-top,
.layout-mix {
  .setting-item,
  .local-user,
  .el-icon {
    color: var(--el-color-white);
  }
}

.dark .setting-item:hover {
  background: rgb(255 255 255 / 20%);
}
</style>
