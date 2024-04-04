<template>
  <div id="app">
    <div v-if="isTest">
      <div class="container-fluid">
        <div class="card jh-test">
          <router-view></router-view>
        </div>
      </div>
    </div>
    <div v-else>
      <ribbon></ribbon>
      <div id="app-header">
        <jhi-navbar></jhi-navbar>
      </div>
      <div class="container-fluid">
        <div class="card jh-card">
          <router-view></router-view>
        </div>
        <b-modal id="login-page" hide-footer lazy>
          <template #modal-title>
            <span id="login-title" data-cy="loginTitle" v-text="t$('login.title')"></span>
          </template>
          <login-form></login-form>
        </b-modal>
        <jhi-footer></jhi-footer>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./app.component.ts"></script>

<style scoped>
.jh-test {
  padding: 1.6rem;
  margin-top: 0.8rem;
  border: none;
}
</style>

