import { Component, OnInit, HostListener } from '@angular/core';
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
  private cacheLastNews: any[] | null = null;
  private cacheRecomendaciones: { [key: number]: any[] } = {};
  private cacheFilteredNews: { [key: string]: any[] } = {};
  private lastLoadedDate: string | null = null;

  constructor(private newsService: NoticiaService) {}

  ngOnInit() {
    this.getLastNews();
  }

  getLastNews() {
    if (this.cacheLastNews) {
      this.noticiasPorFecha = this.agruparNoticiasPorFecha(this.cacheLastNews);
      this.lastLoadedDate = this.getOldestDate(this.cacheLastNews);
    } else {
      this.newsService.getLastNews().subscribe({
        next: (data: any[]) => {
          const newsWithoutLastDate = data.filter(noticia => {
            const noticiaDate = new Date(noticia.fechaPublicacion).toISOString().split('T')[0];
            return noticiaDate !== this.getOldestDate(data);
          });
          this.noticiasPorFecha = this.agruparNoticiasPorFecha(newsWithoutLastDate);
          this.cacheLastNews = data;
          this.lastLoadedDate = this.getOldestDate(data);
        },
        error: () => {
          this.errorMessage = 'Error al cargar noticias';
        }
      });
    }
  }

  loadMoreNews() {
    if (!this.lastLoadedDate) return;

    const startDate = this.lastLoadedDate;
    const endDate = startDate;

    const currentMonth = new Date().getMonth();
    const dateToCheck = new Date(startDate);

    if (dateToCheck.getMonth() !== currentMonth) return;

    this.newsService.getFilteredNews(undefined, startDate, endDate).subscribe({
      next: (data: any[]) => {
        if (data.length > 0) {
          const newNoticiasPorFecha = this.agruparNoticiasPorFecha(data);
          Object.assign(this.noticiasPorFecha, newNoticiasPorFecha);
          this.lastLoadedDate = this.getOldestDate(data);
        } else {
          if (this.lastLoadedDate) {
            this.lastLoadedDate = this.decrementDateByOneDay(this.lastLoadedDate);
            this.loadMoreNews();
          }
        }
      },
      error: () => {
        this.errorMessage = 'Error al cargar más noticias';
      }
    });
  }


  decrementDateByOneDay(dateString: string): string {
    const date = new Date(dateString);
    date.setDate(date.getDate() - 1);
    return date.toISOString().split('T')[0];
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
    if (this.cacheRecomendaciones[idNoticia]) {
      this.recomendaciones = this.cacheRecomendaciones[idNoticia];
    } else {
      this.newsService.getLastNews().subscribe({
        next: (data: any[]) => {
          this.recomendaciones = data.filter(noticia => noticia.id !== idNoticia).slice(0, 3);
          this.cacheRecomendaciones[idNoticia] = this.recomendaciones;
        },
        error: () => {
          this.errorMessage = 'Error al cargar recomendaciones';
        }
      });
    }
  }

  filterByCategory(category: string) {
    if (this.cacheFilteredNews[category]) {
      this.filteredNoticias = this.cacheFilteredNews[category];
      this.filterApplied = true;
    } else {
      this.newsService.getFilteredNews(category).subscribe({
        next: (data: any[]) => {
          this.filteredNoticias = data;
          this.filterApplied = true;
          this.cacheFilteredNews[category] = data;
        },
        error: () => {
          this.errorMessage = 'Error al cargar noticias filtradas';
        }
      });
    }
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

  get fechas() {
    return Object.keys(this.noticiasPorFecha);
  }

  private getOldestDate(noticias: any[]): string | null {
    if (!noticias.length) return null;
    const oldest = noticias.reduce((prev, current) =>
      new Date(prev.fechaPublicacion) < new Date(current.fechaPublicacion) ? prev : current
    );
    return new Date(oldest.fechaPublicacion).toISOString().split('T')[0];
  }

  @HostListener('window:scroll', [])
  onScroll(): void {
    if ((window.innerHeight + window.scrollY) >= document.body.scrollHeight) {
      this.loadMoreNews();
    }
  }
}
