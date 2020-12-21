
package authentication;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import util.Mailing;
import authentication.DbConnection;

/**
 * 
 * Servlet implementation class Register
 * 
 * @author Oluwaseun Johnson
 * 
 */
@WebServlet("/register")
public class Register extends HttpServlet {
	private static final long serialVersionUID = 1L;
	HashMap<String, String> hm  = DbConnection.loadConstant(); 
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Register() {
		super();

	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html");
		response.sendRedirect("register.jsp");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// System.out.println("post request");
		String email = (null == request.getParameter("email")) ? ""
				: request.getParameter("email").replaceAll("\\<.*?\\>", "");
		String name = (null == request.getParameter("name")) ? ""
				: request.getParameter("name").replaceAll("\\<.*?\\>", "");
		String password = (null == request.getParameter("password")) ? ""
				: request.getParameter("password").replaceAll("\\<.*?\\>", "");
		String oldpassword = (null == request.getParameter("oldpassword")) ? ""
				: request.getParameter("oldpassword").replaceAll("\\<.*?\\>", "");

		String phone = (null == request.getParameter("phone")) ? ""
				: request.getParameter("phone").replaceAll("\\<.*?\\>", "");
		String type = "";// request.getParameter("user_type").replaceAll("\\<.*?\\>", "");
		String picture = (null == request.getParameter("profile_picture")) ? ""
				: request.getParameter("profile_picture").replaceAll("\\<.*?\\>", "");

		String submitted = (null == request.getParameter("register")) ? "" : request.getParameter("register");
		String signin = (null == request.getParameter("signin")) ? "" : request.getParameter("signin");
		String action = (null == request.getParameter("action")) ? "" : request.getParameter("action");

		DbConnection dbinstance = new DbConnection();

		PrintWriter pww = response.getWriter();
		HttpSession session = request.getSession();

		if (submitted.equals("yes")) {
			List<ArrayList<String>> login = dbinstance
					.query("SELECT Email FROM usercredentials where Email = '" + email + "' ");
			if (login.size() > 0) {
				response.setContentType("text/html");
				pww.write("exists");

				if (signin.equals("yes")) {
					dbinstance.updateTable("UPDATE usercredentials SET profile_picture='" + picture + "', first_name='"
							+ name + "' WHERE Email ='" + email + "'");
					session.setAttribute("email", email);
					session.setAttribute("username", login.get(0));
				}

			} else {
				String digest = dbinstance.md5Funct(password);
				DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
				LocalDateTime now = LocalDateTime.now();  
				String query_string = "insert into usercredentials (UserName, Email, Password, MessageDigest, user_type,first_name,last_name,phone_number,address,profile_picture,last_updated,added_by,date_added ) VALUES ('"
						+ email + "','" + email + "','" + digest + "','" + digest + "','" + type + "','" + name
						+ "','','','','" + picture + "','','','"+dtf.format(now)+"')";
				boolean inserted = dbinstance.updateTable(query_string);
				session.setAttribute("username", email);
				if (inserted) {
					if (signin.equals("yes")) {
						session.setAttribute("email", email);
					}
					String[] receivers = { email };
					try {
						Mailing m = new Mailing();
						m.postMail(receivers, "FSA - Registration Successful", "Hello " + name
								+ ", <br/><br/> You have been successfully registered on FSA. Kindly find your login details below:<br/><br/> <b>Username/Email: "
								+ email + "</b>. <br/>Password: " + digest
								+ ". <br/><br/>Kindly login at <a href='"+m.hm.get("app_url")+"login.jsp'> FSA </a><br/><br/> Thanks for using FSA");
					} catch (Exception e) {

					}
					response.setContentType("text/html");
					pww.write("success");
				} else {
					if (signin.equals("yes")) {
						session.setAttribute("email", email);
					}

					String[] receivers = { email };
					try {
						Mailing m = new Mailing();
						m.postMail(receivers, "FSA - Registration Successful", "Hello " + name
								+ ", <br/><br/> You have been successfully registered on Blogtrackers. Kidly find your login details below:<br/><br/> <b>Username/Email"
								+ email + "</b>. <br/>Password:" + password
								+ ". <br/><br/>Kindly login at <a href='"+m.hm.get("app_url")+"login.jsp'> FSA </a><br/><br/> Thanks for using FSA");
					} catch (Exception e) {

					}
					response.setContentType("text/html");
					pww.write("success");
				}

			}

		} else if (action.equals("update_profile")) {
			String username = session.getAttribute("email").toString();
			boolean exi = false;
			if (!password.equals("") && !oldpassword.equals("")) {
				List<ArrayList<String>> ex = dbinstance.query("SELECT Email FROM usercredentials where Email = '"
						+ username + "' AND password ='" + oldpassword + "' ");
				if (ex.size() < 1) {
					exi = true;
					response.setContentType("text/html");
					pww.write("nomatch");
				}
			}

			if (!exi) {
				String keys = "UPDATE usercredentials SET ";
				String vals = "";
				if (!name.equals("")) {
					vals += "first_name = '" + name + "',";
				}
				if (!email.equals("")) {
					vals += "Email = '" + email + "',";
				}
				if (!phone.equals("")) {
					vals += "phone_number = '" + phone + "',";
				}
				if (!password.equals("")) {
					vals += "Password = '" + password + "',";
				}

				vals = vals.replaceAll(",$", "");

				String query_string = keys + "" + vals + " WHERE Email = '" + username + "'";
				boolean updated = dbinstance.updateTable(query_string);
				if (updated && !email.equals("")) {
					session.setAttribute("email", email);
					response.setContentType("text/html");
					pww.write("success");
				}
			}

		} else if (action.equals("delete_account")) {
			String idd = session.getAttribute("email").toString();
			boolean updatedd = dbinstance.updateTable("DELETE FROM usercredentials WHERE Email = '" + idd + "'");
			if (updatedd) {
				session.invalidate();
			}

		}

	}
}
