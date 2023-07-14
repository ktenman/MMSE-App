import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { UserAnswerFormService, UserAnswerFormGroup } from './user-answer-form.service';
import { IUserAnswer } from '../user-answer.model';
import { UserAnswerService } from '../service/user-answer.service';
import { ITestEntity } from 'app/entities/test-entity/test-entity.model';
import { TestEntityService } from 'app/entities/test-entity/service/test-entity.service';
import { QuestionId } from 'app/entities/enumerations/question-id.model';

@Component({
  standalone: true,
  selector: 'mmse-user-answer-update',
  templateUrl: './user-answer-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class UserAnswerUpdateComponent implements OnInit {
  isSaving = false;
  userAnswer: IUserAnswer | null = null;
  questionIdValues = Object.keys(QuestionId);

  testEntitiesSharedCollection: ITestEntity[] = [];

  editForm: UserAnswerFormGroup = this.userAnswerFormService.createUserAnswerFormGroup();

  constructor(
    protected userAnswerService: UserAnswerService,
    protected userAnswerFormService: UserAnswerFormService,
    protected testEntityService: TestEntityService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareTestEntity = (o1: ITestEntity | null, o2: ITestEntity | null): boolean => this.testEntityService.compareTestEntity(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ userAnswer }) => {
      this.userAnswer = userAnswer;
      if (userAnswer) {
        this.updateForm(userAnswer);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const userAnswer = this.userAnswerFormService.getUserAnswer(this.editForm);
    if (userAnswer.id !== null) {
      this.subscribeToSaveResponse(this.userAnswerService.update(userAnswer));
    } else {
      this.subscribeToSaveResponse(this.userAnswerService.create(userAnswer));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IUserAnswer>>): void {
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

  protected updateForm(userAnswer: IUserAnswer): void {
    this.userAnswer = userAnswer;
    this.userAnswerFormService.resetForm(this.editForm, userAnswer);

    this.testEntitiesSharedCollection = this.testEntityService.addTestEntityToCollectionIfMissing<ITestEntity>(
      this.testEntitiesSharedCollection,
      userAnswer.testEntity
    );
  }

  protected loadRelationshipsOptions(): void {
    this.testEntityService
      .query()
      .pipe(map((res: HttpResponse<ITestEntity[]>) => res.body ?? []))
      .pipe(
        map((testEntities: ITestEntity[]) =>
          this.testEntityService.addTestEntityToCollectionIfMissing<ITestEntity>(testEntities, this.userAnswer?.testEntity)
        )
      )
      .subscribe((testEntities: ITestEntity[]) => (this.testEntitiesSharedCollection = testEntities));
  }
}
