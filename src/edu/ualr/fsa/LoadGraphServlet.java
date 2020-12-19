package edu.ualr.fsa;

// TODO Remove unused imports

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.sql.Connection;


import java.sql.SQLException;

import javax.annotation.Resource;
import javax.sql.DataSource;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import edu.ualr.fsa.NetworkDAO;

import myclasses.NetworkLoader;
import myclasses.NetworkGraph;
//import org.json.simple.JSONObject;
import org.json.JSONObject;

/**
 * Servlet implementation class LoadGraphServlet
 */
@WebServlet("/load")
public class LoadGraphServlet extends HttpServlet {

    // Registered with Tomcat in web.xml & context.xml
    @Resource(name="jdbc/fsa")
    private DataSource ds;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse
    response) throws ServletException, IOException {
        
        // Data Accessor Object
        // TODO instantiate in doGet or as init()? - see FSAServlet.java 
        NetworkDAO networkDAO = new NetworkDAO();

        // Load graph from MySQL DB connection
        try (Connection connection = ds.getConnection();) {
            
            String networkIdString = request.getParameter("networkId");
            Long networkId = Long.parseLong(networkIdString);
            
            // NetworkDAO instead of above SQL
            Hashtable<String, String> vertexLabelTable = networkDAO.getNodes(networkId);
            List<String> edgeList = networkDAO.getEdges(networkId);
            
            // Convert to ArrayList to accommodate datatype in NetworkGraph.java
            ArrayList<String> edgeListArray = new ArrayList<String>(edgeList);
            

            // Pass name, label & edge result sets to NetworkLoader class
            NetworkGraph networkGraph = NetworkLoader.LoadResultSet(vertexLabelTable, edgeListArray);

            networkGraph.setNetworkGraph(networkGraph);

            // Attach JSON representation of network as HTTP Response payload
            JSONObject networkJSONObjectOutput = new JSONObject();
			try {
				networkJSONObjectOutput = networkGraph.ToJSONObject();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            response.setContentType("application/json");
            response.getWriter().write(networkJSONObjectOutput.toString());

        } catch (NumberFormatException | SQLException e) {
            throw new ServletException("Issue contacting the database.", e);
        }
    }

    // TODO This method is not written in OOP paradigm. Needs to be abstracted
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse
    response) throws ServletException, IOException {
     // HTTP POST not implemented
    }
}
