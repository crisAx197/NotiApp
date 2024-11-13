import { Component, OnInit } from '@angular/core';
import { NoticiaService } from '../noticia.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-home',
  standalone: true,
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css'],
  imports: [CommonModule],
  providers: [NoticiaService]
})
export class HomeComponent implements OnInit {
  noticias: any[] = [];  // Cambiado a 'any' para evitar el uso de DTO
  selectedNoticia: any | null = null;
  errorMessage: string = '';

  constructor(private newsService: NoticiaService) {}

  ngOnInit() {
    this.getLastNews();
  }

  getLastNews() {
    this.newsService.getLastNews().subscribe({
      next: (data: any[]) => {  // Cambiado a 'any[]' para coincidir con la respuesta sin DTO
        this.noticias = data;
      },
      error: () => {
        this.errorMessage = 'Error al cargar noticias';
      }
    });
  }

  selectNoticia(noticia: any) {  // Cambiado a 'any'
    this.selectedNoticia = noticia;
  }
}
