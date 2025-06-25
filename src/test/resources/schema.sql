CREATE TABLE IF  NOT EXISTS students (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    kana_name VARCHAR(100) NOT NULL,
    nickname VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    area VARCHAR(100) NOT NULL,
    age INT,
    sex VARCHAR(10) NOT NULL,
    remark VARCHAR(255),
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS students_courses (
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    student_id INT NOT NULL,
    course_name VARCHAR(100) NOT NULL,
    course_start_at TIMESTAMP NOT NULL,
    course_end_at TIMESTAMP NOT NULL,
    application_status VARCHAR(20) NOT NULL DEFAULT '仮申込'
);