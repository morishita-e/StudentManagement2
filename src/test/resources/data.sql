INSERT INTO students (name, kana_name, nickname, email, area, age, sex, remark, is_deleted) VALUES
('山田太一郎', 'ヤマダタイチロウ', 'タイチ', 'taithi@example.com',  '東京',   51, '男性',   '',       0),
('鈴木一郎',   'スズキイチロウ',   'イチ',   'ichiro@example.com',  '大坂',   30, '男性',   NULL,     0),
('田中花子',   'タナカハナコ',     'ハナ',   'hana@example.com',    '北海道', 23, '女性',   '',       0),
('佐藤良子',   'サトウリョウコ',   'リョウ', 'ryoko@example.com',   '福岡',   28, '女性',   NULL,     0),
('伊藤遥',     'イトウハルカ',     'ハル',   'haruka@example.com',  '愛知',   22, 'その他', '',       0),
('渡辺　三郎', 'ワタナベサブロウ', 'ナベ',   'saburou@example.com', '岩手',   36, '男性',   '特になし', 0),
('近藤四郎',   'コンドウシロウ',   'シロ',   'shiro@example.com',   '大分',   41, '男性',   '特になし', 0),
('今川五郎',   'イマガワゴロウ',   'ゴロウ', 'goro@example.com',    '鹿児島', 31, '男性',   '特になし', 0),
('最中奈々子', 'モナカナナコ',     'ナナ',   'nana@example.com',    '山口',   19, '女性',   '',       0),
('江並浩二',   'エナミコウジ',     'コウジ', 'test@example.com',    '奈良',   36, '男性',   NULL,     0),
('鈴木十郎',   'スズキジュウロウ', 'ジュウ', 'suzu@example.com',    '栃木',   21, '男性',   NULL,     0),
('大木順子',   'オオキジュンコ',   'ジュン', 'zyun@example.com',    '滋賀',   18, '女性',   NULL,     0),
('大和加奈',   'ヤマトカナ',       'カナ',   'kana@example.com',    '山口',   37, '女性',   NULL,     0),
('古川正樹',   'フルカワマサキ',   'マサ',   'masa@example.com',    '徳島',   17, '男性',   NULL,     0);

-- students_coursesテーブルへのデータ挿入
INSERT INTO students_courses
(studentId, courseName, courseStartAt, courseEndAt, applicationStatus)
VALUES (5, '宅建士(宅地建物取引士)', '2024-10-01 00:00:00', '2025-11-30 23:59:59', '仮申込');

INSERT INTO students_courses
(studentId, courseName, courseStartAt, courseEndAt, applicationStatus)
VALUES (2, 'JAVAベーシック', '2024-11-23 10:30:00', '2025-03-31 23:59:59', '仮申込');

INSERT INTO students_courses
(studentId, courseName, courseStartAt, courseEndAt, applicationStatus)
VALUES (2, 'JAVAスタンダード', '2024-11-23 10:30:00', '2025-03-31 23:59:59', '仮申込');

INSERT INTO students_courses
(studentId, courseName, courseStartAt, courseEndAt, applicationStatus)
VALUES (3, '司法試験-裁判官コース', '2024-08-11 15:30:00', '2025-08-11 23:59:59', '仮申込');

INSERT INTO students_courses
(studentId, courseName, courseStartAt, courseEndAt, applicationStatus)
VALUES (4, 'JAVAスタンダード', '2025-01-17 17:00:00', '2025-06-30 23:59:59', '仮申込');

INSERT INTO students_courses
(studentId, courseName, courseStartAt, courseEndAt, applicationStatus)
VALUES (9, '社会マーケティング', '2025-03-13 17:52:44', '2026-03-13 17:52:44', '仮申込');

INSERT INTO students_courses
(studentId, courseName, courseStartAt, courseEndAt, applicationStatus)
VALUES (10, 'AWSコース', '2025-04-03 18:53:47', '2026-04-03 18:53:47', '仮申込');

INSERT INTO students_courses
(studentId, courseName, courseStartAt, courseEndAt, applicationStatus)
VALUES (11, '国家公務員コース', '2025-04-07 16:12:49', '2026-04-07 16:12:49', '仮申込');

INSERT INTO students_courses
(studentId, courseName, courseStartAt, courseEndAt, applicationStatus)
VALUES (12, '電子技師コース', '2025-04-07 16:26:46', '2026-04-07 16:26:46', '仮申込');

INSERT INTO students_courses
(studentId, courseName, courseStartAt, courseEndAt, applicationStatus)
VALUES (13, '簿記コース', '2025-04-07 16:31:46', '2026-04-07 16:31:46', '仮申込');