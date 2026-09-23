SELECT
    *,
    DENSE_RANK() OVER ( PARTITION BY clazzId ORDER BY studentScore DESC ) studentRank
FROM
    (
        SELECT
            clazz_id AS clazzId,
            student_id AS studentId,
            sum( score ) AS studentScore
        FROM
            sys_score
        WHERE
                exam_id = 13
          AND grade_Id = 3
        GROUP BY
            clazz_id,
            student_id
    ) score_summary
