import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface MatchResponse { score:number; matchedSkills:string[]; missingSkills:string[]; }
export interface SemanticMatchResponse extends MatchResponse { engine:string; requiredSkills:string[]; preferredSkills:string[]; technicalCoverage:number; experienceFit:number; roleFit:number; evidenceCount:number; summary:string; recommendations:string[]; }
export interface ResumeDocument { fileName:string; fileType:string; text:string; characters:number; }
export interface JobApplication { id:number; company:string; role:string; jobUrl?:string; status:string; createdAt:string; updatedAt:string; }
@Injectable({providedIn:'root'}) export class ApiService {
 private readonly http=inject(HttpClient); private readonly baseUrl='/api/v1';
 semanticMatch(resumeText:string,jobDescription:string):Observable<SemanticMatchResponse>{return this.http.post<SemanticMatchResponse>(`${this.baseUrl}/matches/semantic`,{resumeText,jobDescription});}
 extractResume(file:File):Observable<ResumeDocument>{const data=new FormData();data.append('file',file);return this.http.post<ResumeDocument>(`${this.baseUrl}/resumes/extract`,data);}
 applications():Observable<JobApplication[]>{return this.http.get<JobApplication[]>(`${this.baseUrl}/applications`);}
 createApplication(company:string,role:string,jobUrl:string):Observable<JobApplication>{return this.http.post<JobApplication>(`${this.baseUrl}/applications`,{company,role,jobUrl,status:'APPLIED'});}
 updateApplication(id:number,company:string,role:string,jobUrl:string):Observable<JobApplication>{return this.http.put<JobApplication>(`${this.baseUrl}/applications/${id}`,{company,role,jobUrl});}
 updateStatus(id:number,status:string):Observable<JobApplication>{return this.http.patch<JobApplication>(`${this.baseUrl}/applications/${id}/status`,{status});}
 deleteApplication(id:number):Observable<void>{return this.http.delete<void>(`${this.baseUrl}/applications/${id}`);}
}
