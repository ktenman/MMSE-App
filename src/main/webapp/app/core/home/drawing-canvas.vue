<template>
  <div>
    <canvas ref="canvas" :height="height" :width="width"></canvas>
    <div v-if="isErasing" :style="{ left: cursorX-300 + 'px', top: cursorY-100 + 'px' }" class="eraser-cursor"></div>
    <div class="mt-3">
      <b-button variant="primary" @click="toggleEraser">
        <font-awesome-icon :icon="isErasing ? 'pencil-alt' : 'eraser'" />
        {{ isErasing ? 'Draw' : 'Erase' }}
      </b-button>
      <b-button variant="danger" @click="clearCanvas">
        <font-awesome-icon icon="trash" />
        Clear
      </b-button>
    </div>
  </div>
</template>

<script lang="ts" src="./drawing-canvas.component.ts"></script>

<style scoped>
canvas {
  border: 1px solid #000;
}

button {
  margin-right: 10px;
}

.eraser-cursor {
  position: absolute;
  width: 30px;
  height: 30px;
  border-radius: 50%;
  background-color: #ccc;
  pointer-events: none;
  z-index: 999;
  transform: translate(-50%, -50%);
}
</style>
