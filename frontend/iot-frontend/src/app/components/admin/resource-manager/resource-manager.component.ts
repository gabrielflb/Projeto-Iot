import { Component, OnInit } from '@angular/core';
import { Resource, ResourceService } from '../../../services/resource.service';



@Component({
  selector: 'app-resource-manager',
  templateUrl: './resource-manager.component.html',
  styleUrls: ['./resource-manager.component.css']
})
export class ResourceManagerComponent implements OnInit {
  resources: Resource[] = [];
  newResource = { name: '', type: '' };

  constructor(private resourceService: ResourceService){}

  ngOnInit(): void {
    this.loadResources();
  }

  loadResources(): void {
    this.resourceService.getResources().subscribe(data => this.resources = data);
  }

  addResource(): void {
    this.resourceService.createResource(this.newResource).subscribe(() => {
      this.loadResources();
      this.newResource = { name: '', type: '' }; 
    });
  }

  deleteResource(id: number): void {
    if (confirm('Tem certeza que deseja excluir este recurso?')) {
      this.resourceService.deleteResource(id).subscribe(() => this.loadResources());
    }
  }
}