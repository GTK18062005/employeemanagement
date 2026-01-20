package com.staffmanagement.repository;

import com.staffmanagement.model.EmployeeDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeDocumentRepository extends JpaRepository<EmployeeDocument, Long> {
    
    List<EmployeeDocument> findByUsernameOrderByUploadedDateDesc(String username);
    
    List<EmployeeDocument> findByDocumentTypeOrderByUploadedDateDesc(String documentType);
    
    List<EmployeeDocument> findByUsernameAndDocumentTypeOrderByUploadedDateDesc(String username, String documentType);
    
    List<EmployeeDocument> findByIsVerifiedFalse();
    
    long countByUsername(String username);
    
    long countByUsernameAndDocumentType(String username, String documentType);
}