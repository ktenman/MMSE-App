<template>
  <div class="home row">
    <div class="col-md-3">
      <span class="hipster img-fluid rounded"></span>
    </div>
    <div class="col-md-9">
      <h1 class="display-4" v-text="t$('home.title')"></h1>
      <p class="lead" v-text="t$('home.subtitle')"></p>

      <div v-if="authenticated">
        <!-- Show quiz end message when quiz ends -->
        <div v-if="quizState === QuizState.FINISHED">
          <div class="alert alert-info">
            {{ quizEndMessage }}
          </div>
          <button class="btn btn-primary" @click="retakeQuiz">Retake Quiz</button>
        </div>

        <!-- Conditionally show multiple choice or input fields based on question type -->
        <div v-if="loading" class="loader-container">
          <span>Loading...</span>
          <!-- Replace with your actual loader -->
        </div>

        <div v-if="quizState === QuizState.PATIENT_INFO">

          <!--          <drawing-canvas :width="800" :height="600"></drawing-canvas>-->

          <h2>Start a New Quiz</h2>
          <form @submit.prevent="startQuiz">
            <div class="form-group">
              <label for="name">Name:</label>
              <input id="name" v-model="patientProfile.name" class="form-control" required type="text">
            </div>
            <div class="form-group">
              <label for="patientId">Patient ID:</label>
              <input id="patientId" v-model="patientProfile.patientId" class="form-control" required type="text">
            </div>
            <button class="btn btn-primary" type="submit">Start Quiz</button>
          </form>
        </div>

        <div v-if="quizState === QuizState.ORIENTATION_QUESTIONS">
          <h2>Examiner Input: Orientation to Place Questions</h2>
          <form @submit.prevent="saveOrientationToPlaceCorrectAnswers">
            <div v-for="(question, index) in orientationToPlaceQuestions" :key="index">
              <h4>{{ question.questionText }}</h4>
              <div class="form-group">
                <label>Correct Answer:</label>
                <input v-model="question.correctAnswer" class="form-control" required
                       type="text" @blur="persistAnswer(question)">
              </div>
            </div>
            <button class="btn btn-primary" type="submit">Save Correct Answers</button>
          </form>
        </div>

        <div v-if="quizState === QuizState.ORIENTATION_ANSWERS">
          <h2>Examiner Input: Answer Options</h2>
          <form @submit.prevent="saveOrientationToPlaceAnswerOptions">
            <div v-for="(question, index) in orientationToPlaceQuestions" :key="index">
              <h4>{{ question.questionText }}</h4>
              <div class="form-group">
                <label>Correct Answer:</label>
                <input v-model="question.correctAnswer" class="form-control" disabled type="text">
              </div>
              <div class="form-group">
                <label>Answer Options (comma-separated):</label>
                <input v-model="question.answerOptions" class="form-control" required type="text">
              </div>
            </div>
            <button class="btn btn-primary" type="submit">Save Answer Options</button>
          </form>
        </div>

        <div v-if="quizState === QuizState.QUIZ && question && !loading">
          <h2>{{ question.questionText }}</h2>
          <div v-if="question.image" class="image-container">
            <img :src="'data:image/png;base64,' + question.image" alt="Question image" class="question-image" />
          </div>

          <div v-if="question.questionType === QuestionType.MULTIPLE_CHOICE">
            <div class="row">
              <div class="col-md-6" v-for="(option, index) in question.answerOptions" :key="index">
                <b-button
                  :pressed="selectedAnswer === option"
                  variant="outline-primary"
                  @click="selectedAnswer = option"
                  class="w-100 p-3 mt-3 large-text capitalize">
                  {{ option.toLowerCase() }}
                </b-button>
              </div>
            </div>
          </div>

          <div v-if="question.questionType === QuestionType.SUBTRACTION_TASK">
            <div class="row">
              <div class="col-md-6" v-for="(option, index) in question.answerOptions" :key="index">
                <input
                  v-model="selectedAnswers[index]"
                  v-focus="index === 0"
                  :max="option.max"
                  :min="option.min"
                  :placeholder="option.placeholder"
                  class="form-control"
                  type="number"
                />
              </div>
            </div>
          </div>

          <div v-if="question.questionType === QuestionType.TEXT_INPUT">
            <div class="row">
              <div class="col-md-6">
                <input
                  v-model="selectedAnswer"
                  v-focus="true"
                  :min="1"
                  class="form-control"
                  required
                  type="text"
                  @keyup.enter="submitAnswer"
                />
                <!-- Always focus this input when it's rendered -->
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
                    <audio ref="audioPlayer" :src="lastRecordedAudioUrl" controls></audio>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div v-if="question.questionType === QuestionType.DRAG_AND_DROP">
            <div v-if="!isPaperPickedUp" :class="{ 'picked-up': isPaperPickedUp, 'folded': isPaperFolded }" class="paper"
                 @click="pickUpPaper">
              <span>Paper</span>
            </div>
            <div
              v-if="isPaperPickedUp && !isPaperOnFloor"
              :class="{ 'picked-up': isPaperPickedUp, 'folded': isPaperFolded }"
              class="paper"
              draggable="true"
              @dragstart="startDragging">
              <div class="paper-content">
                <span v-if="!isPaperFolded">Paper picked</span>
                <span v-else>Folded Paper</span>
              </div>
            </div>
            <b-button v-if="!isPaperFolded" :disabled="!isPaperPickedUp" class="mt-6 mr-2" variant="dark"
                      @click="foldPaper">Fold Paper in Half
            </b-button>
            <div v-if="isPaperFolded" class="floor" @drop="putPaperOnFloor" @dragover.prevent>
              <span>Floor</span>
              <div v-if="isPaperOnFloor" :class="{ 'no-animation': noAnimation }" class="paper folded">
                <span>Folded Paper</span>
              </div>
            </div>
          </div>

          <b-button :disabled="isNextButtonDisabled() || loading" class="mt-3" variant="primary" @click="submitAnswer">
            Next Question
          </b-button>
        </div>

        <div class="alert alert-warning" v-if="!authenticated">
          <span v-text="t$('global.messages.info.authenticated.prefix')"></span>
          <a class="alert-link" v-on:click="openLogin()" v-text="t$('global.messages.info.authenticated.link')"></a
          ><span v-html="t$('global.messages.info.authenticated.suffix')"></span>
        </div>
        <div class="alert alert-warning" v-if="!authenticated">
          <span v-text="t$('global.messages.info.register.noaccount')"></span>&nbsp;
          <router-link class="alert-link" to="/register"
                       v-text="t$('global.messages.info.register.link')"></router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./home.component.ts"></script>
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
  height: 100%; /* Adjust as needed */
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
  animation: pick-up 1s;
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
  50% {
    transform: translate3d(20px, -40px, 20px);
  }
  100% {
    transform: translate3d(0, 0, 0);
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
</style>
