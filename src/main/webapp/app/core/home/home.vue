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
          <b-button
            @click="retakeTest"
            variant="primary"
            class="mt-3"
            v-if="!question && authenticated"
          >
            Retake Test
          </b-button>
        </div>

        <!-- Conditionally show multiple choice or input fields based on question type -->

        <div v-if="loading" class="loader-container">
          <span>Loading...</span> <!-- Replace with your actual loader -->
        </div>

        <div v-if="question && authenticated && !loading">

          <h2>{{ question.questionText }}</h2>
          <div v-if="question.image" class="image-container">
            <img :src="'data:image/png;base64,' + question.image" alt="Question image" class="question-image">
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
                <input type="number" :min="option.min" :max="option.max" v-model="selectedAnswers[index]" :placeholder="option.placeholder"
                       v-focus="index === 0" class="form-control">
              </div>
            </div>
          </div>

          <div v-if="question.questionType === QuestionType.TEXT_INPUT">
            <div class="row">
              <div class="col-md-6">
                <input v-model="selectedAnswer"
                       v-focus="true"
                       :min="1"
                       class="form-control"
                       required
                       type="text"
                       @keyup.enter="submitAnswer"> <!-- Always focus this input when it's rendered -->
              </div>
            </div>
          </div>

          <div v-if="question.questionType === QuestionType.VOICE_INPUT">
            <div class="row">
              <div class="col-md-6">
                <div class="voice-recorder">
                  <button :disabled="isRecording" @click="startRecording">Start Recording</button>
                  <button :disabled="!isRecording" @click="stopRecording">Stop Recording</button>
                  <audio v-if="audioUrl" :src="audioUrl" controls></audio>
                </div>
              </div>
            </div>
          </div>

          <b-button
              @click="submitAnswer"
              variant="primary"
              class="mt-3"
              :disabled="isNextButtonDisabled() || loading">
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
