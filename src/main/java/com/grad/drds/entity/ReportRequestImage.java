package com.grad.drds.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "report_request_images")
public class ReportRequestImage {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
	@Lob
    private byte[] imageData;
	
    @ManyToOne
    @JoinColumn(name = "report_request_id", nullable = false)
    private ReportRequest reportRequest;
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public byte[] getImageData() {
		return imageData;
	}

	public void setImageData(byte[] imageData) {
		this.imageData = imageData;
	}

	public ReportRequest getReportRequest() {
		return reportRequest;
	}

	public void setReportRequest(ReportRequest reportRequest) {
		this.reportRequest = reportRequest;
	}
}