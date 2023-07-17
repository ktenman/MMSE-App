<template>
  <div>
    <h2 id="page-heading" data-cy="TestEntityHeading">
      <span v-text="t$('mmseApp.testEntity.home.title')" id="test-entity-heading"></span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" v-on:click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon>
          <span v-text="t$('mmseApp.testEntity.home.refreshListLabel')"></span>
        </button>
        <router-link :to="{ name: 'TestEntityCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-test-entity"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span v-text="t$('mmseApp.testEntity.home.createLabel')"></span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && testEntities && testEntities.length === 0">
      <span v-text="t$('mmseApp.testEntity.home.notFound')"></span>
    </div>
    <div class="table-responsive" v-if="testEntities && testEntities.length > 0">
      <table class="table table-striped" aria-describedby="testEntities">
        <thead>
          <tr>
            <th scope="row" v-on:click="changeOrder('id')">
              <span v-text="t$('global.field.id')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'id'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('createdAt')">
              <span v-text="t$('mmseApp.testEntity.createdAt')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'createdAt'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('updatedAt')">
              <span v-text="t$('mmseApp.testEntity.updatedAt')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'updatedAt'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('score')">
              <span v-text="t$('mmseApp.testEntity.score')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'score'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('user.login')">
              <span v-text="t$('mmseApp.testEntity.user')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'user.login'"></jhi-sort-indicator>
            </th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="testEntity in testEntities" :key="testEntity.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'TestEntityView', params: { testEntityId: testEntity.id } }">{{ testEntity.id }}</router-link>
            </td>
            <td>{{ formatDateShort(testEntity.createdAt) || '' }}</td>
            <td>{{ formatDateShort(testEntity.updatedAt) || '' }}</td>
            <td>{{ testEntity.score }}</td>
            <td>
              {{ testEntity.user ? testEntity.user.login : '' }}
            </td>
            <td class="text-right">
              <div class="btn-group">
                <router-link :to="{ name: 'TestEntityView', params: { testEntityId: testEntity.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.view')"></span>
                  </button>
                </router-link>
                <router-link :to="{ name: 'TestEntityEdit', params: { testEntityId: testEntity.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.edit')"></span>
                  </button>
                </router-link>
                <b-button
                  v-on:click="prepareRemove(testEntity)"
                  variant="danger"
                  class="btn btn-sm"
                  data-cy="entityDeleteButton"
                  v-b-modal.removeEntity
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
    <b-modal ref="removeEntity" id="removeEntity">
      <template #modal-title>
        <span id="mmseApp.testEntity.delete.question" data-cy="testEntityDeleteDialogHeading" v-text="t$('entity.delete.title')"></span>
      </template>
      <div class="modal-body">
        <p id="mmse-delete-testEntity-heading" v-text="t$('mmseApp.testEntity.delete.question', { id: removeId })"></p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" v-text="t$('entity.action.cancel')" v-on:click="closeDialog()"></button>
          <button
            type="button"
            class="btn btn-primary"
            id="mmse-confirm-delete-testEntity"
            data-cy="entityConfirmDeleteButton"
            v-text="t$('entity.action.delete')"
            v-on:click="removeTestEntity()"
          ></button>
        </div>
      </template>
    </b-modal>
  </div>
</template>

<script lang="ts" src="./test-entity.component.ts"></script>
