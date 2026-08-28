import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ApiService, JobApplication, MatchResponse } from './api.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './app.component.html'
})
export class AppComponent implements OnInit {
  private readonly api = inject(ApiService);

  resumeText = '';
  jobDescription = '';
  company = '';
  role = '';
  jobUrl = '';
  matchResult?: MatchResponse;
  applications: JobApplication[] = [];
  loadingMatch = false;
  error = '';

  readonly statuses = ['SAVED', 'APPLIED', 'ASSESSMENT', 'INTERVIEW', 'OFFER', 'REJECTED', 'WITHDRAWN'];

  ngOnInit(): void {
    this.loadApplications();
  }

  analyze(): void {
    if (!this.resumeText.trim() || !this.jobDescription.trim()) return;
    this.loadingMatch = true;
    this.error = '';
    this.api.match(this.resumeText, this.jobDescription).subscribe({
      next: result => {
        this.matchResult = result;
        this.loadingMatch = false;
      },
      error: () => {
        this.error = 'Could not reach the matching API.';
        this.loadingMatch = false;
      }
    });
  }

  addApplication(): void {
    if (!this.company.trim() || !this.role.trim()) return;
    this.api.createApplication(this.company, this.role, this.jobUrl).subscribe({
      next: application => {
        this.applications = [application, ...this.applications];
        this.company = '';
        this.role = '';
        this.jobUrl = '';
      },
      error: () => this.error = 'Could not save the application.'
    });
  }

  changeStatus(application: JobApplication, status: string): void {
    this.api.updateStatus(application.id, status).subscribe({
      next: updated => this.applications = this.applications.map(item => item.id === updated.id ? updated : item),
      error: () => this.error = 'Could not update application status.'
    });
  }

  private loadApplications(): void {
    this.api.applications().subscribe({
      next: applications => this.applications = applications,
      error: () => this.error = 'Backend is offline. Start the Spring Boot API to use live data.'
    });
  }
}
