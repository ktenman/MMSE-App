import { defineComponent, provide } from "vue";

import UserService from "@/entities/user/user.service";
import TestEntityService from "./test-entity/test-entity.service";
import UserAnswerService from "./user-answer/user-answer.service";
// jhipster-needle-add-entity-service-to-entities-component-import - JHipster will import entities services here

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: "Entities",
  setup() {
    provide("userService", () => new UserService());
    provide("testEntityService", () => new TestEntityService());
    provide("userAnswerService", () => new UserAnswerService());
    // jhipster-needle-add-entity-service-to-entities-component - JHipster will import entities services here
  }
});
