export interface StudentSubjectSelectionVersion {
  id?: number;
  gradeId: number;
  name: string;
  effectiveDate?: string;
  status?: number;
  note?: string;
}

export interface StudentSubjectSelectionRecord {
  id?: number;
  studentId: number;
  studentCode?: string;
  studentName?: string;
  trackCourseId?: number;
  trackCourseName?: string;
  electiveCourseIds?: number[];
  electiveCourseNames?: string[];
  combinationCode: string;
  source?: string;
  note?: string;
}
