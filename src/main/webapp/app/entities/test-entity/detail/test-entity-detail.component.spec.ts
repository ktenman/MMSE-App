import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness, RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { TestEntityDetailComponent } from './test-entity-detail.component';

describe('TestEntity Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TestEntityDetailComponent, RouterTestingModule.withRoutes([], { bindToComponentInputs: true })],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: TestEntityDetailComponent,
              resolve: { testEntity: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding()
        ),
      ],
    })
      .overrideTemplate(TestEntityDetailComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('Should load testEntity on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', TestEntityDetailComponent);

      // THEN
      expect(instance.testEntity).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
