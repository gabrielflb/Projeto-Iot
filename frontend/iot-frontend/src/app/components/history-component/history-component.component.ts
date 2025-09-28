import { Component, OnInit } from '@angular/core';
import { ReservaHistorico } from '../../entity/ReservaHistorico'; 
import { ReservationService } from '../../services/reservation.service';

@Component({
  selector: 'app-history-component',
  templateUrl: './history-component.component.html',
  styleUrl: './history-component.component.css'
})
export class HistoryComponent implements OnInit {
  history: ReservaHistorico[] = [];
  isLoading = true;

  constructor(private reservationService: ReservationService) { }

  ngOnInit(): void {
    this.loadHistory();
  }

  loadHistory(): void {
    this.isLoading = true;
    this.reservationService.getMyHistory().subscribe({
      next: (data) => {
        this.history = data;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Falha ao carregar histórico', err);
        this.isLoading = false;
      }
    });
  }
}