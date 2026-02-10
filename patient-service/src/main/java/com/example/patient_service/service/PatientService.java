package com.example.patient_service.service;

import com.example.patient_service.dto.PatientRequestDTO;
import com.example.patient_service.dto.PatientResponseDTO;
import com.example.patient_service.exception.EmailAlreadyExistException;
import com.example.patient_service.exception.PatientNotFoundException;
import com.example.patient_service.grpc.BillingServiceGrpcClient;
import com.example.patient_service.kafka.PatientProducer;
import com.example.patient_service.mapper.PatientMapper;
import com.example.patient_service.model.Patient;
import com.example.patient_service.repository.PatientRepository;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class PatientService {
    private PatientRepository patientRepository;
    private final BillingServiceGrpcClient billingServiceGrpcClient;
    private final PatientProducer patientProducer;

    public PatientService(PatientRepository patientRepository,BillingServiceGrpcClient billingServiceGrpcClient,PatientProducer patientProducer) {
        this.patientRepository = patientRepository;
        this.billingServiceGrpcClient = billingServiceGrpcClient;
        this.patientProducer = patientProducer;
    }

    public List<PatientResponseDTO> getPatient(){
        List<Patient> patients = patientRepository.findAll();

        return patients.stream()
                .map(PatientMapper::toDTO).toList();
    }

    public PatientResponseDTO createPatient(PatientRequestDTO patientRequestDTO){
        if(patientRepository.existsByEmail(patientRequestDTO.getEmail())){
            throw new EmailAlreadyExistException("A patient with this Email is already exist"+ patientRequestDTO.getEmail());
        }

        Patient newPatient = patientRepository.save(PatientMapper.toModel(patientRequestDTO));

        billingServiceGrpcClient.createBillingAccount(newPatient.getId().toString(), newPatient.getName(), newPatient.getEmail());

        patientProducer.sendEvent(newPatient);
        return PatientMapper.toDTO(newPatient);
    }


    public PatientResponseDTO updatePatient(UUID id, PatientRequestDTO patientRequestDTO){
        Patient patient = patientRepository.findById(id).orElseThrow(()-> new PatientNotFoundException("Patient not find with id: "+ id));

        if(patientRepository.existsByEmailAndIdNot(patientRequestDTO.getEmail(),id)){
            throw new EmailAlreadyExistException("A patient with this Email is already exist"+ patientRequestDTO.getEmail());
        }

        patient.setName(patientRequestDTO.getName());
        patient.setAddress(patientRequestDTO.getAddress());
        patient.setEmail(patientRequestDTO.getEmail());
        patient.setDateOfBirth(LocalDate.parse(patientRequestDTO.getDateOfBirth()));

        Patient updatedPatient = patientRepository.save(patient);

        return PatientMapper.toDTO(updatedPatient);
    }

    public void deletePatient(UUID id){
        patientRepository.deleteById(id);
    }


}
