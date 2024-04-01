<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate role="form" v-on:submit.prevent="save()">
        <h2
          id="mmseApp.patientProfile.home.createOrEditLabel"
          data-cy="PatientProfileCreateUpdateHeading"
          v-text="t$('mmseApp.patientProfile.home.createOrEditLabel')"
        ></h2>
        <div>
          <div v-if="patientProfile.id" class="form-group">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input id="id" v-model="patientProfile.id" class="form-control" name="id" readonly type="text" />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="patient-profile-patientId"
                   v-text="t$('mmseApp.patientProfile.patientId')"></label>
            <input
              id="patient-profile-patientId"
              v-model="v$.patientId.$model"
              :class="{ valid: !v$.patientId.$invalid, invalid: v$.patientId.$invalid }"
              class="form-control"
              data-cy="patientId"
              name="patientId"
              required
              type="text"
            />
            <div v-if="v$.patientId.$anyDirty && v$.patientId.$invalid">
              <small v-for="error of v$.patientId.$errors" :key="error.$uid"
                     class="form-text text-danger">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="patient-profile-name"
                   v-text="t$('mmseApp.patientProfile.name')"></label>
            <input
              id="patient-profile-name"
              v-model="v$.name.$model"
              :class="{ valid: !v$.name.$invalid, invalid: v$.name.$invalid }"
              class="form-control"
              data-cy="name"
              name="name"
              required
              type="text"
            />
            <div v-if="v$.name.$anyDirty && v$.name.$invalid">
              <small v-for="error of v$.name.$errors" :key="error.$uid" class="form-text text-danger">{{ error.$message
                }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="patient-profile-createdAt"
                   v-text="t$('mmseApp.patientProfile.createdAt')"></label>
            <div class="d-flex">
              <input
                id="patient-profile-createdAt"
                :class="{ valid: !v$.createdAt.$invalid, invalid: v$.createdAt.$invalid }"
                :value="convertDateTimeFromServer(v$.createdAt.$model)"
                class="form-control"
                data-cy="createdAt"
                name="createdAt"
                required
                type="datetime-local"
                @change="updateInstantField('createdAt', $event)"
              />
            </div>
            <div v-if="v$.createdAt.$anyDirty && v$.createdAt.$invalid">
              <small v-for="error of v$.createdAt.$errors" :key="error.$uid"
                     class="form-text text-danger">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="patient-profile-updatedAt"
                   v-text="t$('mmseApp.patientProfile.updatedAt')"></label>
            <div class="d-flex">
              <input
                id="patient-profile-updatedAt"
                :class="{ valid: !v$.updatedAt.$invalid, invalid: v$.updatedAt.$invalid }"
                :value="convertDateTimeFromServer(v$.updatedAt.$model)"
                class="form-control"
                data-cy="updatedAt"
                name="updatedAt"
                required
                type="datetime-local"
                @change="updateInstantField('updatedAt', $event)"
              />
            </div>
            <div v-if="v$.updatedAt.$anyDirty && v$.updatedAt.$invalid">
              <small v-for="error of v$.updatedAt.$errors" :key="error.$uid"
                     class="form-text text-danger">{{ error.$message }}</small>
            </div>
          </div>
        </div>
        <div>
          <button id="cancel-save" class="btn btn-secondary" data-cy="entityCreateCancelButton" type="button"
                  v-on:click="previousState()">
            <font-awesome-icon icon="ban"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.cancel')"></span>
          </button>
          <button
            id="save-entity"
            :disabled="v$.$invalid || isSaving"
            class="btn btn-primary"
            data-cy="entityCreateSaveButton"
            type="submit"
          >
            <font-awesome-icon icon="save"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.save')"></span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
<script lang="ts" src="./patient-profile-update.component.ts"></script>
