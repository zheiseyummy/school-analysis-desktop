SELECT
    scs.student_id as studentId,
    count( scs.clazz_id ) as clazzCount,
    GROUP_CONCAT( scs.`year`, '/', sg.`name`,'/', sc.`name` )  as clazzNameList
FROM
    sys_clazz_student scs
        LEFT JOIN sys_clazz sc ON scs.clazz_id = sc.id
        LEFT JOIN sys_grade sg on sc.grade_id = sg.id
GROUP BY
    scs.student_id;