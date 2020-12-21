
package authentication;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import authentication.DbConnection;

/**
 * 
 * Servlet implementation class Login
 * 
 * @author oluwaseun
 * 
 */
/* login servlet */
@WebServlet("/login")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Login() {
		super();

	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// System.out.println("get request");
		response.setContentType("text/html");
		response.sendRedirect("login.jsp");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String username = request.getParameter("email").replaceAll("\\<.*?\\>", "");
		String pass = request.getParameter("password").replaceAll("\\<.*?\\>", "");
		boolean remember = request.getParameter("remember") != null;
		String submitted = request.getParameter("login");

		PrintWriter pww = response.getWriter();
		DbConnection dbinstance = new DbConnection();

		if (submitted.equals("yes")) {
			pass = dbinstance.md5Funct(pass);
			List<ArrayList<String>> login = dbinstance.query(
					"SELECT * FROM usercredentials where Email = '" + username + "' AND Password = '" + pass + "'");
			if (login.size() > 0) {
				HttpSession session = request.getSession();
				ArrayList userinfo = (ArrayList<?>) login.get(0);
				String user = (null == userinfo.get(0)) ? "" : userinfo.get(0).toString();
				session.setAttribute("email", username);
				session.setAttribute("username", user);
				pww.write("success");
				if (remember) {
					Cookie ckUsername = new Cookie("username", username);
					ckUsername.setMaxAge(3600);
					response.addCookie(ckUsername);
				}
				request.getRequestDispatcher("/fsa_servlet").forward(request, response);
			} else {
				response.setContentType("text/html");
				pww.write("invalid");
			}

		}

	}
}
