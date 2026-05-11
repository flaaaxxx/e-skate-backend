CREATE TABLE dynamic_of_change
(
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    admission_date   DATE        NOT NULL,
    disposition_date DATE,
    type             VARCHAR(50) NOT NULL,
    change_number    VARCHAR(50) NOT NULL,
    user_id          BIGINT      NOT NULL,
    project_id       BIGINT      NOT NULL,
    FOREIGN KEY (project_id) REFERENCES project (id),
    FOREIGN KEY (user_id) REFERENCES user_tab (id),
    UNIQUE (change_number, project_id, type),
    CHECK (admission_date <= disposition_date OR disposition_date IS NULL)
);
