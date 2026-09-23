SELECT
    se.`year` AS `year`,
    se.semester AS semester,
    se.exam_type AS examType,
    se.exam_date AS examDate,
    seb.exam_id AS examId,
    se.`name` AS examName,
    seb.grade_clazz_id AS clazzId,
    sc.`name` AS clazzName,
    sc.grade_id AS gradeId,
    sg.`name` AS gradeName
FROM
    sys_exam_body seb
        LEFT JOIN sys_exam se ON seb.exam_id = se.id
        LEFT JOIN sys_clazz sc ON seb.grade_clazz_id = sc.id
        LEFT JOIN sys_grade sg ON sc.grade_id = sg.id
WHERE
        seb.g_or_c = 'C'