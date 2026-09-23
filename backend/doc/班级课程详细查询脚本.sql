SELECT
    sa.id AS id,
    scz.grade_id AS gradeId,
    sg.`name` AS gradeName,
    sa.clazz_id AS clazzId,
    scz.`name` AS clazzName,
    sa.course_id AS courseId,
    sc.`name` AS courseName,
    sa.teacher_id AS teacherId,
    st.`name` AS teacherName
FROM
    sys_arrange sa
        LEFT JOIN sys_clazz scz ON sa.clazz_id = scz.id
        LEFT JOIN sys_grade sg ON scz.grade_id = sg.id
        LEFT JOIN sys_course sc ON sa.course_id = sc.id
        LEFT JOIN sys_teacher st ON sa.teacher_id = st.id