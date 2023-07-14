import {
  entityTableSelector,
  entityDetailsButtonSelector,
  entityDetailsBackButtonSelector,
  entityCreateButtonSelector,
  entityCreateSaveButtonSelector,
  entityCreateCancelButtonSelector,
  entityEditButtonSelector,
  entityDeleteButtonSelector,
  entityConfirmDeleteButtonSelector,
} from '../../support/entity';

describe('UserAnswer e2e test', () => {
  const userAnswerPageUrl = '/user-answer';
  const userAnswerPageUrlPattern = new RegExp('/user-answer(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  // const userAnswerSample = {"createdAt":"2023-07-14T11:57:34.458Z","questionId":"FIRST"};

  let userAnswer;
  // let testEntity;

  beforeEach(() => {
    cy.login(username, password);
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/test-entities',
      body: {"createdAt":"2023-06-30T17:03:08.329Z","updatedAt":"2023-07-01T03:23:59.496Z","score":82879},
    }).then(({ body }) => {
      testEntity = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/user-answers+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/user-answers').as('postEntityRequest');
    cy.intercept('DELETE', '/api/user-answers/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/test-entities', {
      statusCode: 200,
      body: [testEntity],
    });

  });
   */

  afterEach(() => {
    if (userAnswer) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/user-answers/${userAnswer.id}`,
      }).then(() => {
        userAnswer = undefined;
      });
    }
  });

  /* Disabled due to incompatibility
  afterEach(() => {
    if (testEntity) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/test-entities/${testEntity.id}`,
      }).then(() => {
        testEntity = undefined;
      });
    }
  });
   */

  it('UserAnswers menu should load UserAnswers page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('user-answer');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('UserAnswer').should('exist');
    cy.url().should('match', userAnswerPageUrlPattern);
  });

  describe('UserAnswer page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(userAnswerPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create UserAnswer page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/user-answer/new$'));
        cy.getEntityCreateUpdateHeading('UserAnswer');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', userAnswerPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/user-answers',
          body: {
            ...userAnswerSample,
            testEntity: testEntity,
          },
        }).then(({ body }) => {
          userAnswer = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/user-answers+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/user-answers?page=0&size=20>; rel="last",<http://localhost/api/user-answers?page=0&size=20>; rel="first"',
              },
              body: [userAnswer],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(userAnswerPageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(userAnswerPageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details UserAnswer page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('userAnswer');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', userAnswerPageUrlPattern);
      });

      it('edit button click should load edit UserAnswer page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('UserAnswer');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', userAnswerPageUrlPattern);
      });

      it('edit button click should load edit UserAnswer page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('UserAnswer');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', userAnswerPageUrlPattern);
      });

      it.skip('last delete button click should delete instance of UserAnswer', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('userAnswer').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', userAnswerPageUrlPattern);

        userAnswer = undefined;
      });
    });
  });

  describe('new UserAnswer page', () => {
    beforeEach(() => {
      cy.visit(`${userAnswerPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('UserAnswer');
    });

    it.skip('should create an instance of UserAnswer', () => {
      cy.get(`[data-cy="answerText"]`).type('XML');
      cy.get(`[data-cy="answerText"]`).should('have.value', 'XML');

      cy.get(`[data-cy="createdAt"]`).type('2023-07-13T21:56');
      cy.get(`[data-cy="createdAt"]`).blur();
      cy.get(`[data-cy="createdAt"]`).should('have.value', '2023-07-13T21:56');

      cy.get(`[data-cy="updatedAt"]`).type('2023-07-14T07:14');
      cy.get(`[data-cy="updatedAt"]`).blur();
      cy.get(`[data-cy="updatedAt"]`).should('have.value', '2023-07-14T07:14');

      cy.get(`[data-cy="questionId"]`).select('FIRST');

      cy.get(`[data-cy="testEntity"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        userAnswer = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', userAnswerPageUrlPattern);
    });
  });
});
