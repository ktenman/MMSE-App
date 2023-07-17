<template>
  <div>
    <b-dropdown-item to="/test-entity">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.testEntity')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/user-answer">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.userAnswer')"></span>
    </b-dropdown-item>
    <!-- jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here -->
  </div>
</template>

<script lang="ts" src="./entities-menu.component.ts"></script>
