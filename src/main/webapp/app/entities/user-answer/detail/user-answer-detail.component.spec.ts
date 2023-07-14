import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness, RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { UserAnswerDetailComponent } from './user-answer-detail.component';

describe('UserAnswer Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UserAnswerDetailComponent, RouterTestingModule.withRoutes([], { bindToComponentInputs: true })],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: UserAnswerDetailComponent,
              resolve: { userAnswer: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding()
        ),
      ],
    })
      .overrideTemplate(UserAnswerDetailComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('Should load userAnswer on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', UserAnswerDetailComponent);

      // THEN
      expect(instance.userAnswer).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
