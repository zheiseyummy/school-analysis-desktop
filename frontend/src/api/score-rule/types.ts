export interface ScoreRule {
  id?: number;
  name: string;
  method: string;
  targetFullScore?: number;
  configJson?: string;
  status?: number;
  remark?: string;
}
