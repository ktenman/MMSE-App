<template>
  <div>
    <h2 id="page-heading" data-cy="PatientProfileHeading">
      <span id="patient-profile-heading" v-text="t$('mmseApp.patientProfile.home.title')"></span>
      <div class="d-flex justify-content-end">
        <button :disabled="isFetching" class="btn btn-info mr-2" v-on:click="handleSyncList">
          <font-awesome-icon :spin="isFetching" icon="sync"></font-awesome-icon>
          <span v-text="t$('mmseApp.patientProfile.home.refreshListLabel')"></span>
        </button>
        <router-link v-slot="{ navigate }" :to="{ name: 'PatientProfileCreate' }" custom>
          <button
            id="jh-create-entity"
            class="btn btn-primary jh-create-entity create-patient-profile"
            data-cy="entityCreateButton"
            @click="navigate"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span v-text="t$('mmseApp.patientProfile.home.createLabel')"></span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div v-if="!isFetching && patientProfiles && patientProfiles.length === 0" class="alert alert-warning">
      <span v-text="t$('mmseApp.patientProfile.home.notFound')"></span>
    </div>
    <div v-if="patientProfiles && patientProfiles.length > 0" class="table-responsive">
      <table aria-describedby="patientProfiles" class="table table-striped">
        <thead>
        <tr>
          <th scope="row" v-on:click="changeOrder('id')">
            <span v-text="t$('global.field.id')"></span>
            <jhi-sort-indicator :current-order="propOrder" :field-name="'id'" :reverse="reverse"></jhi-sort-indicator>
          </th>
          <th scope="row" v-on:click="changeOrder('patientId')">
            <span v-text="t$('mmseApp.patientProfile.patientId')"></span>
            <jhi-sort-indicator :current-order="propOrder" :field-name="'patientId'"
                                :reverse="reverse"></jhi-sort-indicator>
          </th>
          <th scope="row" v-on:click="changeOrder('name')">
            <span v-text="t$('mmseApp.patientProfile.name')"></span>
            <jhi-sort-indicator :current-order="propOrder" :field-name="'name'" :reverse="reverse"></jhi-sort-indicator>
          </th>
          <th scope="row" v-on:click="changeOrder('createdAt')">
            <span v-text="t$('mmseApp.patientProfile.createdAt')"></span>
            <jhi-sort-indicator :current-order="propOrder" :field-name="'createdAt'"
                                :reverse="reverse"></jhi-sort-indicator>
          </th>
          <th scope="row" v-on:click="changeOrder('updatedAt')">
            <span v-text="t$('mmseApp.patientProfile.updatedAt')"></span>
            <jhi-sort-indicator :current-order="propOrder" :field-name="'updatedAt'"
                                :reverse="reverse"></jhi-sort-indicator>
          </th>
          <th scope="row"></th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="patientProfile in patientProfiles" :key="patientProfile.id" data-cy="entityTable">
          <td>
            <router-link :to="{ name: 'PatientProfileView', params: { patientProfileId: patientProfile.id } }">{{
                patientProfile.id
              }}
            </router-link>
          </td>
          <td>{{ patientProfile.patientId }}</td>
          <td>{{ patientProfile.name }}</td>
          <td>{{ formatDateShort(patientProfile.createdAt) || '' }}</td>
          <td>{{ formatDateShort(patientProfile.updatedAt) || '' }}</td>
          <td class="text-right">
            <div class="btn-group">
              <router-link
                v-slot="{ navigate }"
                :to="{ name: 'PatientProfileView', params: { patientProfileId: patientProfile.id } }"
                custom
              >
                <button class="btn btn-info btn-sm details" data-cy="entityDetailsButton" @click="navigate">
                  <font-awesome-icon icon="eye"></font-awesome-icon>
                  <span class="d-none d-md-inline" v-text="t$('entity.action.view')"></span>
                </button>
              </router-link>
              <router-link
                v-slot="{ navigate }"
                :to="{ name: 'PatientProfileEdit', params: { patientProfileId: patientProfile.id } }"
                custom
              >
                <button class="btn btn-primary btn-sm edit" data-cy="entityEditButton" @click="navigate">
                  <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                  <span class="d-none d-md-inline" v-text="t$('entity.action.edit')"></span>
                </button>
              </router-link>
              <b-button
                v-b-modal.removeEntity
                class="btn btn-sm"
                data-cy="entityDeleteButton"
                variant="danger"
                v-on:click="prepareRemove(patientProfile)"
              >
                <font-awesome-icon icon="times"></font-awesome-icon>
                <span class="d-none d-md-inline" v-text="t$('entity.action.delete')"></span>
              </b-button>
            </div>
          </td>
        </tr>
        </tbody>
        <span ref="infiniteScrollEl"></span>
      </table>
    </div>
    <b-modal id="removeEntity" ref="removeEntity">
      <template #modal-title>
        <span
          id="mmseApp.patientProfile.delete.question"
          data-cy="patientProfileDeleteDialogHeading"
          v-text="t$('entity.delete.title')"
        ></span>
      </template>
      <div class="modal-body">
        <p id="mmse-delete-patientProfile-heading"
           v-text="t$('mmseApp.patientProfile.delete.question', { id: removeId })"></p>
      </div>
      <template #modal-footer>
        <div>
          <button class="btn btn-secondary" type="button" v-on:click="closeDialog()"
                  v-text="t$('entity.action.cancel')"></button>
          <button
            id="mmse-confirm-delete-patientProfile"
            class="btn btn-primary"
            data-cy="entityConfirmDeleteButton"
            type="button"
            v-on:click="removePatientProfile()"
            v-text="t$('entity.action.delete')"
          ></button>
        </div>
      </template>
    </b-modal>
  </div>
</template>

<script lang="ts" src="./patient-profile.component.ts"></script>
