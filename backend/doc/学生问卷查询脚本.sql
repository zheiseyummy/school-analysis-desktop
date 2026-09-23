SELECT
    sis.id AS investigationStudentId,
    sis.investigation_id AS investigationId,
    si.`name` AS investigationName,
    sis.`year` AS `year`,
    sis.grade_id AS gradeId,
    sis.grade_name AS gradeName,
    sis.clazz_id AS clazzId,
    sis.clazz_name AS clazzName,
    sis.student_id AS studentId,
    ss.`name` AS studentName,
    si.course_id AS courseId,
    sc.`name` AS courseName,
    si.exam_id AS examId,
    se.`name` AS examName,
    si.teacher_id AS teacherId,
    st.`name` AS teacherName
FROM
    sys_investigation_student sis
        LEFT JOIN sys_investigation si ON sis.investigation_id = si.id
        LEFT JOIN sys_student ss ON sis.student_id = ss.id
        LEFT JOIN sys_course sc ON si.course_id = sc.id
        LEFT JOIN sys_exam se ON si.exam_id = se.id
        LEFT JOIN sys_teacher st ON si.teacher_id = st.id