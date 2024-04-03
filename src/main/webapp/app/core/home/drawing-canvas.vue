<template>
  <div class="canvas-container">
    <canvas ref="canvas" :height="height" :width="width" @mousemove="updateCursorPosition"></canvas>
    <div v-if="isErasing" ref="eraserCursor" class="eraser-cursor"></div>
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
.canvas-container {
  position: relative;
  display: inline-block;
}

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
