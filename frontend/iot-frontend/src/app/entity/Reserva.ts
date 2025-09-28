export interface Reserva {
  id: number;
  resourceId: number;
  userId: number;
  startTime: Date;
  endTime?: Date;
  status: 'ATIVA' | 'FINALIZADA' | 'CANCELADA';
}