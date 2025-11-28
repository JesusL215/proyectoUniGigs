import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators, FormsModule, FormArray } from '@angular/forms'; // <--- AGREGAR FormsModule
import { Router } from '@angular/router';
import { InternshipService } from '../../../services/internship.service';

@Component({
    selector: 'app-create-internship',
    standalone: true,
    imports: [
        CommonModule,
        ReactiveFormsModule,
        FormsModule
    ],
    templateUrl: './create-internship.component.html',
    styleUrl: './create-internship.component.css'
})
export class CreateInternshipComponent {
    private fb = inject(FormBuilder);
    private internshipService = inject(InternshipService);
    private router = inject(Router);

    internshipForm: FormGroup;
    loading = false;
    errorMessage = '';
    newSkill = '';

    constructor() {
        this.internshipForm = this.fb.group({
            title: ['', [Validators.required, Validators.minLength(5)]],
            description: ['', [Validators.required, Validators.minLength(20)]],
            location: ['', [Validators.required]],
            duration: ['', [Validators.required]],
            requiredSkills: this.fb.array([], [Validators.required])
        });
    }

    get title() { return this.internshipForm.get('title'); }
    get description() { return this.internshipForm.get('description'); }
    get location() { return this.internshipForm.get('location'); }
    get duration() { return this.internshipForm.get('duration'); }
    get requiredSkills() { return this.internshipForm.get('requiredSkills') as FormArray; }

    addSkill(): void {
        const skill = this.newSkill.trim();
        if (skill && !this.requiredSkills.value.includes(skill)) {
            this.requiredSkills.push(this.fb.control(skill));
            this.newSkill = '';
        }
    }

    removeSkill(index: number): void {
        this.requiredSkills.removeAt(index);
    }

    onSubmit(): void {
        if (this.internshipForm.invalid) {
            this.internshipForm.markAllAsTouched();
            return;
        }

        if (this.requiredSkills.length === 0) {
            this.errorMessage = 'Debes agregar al menos una habilidad requerida';
            return;
        }

        this.loading = true;
        this.errorMessage = '';

        const internshipData = {
            ...this.internshipForm.value,
            isActive: true
        };

        this.internshipService.create(internshipData).subscribe({
            next: () => {
                this.router.navigate(['/company/dashboard']);
            },
            error: (error) => {
                this.loading = false;
                this.errorMessage = error.error?.message || 'Error al crear la pasantía';
            }
        });
    }
}
