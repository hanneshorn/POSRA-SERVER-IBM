package posra.servlets;

import java.io.*;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.*;

import javax.servlet.*;
import javax.servlet.http.*;
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
 * Servlet implementation class PolymerServlet
 */

@MultipartConfig
@WebServlet(name = "PolymerServlet", urlPatterns = { "/PolymerServlet" })
public class PolymerServlet extends HttpServlet {

	// private final static String OSRA_LIB = "libosra_java";
	static {
		// fixes issues with DOS line endings
		System.setProperty("line.separator", "\n");

		/*
		 * this attempt to load the shared osra library will fail on 32 bit cyg
		 * using a 64bit VM under 64 bit Eclipse. Can workaround w/ 32bit VM
		 * under 32bit Eclilpse Or, can try to build posra under 64bit cygwin
		 * (immature?) try { System.loadLibrary(OSRA_LIB);
		 * System.out.println("done loading '" + OSRA_LIB + "'!"); } catch
		 * (Error e) { System.err.println("Error loading native library '" +
		 * OSRA_LIB + "'; java.library.path = " +
		 * System.getProperty("java.library.path")); throw e; // re-throw error
		 * }
		 */

	}

	private static final long serialVersionUID = 1L;

	int count;
	private String filePath, osraPath, posraScriptPath;
	private File imagePath;

	public void init() throws ServletException {
		// filePath = getServletContext().getInitParameter("image_location");
		filePath = "C:" + "\\" + "Users" + "\\" + "IBM_ADMIN" + "\\" + "Documents" + "\\" + "POSRA-SERVER-IBM" + "\\" + "posra" + "\\" + "WebContent" + "\\" + "UploadedImages" + "\\";
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
			ImageBean ib = new ImageBean();
			

			// the "fileName" will be the local path (filePath) and just the
			// file name of the uploaded file.
			File newFile = new File(origFilePath);
			String fileName = filePath + newFile.getName();
			ib.setPath(newFile.getName());
			
			System.out.println("file name: " + fileName);

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

				ib.setUploadedFile(newFile);
				request.setAttribute("imagebean", ib);
				
				// "fileName" is fullPath to File On Server
				// "newFile" is the Java File.
				Process process = new ProcessBuilder(osraPath, "-f", "can",
						"-r", "150", fileName).start();

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
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					// pics up return code of process
					// int rc = process.exitValue();

					String SMILES = line;
					String SmiNoPoly = removePolymerInfo(SMILES);

					// fixes minor sidechain issues
					SMILES = fixSideChain(SMILES, "");
					// parses SMILES into a 2D arraylist
					ArrayList<ArrayList<String>> SMILESArray = parseSMILESString(SMILES);
					// deals with the nesting of degrees
					SMILESArray = parseDegrees(SMILESArray);

					// make a copy of the SMILES array
					ArrayList<ArrayList<String>> pureSMILESArray = new ArrayList<ArrayList<String>>();
					for (int i = 0; i < SMILESArray.size(); ++i) {
						pureSMILESArray.add(new ArrayList<String>());
						for (int k = 0; k < SMILESArray.get(i).size(); ++k) {
							pureSMILESArray.get(i).add(
									SMILESArray.get(i).get(k));
						}
					}
					
					// remove polymeric information so that it can be used
					// with JSmol modeling
					pureSMILESArray = makePure(pureSMILESArray);
					
					// add to a JavaBean named PolymerBean so we can
					// pass the request onto the JSP page
					PolymerBean pb1 = new PolymerBean(SmiNoPoly, SMILESArray,
							pureSMILESArray);
					request.setAttribute("polymerbean", pb1);

					// dispatch information to jsp page
					String url = "/results.jsp";
					RequestDispatcher dispatcher = getServletContext()
							.getRequestDispatcher(url);
					dispatcher.forward(request, response);
				}
			}

			catch (IOException e) {
				String url2 = "/error.jsp";
				RequestDispatcher dispatcher2 = getServletContext()
						.getRequestDispatcher(url2);
				dispatcher2.forward(request, response);
			}
		}
	}

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

		if (firstParen < 0 || lastParen < 0) {
			return s;
		}

		String eg1 = s.substring(0, firstParen);
		String eg2 = s.substring(lastParen + 1, s.length());

		boolean eg1hp = hasPolymer(eg1);
		boolean eg2hp = hasPolymer(eg2);

		/* This finds the matching parenthesis of the
		 * paren you pass it. Pass a 1 if you are passing
		 * an opening paren, and a -1 if passing a closing
		 * paren. 's' is the string to iterate over.
		 */
		int firstMatch = findMatchingParen(firstParen, 1, s);
		int lastMatch = findMatchingParen(lastParen, -1, s);

		String firstSeg = s.substring(firstParen + 1, firstMatch);
		String lastSeg = s.substring(lastMatch + 1, lastParen);
		boolean fshp = hasPolymer(firstSeg);
		boolean lshp = hasPolymer(lastSeg);

		if (fshp && !eg1hp) {
			s = switchParentheses(s, eg1, firstSeg, 0);
		}
		if (lshp && !eg2hp) {
			s = switchParentheses(s, eg2, lastSeg, lastParen + 1);
		}

		return s;
	}

	public static boolean hasPolymer(String s) {
		return s.contains("[Lv:") || s.contains("[Po:") || s.contains("[Te:");
	}

	/* This finds the matching parenthesis of the
	 * paren you pass it. Pass a 1 if you are passing
	 * an opening paren, and a -1 if passing a closing
	 * paren. 's' is the string to iterate over.
	 */
	public static int findMatchingParen(int paren, int adder, String s) {
		int match = paren, counter = 1;

		while (counter > 0) {
			match += adder;
			char c = s.charAt(match);
			if (c == '(' || c == '[') {
				counter += adder;
			} else if (c == ')' || c == ']') {
				counter -= adder;
			}
		}

		return match;
	}

	/*
	 * s is the string to iterate over,
	 * eg is the end group to put in the parens
	 * seg is the segment to take out of the parens
	 * If we are passed a 0 in 'firstOrLast',
	 * that signifies replacement at the front 
	 * of the molecule, which requires reversal
	 * of the string originally inside the parens.
	 */
	public static String switchParentheses(String s, String eg, String seg,
			int firstOrLast) {
		eg = "(" + eg + ")";
		String temp = "";
		if (firstOrLast == 0) {
			seg = switchOrder(seg);
			temp = seg + eg;
			return temp + s.substring(temp.length(), s.length());
		}

		temp = eg + seg;
		return s.substring(0, s.length() - temp.length()) + temp;
	}

	/*
	 * This method naively switches the order of everything
	 * in a string except for the contents of polymer brackets,
	 * which are maintained so that their left-to-right 
	 * reading will not be disturbed.
	 */
	public static String switchOrder(String switchMe) {
		String newString = "", temp = "";
		int matcher;

		for (int i = 0; i < switchMe.length(); ++i) {
			if ((i + 4) < switchMe.length()
					&& hasPolymer(switchMe.substring(i, i + 4))) {
				newString = switchMe.substring(i, (switchMe.indexOf("]") + 1))
						+ newString;
				i = switchMe.indexOf("]") + 1;
			}
			if (switchMe.substring(i, (i + 1)).equals("(")
					|| switchMe.substring(i, (i + 1)).equals("[")) {
				matcher = findMatchingParen(i, 1, switchMe);
				temp = switchOrder(switchMe.substring((i + 1), matcher));

				if (switchMe.substring(i, (i + 1)).equals("(")) {
					newString = "(" + temp + ")" + newString;
				} else {
					newString = "[" + temp + "]" + newString;
				}

				i = matcher + 1;
			} else {
				newString = switchMe.substring(i, i + 1) + newString;
			}
		}
		return newString;
	}

	/*
	 * This method runs through the SMILES array and 
	 * replaces all of the end labels (X3, eg) with
	 * [Te] in order to be able to use it for JSmol modeling.
	 */
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

			/*
			 * placeHolder shows where we are now in the string,
			 * previousPlace is where we just were,
			 * endBracket and currentBracket are obvious
			 * xCounter keeps track of which X we are on
			 * as far as polymeric placeholders (CX1 or X4C, eg)
			 * Presub and Innersub reference what is before or
			 * inside the brackets with polymer information
			 */
			int length = SMILES.length();
			int placeHolder = 0, previousPlace = 0;
			int endBracket, currentBracket, xCounter = 1;
			int ai = 0;
			String presub, innersub;
			boolean beginning = true;

			// while we still have parts of the string to trawl through
			while (placeHolder < length) {
				currentBracket = SMILES.indexOf('[', placeHolder);

				//if no next bracket
				if (currentBracket < 0) {
					placeHolder = length;
					break;
				}

				// make sure we do not pass end of string
				// by looking at 4 chars inside bracket
				if (currentBracket + 4 < SMILES.length()) {
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
						previousPlace = endBracket + 1;
					} else { // not special bracket, move on
						++placeHolder;
					}
				} else { // bracket puts you over end
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

	/*
	 * This method looks through the smiles array
	 * and deals with putting the degrees in the 
	 * proper 'place,' and assigned to the proper
	 * SMILES unit or meta-name (RU1, RU2, RU1RU2, etc)
	 */
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
				smilesArray.get(tempSize).add(
						getSmilesOf(smilesArray, finalArray.get(i + 1)));
				// adds the string of RUs first
				smilesArray.get(tempSize).add(finalArray.get(i + 1));
				smilesArray.get(tempSize).add(finalArray.get(i));

			}
		}

		return smilesArray;
	}

	/* This method accepts an array of () and a string with an unbroken
	 * list of the repeat units desired to get the SMILES string of
	 * (e.g., RU1RU3RU5 means you will get the SMILES of RU1, RU3, and RU5.
	 * The array that you pass must have the SMILES strings contained in the
	 * canonical manner.
	 */
	private String getSmilesOf(ArrayList<ArrayList<String>> array, String rus) {
		String[] ruArray = rus.split("RU");
		String retStr = "X" + (ruArray[0] + 1) + "";

		for (int i = 0; i < ruArray.length; ++i) {
			retStr = retStr + array.get(i).get(0).replaceAll("X[\\d]+", "");
		}
		int temp = (Integer.parseInt(ruArray[ruArray.length - 1]) + 1);
		retStr += "X" + temp;
		return retStr;
	}

	private String removePolymerInfo(String smi) {
		String newsmi = "[Te]" + smi.replaceAll("\\[[TePoLv].+?\\]", "");
		newsmi += "[Te]";
		return newsmi;
	}

	// TODO Injecting the polymer into the DB happens here
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// Set a cookie for the user, so that the counter does not increase
		// every time the user presses refresh
		HttpSession session = request.getSession(true);
		// Set the session valid for 5 secs
		session.setMaxInactiveInterval(5);
		response.setContentType("text/plain");

		// Sample json

		// Get Gson object
		Gson gson = new GsonBuilder().setPrettyPrinting().create();

		Polymer poly = createPolymer();
		poly.setName("Polystyrene");
		poly.setExternalId("38529384");

		PolymerHome p = new PolymerHome();
		p.persist(poly);

		String jsonPolymer = gson.toJson(poly, Polymer.class);
		System.out
				.println("And here is a sample json serialized polymer java object:\n\n "
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