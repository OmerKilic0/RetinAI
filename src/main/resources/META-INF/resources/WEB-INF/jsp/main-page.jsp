<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<!DOCTYPE html>
<html lang="en">

<head>
<style>
body {
	font-family: Arial, sans-serif;
	background-color: #8ecae6; /* Açık mavi - Ferah ve göz dostu */
	margin: 0;
	padding: 0;
}

header {
	background-color: #219ebc; /* Orta mavi - Profesyonel ve tıbbi alanlara uygun */
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
	color: #219ebc;
	padding: 6px 12px;
	border: 1px solid #219ebc;
	border-radius: 5px;
	font-size: 18px;
	font-weight: 500;
	cursor: pointer;
	transition: 0.3s ease;
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

.user-form, .image-upload {
	display: block;
	justify-content: center;
	background-color: white;
	padding: 20px;
	border-radius: 10px;
	box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
	width: 50%;
	margin-bottom: 20px;
}

h3 {
	margin-top: 5px;
	margin-bottom: 15px;
	color: #219ebc;
}

label {
	width: 20%;
	display: flex;
	margin-bottom: 5px;
	justify-content: space-between;
	align-items: center;
	color: #219ebc;
}

input {
	width: 100%;
	padding: 10px;
	margin: 8px 0;
	border: 1px solid #ffb703; /* Turuncu vurgu */
	border-radius: 5px;
	font-size: 14px;
	background-color: #ffffff; /* Beyaz arka plan */
	color: #023047;
}

label input {
	width: 1rem;
}

#otherPatientInfo input {
	margin: 5px 0 5px;
	justify-self: center;
}

.upload-box {
	background-color: white;
	border-radius: 10px;
	padding: 20px;
	text-align: center;
	border: 2px dashed #ffb703;
	color: #219ebc;
}

.upload-box img {
	width: 75px;
}

.select-button {
	background-color: #ffb703; /* Canlı turuncu */
	border: 1px solid #219ebc;
	padding: 10px;
	border-radius: 5px;
	cursor: pointer;
	transition: 0.3s ease;
	color: white;
}

.select-button:hover {
	background-color: #219ebc;
	color: white;
}

.submit-container {
	text-align: center;
	display: flex;
	justify-content: center;
}

.submit-button {
	background-color: #ffb703;
	color: white;
	padding: 15px 30px;
	border: none;
	border-radius: 5px;
	font-size: 16px;
	cursor: pointer;
	display: block;
	margin: 0;
	transition: 0.3s ease;
}

.submit-button:hover {
	background-color: #219ebc;
	color: white;
}

#previewContainer {
	display: flex;
	justify-content: center;
}
</style>
<meta charset="UTF-8">
<title>Diabetic Retinopathy Detection</title>
<link rel="stylesheet"
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
</head>

<body>

	<!-- Üst Navbar -->
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

		<!-- Sol taraf: Hasta Bilgileri -->
		<div class="user-form">
			<h3>User Information Form</h3>
			<form id="reportForm" method="post" enctype="multipart/form-data"
				action="/submit">

				<label><input type="radio" name="patientType" value="self"
					checked> Myself</label> <label class="ms-3"><input
					type="radio" name="patientType" value="other"> Someone Else</label>


				<div id="otherPatientInfo" style="display: none;">
					<input type="text" name="patientName" placeholder="Name" required>
					<input type="text" name="patientSurname" placeholder="Surname"
						required> <input type="text" name="patientTckn"
						placeholder="TCKN" required>
				</div>

				<h3 style="margin-top: 20px;">Upload Images (Max 3)</h3>
				<p>You can upload images up to 3 times. Format must be JPG/PNG.
					File size cannot exceed 20MB.</p>
				<div class="upload-box">
					<img src="${pageContext.request.contextPath}/images/upload-icon.png" alt="Upload Icon">
					<p>Drag your images here or select image from your device.</p>
					<input type="file" id="imageUpload" name="images" accept="image/*"
						multiple class="form-control">
				</div>

				<div id="previewContainer" class="mt-3">
					<!-- Yüklenen Görseller Burada Listelenecek -->
				</div>
				<div class="submit-container">
					<button type="submit" class="submit-button">Submit</button>
				</div>
			</form>
		</div>



	</div>

	<script>
		$(document)
				.ready(
						function() {
							// Hasta türüne göre ek alanları göster/gizle
							$('input[name="patientType"]').change(function() {
								if ($(this).val() === 'other') {
									$('#otherPatientInfo').slideDown();
								} else {
									$('#otherPatientInfo').slideUp();
								}
							});

							// Dosya seçme işlemi
							$("#imageUpload")
									.change(
											function() {
												let files = this.files;
												let previewContainer = $("#previewContainer");
												previewContainer.html(""); // Önceki önizlemeleri temizle

												if (files.length > 3) {
													alert("You can only upload up to 3 images.");
													this.value = ""; // Fazla dosya seçildiyse sıfırla
													return;
												}

												for (let i = 0; i < files.length; i++) {
													let reader = new FileReader();
													reader.onload = function(e) {
														let img = $("<img>")
																.attr(
																		"src",
																		e.target.result)
																.addClass(
																		"img-thumbnail m-2")
																.css("width",
																		"100px");
														previewContainer
																.append(img);
													};
													reader
															.readAsDataURL(files[i]);
												}
											});
						});
	</script>

</body>

</html>