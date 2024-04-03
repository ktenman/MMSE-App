import { defineComponent, onMounted, ref } from 'vue';

export default defineComponent({
  props: {
    width: {
      type: Number,
      default: 800
    },
    height: {
      type: Number,
      default: 600
    }
  },
  setup(props) {
    const canvas = ref<HTMLCanvasElement | null>(null);
    const isDrawing = ref(false);
    const isErasing = ref(false);
    const lineWidth = ref(5);
    const strokeStyle = ref('#000000');
    const eraserSize = ref(30);
    const width = ref(800);
    const height = ref(600);
    const cursorX = ref(0);
    const cursorY = ref(0);

    const startDrawing = (event: MouseEvent) => {
      isDrawing.value = true;
      draw(event);
    };

    const stopDrawing = () => {
      isDrawing.value = false;
      const context = canvas.value?.getContext('2d');
      if (context) {
        context.beginPath();
      }
    };

    const draw = (event: MouseEvent) => {
      if (!canvas.value) return;

      const rect = canvas.value.getBoundingClientRect();
      const x = event.clientX - rect.left;
      const y = event.clientY - rect.top;

      cursorX.value = event.clientX;
      cursorY.value = event.clientY;

      const context = canvas.value.getContext('2d');
      if (context) {
        context.lineWidth = isErasing.value ? eraserSize.value : lineWidth.value;
        context.strokeStyle = isErasing.value ? 'rgba(0, 0, 0, 1)' : strokeStyle.value;

        if (isErasing.value) {
          context.globalCompositeOperation = 'destination-out';
        } else {
          context.globalCompositeOperation = 'source-over';
        }

        if (isDrawing.value) {
          context.lineTo(x, y);
          context.stroke();
          context.beginPath();
          context.moveTo(x, y);
        }
      }
    };

    const toggleEraser = () => {
      isErasing.value = !isErasing.value;
    };

    const clearCanvas = () => {
      const context = canvas.value?.getContext('2d');
      if (context) {
        context.clearRect(0, 0, width.value, height.value);
      }
    };

    onMounted(() => {
      const context = canvas.value?.getContext('2d');
      if (context) {
        context.lineJoin = 'round';
        context.lineCap = 'round';
      }

      canvas.value?.addEventListener('mousedown', startDrawing);
      canvas.value?.addEventListener('mousemove', draw);
      canvas.value?.addEventListener('mouseup', stopDrawing);
      canvas.value?.addEventListener('mouseout', stopDrawing);
    });

    return {
      canvas,
      isErasing,
      lineWidth,
      strokeStyle,
      eraserSize,
      cursorX,
      cursorY,
      toggleEraser,
      clearCanvas
    };
  }
});
