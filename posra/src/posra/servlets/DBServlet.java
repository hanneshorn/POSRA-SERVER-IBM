package posra.servlets;

import java.io.IOException;

import java.util.*;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import posra.dataaccess.Polymer;
import posra.dataaccess.PolymerHome;
import posra.dataaccess.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
// import com.ibm.util.SystemUtils;

@MultipartConfig
@WebServlet(name = "DBServlet", urlPatterns = { "/DBServlet" })
public class DBServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DBServlet() {
		super();
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String error = "";
		try {
			HttpSession session = request.getSession(true);

			PolymerBean pb1 = (PolymerBean) session.getAttribute("pbean");
			String pname = (String)request.getParameter("pname");
			
			System.out.println(pb1.toString());
			
			if (pb1 != null && pname != null) {

				Gson gson = new GsonBuilder().setPrettyPrinting().create();

				/** Create the Polymer Itself **/
				
				Polymer poly = createPolymer();
				poly.setName(pname);
				poly.setExternalId(pb1.getSmi());

				PolymerHome ph = new PolymerHome();
				ph.persist(poly);
				
				/** Populate the SMILES string table **/
				
				ArrayList<ArrayList<String>> array = pb1.getPureSMILESArray();
				for(int i=0; i<array.size(); ++i) {
					String tempsmi = array.get(i).get(0).replaceAll("\\[Te\\]", "");
					System.out.println(tempsmi);
					Smiles sm1 = new Smiles(tempsmi);
					SmilesHome sh1 = new SmilesHome();
					sh1.persist(sm1);
				}


				String jsonPolymer = gson.toJson(poly, Polymer.class);
				System.out.println("And here is a sample json ser p obj:\n\n "
						+ jsonPolymer);

				String url1 = "/addtodb.jsp";
				RequestDispatcher dispatcher1 = getServletContext()
						.getRequestDispatcher(url1);
				dispatcher1.forward(request, response);

			} else {
				String url2 = "/error.jsp";
				request.setAttribute("error", "There was no polymer submitted from the results page, or you did not type in a name for the polymer.");
				RequestDispatcher dispatcher2 = getServletContext()
						.getRequestDispatcher(url2);
				dispatcher2.forward(request, response);
			}
		} catch (Exception e) {
			String url3 = "/error.jsp";
			if(error.equals("")) { request.setAttribute("error", "Probably a duplicate entry issue"); }
			else { request.setAttribute("error",  error); }
			RequestDispatcher dispatcher3 = getServletContext()
					.getRequestDispatcher(url3);
			dispatcher3.forward(request, response);
		}
	}

	protected Polymer createPolymer() {
		Polymer p = new Polymer();
		p.setName("test");
		// PolymerHome ph = new PolymerHome();

		return p;
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

}
