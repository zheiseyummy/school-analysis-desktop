<template>
  <!-- 顶部导航栏 -->
  <div class="navbar-container">
    <!-- 导航栏左侧 -->
    <NavbarLeft />
  </div>
</template>

<style lang="scss" scoped>
.navbar-container {
  @apply flex-x-between;

  height: $navbar-height;
  background: var(--el-bg-color);
}
</style>
