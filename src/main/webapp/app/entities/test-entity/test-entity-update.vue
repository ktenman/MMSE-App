<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" role="form" novalidate v-on:submit.prevent="save()">
        <h2
          id="mmseApp.testEntity.home.createOrEditLabel"
          data-cy="TestEntityCreateUpdateHeading"
          v-text="t$('mmseApp.testEntity.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group" v-if="testEntity.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="testEntity.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('mmseApp.testEntity.createdAt')" for="test-entity-createdAt"></label>
            <div class="d-flex">
              <input
                id="test-entity-createdAt"
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
            <label class="form-control-label" v-text="t$('mmseApp.testEntity.updatedAt')" for="test-entity-updatedAt"></label>
            <div class="d-flex">
              <input
                id="test-entity-updatedAt"
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
            <label class="form-control-label" v-text="t$('mmseApp.testEntity.score')" for="test-entity-score"></label>
            <input
              type="number"
              class="form-control"
              name="score"
              id="test-entity-score"
              data-cy="score"
              :class="{ valid: !v$.score.$invalid, invalid: v$.score.$invalid }"
              v-model.number="v$.score.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('mmseApp.testEntity.user')" for="test-entity-user"></label>
            <select class="form-control" id="test-entity-user" data-cy="user" name="user" v-model="testEntity.user" required>
              <option v-if="!testEntity.user" v-bind:value="null" selected></option>
              <option
                v-bind:value="testEntity.user && userOption.id === testEntity.user.id ? testEntity.user : userOption"
                v-for="userOption in users"
                :key="userOption.id"
              >
                {{ userOption.login }}
              </option>
            </select>
          </div>
          <div v-if="v$.user.$anyDirty && v$.user.$invalid">
            <small class="form-text text-danger" v-for="error of v$.user.$errors" :key="error.$uid">{{ error.$message }}</small>
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
<script lang="ts" src="./test-entity-update.component.ts"></script>
