SELECT
    clazz_id AS clazzId,
    student_id AS studentId,
    course_id AS courseId,
    score AS studentScore,
    degree AS studentDegree,
    RANK() OVER ( PARTITION BY clazz_id, course_id ORDER BY score DESC ) AS studentRank
FROM
    sys_score
WHERE
        exam_id = 13
  AND grade_id = 3;