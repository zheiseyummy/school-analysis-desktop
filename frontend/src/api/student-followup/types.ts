export interface StudentFollowup {
  id?: number;
  studentId?: number;
  learningStatus: string;
  specialSituation?: string;
  followupContent: string;
  nextAction?: string;
  followupDate: string;
  createTime?: string;
  updateTime?: string;
}

export type StudentFollowupForm = Omit<
  StudentFollowup,
  "id" | "studentId" | "createTime" | "updateTime"
>;
