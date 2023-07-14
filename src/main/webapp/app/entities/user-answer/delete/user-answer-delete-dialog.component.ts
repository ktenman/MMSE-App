import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { IUserAnswer } from '../user-answer.model';
import { UserAnswerService } from '../service/user-answer.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  standalone: true,
  templateUrl: './user-answer-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class UserAnswerDeleteDialogComponent {
  userAnswer?: IUserAnswer;

  constructor(protected userAnswerService: UserAnswerService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.userAnswerService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
