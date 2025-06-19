package com.grad.drds.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.grad.drds.entity.ReportRequest;
import com.grad.drds.repository.ReportRequestRepository;

@Service
public class ReportRequestService {

	@Autowired
    private ReportRequestRepository reportRequestRepository;
	
	public ReportRequestService(ReportRequestRepository reportRequestRepository) {
		this.reportRequestRepository = reportRequestRepository;
	}
	
	public ReportRequest createReportRequest(ReportRequest reportRequest) {
        reportRequest.setRequestDate(new Date());
        return reportRequestRepository.save(reportRequest);
    }
	
	public List<ReportRequest> getReports(String patientName, Date startDate, Date endDate) {
		if (patientName != null && !patientName.isEmpty() && startDate != null && endDate != null) {
            return reportRequestRepository.findByPatientNameContainingAndRequestDateBetween(patientName, startDate, endDate);
        } else if (patientName != null && !patientName.isEmpty()) {
            return reportRequestRepository.findByPatientNameContaining(patientName);
        } else if (startDate != null && endDate != null) {
            return reportRequestRepository.findByRequestDateBetween(startDate, endDate);
        } else {
            return reportRequestRepository.findAll();
        }
    }
	
	public ReportRequest getReportById(int id) {
		return reportRequestRepository.findById(id);
	}
	
	public void updateReportRequest(ReportRequest reportRequest) {
		reportRequestRepository.save(reportRequest);
	}
}
