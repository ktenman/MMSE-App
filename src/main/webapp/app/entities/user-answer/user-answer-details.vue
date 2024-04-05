<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="userAnswer">
        <h2 class="jh-entity-heading" data-cy="userAnswerDetailsHeading">
          <span v-text="t$('mmseApp.userAnswer.detail.title')"></span> {{ userAnswer.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('mmseApp.userAnswer.answerText')"></span>
          </dt>
          <dd>
            <span>{{ userAnswer.answerText }}</span>
          </dd>
          <dt>
            <span v-text="t$('mmseApp.userAnswer.createdAt')"></span>
          </dt>
          <dd>
            <span v-if="userAnswer.createdAt">{{ formatDateLong(userAnswer.createdAt) }}</span>
          </dd>
          <dt>
            <span v-text="t$('mmseApp.userAnswer.updatedAt')"></span>
          </dt>
          <dd>
            <span v-if="userAnswer.updatedAt">{{ formatDateLong(userAnswer.updatedAt) }}</span>
          </dd>
          <dt>
            <span v-text="t$('mmseApp.userAnswer.questionId')"></span>
          </dt>
          <dd>
            <span v-text="t$('mmseApp.QuestionId.' + userAnswer.questionId)"></span>
          </dd>
          <dt>
            <span v-text="t$('mmseApp.userAnswer.testEntity')"></span>
          </dt>
          <dd>
            <div v-if="userAnswer.testEntity">
              <router-link :to="{ name: 'TestEntityView', params: { testEntityId: userAnswer.testEntity.id } }"
              >{{ userAnswer.testEntity.id }}
              </router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info"
                data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link
          v-if="userAnswer.id"
          :to="{ name: 'UserAnswerEdit', params: { userAnswerId: userAnswer.id } }"
          custom
          v-slot="{ navigate }"
        >
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span
            v-text="t$('entity.action.edit')"></span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./user-answer-details.component.ts"></script>
