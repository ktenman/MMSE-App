import { defineComponent, onMounted, PropType, ref } from 'vue';
import { IQuestion } from '@/shared/model/question.model';
import { ITestEntity } from '@/shared/model/test-entity.model';
import TestService from '@/core/test/test.service';

interface Props {
  width: number;
  height: number;
  question: IQuestion | null;
  testEntity: ITestEntity;
}

export default defineComponent({
  name: 'DrawingCanvas',
  props: {
    width: {
      type: Number,
      default: 800
    },
    height: {
      type: Number,
      default: 600
    },
    question: {
      type: Object as PropType<IQuestion | null>,
      required: true
    },
    testEntity: {
      type: Object as PropType<ITestEntity>,
      required: true
    },
  },
  emits: ['drawing-saved'],
  setup(props: Props, { emit }) {
    const canvas = ref<HTMLCanvasElement | null>(null);
    const isDrawing = ref(false);
    const isErasing = ref(false);
    const lineWidth = ref(3);
    const strokeStyle = ref('#000000');
    const eraserSize = ref(30);
    const width = ref(800);
    const height = ref(600);
    const cursorX = ref(0);
    const cursorY = ref(0);
    const testService = new TestService();
    const isDrawingSaved = ref(false);

    const saveDrawing = async () => {
      if (canvas.value) {
        // const dataURL = canvas.value.toDataURL('image/png');
        // const binaryData = atob(dataURL.split(',')[1]);
        // const arrayBuffer = new ArrayBuffer(binaryData.length);
        // const uint8Array = new Uint8Array(arrayBuffer);
        // for (let i = 0; i < binaryData.length; i++) {
        //   uint8Array[i] = binaryData.charCodeAt(i);
        // }
        // const blob = new Blob([uint8Array], { type: 'image/png' });

        const tempCanvas = document.createElement('canvas');
        tempCanvas.width = canvas.value.width;
        tempCanvas.height = canvas.value.height;
        const tempContext = tempCanvas.getContext('2d');
        if (tempContext) {
          tempContext.fillStyle = '#ffffff'; // White background
          tempContext.fillRect(0, 0, tempCanvas.width, tempCanvas.height);
          tempContext.drawImage(canvas.value, 0, 0);
          const dataURL = tempCanvas.toDataURL('image/png');
          const binaryData = atob(dataURL.split(',')[1]);
          const arrayBuffer = new ArrayBuffer(binaryData.length);
          const uint8Array = new Uint8Array(arrayBuffer);
          for (let i = 0; i < binaryData.length; i++) {
            uint8Array[i] = binaryData.charCodeAt(i);
          }
          const blob = new Blob([uint8Array], { type: 'image/png' });
          const fileName = await testService.sendImageToServer(blob, props.question?.questionId, props.testEntity.id);
          emit('drawing-saved', fileName);
          isDrawingSaved.value = true;
        }
      }
    };

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

    const eraserCursor = ref<HTMLElement | null>(null);

    const updateCursorPosition = (event: MouseEvent) => {
      if (eraserCursor.value) {
        eraserCursor.value.style.setProperty('--cursor-x', `${event.offsetX}px`);
        eraserCursor.value.style.setProperty('--cursor-y', `${event.offsetY}px`);
        eraserCursor.value.style.left = 'var(--cursor-x)';
        eraserCursor.value.style.top = 'var(--cursor-y)';
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
      clearCanvas,
      eraserCursor,
      updateCursorPosition,
      isDrawingSaved,
      saveDrawing
    };
  },
});
