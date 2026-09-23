<template>
  <el-table
    ref="scoreViewTableRef"
    v-loading="loading"
    :data="tableDataList"
    highlight-current-row
    border
  >
    <el-table-column
      type="index"
      width="60"
      label="序号"
      align="center"
      :fixed="true"
      v-if="columns && columns.length > 0"
    />
    <template v-for="column in columns" :key="column.prop">
      <template v-if="column.children">
        <el-table-column
          :key="column.prop"
          :label="column.label"
          :prop="column.prop"
          :width="column.width"
          :fixed="column.fixed"
          :sortable="column.sortable"
          :show-overflow-tooltip="true"
          align="center"
        >
          <el-table-column
            v-for="child in column.children"
            :key="child.prop"
            :label="child.label"
            :prop="child.prop"
            :width="child.width"
            :fixed="child.fixed"
            :sortable="child.sortable"
            :show-overflow-tooltip="true"
            align="center"
          >
            <template #default="scope">
              <div
                v-html="scope.row[child.prop]"
                style="display: flex; justify-content: center"
              ></div>
            </template>
          </el-table-column>
        </el-table-column>
      </template>
      <el-table-column
        v-else
        :key="column.prop"
        :label="column.label"
        :prop="column.prop"
        :width="column.width"
        :fixed="column.fixed"
        :sortable="column.sortable"
        :show-overflow-tooltip="true"
        align="center"
      >
        <template #default="scope">
          <div
            v-html="scope.row[column.prop]"
            style="display: flex; justify-content: center"
          ></div>
        </template>
      </el-table-column>
    </template>
  </el-table>
</template>
<script setup lang="ts">
import type { PropType } from "vue";

const props = defineProps({
  loading: {
    type: Boolean,
    required: true,
  },
  tableDataList: {
    type: Array as PropType<Record<string, any>[]>,
    default: () => [],
  },
  columns: {
    type: Array as PropType<Column[]>,
    default: () => [],
  },
});
</script>
