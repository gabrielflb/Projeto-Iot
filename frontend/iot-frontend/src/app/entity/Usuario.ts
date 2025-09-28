export interface Usuario {
  id: number;
  nome: string;
  email: string;
  role: 'ADMIN' | 'USER';
  department?: string;
}