<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" role="form" novalidate v-on:submit.prevent="save()">
        <h2
          id="mmseApp.userAnswer.home.createOrEditLabel"
          data-cy="UserAnswerCreateUpdateHeading"
          v-text="t$('mmseApp.userAnswer.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group" v-if="userAnswer.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="userAnswer.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('mmseApp.userAnswer.answerText')" for="user-answer-answerText"></label>
            <input
              type="text"
              class="form-control"
              name="answerText"
              id="user-answer-answerText"
              data-cy="answerText"
              :class="{ valid: !v$.answerText.$invalid, invalid: v$.answerText.$invalid }"
              v-model="v$.answerText.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('mmseApp.userAnswer.createdAt')" for="user-answer-createdAt"></label>
            <div class="d-flex">
              <input
                id="user-answer-createdAt"
                data-cy="createdAt"
                type="datetime-local"
                class="form-control"
                name="createdAt"
                :class="{ valid: !v$.createdAt.$invalid, invalid: v$.createdAt.$invalid }"
                required
                :value="convertDateTimeFromServer(v$.createdAt.$model)"
                @change="updateInstantField('createdAt', $event)"
              />
            </div>
            <div v-if="v$.createdAt.$anyDirty && v$.createdAt.$invalid">
              <small class="form-text text-danger" v-for="error of v$.createdAt.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('mmseApp.userAnswer.updatedAt')" for="user-answer-updatedAt"></label>
            <div class="d-flex">
              <input
                id="user-answer-updatedAt"
                data-cy="updatedAt"
                type="datetime-local"
                class="form-control"
                name="updatedAt"
                :class="{ valid: !v$.updatedAt.$invalid, invalid: v$.updatedAt.$invalid }"
                :value="convertDateTimeFromServer(v$.updatedAt.$model)"
                @change="updateInstantField('updatedAt', $event)"
              />
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('mmseApp.userAnswer.questionId')" for="user-answer-questionId"></label>
            <select
              class="form-control"
              name="questionId"
              :class="{ valid: !v$.questionId.$invalid, invalid: v$.questionId.$invalid }"
              v-model="v$.questionId.$model"
              id="user-answer-questionId"
              data-cy="questionId"
              required
            >
              <option
                v-for="questionId in questionIdValues"
                :key="questionId"
                v-bind:value="questionId"
                v-bind:label="t$('mmseApp.QuestionId.' + questionId)"
              >
                {{ questionId }}
              </option>
            </select>
            <div v-if="v$.questionId.$anyDirty && v$.questionId.$invalid">
              <small class="form-text text-danger" v-for="error of v$.questionId.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('mmseApp.userAnswer.testEntity')" for="user-answer-testEntity"></label>
            <select
              class="form-control"
              id="user-answer-testEntity"
              data-cy="testEntity"
              name="testEntity"
              v-model="userAnswer.testEntity"
              required
            >
              <option v-if="!userAnswer.testEntity" v-bind:value="null" selected></option>
              <option
                v-bind:value="
                  userAnswer.testEntity && testEntityOption.id === userAnswer.testEntity.id ? userAnswer.testEntity : testEntityOption
                "
                v-for="testEntityOption in testEntities"
                :key="testEntityOption.id"
              >
                {{ testEntityOption.id }}
              </option>
            </select>
          </div>
          <div v-if="v$.testEntity.$anyDirty && v$.testEntity.$invalid">
            <small class="form-text text-danger" v-for="error of v$.testEntity.$errors" :key="error.$uid">{{ error.$message }}</small>
          </div>
        </div>
        <div>
          <button type="button" id="cancel-save" data-cy="entityCreateCancelButton" class="btn btn-secondary" v-on:click="previousState()">
            <font-awesome-icon icon="ban"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.cancel')"></span>
          </button>
          <button
            type="submit"
            id="save-entity"
            data-cy="entityCreateSaveButton"
            :disabled="v$.$invalid || isSaving"
            class="btn btn-primary"
          >
            <font-awesome-icon icon="save"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.save')"></span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
<script lang="ts" src="./user-answer-update.component.ts"></script>
