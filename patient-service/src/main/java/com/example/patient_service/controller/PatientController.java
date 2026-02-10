package com.example.patient_service.controller;

import com.example.patient_service.dto.PatientRequestDTO;
import com.example.patient_service.dto.PatientResponseDTO;
import com.example.patient_service.dto.validators.CreatePatientValidationGroup;
import com.example.patient_service.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.groups.Default;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/patient")
@Tag(name = "Patient", description = "API for managing patients")
public class PatientController {
    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping
    @Operation(summary = "Get patients")
    public ResponseEntity<List<PatientResponseDTO>> getPatient() {
        List<PatientResponseDTO> patients = patientService.getPatient();

        return ResponseEntity.ok().body(patients);
    }

    @PostMapping
    @Operation(summary = "Create new patient")
    public ResponseEntity<PatientResponseDTO> createPatient(@Validated({Default.class, CreatePatientValidationGroup.class}) @RequestBody PatientRequestDTO patientRequestDTO) {
        PatientResponseDTO patientResponseDTO = patientService.createPatient(patientRequestDTO);

        return ResponseEntity.ok().body(patientResponseDTO);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update patient")
    public ResponseEntity<PatientResponseDTO> updatePatient(@PathVariable UUID id,@Validated({Default.class}) @RequestBody PatientRequestDTO patientRequestDTO){
        PatientResponseDTO patientResponseDTO = patientService.updatePatient(id, patientRequestDTO);

        return ResponseEntity.ok().body(patientResponseDTO);
    }


    @DeleteMapping("/{id}")
    @Operation(summary = "Delete patient")
    public ResponseEntity<Void> deletePatient(@PathVariable UUID id){
        patientService.deletePatient(id);
        return ResponseEntity.noContent().build();
    }



}
