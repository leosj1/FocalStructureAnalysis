package edu.ualr.fsa;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Hashtable;

import javax.sql.DataSource;
import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.ualr.fsa.NetworkDAO;

/**
 * Servlet implementation class FSAServlet
 */
@WebServlet("")
public class FSAServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Resource(name = "jdbc/fsa") // Registered with Tomcat in web.xml & context.xml
	private DataSource ds;
	private NetworkDAO networkDAO;

	@Override
	public void init() {
		networkDAO = new NetworkDAO(ds);
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		try {
			try {
//				String owner = request.getUserPrincipal().getName();
				String owner = "seun";

				Hashtable<Integer, String> networkHash = networkDAO.getNetworks(owner);
				request.setAttribute("networkIds", networkHash);
			} catch (SQLException e) {
				throw new ServletException("Issue contacting the database.", e);
			}
		} catch (Exception e) {
			System.out.println(e + "");
		}

		// Unified JSP view
		request.getRequestDispatcher("/fsa_index.jsp").forward(request, response);
//		request.getRequestDispatcher("/index.jsp").forward(request, response);

		// Upload page only for unauthenticated user
//		 if (request.isUserInRole("auth")) {
//		 request.getRequestDispatcher("/WEB-INF/fsa_index.jsp").forward(request,
//		 response);
//		 } else {
//		 request.getRequestDispatcher("/web/WEB-INF/index.jsp").forward(request,
//		 response);
//		 }

	}
	
}
