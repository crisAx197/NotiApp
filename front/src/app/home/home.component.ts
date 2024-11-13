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
  noticiasPorFecha: { [key: string]: any[] } = {};
  selectedNoticia: any | null = null;
  recomendaciones: any[] = [];
  filteredNoticias: any[] = [];
  errorMessage: string = '';
  categories = ['Política', 'Economía', 'Tecnología', 'Cultura', 'Deportes'];
  view: 'latest' | 'category' = 'latest';
  filterApplied: boolean = false;

  constructor(private newsService: NoticiaService) {}

  ngOnInit() {
    this.getLastNews();
  }

  getLastNews() {
    this.newsService.getLastNews().subscribe({
      next: (data: any[]) => {
        this.noticiasPorFecha = this.agruparNoticiasPorFecha(data);
      },
      error: () => {
        this.errorMessage = 'Error al cargar noticias';
      }
    });
  }

  agruparNoticiasPorFecha(noticias: any[]): { [key: string]: any[] } {
    return noticias.reduce((acc, noticia) => {
      const fecha = new Date(noticia.fechaPublicacion).toLocaleDateString();
      if (!acc[fecha]) {
        acc[fecha] = [];
      }
      acc[fecha].push(noticia);
      return acc;
    }, {} as { [key: string]: any[] });
  }

  selectNoticia(noticia: any) {
    this.newsService.getNoticiaById(noticia.id).subscribe({
      next: (data: any) => {
        this.selectedNoticia = data;
        this.getRecomendaciones(data.id);
      },
      error: () => {
        this.errorMessage = 'Error al cargar la noticia';
      }
    });
  }

  getRecomendaciones(idNoticia: number) {
    this.newsService.getLastNews().subscribe({
      next: (data: any[]) => {
        this.recomendaciones = data.filter(noticia => noticia.id !== idNoticia).slice(0, 3);
      },
      error: () => {
        this.errorMessage = 'Error al cargar recomendaciones';
      }
    });
  }

  filterByCategory(category: string) {
    this.newsService.getFilteredNews(category).subscribe({
      next: (data: any[]) => {
        this.filteredNoticias = data;
        this.filterApplied = true;
      },
      error: () => {
        this.errorMessage = 'Error al cargar noticias filtradas';
      }
    });
  }

  get fechas() {
    return Object.keys(this.noticiasPorFecha);
  }
}
