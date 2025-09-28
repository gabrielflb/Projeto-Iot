import { Component, OnInit, OnDestroy } from '@angular/core';
import { Resource, ResourceService } from '../../services/resource.service';
import { ReservationService } from '../../services/reservation.service';
import { AuthService } from '../../services/auth.service'; 
import { Usuario } from '../../entity/Usuario'; 
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit, OnDestroy {
  resources: Resource[] = [];
  isLoading = true;
  currentUser: Usuario | null = null;
  private userSubscription!: Subscription;

  constructor(
    private resourceService: ResourceService,
    private reservationService: ReservationService,
    private authService: AuthService
  ) { }

  ngOnInit(): void {
    this.userSubscription = this.authService.currentUser$.subscribe(user => {
      this.currentUser = user;
    });
    this.loadResources();
  }

  ngOnDestroy(): void {
    if (this.userSubscription) {
      this.userSubscription.unsubscribe();
    }
  }

  loadResources(): void {
    this.isLoading = true;
    this.resourceService.getResources().subscribe({
      next: (data) => {
        this.resources = data;
        this.isLoading = false;
      },
      error: (err) => console.error('Falha ao carregar recursos', err)
    });
  }

  handleReserve(resourceId: number): void {
    const duration = prompt("Por quantos minutos você deseja reservar?", "30");
    if (duration && !isNaN(Number(duration))) {
      this.reservationService.createReservation(resourceId, Number(duration)).subscribe({
        next: () => {
          alert('Recurso reservado com sucesso!');
          this.loadResources();
        },
        error: (err) => alert(`Falha ao reservar: ${err.error}`)
      });
    }
  }

  handleRelease(resourceId: number): void {
    if (confirm('Tem certeza que deseja liberar este recurso?')) {
      this.resourceService.releaseResource(resourceId).subscribe({     
        next: () => {
          alert('Recurso liberado com sucesso!');
          this.loadResources(); 
        },
        error: (err) => {
          alert(`Falha ao liberar: ${err.error}`);
          console.error(err);
        }
      });
    }
  }
}