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
        <div v-if="quizEndMessage" class="alert alert-info">
          {{ quizEndMessage }}
        </div>

        <div v-if="question && authenticated">
          <h2>{{ question.questionText }}</h2>
          <div v-if="question.image" class="image-container">
            <img :src="'data:image/png;base64,' + question.image" alt="Question image" class="question-image">
          </div>

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

          <b-button
            @click="submitAnswer"
            variant="primary"
            class="mt-3"
            :disabled="!selectedAnswer">
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
  justify-content: center;
  align-items: center;
  height: 100%; /* Adjust as needed */
}

.question-image {
  max-width: 100%;
  height: auto;
}
</style>
