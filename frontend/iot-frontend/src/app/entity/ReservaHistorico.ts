export interface ReservaHistorico {
  id: number;
  resource: {
    id: number;
    name: string;
  };
  tempoInicial: string;
  tempoFinal: string;
  status: string;
}