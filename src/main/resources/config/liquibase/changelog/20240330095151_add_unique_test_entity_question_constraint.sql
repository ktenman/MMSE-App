ALTER TABLE user_answer
    ADD CONSTRAINT unique_test_entity_question UNIQUE (test_entity_id, question_id);
