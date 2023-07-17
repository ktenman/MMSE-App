import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { TestEntityFormService, TestEntityFormGroup } from './test-entity-form.service';
import { ITestEntity } from '../test-entity.model';
import { TestEntityService } from '../service/test-entity.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';

@Component({
  standalone: true,
  selector: 'mmse-test-entity-update',
  templateUrl: './test-entity-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class TestEntityUpdateComponent implements OnInit {
  isSaving = false;
  testEntity: ITestEntity | null = null;

  usersSharedCollection: IUser[] = [];

  editForm: TestEntityFormGroup = this.testEntityFormService.createTestEntityFormGroup();

  constructor(
    protected testEntityService: TestEntityService,
    protected testEntityFormService: TestEntityFormService,
    protected userService: UserService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ testEntity }) => {
      this.testEntity = testEntity;
      if (testEntity) {
        this.updateForm(testEntity);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const testEntity = this.testEntityFormService.getTestEntity(this.editForm);
    if (testEntity.id !== null) {
      this.subscribeToSaveResponse(this.testEntityService.update(testEntity));
    } else {
      this.subscribeToSaveResponse(this.testEntityService.create(testEntity));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITestEntity>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(testEntity: ITestEntity): void {
    this.testEntity = testEntity;
    this.testEntityFormService.resetForm(this.editForm, testEntity);

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(this.usersSharedCollection, testEntity.user);
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.testEntity?.user)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));
  }
}
