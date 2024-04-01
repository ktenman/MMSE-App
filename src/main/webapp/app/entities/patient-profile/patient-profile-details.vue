<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="patientProfile">
        <h2 class="jh-entity-heading" data-cy="patientProfileDetailsHeading">
          <span v-text="t$('mmseApp.patientProfile.detail.title')"></span> {{ patientProfile.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('mmseApp.patientProfile.patientId')"></span>
          </dt>
          <dd>
            <span>{{ patientProfile.patientId }}</span>
          </dd>
          <dt>
            <span v-text="t$('mmseApp.patientProfile.name')"></span>
          </dt>
          <dd>
            <span>{{ patientProfile.name }}</span>
          </dd>
          <dt>
            <span v-text="t$('mmseApp.patientProfile.createdAt')"></span>
          </dt>
          <dd>
            <span v-if="patientProfile.createdAt">{{ formatDateLong(patientProfile.createdAt) }}</span>
          </dd>
          <dt>
            <span v-text="t$('mmseApp.patientProfile.updatedAt')"></span>
          </dt>
          <dd>
            <span v-if="patientProfile.updatedAt">{{ formatDateLong(patientProfile.updatedAt) }}</span>
          </dd>
        </dl>
        <button class="btn btn-info" data-cy="entityDetailsBackButton" type="submit"
                v-on:click.prevent="previousState()">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link
          v-if="patientProfile.id"
          v-slot="{ navigate }"
          :to="{ name: 'PatientProfileEdit', params: { patientProfileId: patientProfile.id } }"
          custom
        >
          <button class="btn btn-primary" @click="navigate">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span
            v-text="t$('entity.action.edit')"></span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./patient-profile-details.component.ts"></script>
