SELECT
    scs.clazz_id as clazzId,
    count( scs.student_id ) as studentCount,
    GROUP_CONCAT( ss.`name` )  as studentNameList
FROM
    sys_clazz_student scs
        LEFT JOIN sys_student ss ON scs.student_id = ss.id
WHERE
        scs.YEAR = 2024
GROUP BY
    scs.clazz_id