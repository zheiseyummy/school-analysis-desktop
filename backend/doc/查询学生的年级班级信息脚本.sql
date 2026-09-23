SELECT
    scs.`year` AS `year`,
    sc.grade_id AS gradeId,
    sg.`name` AS gradeName,
    scs.clazz_id AS clazzId,
    sc.`name` AS clazzName,
    sc.clazz_type AS clazzType,
    scs.student_id AS studentId,
    ss.`name` AS studentName,
    ss.sex AS studentSex
FROM
    sys_clazz_student scs
        LEFT JOIN sys_clazz sc ON scs.clazz_id = sc.id
        LEFT JOIN sys_grade sg ON sc.grade_id = sg.id
        LEFT JOIN sys_student ss ON scs.student_id = ss.id
WHERE
        sc.grade_id = 3
  AND scs.`year` = 2024