import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ApiService, JobApplication, SemanticMatchResponse } from './api.service';

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
  matchResult?: SemanticMatchResponse;
  applications: JobApplication[] = [];
  loadingMatch = false;
  error = '';
  editingId?: number;
  editCompany = '';
  editRole = '';
  editJobUrl = '';

  readonly statuses = ['SAVED', 'APPLIED', 'ASSESSMENT', 'INTERVIEW', 'OFFER', 'REJECTED', 'WITHDRAWN'];

  ngOnInit(): void {
    this.loadApplications();
  }

  analyze(): void {
    if (!this.resumeText.trim() || !this.jobDescription.trim()) return;
    this.loadingMatch = true;
    this.error = '';
    this.api.semanticMatch(this.resumeText, this.jobDescription).subscribe({
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
    this.error = '';
    this.api.createApplication(this.company, this.role, this.jobUrl).subscribe({
      next: application => {
        this.applications = [application, ...this.applications];
        this.company = '';
        this.role = '';
        this.jobUrl = '';
      },
      error: response => this.error = response.status === 409
        ? 'That company and role are already in your tracker.'
        : 'Could not save the application.'
    });
  }

  startEdit(application: JobApplication): void {
    this.editingId = application.id;
    this.editCompany = application.company;
    this.editRole = application.role;
    this.editJobUrl = application.jobUrl ?? '';
    this.error = '';
  }

  cancelEdit(): void {
    this.editingId = undefined;
  }

  saveEdit(application: JobApplication): void {
    if (!this.editCompany.trim() || !this.editRole.trim()) return;
    this.api.updateApplication(application.id, this.editCompany, this.editRole, this.editJobUrl).subscribe({
      next: updated => {
        this.applications = this.applications.map(item => item.id === updated.id ? updated : item);
        this.editingId = undefined;
      },
      error: response => this.error = response.status === 409
        ? 'Another application with that company and role already exists.'
        : 'Could not update the application.'
    });
  }

  deleteApplication(application: JobApplication): void {
    if (!window.confirm(`Delete ${application.role} at ${application.company}?`)) return;
    this.api.deleteApplication(application.id).subscribe({
      next: () => this.applications = this.applications.filter(item => item.id !== application.id),
      error: () => this.error = 'Could not delete the application.'
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
