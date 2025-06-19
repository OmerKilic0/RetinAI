package com.grad.drds.controller;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.grad.drds.entity.ReportRequest;
import com.grad.drds.entity.ReportRequestImage;
import com.grad.drds.entity.User_;
import com.grad.drds.repository.ReportRequestImageRepository;
import com.grad.drds.service.ReportRequestService;
import com.grad.drds.service.UserService;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;

@Controller
@RequestMapping("")
public class ReportController {

	@Autowired
    private ReportRequestService reportRequestService;
	
	@Autowired
	private UserService userService;
	
	@Autowired 
	private ReportRequestImageRepository reportRequestImageRepository;
	
	public ReportController(ReportRequestService reportRequestService, UserService userService,
			ReportRequestImageRepository reportRequestImageRepository) {
		this.reportRequestService = reportRequestService;
		this.userService = userService;
		this.reportRequestImageRepository = reportRequestImageRepository;
	}
	
	@PostMapping("/submit")
    public String submitReport(@RequestParam("patientType") String patientType, @RequestParam("patientName") String patientName,
            @RequestParam("patientSurname") String patientSurname, @RequestParam("patientTckn") String patientTckn,
           @RequestParam("images") MultipartFile[] images, Model model) {
        
		User_ user = userService.authenticateUser();
		model.addAttribute("fullName", user.getName() + " " + user.getSurname());
		
        ReportRequest reportRequest = new ReportRequest();
        reportRequest.setRequester(user);
        reportRequest.setPatientName(patientName);
        reportRequest.setPatientSurname(patientSurname);
        reportRequest.setPatientTckn(patientTckn.replaceAll("[^0-9]", "").trim());

        reportRequest = reportRequestService.createReportRequest(reportRequest);
        
        List<byte[]> imageList = new ArrayList<>();
        
        for (MultipartFile image : images) {
        	if (!image.isEmpty()) {
                try {
                    ReportRequestImage reportImage = new ReportRequestImage();
                    reportImage.setReportRequest(reportRequest);
                    reportImage.setImageData(image.getBytes());
                    reportRequestImageRepository.save(reportImage);
                    
                    imageList.add(image.getBytes());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        
        Map<String, Object> aiResult = callAIModel(imageList);

        String result = (String) aiResult.get("result");
        String severity = (String) aiResult.get("severity");
        String resultConfidence = (String) aiResult.get("result_confidence");
        List<Double> probabilities = ((List<?>) aiResult.get("probabilities"))
                .stream()
                .map(Object::toString)
                .map(Double::parseDouble)
                .collect(Collectors.toList());

        reportRequest.setResult(result);
        reportRequest.setResultDrLevel(severity);
        reportRequest.setSeverityDistribution(probabilities.toString());
        
        String confidenceStr = resultConfidence.split(" with ")[0].replace("The patient has ", "").trim();
        reportRequest.setResultSentence(result + " with " + confidenceStr + " probability.");
        reportRequestService.updateReportRequest(reportRequest);
        
        return "redirect:/reports/" + reportRequest.getId();
	}
	
	@SuppressWarnings("unchecked")
	private Map<String, Object> callAIModel(List<byte[]> images) {
	    RestTemplate restTemplate = new RestTemplate();
	    
	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.MULTIPART_FORM_DATA);
	    
	    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
	    for (byte[] image : images) {
	        body.add("images", new ByteArrayResource(image) {
	            @Override
	            public String getFilename() {
	                return "image.png";
	            }
	        });
	    }

	    HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
	    
	    @SuppressWarnings("rawtypes")
		ResponseEntity<Map> response = restTemplate.postForEntity("https://flask-api-keras.onrender.com/predict", requestEntity, Map.class);
	    
	    return response.getBody();
	}
	
	@GetMapping("/reports")
    public String getReports(@RequestParam(value = "patientName", required = false) String patientName,
                             @RequestParam(value = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
                             @RequestParam(value = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
                             Model model) {
		User_ user = userService.authenticateUser();
		model.addAttribute("fullName", user.getName() + " " + user.getSurname());
        List<ReportRequest> allReports = reportRequestService.getReports(patientName, startDate, endDate);
        
        if (allReports == null) {
            allReports = new ArrayList<>();
        }
        
        List<ReportRequest> reports = allReports.stream()
                .filter(r -> r.getRequester().getId() == user.getId())
                .collect(Collectors.toList());
        
        for (ReportRequest report : reports) {
            List<String> base64Images = report.getImages().stream()
                    .map(image -> "data:image/png;base64," + Base64.getEncoder().encodeToString(image.getImageData()))
                    .collect(Collectors.toList());
                report.setBase64Images(base64Images);
            }
        
        model.addAttribute("reports", reports);
        return "reports";
    }
	
	@GetMapping("/reports/{id}")
	public String getReportById(@PathVariable int id, Model model) {
		User_ user = userService.authenticateUser();
		model.addAttribute("fullName", user.getName() + " " + user.getSurname());
	    ReportRequest report = reportRequestService.getReportById(id);
	    
	    if (report == null) {
	        return "redirect:/reports";
	    }
	    
	    if (report.getRequester().getId() != user.getId()) {
	        return "forward:/error-custom?msg=You are not authorized to view this report.";
	    }
	    
	    List<String> base64Images = report.getImages().stream()
	            .map(image -> "data:image/png;base64," + Base64.getEncoder().encodeToString(image.getImageData()))
	            .collect(Collectors.toList());

	    model.addAttribute("report", report);
	    model.addAttribute("base64Images", base64Images);
	    return "result";
	}
	
	@PostMapping("/generate-pdf")
	public ResponseEntity<byte[]> generatePdf(@RequestBody Map<String, Integer> request) {
	    try {
	        int reportId = request.get("reportId");
	        ReportRequest reportRequest = reportRequestService.getReportById(reportId);
	        if (reportRequest == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

	        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	        Document document = new Document();
	        PdfWriter.getInstance(document, outputStream);
	        document.open();

	        Font appTitleFont = new Font(Font.FontFamily.HELVETICA, 26, Font.BOLD, BaseColor.DARK_GRAY);
	        Paragraph appTitle = new Paragraph("RetinAI", appTitleFont);
	        appTitle.setAlignment(Element.ALIGN_CENTER);
	        appTitle.setSpacingAfter(9f);
	        document.add(appTitle);

	        Font titleFont = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD, BaseColor.DARK_GRAY);
	        Paragraph title = new Paragraph("Diabetic Retinopathy Detection Report", titleFont);
	        title.setAlignment(Element.ALIGN_CENTER);
	        title.setSpacingBefore(10f);
	        title.setSpacingAfter(20f);
	        document.add(title);

	        LineSeparator ls = new LineSeparator();
	        document.add(new Chunk(ls));

	        Font headerFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
	        Font normalFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);

	        // Patient Info Table
	        PdfPTable patientTable = new PdfPTable(2);
	        patientTable.setWidthPercentage(100);
	        patientTable.setSpacingBefore(20f);
	        patientTable.setSpacingAfter(20f);
	        patientTable.setWidths(new float[]{1f, 2f});

	        addStyledCell(patientTable, "Patient Name:", headerFont, BaseColor.LIGHT_GRAY);
	        addStyledCell(patientTable, reportRequest.getPatientName() + " " + reportRequest.getPatientSurname(), normalFont, BaseColor.WHITE);

	        addStyledCell(patientTable, "TCKN:", headerFont, BaseColor.LIGHT_GRAY);
	        addStyledCell(patientTable, reportRequest.getPatientTckn(), normalFont, BaseColor.WHITE);

	        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
	        addStyledCell(patientTable, "Report Created:", headerFont, BaseColor.LIGHT_GRAY);
	        addStyledCell(patientTable, formatter.format(reportRequest.getRequestDate()), normalFont, BaseColor.WHITE);

	        document.add(patientTable);

	        // Severity Distribution Table
	        Paragraph sectionTitle = new Paragraph("Predicted Severity Levels", headerFont);
	        sectionTitle.setAlignment(Element.ALIGN_CENTER);
	        sectionTitle.setSpacingBefore(10f);
	        sectionTitle.setSpacingAfter(10f);
	        document.add(sectionTitle);

	        PdfPTable resultTable = new PdfPTable(2);
	        resultTable.setWidthPercentage(100);
	        resultTable.setSpacingBefore(10f);
	        resultTable.setWidths(new float[]{2f, 1f});

	        // Table headers
	        addStyledCell(resultTable, "Severity Level", headerFont, new BaseColor(220, 220, 220));
	        addStyledCell(resultTable, "Probability (%)", headerFont, new BaseColor(220, 220, 220));

	        List<String> severityLabels = Arrays.asList("No DR", "Mild", "Moderate", "Severe", "Proliferative");
	        String[] probs = reportRequest.getSeverityDistribution().replaceAll("[\\[\\]\\s]", "").split(",");

	        // Maksimum olasılığı bul
	        double maxVal = -1.0;
	        int maxIndex = -1;
	        for (int i = 0; i < probs.length; i++) {
	            double val = Double.parseDouble(probs[i]);
	            if (val > maxVal) {
	                maxVal = val;
	                maxIndex = i;
	            }
	        }

	        // Tablo satırlarını yaz
	        for (int i = 0; i < severityLabels.size(); i++) {
	            BaseColor rowColor;
	            if (i == maxIndex) {
	                rowColor = new BaseColor(255,183,3);
	            } else {
	                rowColor = (i % 2 == 0) ? new BaseColor(245, 245, 245) : BaseColor.WHITE;
	            }
	            addStyledCell(resultTable, severityLabels.get(i), normalFont, rowColor);
	            double percent = Double.parseDouble(probs[i]) * 100.0;
	            addStyledCell(resultTable, String.format(Locale.US, "%.2f%%", percent), normalFont, rowColor);
	        }

	        // Predicted result row
	        addStyledCell(resultTable, "Predicted Result:", headerFont, new BaseColor(230, 230, 250));
	        addStyledCell(resultTable, reportRequest.getResultSentence(), normalFont, new BaseColor(230, 230, 250));

	        addStyledCell(resultTable, "Predicted Severity:", headerFont, new BaseColor(230, 230, 250));
	        addStyledCell(resultTable, reportRequest.getResultDrLevel(), normalFont, new BaseColor(230, 230, 250));

	        document.add(resultTable);

	        String resultLevelSentence = reportRequest.getResultDrLevel();

		     // Regex ile "Moderate DR", "Mild DR", vb. kısmı al
		     String extractedLevel = "";
		     Matcher matcher = Pattern.compile("has (.*?) with").matcher(resultLevelSentence);
		     if (matcher.find()) {
		         extractedLevel = matcher.group(1).trim();  // Örneğin "Moderate DR"
		     }
	     
	        Map<String, String> severityDescriptions = new HashMap<>();
	        severityDescriptions.put("No DR", "There are no signs of diabetic retinopathy in the retina. The blood vessels appear normal, and no damage or abnormalities are detected. Regular monitoring is recommended.");
	        severityDescriptions.put("Mild DR", "Early signs of diabetic retinopathy are present. This typically includes small microaneurysms (tiny bulges in blood vessels). Vision is usually not affected at this stage. Regular eye exams and blood sugar control are important.");
	        severityDescriptions.put("Moderate DR", "Noticeable damage to the blood vessels in the retina is observed. There may be bleeding or fluid leakage. At this stage, vision may begin to be affected, and closer monitoring and medical guidance are recommended.");
	        severityDescriptions.put("Severe DR", "A significant amount of blood vessel damage is present, with widespread bleeding and leakage. New, fragile vessels may begin to grow (pre-proliferative stage). Vision is likely to be impaired, and prompt medical treatment is advised.");
	        severityDescriptions.put("Proliferative DR", "This is the most advanced stage of diabetic retinopathy. New abnormal blood vessels form, which can bleed and cause scar tissue. There is a high risk of vision loss. Immediate treatment by a retina specialist is essential.");

	        String explanation = severityDescriptions.getOrDefault(extractedLevel, "");
	        if (!explanation.isEmpty()) {
	            Paragraph explanationPara = new Paragraph("Description: " + explanation, normalFont);
	            explanationPara.setSpacingBefore(15f);
	            document.add(explanationPara);
	        }

	        document.add(new Chunk(ls));
	        Paragraph footer = new Paragraph("Thank you for using our DR Detection System.", normalFont);
	        footer.setAlignment(Element.ALIGN_CENTER);
	        footer.setSpacingBefore(30f);
	        document.add(footer);

	        document.close();
	        byte[] pdfBytes = outputStream.toByteArray();

	        HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.APPLICATION_PDF);
	        headers.setContentDisposition(ContentDisposition.attachment().filename("Retina_Report.pdf").build());

	        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);

	    } catch (Exception e) {
	        e.printStackTrace();
	        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}
	
	private void addStyledCell(PdfPTable table, String text, Font font, BaseColor bgColor) {
	    PdfPCell cell = new PdfPCell(new Phrase(text, font));
	    cell.setBackgroundColor(bgColor);
	    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
	    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
	    cell.setPadding(10f);
	    table.addCell(cell);
	}


    // PDF için DTO (Gelen JSON verisini almak için)
    public static class PdfRequest {
    	private String imageUrl;
        private String severityLevel;
        private String analysisNotes;
        private String patientName;
        private String patientSurname;
        private String patientTckn;

        // Getter-Setter
        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
        public String getSeverityLevel() { return severityLevel; }
        public void setSeverityLevel(String severityLevel) { this.severityLevel = severityLevel; }
        public String getAnalysisNotes() { return analysisNotes; }
        public void setAnalysisNotes(String analysisNotes) { this.analysisNotes = analysisNotes; }
        public String getPatientName() { return patientName; }
        public void setPatientName(String patientName) { this.patientName = patientName; }
        public String getPatientSurname() { return patientSurname; }
        public void setPatientSurname(String patientSurname) { this.patientSurname = patientSurname; }
        public String getPatientTckn() { return patientTckn; }
        public void setPatientTckn(String patientTckn) { this.patientTckn = patientTckn; }
    }
}
