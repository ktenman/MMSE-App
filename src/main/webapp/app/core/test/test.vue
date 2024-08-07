<template>
  <div class="test">
    <div v-if="quizEndMessage">
      <div class="alert alert-info">
        {{ quizEndMessage }}
      </div>
      <div v-if="quizResults">
        <h4>Quiz Results (Incorrect Answers)</h4>
        <p>Below are the questions you answered incorrectly. Review the correct answers and your responses to understand
          your mistakes.</p>
        <p>Total time taken: {{ formatDuration(quizResults.duration) }}</p>
        <ul>
          <li v-for="(result, questionId) in incorrectAnswers" :key="questionId">
            <strong>{{ result.questionText }}</strong><br />
            Your answer: {{ result.userAnswer }}<br />
            Correct answer: {{ result.correctAnswer }}<br />
            <!--            Correct: {{ result.correct ? 'Yes' : 'No' }}<br />-->
            Points: {{ result.score }}/{{ result.maxScore }}
          </li>
        </ul>
      </div>
    </div>

    <div v-if="loading" class="loader-container">
      <span>Loading...</span>
    </div>

    <div v-if="question && !loading">
      <h3>{{ question.questionText }}</h3>

      <div v-if="question.image" class="image-container">
        <img :src="'data:image/png;base64,' + question.image" alt="Question image" class="question-image" />
      </div>

      <div v-if="question?.instructions?.length > 0" class="instructions">
        <strong>Instructions:</strong>
        <div class="markdown-instructions">
          <ul v-for="(instruction, index) in question.instructions" :key="index">
            <li>
              {{ instruction }}
            </li>
          </ul>
        </div>
      </div>

      <div v-if="question.questionType === QuestionType.MULTIPLE_CHOICE">
        <div class="row">
          <div v-for="(option, index) in question.answerOptions" :key="index" class="col-md-6">
            <b-button
              :pressed="selectedAnswer === option"
              class="w-100 p-3 mt-3 large-text capitalize"
              variant="outline-primary"
              @click="selectedAnswer = option"
            >
              {{ option.toLowerCase() }}
            </b-button>
          </div>
        </div>
      </div>

      <div v-if="question.questionType === QuestionType.SUBTRACTION_TASK">
        <div class="row">
          <div v-for="(option, index) in question.answerOptions" :key="index" class="col-md-6">
            <label class="col-form-label">{{ option.placeholder }}:</label>
            <input v-model="selectedAnswers[index]" :max="option.max" :min="option.min" class="form-control col-md-11"
                   type="number" />
          </div>
        </div>
      </div>

      <div v-if="question.questionType === QuestionType.TEXT_INPUT">
        <div class="row">
          <div class="col-md-6">
            <input v-model="selectedAnswer" v-focus="true" :min="1" class="form-control" required type="text"
                   @keyup.enter="submitAnswer" />
          </div>
        </div>
      </div>

      <div v-if="question.questionType === QuestionType.DRAWING">
        <div class="row">
          <div class="col-md-6">
            <drawing-canvas
              ref="drawingCanvas"
              :height="390"
              :question="question"
              :testEntity="testEntity"
              :width="640"
              @drawing-saved="updateDrawingFileName"
            ></drawing-canvas>
          </div>
        </div>
      </div>

      <div v-if="question.questionType === QuestionType.VOICE_INPUT">
        <div class="row">
          <div class="col-md-12">
            <div class="voice-recorder">
              <div class="button-group d-flex align-items-center">
                <b-button :disabled="isRecording" class="mt-6 mr-2" variant="primary" @click="startRecording">
                  <font-awesome-icon icon="microphone" />
                  Start Recording
                </b-button>
                <b-button :disabled="!isRecording" class="mt-6" variant="danger" @click="stopRecording">
                  <font-awesome-icon icon="stop" />
                  Stop Recording
                </b-button>
              </div>
              <div v-if="isRecording" class="recording-timer ml-3">
                <br />
                Recording: {{ recordingDuration }}s
                <br />
              </div>
              <div v-if="lastRecordedAudioUrl" class="audio-player mt-3">
                <p>To listen to your recorded audio, press the play button below:</p>
                <audio :src="lastRecordedAudioUrl" controls></audio>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div v-if="question.questionType === QuestionType.DRAG_AND_DROP">
        <div v-if="!isPaperPickedUp" :class="{ 'picked-up': isPaperPickedUp, folded: isPaperFolded }" class="paper"
             @click="pickUpPaper">
          <span>Paper</span>
        </div>
        <div
          v-if="isPaperPickedUp && !isPaperOnFloor"
          :class="{ 'picked-up': isPaperPickedUp, folded: isPaperFolded }"
          class="paper"
          draggable="true"
          @dragstart="startDragging"
        >
          <div class="paper-content">
            <span v-if="!isPaperFolded">Paper selected</span>
            <span v-else>Folded Paper</span>
          </div>
        </div>
        <b-button v-if="!isPaperFolded" :disabled="!isPaperPickedUp" class="mt-6 mr-2" variant="dark"
                  @click="foldPaper">
          <font-awesome-icon icon="book-open" />
          Fold Paper in Half
        </b-button>
        <div v-if="isPaperFolded" class="floor" @drop="putPaperOnFloor" @dragover.prevent>
          <span>Floor</span>
          <div v-if="isPaperOnFloor" :class="{ 'no-animation': noAnimation }" class="paper folded">
            <span>Folded Paper</span>
          </div>
        </div>
      </div>
      <div v-if="errorMessage">
        <p />
        <div class="alert alert-danger" role="alert">
          {{ errorMessage }}
          <button aria-label="Close" class="close" type="button" @click="closeErrorMessage">
            <span aria-hidden="true">&times;</span>
          </button>
        </div>
      </div>

      <div class="row">
        <div class="col-md-12">
          <b-button :disabled="isNextButtonDisabled() || loading" class="mt-3" variant="primary" @click="submitAnswer">
            Next Task
            <font-awesome-icon icon="arrow-right" />
          </b-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./test.component.ts"></script>

<style scoped>
.large-text {
  font-size: 1.25em;
}

.capitalize {
  text-transform: capitalize;
}

.image-container {
  display: flex;
  justify-content: left;
  align-items: center;
  height: 100%;
}

.question-image {
  max-width: 65%;
  height: auto;
}

.paper {
  width: 300px;
  height: 210px;
  background-color: #fcfcf1;
  border: 1px solid gray;
  cursor: pointer;
  margin-bottom: 20px;
  transition: width 1.5s, height 1s;
}

.paper span {
  font-size: 16px;
  text-align: center;
}

.paper.picked-up {
  width: 315px;
  height: 221px;
  background-color: #eeeee3;
  animation: pick-up 1s;
  border: 2px solid gray;
}

.paper.folded {
  width: 150px;
  height: 210px;
  animation: fold 1.5s;
}

.paper.folded.no-animation {
  animation: none;
}

.floor {
  width: 600px;
  height: 300px;
  border: 1px dashed black;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

.floor span {
  font-size: 16px;
  text-align: center;
}

@keyframes pick-up {
  0% {
    transform: translate3d(0, 0, 0);
  }
  100% {
    transform: translate3d(0, 0, 0) scale(1.02);
  }
}

@keyframes fold {
  0% {
    transform: scale(0.6) rotate(0deg);
  }
  50% {
    transform: scale(0.8) rotate(10deg);
  }
  100% {
    transform: scale(1) rotate(0deg);
  }
}

.markdown-instructions ul {
  padding-top: 0.5rem;
  list-style-type: disc;
  padding-left: 1.3rem;
  line-height: 0.4rem;
}

.markdown-instructions li {
  margin-bottom: 0.8rem;
}
</style>
