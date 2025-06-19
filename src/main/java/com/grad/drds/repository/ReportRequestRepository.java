package com.grad.drds.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.grad.drds.entity.ReportRequest;
import com.grad.drds.entity.User_;

@Repository
public interface ReportRequestRepository extends JpaRepository<ReportRequest, Integer>{
	ReportRequest findById(int id);
	List<ReportRequest> findByRequester(User_ requester);
	
	List<ReportRequest> findByPatientNameContainingAndRequestDateBetween(String patientName, Date startDate, Date endDate);

    List<ReportRequest> findByPatientNameContaining(String patientName);

    List<ReportRequest> findByRequestDateBetween(Date startDate, Date endDate);

    List<ReportRequest> findAll();
}
