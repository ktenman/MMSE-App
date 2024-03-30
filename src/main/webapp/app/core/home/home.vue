<template>
  <div class="home row">
    <div class="col-md-3">
      <span class="hipster img-fluid rounded"></span>
    </div>
    <div class="col-md-9">
      <h1 class="display-4" v-text="t$('home.title')"></h1>
      <p class="lead" v-text="t$('home.subtitle')"></p>

      <div>
        <!-- Show quiz end message when quiz ends -->

        <div>
          <div v-if="quizEndMessage" class="alert alert-info">
            {{ quizEndMessage }}
          </div>
          <b-button v-if="!question && authenticated" class="mt-3" variant="primary" @click="retakeTest"> Retake Test
          </b-button>
        </div>

        <!-- Conditionally show multiple choice or input fields based on question type -->

        <div v-if="loading" class="loader-container">
          <span>Loading...</span>
          <!-- Replace with your actual loader -->
        </div>

        <div v-if="question && authenticated && !loading">
          <h2>{{ question.questionText }}</h2>
          <div v-if="question.image" class="image-container">
            <img :src="'data:image/png;base64,' + question.image" alt="Question image" class="question-image" />
          </div>

          <!-- Render this div if question type is multiple choice -->
          <div v-if="question.questionType === QuestionType.MULTIPLE_CHOICE">
            <div class="row">
              <div class="col-md-6" v-for="(option, index) in question.answerOptions" :key="index">
                <b-button
                  :pressed="selectedAnswer === option"
                  variant="outline-primary"
                  @click="selectedAnswer = option"
                  class="w-100 p-3 mt-3 large-text capitalize"
                >
                  {{ option.toLowerCase() }}
                </b-button>
              </div>
            </div>
          </div>

          <!-- Render this div if question type is subtraction task -->
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
                    <div v-if="isRecording" class="recording-timer ml-3">{{ recordingDuration }}s</div>
                  </div>
                  <div v-if="lastRecordedAudioUrl" class="audio-player mt-3">
                    <p>To listen to your recorded audio, press the play button below:</p>
                    <audio ref="audioPlayer" :src="lastRecordedAudioUrl" controls></audio>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <b-button :disabled="isNextButtonDisabled() || loading" class="mt-3" variant="primary" @click="submitAnswer">
            Next
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
</style>
