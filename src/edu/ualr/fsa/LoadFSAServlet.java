package edu.ualr.fsa;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import javax.annotation.Resource;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import myclasses.NetworkGraph;
import myclasses.NetworkLoader;
import myclasses.FocalStructures;
import org.json.JSONObject;

import edu.ualr.fsa.NetworkDAO;


/**
 * Servlet implementation class LoadFSAServlet
 */
@WebServlet("/load/fsa/unweighted")
public class LoadFSAServlet extends HttpServlet {
    // TODO What Servlet specs: serialVersionUID, @Override, etc.
    private static final long serialVersionUID = 1L;

    // Registered with Tomcat in web.xml & context.xml
    @Resource(name="jdbc/fsa")
    private DataSource ds;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoadFSAServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
    * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
    */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws 
    ServletException, IOException {
        
        // Data Accessor Object
        // TODO instantiate in doGet or as init()? - see FSAServlet.java 
//        NetworkDAO networkDAO = new NetworkDAO(ds);
        NetworkDAO networkDAO = new NetworkDAO();

        try (Connection connection = ds.getConnection();) {
            
            String networkIdString = request.getParameter("networkId");
            Long networkId = Long.parseLong(networkIdString);
            
            // NetworkDAO
            Hashtable<String, String> vertexLabelTable = networkDAO.getNodes(networkId);
            List<String> edgeList = networkDAO.getEdges(networkId);
            ArrayList<String> edgeListArray = new ArrayList<String>(edgeList);

            NetworkGraph networkGraph = NetworkLoader.LoadResultSet(vertexLabelTable, edgeListArray);
            networkGraph.setNetworkGraph(networkGraph);

            // Custom Java Class - Create FSA Weighted network
            // NOTE why aren't the algorithm limits checked here?
            FocalStructures fStructures = new FocalStructures(networkGraph.getNetworkGraph());

            // Create FSA Unweighted graph
            fStructures.getFocalStructures();

            // Create JSON
            //networkJSONOutput = networkGraph.ToJSONString(fStructures.fStructuresIndexTable); // Fatih
            JSONObject networkJSONObjectOutput = networkGraph.ToJSONObject(fStructures.fStructuresIndexTable);

            // Attach JSON representation of network as HTTP Response payload
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(networkJSONObjectOutput.toString());
            System.out.println("here");

        } catch (NumberFormatException | SQLException e) {
            throw new ServletException("Cannot obtain network from database.", e);
        } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
