import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ApiService, JobApplication, SemanticMatchResponse } from './api.service';

@Component({ selector: 'app-root', standalone: true, imports: [CommonModule, FormsModule], templateUrl: './app.component.html' })
export class AppComponent implements OnInit {
  private readonly api = inject(ApiService);
  resumeText=''; jobDescription=''; company=''; role=''; jobUrl='';
  matchResult?: SemanticMatchResponse; applications: JobApplication[]=[];
  loadingMatch=false; loadingResume=false; error=''; resumeFileName='';
  editingId?:number; editCompany=''; editRole=''; editJobUrl='';
  readonly statuses=['SAVED','APPLIED','ASSESSMENT','INTERVIEW','OFFER','REJECTED','WITHDRAWN'];
  ngOnInit():void { this.loadApplications(); }

  uploadResume(event: Event):void {
    const input=event.target as HTMLInputElement; const file=input.files?.[0]; if(!file)return;
    this.loadingResume=true; this.error=''; this.resumeFileName=file.name;
    this.api.extractResume(file).subscribe({
      next:doc=>{this.resumeText=doc.text;this.resumeFileName=`${doc.fileName} · ${doc.fileType} · ${doc.characters.toLocaleString()} characters`;this.loadingResume=false;},
      error:r=>{this.error=r.error?.message ?? 'Could not read this resume file.';this.loadingResume=false;this.resumeFileName='';}
    });
  }
  analyze():void { if(!this.resumeText.trim()||!this.jobDescription.trim())return;this.loadingMatch=true;this.error='';this.api.semanticMatch(this.resumeText,this.jobDescription).subscribe({next:r=>{this.matchResult=r;this.loadingMatch=false;},error:()=>{this.error='Could not reach the matching API.';this.loadingMatch=false;}}); }
  addApplication():void { if(!this.company.trim()||!this.role.trim())return;this.error='';this.api.createApplication(this.company,this.role,this.jobUrl).subscribe({next:a=>{this.applications=[a,...this.applications];this.company='';this.role='';this.jobUrl='';},error:r=>this.error=r.status===409?'That company and role are already in your tracker.':'Could not save the application.'}); }
  startEdit(a:JobApplication):void {this.editingId=a.id;this.editCompany=a.company;this.editRole=a.role;this.editJobUrl=a.jobUrl??'';this.error='';}
  cancelEdit():void {this.editingId=undefined;}
  saveEdit(a:JobApplication):void {if(!this.editCompany.trim()||!this.editRole.trim())return;this.api.updateApplication(a.id,this.editCompany,this.editRole,this.editJobUrl).subscribe({next:u=>{this.applications=this.applications.map(i=>i.id===u.id?u:i);this.editingId=undefined;},error:r=>this.error=r.status===409?'Another application with that company and role already exists.':'Could not update the application.'});}
  deleteApplication(a:JobApplication):void {if(!window.confirm(`Delete ${a.role} at ${a.company}?`))return;this.api.deleteApplication(a.id).subscribe({next:()=>this.applications=this.applications.filter(i=>i.id!==a.id),error:()=>this.error='Could not delete the application.'});}
  changeStatus(a:JobApplication,status:string):void {this.api.updateStatus(a.id,status).subscribe({next:u=>this.applications=this.applications.map(i=>i.id===u.id?u:i),error:()=>this.error='Could not update application status.'});}
  private loadApplications():void {this.api.applications().subscribe({next:a=>this.applications=a,error:()=>this.error='Backend is offline. Start the Spring Boot API to use live data.'});}
}
