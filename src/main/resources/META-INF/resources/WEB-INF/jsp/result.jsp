<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">

<head>
<meta charset="UTF-8">
<title>Diabetic Retinopathy Detection - Result</title>
<style>
body {
    font-family: Arial, sans-serif;
    background-color: #8ecae6; /* Soft mavi arka plan */
    margin: 0;
    padding: 0;
}

header {
    background-color: #219ebc; /* Orta mavi başlık */
    color: white;
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 10px 20px;
}

.logo {
    display: flex;
    align-items: center;
    font-size: 20px;
    font-weight: 500;
}

.logo img {
    width: 50px;
    margin-right: 10px;
}

.user-info {
    display: flex;
    align-items: center;
}

.user-info button {
    margin: 5px;
    background-color: white;
    color: #219ebc; /* Orta mavi buton */
    padding: 6px 12px;
    border: 1px solid #219ebc;
    border-radius: 5px;
    font-size: 18px;
    font-weight: 500;
    cursor: pointer;
}

.user-info button:hover {
    background-color: #ffb703; /* Canlı turuncu hover efekti */
	color: white;
}

.container {
    display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	padding: 20px;
}

.image-column {
    display: flex;
    margin: 20px;
    gap: 10px;
    justify-content: flex-start;
    min-width: 100px;
}

.image-column img {
    width: 100px;
    height: 100px;
    border-radius: 5px;
    border: 1px solid #ccc;
}

.report-details {
    text-align: start;
    display: flex;
    background-color: #ffffff; /* Turuncu arka plan */
    padding: 20px;
    border-radius: 10px;
    width: 100%;
    margin-top: 20px;
}

.report-details h4 {
    margin-top: 10px;
}

.report-details p {
    font-size: 20px;
}

.download-section {
    display: block;
    text-align: center;
}

.download-button {
    background-color: #ffb703; 
    color: white;
    padding: 10px 20px;
    border: none;
    border-radius: 5px;
    font-size: 16px;
    cursor: pointer;
}

.download-button:hover {
    background-color: #219ebc; 
}

.report-section {
    text-align: center;
    justify-content: center;
    display: flex;
    align-items: center;
    margin-bottom: 50px;
    position: relative;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
    padding-top: 60px;
}

.report-section .report-title {
    position: absolute;
    top: 20px;
    left: 30px;
    font-size: 22px;
    color: #333;
}

.report-section .report-date {
	position: absolute;
    top: 20px;
    right: 30px;
    font-size: 18px;
    color: #555;
}
</style>

<link rel="stylesheet"
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
</head>

<body>

	<header>
		<div class="logo" onclick="location.href='/'" style="cursor: pointer;">
			<img src="${pageContext.request.contextPath}/images/logo.png" alt="Diabetic Retinopathy Logo"> <span>DIABETIC
				RETINOPATHY DETECTION SYSTEM</span>
		</div>
		<div class="user-info">
			<button onclick="location.href='/'">Detect DR</button>
			<button onclick="location.href='/reports'">Previous Detections</button>
			<button onclick="location.href='/profile'">${fullName}</button>
			<button onclick="location.href='/logout'">Logout</button>
		</div>
	</header>

	<div class="container mt-4">
		<div class="row">

			<!-- Right Column: Analysis Results -->
			<div class="report-section">

				<div class="report-title">
			        <h3>Severity Report</h3>
			    </div>
    
				<div class="report-date">
			        <strong>Report Created:</strong> <fmt:formatDate value="${report.requestDate}" pattern="dd-MM-yyyy HH:mm" />
			    </div>
            
				<div class="report-details">
					<div style="display: inline-block;">
						<div class="image-column">
							<img src="${pageContext.request.contextPath}/images/ret1.png" alt="Retina Image" class="img-fluid" />
						</div>
						<div class="image-column">
							<img src="${pageContext.request.contextPath}/images/ret2.png" alt="Retina Image" class="img-fluid" />
						</div>
						<div class="image-column">
							<img src="${pageContext.request.contextPath}/images/ret3.png" alt="Retina Image" class="img-fluid" />
						</div>
					</div>
					<div style="display: inline-block; align-items: start; margin: 20px;">
						<p>
							<strong>Name:</strong> ${report.patientName}
						</p>
						<p>
							<strong>Surname:</strong> ${report.patientSurname}
						</p>
						<p>
							<strong>TCKN:</strong> ${report.patientTckn}
						</p>
						<p>
							<strong>Result:</strong> ${report.result}
						</p>
						<p>
							<strong>Severity:</strong> ${report.resultDrLevel}
						</p>
					</div>
				</div>

			</div>
			<!-- Download Button -->
			<div class="download-section">
				<button class="download-button" onclick="downloadPDF(${report.id})">📥 Download as PDF</button>
			</div>
		</div>
	</div>

		<script>
                function downloadPDF(reportId) {
				    fetch('/generate-pdf', {
				        method: 'POST',
				        headers: {
				            'Content-Type': 'application/json'
				        },
				        body: JSON.stringify({ reportId: reportId }) // Sadece ID gönderiyoruz
				    })
				    .then(response => {
				        if (!response.ok) {
				            throw new Error("PDF generation failed");
				        }
				        return response.blob();
				    })
				    .then(blob => {
				        let link = document.createElement('a');
				        link.href = window.URL.createObjectURL(blob);
				        link.download = "Retina_Report.pdf";
				        document.body.appendChild(link);
				        link.click();
				        document.body.removeChild(link);
				    })
				    .catch(error => console.error('Error:', error));
				}
            </script>

</body>

</html>