import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface MatchResponse {
  score: number;
  matchedSkills: string[];
  missingSkills: string[];
}

export interface SemanticMatchResponse extends MatchResponse {
  engine: string;
  summary: string;
  recommendations: string[];
}

export interface JobApplication {
  id: number;
  company: string;
  role: string;
  jobUrl?: string;
  status: string;
  createdAt: string;
  updatedAt: string;
}

@Injectable({ providedIn: 'root' })
export class ApiService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = '/api/v1';

  match(resumeText: string, jobDescription: string): Observable<MatchResponse> {
    return this.http.post<MatchResponse>(`${this.baseUrl}/matches`, { resumeText, jobDescription });
  }

  semanticMatch(resumeText: string, jobDescription: string): Observable<SemanticMatchResponse> {
    return this.http.post<SemanticMatchResponse>(`${this.baseUrl}/matches/semantic`, { resumeText, jobDescription });
  }

  applications(): Observable<JobApplication[]> {
    return this.http.get<JobApplication[]>(`${this.baseUrl}/applications`);
  }

  createApplication(company: string, role: string, jobUrl: string): Observable<JobApplication> {
    return this.http.post<JobApplication>(`${this.baseUrl}/applications`, {
      company, role, jobUrl, status: 'APPLIED'
    });
  }

  updateStatus(id: number, status: string): Observable<JobApplication> {
    return this.http.patch<JobApplication>(`${this.baseUrl}/applications/${id}/status`, { status });
  }
}
