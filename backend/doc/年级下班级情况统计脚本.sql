SELECT
    grade_id as gradeId,
    count( id ) as clazzCount,
    GROUP_CONCAT( NAME )  as clazzNameList
FROM
    sys_clazz
GROUP BY
    grade_id