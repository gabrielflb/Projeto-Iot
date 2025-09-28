export interface Recurso {
  id: number;
  name: string;
  type: string;
  status: 'LIVRE' | 'OCUPADO';
  capacity?: number;
  location?: string;
  lastUsed?: string;
}