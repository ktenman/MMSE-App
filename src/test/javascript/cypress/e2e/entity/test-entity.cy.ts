import {
  entityConfirmDeleteButtonSelector,
  entityCreateButtonSelector,
  entityCreateCancelButtonSelector,
  entityCreateSaveButtonSelector,
  entityDeleteButtonSelector,
  entityDetailsBackButtonSelector,
  entityDetailsButtonSelector,
  entityEditButtonSelector,
  entityTableSelector
} from "../../support/entity";

describe("TestEntity e2e test", () => {
  const testEntityPageUrl = "/test-entity";
  const testEntityPageUrlPattern = new RegExp("/test-entity(\\?.*)?$");
  const username = Cypress.env("E2E_USERNAME") ?? "user";
  const password = Cypress.env("E2E_PASSWORD") ?? "user";
  // const testEntitySample = {"createdAt":"2023-07-01T09:48:51.071Z"};

  let testEntity;
  // let user;

  beforeEach(() => {
    cy.login(username, password);
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/users',
      body: {"login":"Northwest teal","firstName":"Silas","lastName":"Brown"},
    }).then(({ body }) => {
      user = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept("GET", "/api/test-entities+(?*|)").as("entitiesRequest");
    cy.intercept("POST", "/api/test-entities").as("postEntityRequest");
    cy.intercept("DELETE", "/api/test-entities/*").as("deleteEntityRequest");
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/users', {
      statusCode: 200,
      body: [user],
    });

  });
   */

  afterEach(() => {
    if (testEntity) {
      cy.authenticatedRequest({
        method: "DELETE",
        url: `/api/test-entities/${testEntity.id}`
      }).then(() => {
        testEntity = undefined;
      });
    }
  });

  /* Disabled due to incompatibility
  afterEach(() => {
    if (user) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/users/${user.id}`,
      }).then(() => {
        user = undefined;
      });
    }
  });
   */

  it("TestEntities menu should load TestEntities page", () => {
    cy.visit("/");
    cy.clickOnEntityMenuItem("test-entity");
    cy.wait("@entitiesRequest").then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should("not.exist");
      } else {
        cy.get(entityTableSelector).should("exist");
      }
    });
    cy.getEntityHeading("TestEntity").should("exist");
    cy.url().should("match", testEntityPageUrlPattern);
  });

  describe("TestEntity page", () => {
    describe("create button click", () => {
      beforeEach(() => {
        cy.visit(testEntityPageUrl);
        cy.wait("@entitiesRequest");
      });

      it("should load create TestEntity page", () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should("match", new RegExp("/test-entity/new$"));
        cy.getEntityCreateUpdateHeading("TestEntity");
        cy.get(entityCreateSaveButtonSelector).should("exist");
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait("@entitiesRequest").then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should("match", testEntityPageUrlPattern);
      });
    });

    describe("with existing value", () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/test-entities',
          body: {
            ...testEntitySample,
            user: user,
          },
        }).then(({ body }) => {
          testEntity = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/test-entities+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/test-entities?page=0&size=20>; rel="last",<http://localhost/api/test-entities?page=0&size=20>; rel="first"',
              },
              body: [testEntity],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(testEntityPageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function() {
        cy.visit(testEntityPageUrl);

        cy.wait("@entitiesRequest").then(({ response }) => {
          if (response.body.length === 0) {
            this.skip();
          }
        });
      });

      it("detail button click should load details TestEntity page", () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading("testEntity");
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait("@entitiesRequest").then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should("match", testEntityPageUrlPattern);
      });

      it("edit button click should load edit TestEntity page and go back", () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading("TestEntity");
        cy.get(entityCreateSaveButtonSelector).should("exist");
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait("@entitiesRequest").then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should("match", testEntityPageUrlPattern);
      });

      it("edit button click should load edit TestEntity page and save", () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading("TestEntity");
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait("@entitiesRequest").then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should("match", testEntityPageUrlPattern);
      });

      it.skip("last delete button click should delete instance of TestEntity", () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading("testEntity").should("exist");
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait("@deleteEntityRequest").then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait("@entitiesRequest").then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should("match", testEntityPageUrlPattern);

        testEntity = undefined;
      });
    });
  });

  describe("new TestEntity page", () => {
    beforeEach(() => {
      cy.visit(`${testEntityPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading("TestEntity");
    });

    it.skip("should create an instance of TestEntity", () => {
      cy.get(`[data-cy="createdAt"]`).type("2023-07-01T01:35");
      cy.get(`[data-cy="createdAt"]`).blur();
      cy.get(`[data-cy="createdAt"]`).should("have.value", "2023-07-01T01:35");

      cy.get(`[data-cy="updatedAt"]`).type("2023-06-30T14:08");
      cy.get(`[data-cy="updatedAt"]`).blur();
      cy.get(`[data-cy="updatedAt"]`).should("have.value", "2023-06-30T14:08");

      cy.get(`[data-cy="score"]`).type("10532");
      cy.get(`[data-cy="score"]`).should("have.value", "10532");

      cy.get(`[data-cy="user"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait("@postEntityRequest").then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        testEntity = response.body;
      });
      cy.wait("@entitiesRequest").then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should("match", testEntityPageUrlPattern);
    });
  });
});
