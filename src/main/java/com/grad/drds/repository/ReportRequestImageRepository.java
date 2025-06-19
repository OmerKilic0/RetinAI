package com.grad.drds.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.grad.drds.entity.ReportRequest;
import com.grad.drds.entity.ReportRequestImage;

@Repository
public interface ReportRequestImageRepository extends JpaRepository<ReportRequestImage, Integer>{
	ReportRequestImage findById(int id);
	List<ReportRequestImage> findByReportRequest(ReportRequest reportRequest);
}
