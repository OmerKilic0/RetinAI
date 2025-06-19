<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">

<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Reports</title>
<style>
body {
    font-family: Arial, sans-serif;
    background-color: #8ecae6; /* Açık mavi arka plan */
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

.user-info img {
    width: 30px;
    margin-left: 10px;
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
    padding: 20px;
}

.back-button {
    background-color: #ffb703; /* Turuncu geri butonu */
    border: none;
    padding: 10px;
    color: white;
    border-radius: 5px;
    cursor: pointer;
    margin-bottom: 15px;
    display: inline-block;
}

.back-button:hover {
    background-color: #219ebc; /* Daha koyu turuncu hover */
}

.report-header {
    display: block;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 10px;
}

.filter-button {
    background-color: #ffb703; 
    border: none;
    padding: 10px;
    color: white;
    margin-bottom: 10px;
    border-radius: 5px;
    cursor: pointer;
    display: flex;
}

.view-button, .download-button {
    background-color: #ffb703; 
    padding: 10px;
    border-radius: 5px;
    color: white;
    cursor: pointer;
    border: none;
}

a:link:hover, a:visited:hover, .view-button:hover, .download-button:hover {
    background-color: #219ebc;
}

.filter-button:hover {
    background-color: #219ebc; /* Turuncu filtre hover */
}

#filter-contents {
    display: flex;
    justify-content: flex-end;
    margin-top: 20px;
}

label {
    display: flex;
    margin: 0px 5px 5px 30px;
    justify-content: space-between;
    align-items: center;
}

input {
    padding: 10px;
    border: 1px solid #ccc;
    border-radius: 5px;
    font-size: 14px;
    margin: 0px 10px 5px 0px;
}

a:link, a:visited {
    background-color: #8ecae6; /* Açık mavi link */
    border-radius: 5px;
    cursor: pointer;
    border: none;
    text-decoration: none;
    color: black;
}

.filter-submit {
    margin: 0 0 5px 30px;
    background-color: #ffb703; /* Orta mavi filtre gönder */
    color: white;
    padding: 6px 12px;
    border: 1px solid #ccc;
    border-radius: 5px;
    font-size: 16px;
    cursor: pointer;
}

.filter-submit:hover {
    background-color: #219ebc; /* Koyu mavi hover */
}

table {
    width: 100%;
    border-collapse: collapse;
    background-color: white;
    border-radius: 10px;
    overflow: hidden;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}

th, td {
    padding: 10px;
    border-bottom: 1px solid #ccc;
    text-align: left;
}

th {
    background-color: #ffb703; /* Turuncu başlık */
}

td img {
    width: 80px;
    height: auto;
    border-radius: 5px;
    border: 1px solid #ccc;
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
	<div class="container">


		<button class="back-button" onclick="location.href='/'">
			<span class="glyphicon glyphicon-arrow-left"></span>← Back to Home
			Page
		</button>

		<div class="report-header">
			<div style="display: flex; justify-content: space-between;">
				<h3 style="display: inline-block; margin: 10px 0;">Previous
					Reports</h3>
				<div
					style="display: inline-flex; justify-content: flex-end; text-align: center;">
					<button class="filter-button">☰ Filter By ▼</button>

				</div>
			</div>
			<div id="filter-contents" style="display: none;">
				<form method="get"
					action="${pageContext.request.contextPath}/reports">
					<label for="patientName">Patient Name:</label> <input type="text"
						id="patientName" name="patientName" value="${param.patientName}" />

					<label for="startDate">Start Date:</label> <input type="date"
						id="startDate" name="startDate" value="${param.startDate}" /> <label
						for="endDate">End Date:</label> <input type="date" id="endDate"
						name="endDate" value="${param.endDate}" />

					<button class="filter-submit" type="submit">Filter</button>
				</form>
			</div>
		</div>
		<table>
			<thead>
				<tr>
					<th>Patient Name</th>
					<th>Patient Surname</th>
					<th>Patient TCKN</th>
					<th>Request Date</th>
					<th>AI Result</th>
					<th>PDF Report</th>
					<th>Details</th>
				</tr>
			</thead>
			<tbody>
				<c:if test="${empty reports}">
					<tr>
						<td colspan="7">No reports available</td>
					</tr>
				</c:if>

				<c:forEach var="report" items="${reports}">
					<tr>
						<td>${report.patientName}</td>
						<td>${report.patientSurname}</td>
						<td>${report.patientTckn}</td>
						<td><fmt:formatDate value="${report.requestDate}"
								pattern="dd-MM-yyyy" /></td>
						<td>
						    <c:choose>
						        <c:when test="${report.result == 'No DR'}">
							        DR not detected
							    </c:when>
							    <c:when test="${report.result == 'DR'}">
							        DR detected
							    </c:when>
						    </c:choose></td>
						<td>
							<button class="download-button" onclick="downloadPDF(${report.id})">📥 Download as PDF</button>
						</td>
						<td><a
							href="${pageContext.request.contextPath}/reports/${report.id}">
								<button class="view-button">View Report</button>
						</a></td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</div>
	<script>
                $(document).ready(function () {
                    $(".filter-button").click(function () {
                        $("#filter-contents").slideToggle();
                    });
                })
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