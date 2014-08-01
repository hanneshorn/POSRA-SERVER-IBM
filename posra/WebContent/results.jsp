<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.*"%>
<%@ page import="posra.servlets.PolymerBean"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<meta name="viewport" content="width=device-width, initial-scale=1">
<script type="text/javascript"
	src="http://code.jquery.com/jquery-latest.min.js"></script>
<link href="bootstrap/bootstrap-3.1.1-dist/css/bootstrap.min.css"
	rel="stylesheet">
<link rel="stylesheet" type="text/css" href="myStyle.css">
<script src="bootstrap/bootstrap-3.1.1-dist/js/bootstrap.min.js"></script>
<!--  Scripts for JSMol -->
<script type="text/javascript"
	src="jmol-14.0.13/jsmol/JSmol.min.nojq.js"></script>
<script type="text/javascript" src="myQueryScript.js"></script>
<!-- End scripts for JSMol -->
<title>P-OSRA Submission Results</title>
</head>
<body>

	<div class="wrapper">

		<div class="header">
			<img src="images/POSRAheader.gif"
				style="height: 200px; padding: 20px;" />
		</div>

		<div class="header">
			<h2>
				<strong>P-OSRA Submission Results</strong>
			</h2>
		</div>

		<div class="container-fluid">
			<div class="row bootstrappieces">

				<div class="col-md-3 shadowboxes">
					If you would like to return to the original page and enter another
					query, <br /> please press the back button on your browser or the
					Return button below.
					<form action='index.html' method='post'>
						<input type='submit' value='Return'>
					</form>
				</div>

				<div class="col-md-6 shadowboxes">
					<table>
						<tr>
							<td>
								<table>
									<thead>
										<tr>
											<th colspan='3'>POSRA 2D Structure Results</th>
										</tr>
									</thead>
									<tbody>
										<tr>
											<td><em>Fragment (click for 3D)</em></td>
											<td><em>Name</em></td>
											<td><em>Degree</em></td>
										<tr>
											<td colspan='3'></td>
										</tr>

										<c:forEach var="outerArray" items="${polymerbean.smilesArray}"
											varStatus="loop">
											<tr>
												<td style="text-align: left"><a
													href="javascript:Jmol.loadFile(jmolApplet0,'$${polymerbean.pureSMILESArray[loop.index][0]}')">
														${outerArray[0]}</a></td>
												<c:forEach var="innerArray" begin="1" items="${outerArray}">
													<td>${innerArray}</td>
												</c:forEach>
											</tr>
										</c:forEach>



										<tr>
											<th colspan='3'>POSRA 3D Structure Results</th>
										</tr>
										<tr>
											<td colspan='3'>Please click the links above to display
												3D images</td>
										</tr>

										<tr>
											<td colspan='3'><a
												href="javascript:Jmol.loadFile(jmolApplet0,'$${polymerbean.smiNoPoly}')">
													Click for 3D view of full (submitted) monomer</a></td>
										</tr>
									</tbody>
									<tr>
										<td style='height: 200px' colspan='3'><div id='appdiv'></div></td>
									<tr>
									</tbody>
								</table>
							</td>

							<td>
								<table>
									<tr>
										<th>Submitted Image</th>
									</tr>
									<tr>
										<td style="background: white"><img
											src="ImageServlet?imageName=${imagebean.name}&path=${imagebean.path}"
											style="width: 250px"></td>
									</tr>
								</table>
							</td>
						</tr>
					</table>
				</div>

				<div class="col-md-2 shadowboxes">
					<table>
						<tr>
							<th colspan="2">Legend for P-OSRA Results</th>
						</tr>
						<tr style="text-align: left">
							<td style="width: 50px">EG:</td>
							<td>End Group</td>
						</tr>
						<tr style="text-align: left">
							<td style="width: 50px">RU:</td>
							<td>Repeat Unit</td>
						</tr>
						<tr>
							<td colspan="2">~ ~ ~</td>
						</tr>

					</table>

					<div style="text-align: left">
						For more information on how to read SMILES strings, click <a
							href='http://www.daylight.com/dayhtml/doc/theory/theory.smiles.html'>
							here</a>.<br /> <br /> For more information on how to interpret the
						degrees assigned to each segment of your polymer, press 'Return'
						and click the 'Documentation' tab.
					</div>
					
					<div>
					
					Do you want to add to a database? Click below:
					
					<form action="DBServlet" method="post"
									enctype="multipart/form-data">
						<jsp:useBean id="pbean" class="posra.servlets.PolymerBean" scope="session">
						
						<jsp:setProperty name="pbean" property="smiNoPoly" value="${polymerbean.smiNoPoly}" />
						<jsp:setProperty name="pbean" property="smi" value="${polymerbean.smi }" />
						<jsp:setProperty name="pbean" property="smilesArray" value="${polymerbean.smilesArray}" />
						<jsp:setProperty name="pbean" property="pureSMILESArray" value="${polymerbean.pureSMILESArray}" />
						
						</jsp:useBean>
						
						<input type="submit" value="Add to Database" />
					</form>
					
					</div>
				</div>

			</div>
			<!-- end row/bootstrappieces -->
		</div>
		<!-- end container-fluid -->
	</div>
	<!-- end of wrapper -->

	<div class="footer">p-osra is an open-source project funded and
		run by developers at IBM.</div>
</body>
</html>