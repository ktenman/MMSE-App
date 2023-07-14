import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { TestEntityFormService } from './test-entity-form.service';
import { TestEntityService } from '../service/test-entity.service';
import { ITestEntity } from '../test-entity.model';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';

import { TestEntityUpdateComponent } from './test-entity-update.component';

describe('TestEntity Management Update Component', () => {
  let comp: TestEntityUpdateComponent;
  let fixture: ComponentFixture<TestEntityUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let testEntityFormService: TestEntityFormService;
  let testEntityService: TestEntityService;
  let userService: UserService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([]), TestEntityUpdateComponent],
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
      .overrideTemplate(TestEntityUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TestEntityUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    testEntityFormService = TestBed.inject(TestEntityFormService);
    testEntityService = TestBed.inject(TestEntityService);
    userService = TestBed.inject(UserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call User query and add missing value', () => {
      const testEntity: ITestEntity = { id: 456 };
      const user: IUser = { id: 74470 };
      testEntity.user = user;

      const userCollection: IUser[] = [{ id: 90852 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [user];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ testEntity });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining)
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const testEntity: ITestEntity = { id: 456 };
      const user: IUser = { id: 24641 };
      testEntity.user = user;

      activatedRoute.data = of({ testEntity });
      comp.ngOnInit();

      expect(comp.usersSharedCollection).toContain(user);
      expect(comp.testEntity).toEqual(testEntity);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITestEntity>>();
      const testEntity = { id: 123 };
      jest.spyOn(testEntityFormService, 'getTestEntity').mockReturnValue(testEntity);
      jest.spyOn(testEntityService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ testEntity });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: testEntity }));
      saveSubject.complete();

      // THEN
      expect(testEntityFormService.getTestEntity).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(testEntityService.update).toHaveBeenCalledWith(expect.objectContaining(testEntity));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITestEntity>>();
      const testEntity = { id: 123 };
      jest.spyOn(testEntityFormService, 'getTestEntity').mockReturnValue({ id: null });
      jest.spyOn(testEntityService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ testEntity: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: testEntity }));
      saveSubject.complete();

      // THEN
      expect(testEntityFormService.getTestEntity).toHaveBeenCalled();
      expect(testEntityService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITestEntity>>();
      const testEntity = { id: 123 };
      jest.spyOn(testEntityService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ testEntity });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(testEntityService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareUser', () => {
      it('Should forward to userService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(userService, 'compareUser');
        comp.compareUser(entity, entity2);
        expect(userService.compareUser).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
