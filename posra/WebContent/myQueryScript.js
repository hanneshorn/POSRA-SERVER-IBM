var jmolApplet0; // set up in HTML table, below

var s = document.location.search;

Jmol._debugCode = (s.indexOf("debugcode") >= 0);

jmol_isReady = function(applet) {
	document.title = (applet._id + " - Jmol " + ___JmolVersion)
	Jmol._getElement(applet, "appletdiv").style.border = "1px solid blue"
}

var Info = {
	width : 300,
	height : 300,
	debug : false,
	color : "0xFFFFFF",
	addSelectionOptions : true,
	use : "HTML5", // JAVA HTML5 WEBGL are all options
	j2sPath : "./jmol-14.0.13/jsmol/j2s", // this needs to point to where the j2s directory is.
	jarPath : "./jmol-14.0.13/jsmol/java",// this needs to point to where the java directory is.
	jarFile : "JmolAppletSigned.jar",
	isSigned : true,
	serverURL : "http://chemapps.stolaf.edu/jmol/jsmol/php/jsmol.php",
	readyFunction : jmol_isReady,
	disableJ2SLoadMonitor : true,
	disableInitialConsole : true,
	allowJavaScript : true,
	coverImage: "./images/issueblurry.gif"
}

$(document).ready(function() {
	$("#appdiv").html(Jmol.getAppletHtml("jmolApplet0", Info))
})
var lastPrompt = 0;