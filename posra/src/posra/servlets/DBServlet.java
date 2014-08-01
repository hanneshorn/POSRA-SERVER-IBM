package posra.servlets;

import java.io.IOException;

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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ibm.util.SystemUtils;

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
		// TODO Auto-generated method stub
		try {
			HttpSession session = request.getSession(true);

			PolymerBean pb1 = (PolymerBean) session.getAttribute("pbean");
			if (pb1 != null) {

				// if necessary
				// response.setContentType("text/plain");

				// get Gson object
				Gson gson = new GsonBuilder().setPrettyPrinting().create();

				Polymer poly = createPolymer();
				poly.setName("newNameHere");
				poly.setExternalId("Smiles here");

				PolymerHome ph = new PolymerHome();
				ph.persist(poly);

				String jsonPolymer = gson.toJson(poly, Polymer.class);
				System.out.println("And here is a sample json ser p obj:\n\n "
						+ jsonPolymer);

				String url1 = "/addtodb.jsp";
				RequestDispatcher dispatcher1 = getServletContext()
						.getRequestDispatcher(url1);
				dispatcher1.forward(request, response);

			} else {
				String url2 = "/error.jsp";
				request.setAttribute("error", "No Polymer Bean was returned from the Results page.");
				RequestDispatcher dispatcher2 = getServletContext()
						.getRequestDispatcher(url2);
				dispatcher2.forward(request, response);
			}
		} catch (Exception e) {
			String url3 = "/error.jsp";
			request.setAttribute("error", "Probably a duplicate entry issue");
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
