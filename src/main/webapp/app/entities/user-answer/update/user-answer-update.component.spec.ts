import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { UserAnswerFormService } from './user-answer-form.service';
import { UserAnswerService } from '../service/user-answer.service';
import { IUserAnswer } from '../user-answer.model';
import { ITestEntity } from 'app/entities/test-entity/test-entity.model';
import { TestEntityService } from 'app/entities/test-entity/service/test-entity.service';

import { UserAnswerUpdateComponent } from './user-answer-update.component';

describe('UserAnswer Management Update Component', () => {
  let comp: UserAnswerUpdateComponent;
  let fixture: ComponentFixture<UserAnswerUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let userAnswerFormService: UserAnswerFormService;
  let userAnswerService: UserAnswerService;
  let testEntityService: TestEntityService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([]), UserAnswerUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(UserAnswerUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(UserAnswerUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    userAnswerFormService = TestBed.inject(UserAnswerFormService);
    userAnswerService = TestBed.inject(UserAnswerService);
    testEntityService = TestBed.inject(TestEntityService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call TestEntity query and add missing value', () => {
      const userAnswer: IUserAnswer = { id: 456 };
      const testEntity: ITestEntity = { id: 66286 };
      userAnswer.testEntity = testEntity;

      const testEntityCollection: ITestEntity[] = [{ id: 74598 }];
      jest.spyOn(testEntityService, 'query').mockReturnValue(of(new HttpResponse({ body: testEntityCollection })));
      const additionalTestEntities = [testEntity];
      const expectedCollection: ITestEntity[] = [...additionalTestEntities, ...testEntityCollection];
      jest.spyOn(testEntityService, 'addTestEntityToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ userAnswer });
      comp.ngOnInit();

      expect(testEntityService.query).toHaveBeenCalled();
      expect(testEntityService.addTestEntityToCollectionIfMissing).toHaveBeenCalledWith(
        testEntityCollection,
        ...additionalTestEntities.map(expect.objectContaining)
      );
      expect(comp.testEntitiesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const userAnswer: IUserAnswer = { id: 456 };
      const testEntity: ITestEntity = { id: 24614 };
      userAnswer.testEntity = testEntity;

      activatedRoute.data = of({ userAnswer });
      comp.ngOnInit();

      expect(comp.testEntitiesSharedCollection).toContain(testEntity);
      expect(comp.userAnswer).toEqual(userAnswer);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IUserAnswer>>();
      const userAnswer = { id: 123 };
      jest.spyOn(userAnswerFormService, 'getUserAnswer').mockReturnValue(userAnswer);
      jest.spyOn(userAnswerService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ userAnswer });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: userAnswer }));
      saveSubject.complete();

      // THEN
      expect(userAnswerFormService.getUserAnswer).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(userAnswerService.update).toHaveBeenCalledWith(expect.objectContaining(userAnswer));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IUserAnswer>>();
      const userAnswer = { id: 123 };
      jest.spyOn(userAnswerFormService, 'getUserAnswer').mockReturnValue({ id: null });
      jest.spyOn(userAnswerService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ userAnswer: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: userAnswer }));
      saveSubject.complete();

      // THEN
      expect(userAnswerFormService.getUserAnswer).toHaveBeenCalled();
      expect(userAnswerService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IUserAnswer>>();
      const userAnswer = { id: 123 };
      jest.spyOn(userAnswerService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ userAnswer });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(userAnswerService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareTestEntity', () => {
      it('Should forward to testEntityService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(testEntityService, 'compareTestEntity');
        comp.compareTestEntity(entity, entity2);
        expect(testEntityService.compareTestEntity).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
