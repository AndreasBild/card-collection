-- Create the grading table
CREATE TABLE grading (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    grade FLOAT NOT NULL,
    grading_company VARCHAR(255) NOT NULL,
    CONSTRAINT check_grade_range CHECK (grade >= 6.0 AND grade <= 10.0)
    -- Constraint for 0.5 steps is harder to enforce directly in all SQL dialects consistently
    -- and will be handled at the application/service layer.
);

-- Add the grading_id foreign key to the card table
-- This assumes the 'card' table already exists.
ALTER TABLE card
ADD COLUMN grading_id BIGINT NULL;

ALTER TABLE card
ADD CONSTRAINT fk_card_grading
    FOREIGN KEY (grading_id)
    REFERENCES grading(id)
    ON DELETE SET NULL;
