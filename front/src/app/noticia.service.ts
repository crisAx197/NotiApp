import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class NoticiaService {
  private apiUrl = 'http://localhost:8080/news';

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('jwtToken');
    return new HttpHeaders({
      Authorization: `Bearer ${token}`
    });
  }

  getLastNews(): Observable<any> {
    return this.http.get(`${this.apiUrl}/lastNews`, { headers: this.getHeaders() });
  }

  getRecommendedNews(userId: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/recommendations/${userId}`, { headers: this.getHeaders() });
  }

  getNoticiaById(id: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/${id}`, { headers: this.getHeaders() });
  }

  getFilteredNews(categoria?: string, startDate?: string, endDate?: string): Observable<any> {
    let url = `${this.apiUrl}/filter?`;

    if (categoria) url += `categoria=${categoria}&`;

    if (startDate) {
      const formattedStartDate = `${startDate}T00:00:00`;
      url += `startDate=${formattedStartDate}&`;
    }

    if (endDate) {
      const formattedEndDate = `${endDate}T23:59:59`;
      url += `endDate=${formattedEndDate}&`;
    }

    return this.http.get(url, { headers: this.getHeaders() });
  }
}
