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
} from '../../support/entity';

describe('PatientProfile e2e test', () => {
  const patientProfilePageUrl = '/patient-profile';
  const patientProfilePageUrlPattern = new RegExp('/patient-profile(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const patientProfileSample = {
    patientId: 'whereas',
    name: 'culminate',
    createdAt: '2024-03-31T17:53:28.750Z',
    updatedAt: '2024-03-31T21:06:46.508Z'
  };

  let patientProfile;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/patient-profiles+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/patient-profiles').as('postEntityRequest');
    cy.intercept('DELETE', '/api/patient-profiles/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (patientProfile) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/patient-profiles/${patientProfile.id}`
      }).then(() => {
        patientProfile = undefined;
      });
    }
  });

  it('PatientProfiles menu should load PatientProfiles page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('patient-profile');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('PatientProfile').should('exist');
    cy.url().should('match', patientProfilePageUrlPattern);
  });

  describe('PatientProfile page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(patientProfilePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create PatientProfile page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/patient-profile/new$'));
        cy.getEntityCreateUpdateHeading('PatientProfile');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', patientProfilePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/patient-profiles',
          body: patientProfileSample
        }).then(({ body }) => {
          patientProfile = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/patient-profiles+(?*|)',
              times: 1
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/patient-profiles?page=0&size=20>; rel="last",<http://localhost/api/patient-profiles?page=0&size=20>; rel="first"'
              },
              body: [patientProfile]
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(patientProfilePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details PatientProfile page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('patientProfile');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', patientProfilePageUrlPattern);
      });

      it('edit button click should load edit PatientProfile page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('PatientProfile');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', patientProfilePageUrlPattern);
      });

      it('edit button click should load edit PatientProfile page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('PatientProfile');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', patientProfilePageUrlPattern);
      });

      it('last delete button click should delete instance of PatientProfile', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('patientProfile').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', patientProfilePageUrlPattern);

        patientProfile = undefined;
      });
    });
  });

  describe('new PatientProfile page', () => {
    beforeEach(() => {
      cy.visit(`${patientProfilePageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('PatientProfile');
    });

    it('should create an instance of PatientProfile', () => {
      cy.get(`[data-cy="patientId"]`).type('necessary able');
      cy.get(`[data-cy="patientId"]`).should('have.value', 'necessary able');

      cy.get(`[data-cy="name"]`).type('imperturbable');
      cy.get(`[data-cy="name"]`).should('have.value', 'imperturbable');

      cy.get(`[data-cy="createdAt"]`).type('2024-04-01T05:12');
      cy.get(`[data-cy="createdAt"]`).blur();
      cy.get(`[data-cy="createdAt"]`).should('have.value', '2024-04-01T05:12');

      cy.get(`[data-cy="updatedAt"]`).type('2024-04-01T04:36');
      cy.get(`[data-cy="updatedAt"]`).blur();
      cy.get(`[data-cy="updatedAt"]`).should('have.value', '2024-04-01T04:36');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        patientProfile = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', patientProfilePageUrlPattern);
    });
  });
});
