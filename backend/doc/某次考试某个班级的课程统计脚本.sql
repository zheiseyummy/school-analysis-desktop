-- 某次考试某个班级课程统计情况

SELECT
    course_id AS courseId,
    ROUND(avg( score )) AS avgScore,
    max( score ) AS maxScore,
    min( score )  AS minScore,
    COUNT(CASE WHEN degree = 1 THEN 1 END) AS aCount,
    COUNT(CASE WHEN degree = 2 THEN 1 END) AS bCount,
    COUNT(CASE WHEN degree = 3 THEN 1 END) AS cCount,
    COUNT(CASE WHEN degree = 4 THEN 1 END) AS dCount,
    COUNT(CASE WHEN degree = 5 THEN 1 END) AS eCount
FROM
    sys_score
WHERE
        exam_id = 13
  AND clazz_id = 3
GROUP BY
    course_id;