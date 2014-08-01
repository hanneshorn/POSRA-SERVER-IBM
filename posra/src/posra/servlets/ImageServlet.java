package posra.servlets;

import java.io.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/ImageServlet")
public class ImageServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public ImageServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		FileInputStream iStream = null;
		ServletOutputStream oStream = null;
		
		try {	
			String imageName = request.getParameter("imageName");
			String path = request.getParameter("path");
			
			response.setContentType("image/gif");
			File file = new File(path + "" + imageName);
			response.setContentLength((int)file.length());
			
			iStream = new FileInputStream(file);
			oStream = response.getOutputStream();
			
			byte[] buffer = new byte[1024];
			int len = 0;
			
			while((len = iStream.read(buffer)) != -1) {
				oStream.write(buffer, 0, len);
			}
		}
		catch(IOException e) {
			System.out.println("Whoops");
		}
		finally {	
			if(iStream != null) {
				iStream.close();
			}
			if(oStream != null) {
				oStream.flush();
				oStream.close();
			}
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// pass through to the doGet method
		doGet(request, response);
	}

}
