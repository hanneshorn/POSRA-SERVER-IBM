package posra.servlets;

import java.io.*;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import posra.dataaccess.Polymer;
import posra.dataaccess.PolymerHome;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Servlet implementation class FileCounter
 */

@MultipartConfig
@WebServlet(name = "PolymerServlet", urlPatterns = { "/PolymerServlet" })
public class PolymerServlet extends HttpServlet {

	// private final static String OSRA_LIB = "libosra_java";
	static {
		System.setProperty("line.separator", "\n"); // so we won't have to mess
													// with DOS line endings
													// ever again
		// load the shared osra library.
		// NOTE that this is currently going to fail on 32bit cygwin using a
		// 64bit VM under 64bit Eclipse!!
		// workarounds: - use a 32bit VM under 32bit Eclipse
		// - try to build posra (and all its prereqs) under 64bit cygwin - some
		// likelihood to fail with current immature state of 64bit cygwin
		// try {
		// System.loadLibrary(OSRA_LIB);
		// System.out.println("done loading '" + OSRA_LIB + "'!");
		// } catch (Error e) {
		// System.err.println("Error loading native library '" + OSRA_LIB +
		// "'; java.library.path=" + System.getProperty("java.library.path"));
		// throw e; // re-throw
		// }
	}

	private static final long serialVersionUID = 1L;

	int count;
	private String filePath, osraPath, posraScriptPath;
	private File imagePath;

	public void init() throws ServletException {
		filePath = getServletContext().getInitParameter("image_location");
		osraPath = getServletContext().getInitParameter("osra_location");
		posraScriptPath = getServletContext().getInitParameter(
				"posra_script_location");
		imagePath = new File(filePath);

		if (!new File(osraPath).exists()) {
			System.err.println("POSRA binary '" + osraPath + "' not found!");
			System.exit(1);
		}
		if (!new File(posraScriptPath).exists()) {
			System.err.println("POSRA script '" + posraScriptPath
					+ "' not found!");
			System.exit(1);
		}
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();

		out.print("<!DOCTYPE html>\n<html lang=\"en\">");
		out.print("<head>\n\t<meta charset=\"utf-8\"/>"
				+ "<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\" />"
				+ "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">"
				+ "<link href=\"bootstrap/bootstrap-3.1.1-dist/css/bootstrap.min.css\" rel=\"stylesheet\">"
				+ "<link rel=\"stylesheet\" type=\"text/css\" href=\"myStyle.css\">"
				+ "<script type=\"text/javascript\" src=\"http://code.jquery.com/jquery-latest.min.js\"></script>"
				+ "<script src=\"bootstrap/bootstrap-3.1.1-dist/js/bootstrap.min.js\"></script>"
				+ "<script type=\"text/javascript\" src=\"jmol-14.0.13/jsmol/JSmol.min.js\"></script>"
				+ "<script type=\"text/javascript\" src=\"myQueryScript.js\"></script>"
				+ "</head>");

		out.print("<body>");

		if (!imagePath.exists()) {
			if (!imagePath.mkdir()) {
				throw new IOException("\"images\" directory could not be made.");
			}
		}

		// reading through request
		for (Part part : request.getParts()) {

			String name = part.getName();
			InputStream is = request.getPart(name).getInputStream();

			// origFilePath is the path on the client of the file being uploaded
			String origFilePath = getUploadedFileName(part);

			// the "fileName" will be the local path (filePath) and just the
			// file name of the uploaded file.
			File newFile = new File(origFilePath);
			String fileName = filePath + newFile.getName();

			// There is io here, a try/catch is needed in case there is a
			// problem writing the file
			try {
				FileOutputStream fos = new FileOutputStream(fileName);
				int data = 0;

				while ((data = is.read()) != -1) {
					fos.write(data);
				}

				fos.close();
				is.close();

				out.print("<div class=\"header\"> " + "<h2>POSRA Results</h2>"
						+ "</div>");

				// Add POSRA CALL
				// "fileName" is fullPath to File On Server
				// "newFile" is the Java File.

	    // out.print("command is: '" + osraPath + " -f can -r 150 " + fileName + "'");
		Process process = new ProcessBuilder(osraPath, "-f", "can", "-r", "150", fileName).start();

	    //out.print("command is: 'sh " + posraScriptPath + " -f can -r 150 " + fileName.replace("\\", "/") + "'");	  
	    //Process process = new ProcessBuilder("sh", posraScriptPath, "-f", "can", "-r", "150", fileName.replace("\\", "/")).start();

				try (BufferedReader pbr = new BufferedReader(
						new InputStreamReader(process.getInputStream()))) {
					// stdout from process
					String line;
					while ((line = pbr.readLine()) != null) {
						System.out.println(line);
						break; // first line is SMILES
					}
          
          try { // wait for the process to complete
			process.waitFor();
    	  } catch (InterruptedException e) { e.printStackTrace(); }
        
          int rc = process.exitValue(); // pick up process' return code
          String SMILES = line;
          String SmiNoPoly = removePolymerInfo(SMILES);
          request.setAttribute("SmiNoPoly", SmiNoPoly);
          
          // out.print("<h5>SMILES is " + SMILES + ", rc=" + rc + "</h5>");
		  
					// fix side chains here before parsing the smiles string into an array
					SMILES = fixSideChain(SMILES, "");
					
					ArrayList<ArrayList<String>> SMILESArray = parseSMILESString(SMILES);
					
					// This method parses the "old way". I've left the method
					// for compatibility's sake below as well, but also commented out.
					// SMILESArray = combForDegrees(SMILESArray);
					
					SMILESArray = parseDegrees(SMILESArray);
					
					ArrayList<ArrayList<String>> pureSMILESArray = new ArrayList<ArrayList<String>>();
					
					for(int i=0; i<SMILESArray.size(); ++i){
						pureSMILESArray.add(new ArrayList<String>());
						for(int k=0; k<SMILESArray.get(i).size(); ++k) {
							pureSMILESArray.get(i).add(SMILESArray.get(i).get(k));
						}
					}
					
					pureSMILESArray = makePure(pureSMILESArray);
					
					out.print("<table>"
							+ "<thead><tr><th colspan='3'>POSRA 2D Structure Results</th></tr></thead>"
							+ "<tbody>" + "<tr>" + "<td><em>Fragment</em></td>"
									+ "<td><em>Name</em></td>"
							+ "<td><em>Degree</em></td>");

					boolean startOfDegree = true;

					for (int i = 0; i < SMILESArray.size(); ++i) {
						// javascript:Jmol.loadFile(jmolApplet0,'$TeCCCOCCC(=O)OCCTe')
						out.print("<tr>" + "<td><a href=javascript:Jmol.loadFile(jmolApplet0,'$" + pureSMILESArray.get(i).get(0) + "')>" + SMILESArray.get(i).get(0)
								+ "</a></td>");

						startOfDegree = true;

						for (int k = 1; k < SMILESArray.get(i).size(); ++k) {
							out.print("<td>" + SMILESArray.get(i).get(k) + "</td>");
						}

						out.print("</tr>");
					}

					out.print("<tr>"
							+ "<td colspan='3'></td></tr>"
							+ "<tr>"
							+ "<th colspan='3'>POSRA 3D Structure Results</th></tr>"
							+ "<tr><td colspan='3'>Please click the links above to display 3D images</td></tr>"
							+ "<tr><td colspan='3'><a href=javascript:Jmol.loadFile(jmolApplet0,'$" + SmiNoPoly + "')>Click for 3D view of full (submitted) monomer</a></td></tr>"
							+ "<tr><td style='height: 200px' colspan='3'><div id='appdiv'></div></td><tr>");

					out.print("</tbody></table>");
					out.print("<div class='rightfloater'><a href='http://www.daylight.com/dayhtml/doc/theory/theory.smiles.html'>"
							+ "How do I read SMILES strings?</a><br />"
							+ "For more information on how to interpret the degrees assigned to each "
							+ "segment of your polymer, press 'Return' and click the 'Documentation' tab."
							+ "</div>");
					out.print("<br /><p>If you would like to return to the original page and enter another query, <br />"
							+ "please press the back button on your browser or the Return button below.");
					out.print("<form action='index.html' method='post'>"
							+ "<input type='submit' value='Return'>"
							+ "</form>");
				} // close for
			} // close try

			catch (IOException e) {
				out.print("<h5>There was an error</h5>");
				out.print("Debug: <br />Uploaded File Name: " + origFilePath
						+ "<br />");
				out.print("Destination Directory: " + filePath + "<br />");
			}
		} // close for loop

		out.print("</body></html>");
		out.close();

	}// close method doPost

	private String getUploadedFileName(Part p) {
		String file = "", header = "Content-Disposition";
		String[] strArray = p.getHeader(header).split(";");

		for (String split : strArray) {
			if (split.trim().startsWith("filename")) {
				file = split.substring(split.indexOf('=') + 1);
				file = file.trim().replace("\"", "");
			}
		}
		return file;
	}

	private String fixSideChain(String s, String stack) {

		int firstParen = s.indexOf("(");
		int lastParen = s.lastIndexOf(")");
		
		if(firstParen < 0 || lastParen < 0) { return s; }
		
		String eg1=s.substring(0,firstParen);
		String eg2=s.substring(lastParen + 1, s.length());
		
		boolean eg1hp = hasPolymer(eg1);
		boolean eg2hp = hasPolymer(eg2);
		
		//find the matching parenthesis of the firstParen
		// args are as such:
		// the index of the paren; -1 if closing, 1 if opening; string to iterate over
		int firstMatch = findMatchingParen(firstParen, 1, s);
		int lastMatch = findMatchingParen(lastParen, -1, s);
		
		String firstSeg = s.substring(firstParen+1, firstMatch);
		String lastSeg = s.substring(lastMatch+1, lastParen);
		boolean fshp = hasPolymer(firstSeg);
		boolean lshp = hasPolymer(lastSeg);
		
		if(fshp && !eg1hp) { 
			s = switchParentheses(s, eg1, firstSeg, 0);
		}
		if(lshp && !eg2hp) { 
			s = switchParentheses(s, eg2, lastSeg, lastParen + 1);
		}
		
		return s;
	}
	
	public static boolean hasPolymer(String s) {
		return s.contains("[Lv:") || s.contains("[Po:") || s.contains("[Te:");
	}
	
	public static int findMatchingParen(int paren, int adder, String s) {
		int match = paren, counter = 1;
		
		while(counter > 0) {
			match += adder;
			char c = s.charAt(match);
			if(c =='(' || c == '[') { counter += adder; }
			else if(c == ')' || c == ']') { counter -= adder; }
		}
		
		return match;
	}
	
	public static String switchParentheses(String s, String eg, String seg, int firstOrLast) {
		// if we are passed a 0 in 'firstOrLast', we are dealing with a replacement at the front
		// and we will need to rearrange the string that was originally inside the parentheses
		// (which is here called the 'seg')
		
		eg = "(" + eg + ")";
		String temp = "";
		if(firstOrLast == 0) { 
			seg = switchOrder(seg);
			temp = seg + eg;
			return temp + s.substring(temp.length(), s.length());
		}
		
		temp = eg + seg; 
		return s.substring(0, s.length() - temp.length()) + temp;	
	}
	
	public static String switchOrder(String switchMe) {
		// switch order of everything naively except the 
		// contents of polymer brackets... want to maintain
		// the left-to-right reading of those.
		String newString = "", temp = "";
		int matcher;
		
		for(int i=0; i<switchMe.length(); ++i) {
			if((i+4) < switchMe.length() && hasPolymer(switchMe.substring(i, i+4))) {
				newString = switchMe.substring(i, (switchMe.indexOf("]")+1)) + newString;
				i = switchMe.indexOf("]") + 1;
			} if (switchMe.substring(i, (i+1)).equals("(") || 
				  switchMe.substring(i, (i+1)).equals("[")) {
				matcher = findMatchingParen(i, 1, switchMe);
				temp = switchOrder(switchMe.substring((i+1), matcher));
				
				if(switchMe.substring(i, (i+1)).equals("(")) {
					newString = "(" + temp + ")" + newString;
				}
				else {
					newString = "[" + temp + "]" + newString;
				}
				
				i = matcher+1;
			} else {
				newString = switchMe.substring(i, i+1) + newString;
			}
		}
		return newString;
	}

	private ArrayList<ArrayList<String>> makePure(
			ArrayList<ArrayList<String>> SArray) {
		String curStr = "";
		for (int i = 0; i < SArray.size(); ++i) {
			// only dealing with first item, so get(i).get(0)
			curStr = SArray.get(i).get(0);
			curStr = curStr.replaceAll("X[\\d]+", "[Te]");
			SArray.get(i).set(0, curStr);
		}

		return SArray;
	}

	private ArrayList<ArrayList<String>> parseSMILESString(String SMILES) {
		ArrayList<ArrayList<String>> smilesArray = new ArrayList<ArrayList<String>>();

		// if SMILES string does not exist/was not printed
		if (SMILES == null || SMILES.length() < 1) {
			System.out.println("There was no SMILES string to process.");
			smilesArray.add(new ArrayList<String>());
			smilesArray.get(0).add("There was no SMILES string to process.");
		} else { // smiles string does exist
			SMILES = SMILES.trim();

			// useful variables that mark different placeholders in the SMILES
			// string
			// The placeholder shows where we are now, the previousPlace is
			// where we
			// just were, the endBracket and currentBracket are as you would
			// think, the
			// xCounter keeps track of which "X" we are on in the printint out
			// of the
			// Polymer subunits (which look like CX1, X1CCOCCX2, X2C, for
			// example)
			//
			// Presub and Innersub are in reference to what is before and inside
			// brackets with polymer information ([Lv\Po\Te...])
			int length = SMILES.length();
			int placeHolder = 0, previousPlace = 0;
			int endBracket, currentBracket, xCounter = 1;
			int ai = 0;
			String presub, innersub;
			boolean beginning = true;

			// while we still have parts of the string to trawl through
			while (placeHolder < length) {
				// the bracket we're looking at is the next bracket in the
				// string
				currentBracket = SMILES.indexOf('[', placeHolder);

				// if the indexOf function returned -1, there is no next bracket
				// so we will jump into this routine and break the while
				if (currentBracket < 0) {
					placeHolder = length;
					break;
				}

				// Ensure that the looking at the next four characters after a
				// given
				// bracket do not put us past the length. If it does put us past
				// the
				// length, we simply add the rest of the SMILES string to a new
				// segment
				// and call 'er done.
				if (currentBracket + 4 < SMILES.length()) {
					// if bracket exists, does it match any of the following?
					// and set place to current bracket
					placeHolder = currentBracket;
					if (SMILES.substring(currentBracket, currentBracket + 4)
							.equals("[Po:")
							|| SMILES.substring(currentBracket,
									currentBracket + 4).equals("[Lv:")
							|| SMILES.substring(currentBracket,
									currentBracket + 4).equals("[Te:")) {

						// add a new segment string array
						smilesArray.add(new ArrayList<String>());
						// keep track of the next closing bracket and set the
						// placeholder there
						endBracket = SMILES.indexOf(']', placeHolder);
						placeHolder = endBracket + 1;

						presub = SMILES
								.substring(previousPlace, currentBracket);

						if (beginning) {
							if (presub.equals("")) {
								presub = "[H]";
							}
							smilesArray.get(ai).add(
									"" + presub + "X" + xCounter + "");
							beginning = false;
						} else {
							smilesArray.get(ai).add(
									"X" + xCounter + presub + "X"
											+ (xCounter + 1));
							++xCounter;
						}

						innersub = SMILES.substring(currentBracket,
								endBracket + 1);

						int degPH = 0; // placeholder for degree subfunction
						while (degPH < innersub.length()) {
							if (!(innersub.indexOf(':', degPH) < 0)
									&& !(innersub.indexOf(';', degPH + 1) < 0)) {
								smilesArray.get(ai).add(
										innersub.substring(
												innersub.indexOf(':') + 1,
												innersub.indexOf(';')));
								degPH = innersub.indexOf(';');
							} else if (!(innersub.indexOf(':', degPH) < 0)
									&& !(innersub.indexOf(']', degPH + 1) < 0)) {
								smilesArray.get(ai).add(
										innersub.substring(
												innersub.indexOf(':') + 1,
												innersub.indexOf(']')));
								degPH = innersub.length();
								break;
							} else if (!(innersub.indexOf(';', degPH) < 0)
									&& !(innersub.indexOf(';', degPH + 1) < 0)) {
								smilesArray
										.get(ai)
										.add(innersub.substring(
												innersub.indexOf(';', degPH) + 1,
												innersub.indexOf(';', degPH + 1)));
								degPH = innersub.indexOf(';');
							} else if (!(innersub.indexOf(';', degPH) < 0)
									&& !(innersub.indexOf(']', degPH + 1) < 0)) {
								smilesArray
										.get(ai)
										.add(innersub.substring(
												innersub.indexOf(';', degPH) + 1,
												innersub.indexOf(']')));
								degPH = innersub.length();
								break;
							} else {
								degPH = innersub.length();
								break;
							}
						}

						++ai;

						if (SMILES.indexOf('[', placeHolder) < 0) {
							smilesArray.add(new ArrayList<String>());
							String endSubString = SMILES.substring(
									(endBracket + 1), length);
							if (endSubString.equals("")) {
								endSubString = "[H]";
							}

							smilesArray.get(ai).add(
									"X" + xCounter + endSubString);
						}

						// end sets
						previousPlace = endBracket + 1;
						// ++ai; -- done above in a more advantageous place?
					} else { // if bracket is not one of the special brackets,
								// crawl
								// down one more.
						++placeHolder;
					}
				} else { // dealing with fringe cases where we have something in
							// brackets in an endgroup very near to the end,
							// e.g. polystyrene
					smilesArray.add(new ArrayList<String>());
					smilesArray.get(ai).add(
							"X" + xCounter
									+ SMILES.substring(placeHolder, length));
					placeHolder = length;
				}
			}
		}
		if (smilesArray.isEmpty()) {
			smilesArray.add(new ArrayList<String>());
			smilesArray.get(0).add(SMILES);
		}
		return smilesArray;
	}
	
	private ArrayList<ArrayList<String>> parseDegrees(
			ArrayList<ArrayList<String>> smilesArray) {
		
		int RUcounter = 1, sCounter = 1;
		boolean beginFlag = true;
		// first go through and add the metonyms
		for (int i = 0; i < smilesArray.size(); ++i) {
			if (beginFlag || i == smilesArray.size() - 1) {
				smilesArray.get(i).add(1, "EG" + sCounter);
				++sCounter;
				beginFlag = false;
			} else {
				smilesArray.get(i).add(1, "RU" + RUcounter);
				++RUcounter;
			}
		}

		ArrayList<String> finalArray = new ArrayList<String>();
		ArrayList<String> metaArray = new ArrayList<String>();
		ArrayList<String> rowArray = new ArrayList<String>();
		String RUorS;
		int tempMetaIndex;
		String tempMetaString;

		for (int i = 0; i < smilesArray.size(); ++i) {
			RUorS = smilesArray.get(i).get(1);

			for (int k = 2; k < smilesArray.get(i).size(); ++k) {
				rowArray.add(smilesArray.get(i).get(k));
			} // row array populated with degrees
			for (int j = 0; j < rowArray.size(); ++j) { // iterate through row
				if (metaArray.contains(rowArray.get(j))) { // ra deg in ma
					tempMetaIndex = metaArray.indexOf(rowArray.get(j));
					if (tempMetaIndex % 2 == 0) { // ensures getting a degree
						tempMetaString = metaArray.get(tempMetaIndex + 1);
						finalArray.add(metaArray.get(tempMetaIndex));
						finalArray.add(tempMetaString + RUorS);
						metaArray.remove(tempMetaIndex);
						// not redundant! remove shifts all to left 1 place
						metaArray.remove(tempMetaIndex);
					}
				} else { // ra deg not in ma
					metaArray.add(rowArray.get(j));
					metaArray.add("");
				}
			}
			// interate through meta only look @ degs
			for (int l = 0; l < metaArray.size(); l += 2) {
				if (!rowArray.contains(metaArray.get(l))) {
					tempMetaString = metaArray.get(l + 1);
					metaArray.set(l + 1, tempMetaString + RUorS);
				}
			}
			rowArray.clear();
		}
		ArrayList<String> curElem = new ArrayList<String>();
		boolean cont = false;

		for (int i = 0; i < smilesArray.size(); ++i) {
			curElem = smilesArray.get(i);
			if (curElem.size() > 2) {
				curElem.subList(2, curElem.size()).clear();
			}
			for (int k = 1; k < finalArray.size(); k += 2) {
				if (finalArray.get(k).equals(curElem.get(1))) {
					cont = true;
					tempMetaIndex = finalArray.indexOf(smilesArray.get(i)
							.get(1)); // if they match
					curElem.add(finalArray.get(tempMetaIndex - 1));
					finalArray.set(k, "");
					finalArray.set(k - 1, "");
				}
			}
			if (!cont) {
				curElem.add("");
			}
			cont = false;
		}
		int tempSize;
		
		for (int i = 0; i < finalArray.size(); i += 2) {
			tempSize = smilesArray.size();
			if (!finalArray.get(i).equals("")) {
				smilesArray.add(new ArrayList<String>());
				smilesArray.get(tempSize).add(getSmilesOf(smilesArray, finalArray.get(i+1)));
				// adds the string of RUs first
				smilesArray.get(tempSize).add(finalArray.get(i + 1));
				smilesArray.get(tempSize).add(finalArray.get(i));
				
			}
		}
		
		return smilesArray;
	}
	
	private String getSmilesOf(ArrayList<ArrayList<String>> array, String rus) {
		String[] ruArray = rus.split("RU");
		String retStr = "X" + (ruArray[0] + 1) + "";

		for(int i=0; i<ruArray.length; ++i){
			retStr = retStr + array.get(i).get(0).replaceAll("X[\\d]+", "");
		}
		int temp = (Integer.parseInt(ruArray[ruArray.length - 1]) + 1);
		retStr += "X" + temp;
		return retStr;
	}
	
	private String removePolymerInfo(String smi) {
		String newsmi = "[Te]" + smi.replaceAll("\\[[TePoLv].+?\\]","");
		newsmi += "[Te]";
		
		System.out.println("New smi: " + newsmi);
		
		return newsmi;
	}

	/*
	 * private ArrayList<ArrayList<String>> combForDegrees(
	 * ArrayList<ArrayList<String>> smilesArray) { ArrayList<String> AllDegArray
	 * = new ArrayList<String>(); ArrayList<String> CurRowDegs = new
	 * ArrayList<String>(); String thisItem;
	 * 
	 * for (int i = 0; i < smilesArray.size(); ++i) { // start from 1 to get
	 * only degree information String segmentName = (String)
	 * smilesArray.get(i).get(0); for (int k = 1; k < smilesArray.get(i).size();
	 * ++k) { // add all elements of this row to the CRD
	 * CurRowDegs.add(smilesArray.get(i).get(k)); } smilesArray.get(i).clear();
	 * smilesArray.get(i).add(segmentName); for (int j = 0; j <
	 * AllDegArray.size(); ++j) { smilesArray.get(i).add(AllDegArray.get(j)); }
	 * for (int l = 0; l < CurRowDegs.size(); ++l) { thisItem =
	 * CurRowDegs.get(l); if (AllDegArray.contains(thisItem)) {
	 * AllDegArray.remove(thisItem); } else { AllDegArray.add(thisItem); } }
	 * CurRowDegs.clear(); }
	 * 
	 * return smilesArray; }
	 */

	// This is an unused method for now, but could be used for testing.
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// Set a cookie for the user, so that the counter does not increase
		// every time the user presses refresh
		HttpSession session = request.getSession(true);
		// Set the session valid for 5 secs
		session.setMaxInactiveInterval(5);
		response.setContentType("text/plain");
		PrintWriter out = response.getWriter();

		// Sample json

		// Get Gson object
		Gson gson = new GsonBuilder().setPrettyPrinting().create();

		Polymer poly = createPolymer();
		poly.setName("Polystyrene");
		poly.setExternalId("38529384");

		PolymerHome p = new PolymerHome();
		p.persist(poly);

		String jsonPolymer = gson.toJson(poly, Polymer.class);
		out.println("And here is a sample json serialized polymer java object:\n\n "
				+ jsonPolymer);
	}

	protected Polymer createPolymer() {
		Polymer p = new Polymer();
		p.setName("test");
		// PolymerHome ph = new PolymerHome();

		return p;
	}

	public void destroy() {
		super.destroy();
	}
}