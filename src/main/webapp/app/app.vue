<template>
  <div id="app">
    <ribbon v-if="!isTest"></ribbon>
    <div id="app-header">
      <jhi-navbar v-if="!isTest"></jhi-navbar>
    </div>
    <div class="container-fluid">
      <div class="card jh-card">
        <router-view></router-view>
      </div>
      <b-modal v-if="!isTest" id="login-page" hide-footer lazy>
        <template #modal-title>
          <span data-cy="loginTitle" id="login-title" v-text="t$('login.title')"></span>
        </template>
        <login-form></login-form>
      </b-modal>

      <jhi-footer v-if="!isTest"></jhi-footer>
    </div>
  </div>
</template>

<script lang="ts" src="./app.component.ts"></script>
