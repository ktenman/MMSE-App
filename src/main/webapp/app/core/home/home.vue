<template>
  <div class="home row">
    <div class="col-md-2">
      <span class="hipster img-fluid rounded"></span>
    </div>
    <div class="col-md-10">
      <h2 class="display-4" v-text="t$('home.title')"></h2>
      <p class="lead" v-text="t$('home.subtitle')"></p>

      <div v-if="authenticated">
        <div v-if="loading" class="loader-container">
          <span>Loading...</span>
        </div>

        <div v-if="quizState === QuizState.PATIENT_INFO">
          <h2>Create a New Quiz</h2>
          <form @submit.prevent="startQuiz">
            <div class="form-group">
              <label for="name">Name:</label>
              <input id="name" v-model="patientProfile.name" class="form-control" required type="text" />
            </div>
            <div class="form-group">
              <label for="patientId">Patient ID:</label>
              <input id="patientId" v-model="patientProfile.patientId" class="form-control" required type="text" />
            </div>
            <b-button type="submit" variant="primary">
              <font-awesome-icon icon="save" />
              Save and Continue
            </b-button>
          </form>
        </div>

        <div v-if="quizState === QuizState.ORIENTATION_QUESTIONS">
          <h2>Examiner Input: Orientation to Place Questions</h2>
          <form @submit.prevent="saveOrientationToPlaceCorrectAnswers">
            <div v-for="(question, index) in orientationToPlaceQuestions" :key="index">
              <h4>{{ question.questionText }}</h4>
              <div class="form-group">
                <label>Correct Answer:</label>
                <input v-model="question.correctAnswer" class="form-control" required type="text"
                       @blur="persistAnswer(question)" />
              </div>
            </div>
            <b-button class="mt-6 mr-2" variant="info" @click="navigateBack">
              <font-awesome-icon icon="arrow-left" />
              Back
            </b-button>
            <b-button class="mt-6" type="submit" variant="primary">
              <font-awesome-icon icon="save" />
              Save Correct Answers
            </b-button>
          </form>
        </div>

        <div v-if="quizState === QuizState.ORIENTATION_ANSWERS">
          <h2>Examiner Input: Answer Options</h2>
          <form @submit.prevent="saveOrientationToPlaceAnswerOptions">
            <div v-for="(question, index) in orientationToPlaceQuestions" :key="index">
              <h4>{{ question.questionText }}</h4>
              <div class="form-group">
                <label>Correct Answer:</label>
                <input v-model="question.correctAnswer" class="form-control" disabled type="text" />
              </div>
              <div class="form-group">
                <label>Answer Options (comma-separated):</label>
                <input v-model="question.answerOptions" class="form-control" required type="text" />
              </div>
            </div>
            <div v-if="errorMessage" class="alert alert-danger" role="alert">
              {{ errorMessage }}
              <button aria-label="Close" class="close" type="button" @click="closeErrorMessage">
                <span aria-hidden="true">&times;</span>
              </button>
            </div>
            <b-button class="mt-6 mr-2" variant="info" @click="navigateBack">
              <font-awesome-icon icon="arrow-left" />
              Back
            </b-button>
            <b-button class="mt-6" type="submit" variant="primary">
              <font-awesome-icon icon="save" />
              Save Answer Options
            </b-button>
          </form>
        </div>

        <div v-if="quizState === QuizState.SHOW_TEST_LINK">
          <p>Please click the following link to continue the test:</p>
          <div class="button-group d-flex align-items-center">
            <router-link
              id="testLinkInput"
              :to="{ name: 'TestView', params: { testEntityHash: testEntity.hash } }"
              class="mt-6 mr-2"
              target="_blank"
            >
              {{ testLink }}
            </router-link>
            <b-button class="mt-6 mr-2" variant="outline-secondary" @click="copyTestLink">
              <font-awesome-icon icon="copy"></font-awesome-icon>
              Copy
            </b-button>
          </div>
          <p />
          <b-button class="mt-6" variant="primary" @click="createNewQuiz">
            <font-awesome-icon icon="plus"></font-awesome-icon>
            Create new Quiz
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
.paper span {
  font-size: 16px;
  text-align: center;
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
