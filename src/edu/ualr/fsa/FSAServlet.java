package edu.ualr.fsa;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Hashtable;

import javax.sql.DataSource;
import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import edu.ualr.fsa.NetworkDAO;

/**
 * Servlet implementation class FSAServlet
 */
@WebServlet("/fsa_servlet")
public class FSAServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Resource(name = "jdbc/fsa") // Registered with Tomcat in web.xml & context.xml
	private DataSource ds;
	private NetworkDAO networkDAO;

//	@Override
//	public void init() {
//		networkDAO = new NetworkDAO(ds);
//	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		try {
			try {
//				String owner = request.getUserPrincipal().getName();
//				String owner = "seun";
				
				HttpSession session = request.getSession();
				String owner = session.getAttribute("username").toString();
				
				NetworkDAO networkDAO = new NetworkDAO();
				
				Hashtable<Integer, String> networkHash = networkDAO.getNetworks(owner);
				request.setAttribute("networkIds", networkHash);
				session.setAttribute("networkIds", networkHash);
				PrintWriter pww = response.getWriter();
				pww.write("success");
			} catch (SQLException e) {
				throw new ServletException("Issue contacting the database.", e);
			}
		} catch (Exception e) {
			System.out.println(e + "");
		}

		// Unified JSP view
		//request.getRequestDispatcher("/index.jsp").forward(request, response);

	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("");
	}
	
}
