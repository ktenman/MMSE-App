<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="testEntity">
        <h2 class="jh-entity-heading" data-cy="testEntityDetailsHeading">
          <span v-text="t$('mmseApp.testEntity.detail.title')"></span> {{ testEntity.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('mmseApp.testEntity.createdAt')"></span>
          </dt>
          <dd>
            <span v-if="testEntity.createdAt">{{ formatDateLong(testEntity.createdAt) }}</span>
          </dd>
          <dt>
            <span v-text="t$('mmseApp.testEntity.updatedAt')"></span>
          </dt>
          <dd>
            <span v-if="testEntity.updatedAt">{{ formatDateLong(testEntity.updatedAt) }}</span>
          </dd>
          <dt>
            <span v-text="t$('mmseApp.testEntity.score')"></span>
          </dt>
          <dd>
            <span>{{ testEntity.score }}</span>
          </dd>
          <dt>
            <span v-text="t$('mmseApp.testEntity.user')"></span>
          </dt>
          <dd>
            {{ testEntity.user ? testEntity.user.login : '' }}
          </dd>
          <dt>
            <span v-text="t$('mmseApp.testEntity.patientProfile')"></span>
          </dt>
          <dd>
            <div v-if="testEntity.patientProfile">
              <router-link
                :to="{ name: 'PatientProfileView', params: { patientProfileId: testEntity.patientProfile.id } }"
              >{{ testEntity.patientProfile.id }}
              </router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info"
                data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link
          v-if="testEntity.id"
          :to="{ name: 'TestEntityEdit', params: { testEntityId: testEntity.id } }"
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

<script lang="ts" src="./test-entity-details.component.ts"></script>
