package com.grad.drds.entity;

import java.util.Date;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;

@Entity
@Table(name = "report_requests")
public class ReportRequest {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    private String patientName;
    private String patientSurname;
    
    @Column(length=11)
    private String patientTckn;
    
    @ManyToOne
    @JoinColumn(name = "requester_user_id", nullable = false)
    private User_ requester;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date requestDate;
    private String result;
    private String resultConfidence;
    private String resultSentence;
    private String resultDrLevel;
    private String resultNotes;
    private String severityDistribution;
    private String pdfPath;
    
    @OneToMany(mappedBy = "reportRequest", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReportRequestImage> images;
    
    @Transient
    private List<String> base64Images;
    
    @PrePersist
    protected void onCreate() {
        this.requestDate = new Date();
    }
    
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getPatientName() {
		return patientName;
	}
	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}
	public String getPatientSurname() {
		return patientSurname;
	}
	public void setPatientSurname(String patientSurname) {
		this.patientSurname = patientSurname;
	}
	public String getPatientTckn() {
		return patientTckn;
	}
	public void setPatientTckn(String patientTckn) {
		this.patientTckn = patientTckn;
	}
	public User_ getRequester() {
		return requester;
	}
	public void setRequester(User_ requester) {
		this.requester = requester;
	}
	public Date getRequestDate() {
		return requestDate;
	}
	public void setRequestDate(Date requestDate) {
		this.requestDate = requestDate;
	}
	public String getResultDrLevel() {
		return resultDrLevel;
	}
	public void setResultDrLevel(String resultDrLevel) {
		this.resultDrLevel = resultDrLevel;
	}
	public String getResultNotes() {
		return resultNotes;
	}
	public void setResultNotes(String resultNotes) {
		this.resultNotes = resultNotes;
	}
	public String getPdfPath() {
		return pdfPath;
	}
	public void setPdfPath(String pdfPath) {
		this.pdfPath = pdfPath;
	}
	public List<ReportRequestImage> getImages() {
		return images;
	}
	public void setImages(List<ReportRequestImage> images) {
		this.images = images;
	}
	
	public List<String> getBase64Images() {
        return base64Images;
    }

    public void setBase64Images(List<String> base64Images) {
        this.base64Images = base64Images;
    }

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getResultConfidence() {
		return resultConfidence;
	}

	public void setResultConfidence(String resultConfidence) {
		this.resultConfidence = resultConfidence;
	}

	public String getResultSentence() {
		return resultSentence;
	}

	public void setResultSentence(String resultSentence) {
		this.resultSentence = resultSentence;
	}

	public String getSeverityDistribution() {
		return severityDistribution;
	}

	public void setSeverityDistribution(String severityDistribution) {
		this.severityDistribution = severityDistribution;
	}
}