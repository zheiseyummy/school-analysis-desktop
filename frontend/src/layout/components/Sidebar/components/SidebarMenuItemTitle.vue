<template>
  <el-icon v-if="icon && icon.startsWith('el-icon')" class="sub-el-icon">
    <component :is="icon.replace('el-icon-', '')" />
  </el-icon>
  <svg-icon v-else-if="icon" :icon-class="icon" />
  <svg-icon v-else icon-class="menu" />
  <span v-if="title" class="ml-1">{{ translateRouteTitle(title) }}</span>
</template>

<script setup lang="ts">
import { translateRouteTitle } from "@/utils/i18n";

defineProps({
  icon: {
    type: String,
    default: "",
  },
  title: {
    type: String,
    default: "",
  },
});
</script>

<style lang="scss" scoped>
.sub-el-icon {
  width: 18px !important;
  height: 18px !important;
  margin-right: 7px !important;
  font-size: 17px !important;
  color: currentcolor;
  vertical-align: -3px;
  transition: transform 0.2s ease, color 0.2s ease;
}

.el-menu-item:hover .sub-el-icon,
.el-sub-menu__title:hover .sub-el-icon {
  transform: translateY(-1px);
}

.hideSidebar {
  .el-sub-menu,
  .el-menu-item {
    .svg-icon,
    .sub-el-icon {
      margin-left: 20px;
    }
  }
}
</style>
