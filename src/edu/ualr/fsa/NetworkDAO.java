package edu.ualr.fsa;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Driver;
import java.sql.*;

import javax.sql.DataSource;

import au.com.bytecode.opencsv.CSVReader;
import authentication.DbConnection;

//import com.opencsv.CSVReader;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;

/**
 * @author Andrew Pyle axpyle@ualr.edu MS-Information Science 2018
 *
 *
 */
public class NetworkDAO {

//	private DataSource ds;
//
//	public NetworkDAO(DataSource ds) {
//		this.ds = ds;
//	}
	
	DbConnection ds = new DbConnection();

	// Accesses all Networks in MySQL database configured as JNDI resource and
	// converts to List<String>
	public Hashtable<Integer, String> getNetworks(String owner) throws SQLException {
        // List of all network names
        List<String> labels = new ArrayList<String>();
        Hashtable<Integer, String> networkHash = new Hashtable<Integer, String>();
        
        try (
            Connection connection = ds.getConnection();
            PreparedStatement networkStmt = connection.prepareStatement(
                "SELECT network_id " +
                "     , network_label " +
                "  FROM network " +
                " WHERE owner_id = ? " +
                "    OR owner_id = \'demo\' "); // FIXME Properties file to avoid hardcoding 'demo'
        ) {
            networkStmt.setString(1, owner);
            
            try (ResultSet rs = networkStmt.executeQuery(); ) {
                while(rs.next()) {
                    labels.add(rs.getString("network_label"));
                    networkHash.put(rs.getInt("network_id"), rs.getString("network_label"));
                }
            }
        }catch (Exception e) {
        	System.out.println("");
        }
        return networkHash;
    }

	public Hashtable<String, String> getNodes(Long networkId) throws SQLException {

		Hashtable<String, String> vertexLabelTable = new Hashtable<String, String>();

		try (Connection connection = ds.getConnection();
				PreparedStatement vertexStmt = connection.prepareStatement("SELECT " + "  node.node_id AS name "
						+ ", node.node_label AS label " + "FROM node " + "WHERE network_id = ? ");) {
			// Set & escape parameters
			vertexStmt.setLong(1, networkId);
			// System.out.println(vertexStmt.toString()); // Debug

			try (ResultSet nodeRS = vertexStmt.executeQuery();) {

				while (nodeRS.next()) {
					// Hashtable { name="label", ... , }
					String id = nodeRS.getString("name");
					String label = nodeRS.getString("label");
					// Entry into hashtable
					vertexLabelTable.put(id, label);
				}
			}
		}
		return vertexLabelTable;
	}

	public List<String> getEdges(Long networkId) throws SQLException {

		List<String> edgeList = new ArrayList<String>();

		try (Connection connection = ds.getConnection();
				PreparedStatement edgeStmt = connection.prepareStatement("SELECT " + "  edge.source_id "
						+ ", edge.target_id " + ", edge.weight " + "FROM edge " + "WHERE network_id = ?");) {
			// Set & escape parameters
			edgeStmt.setLong(1, networkId);
			// System.out.println(edgeStmt.toString()); // Debug

			try (ResultSet edgeRS = edgeStmt.executeQuery();) {

				while (edgeRS.next()) {
					// edgeList [ source_id,target_id,weight, x,x,x, ]
					String source_id = edgeRS.getString("source_id");
					String target_id = edgeRS.getString("target_id");
					String weight = edgeRS.getString("weight");

					// Assemble proper line
					String line = source_id + "," + target_id + "," + weight;

					// Append line to edgeList
					edgeList.add(line);
				}
			}
		}
		return edgeList;
	}

	// INSERTs all rows of local CSV file to MySQL Database configured as JNDI
	// resource
	public void insertNetworkFromCSVFile(String filePath, String networkName, String owner)
			throws SQLException, IOException {

		// Time this method
		long startTime = System.nanoTime();

		// Data from CSV
		ArrayList<String> nodes = new ArrayList<>();
		ArrayList<String> edges = new ArrayList<>();

		try (Connection connection = ds.getConnection();
				BufferedReader br = new BufferedReader(new FileReader(filePath));) {

			// network -----------------------

			// Prepare network INSERT statement
			String networkInsertQuery = "INSERT INTO network (network_label, owner_id) values (?, ?)";
			PreparedStatement networkInsertStmt = connection.prepareStatement(networkInsertQuery,
					PreparedStatement.RETURN_GENERATED_KEYS);
			networkInsertStmt.setString(1, networkName); // Set & escape parameters
			networkInsertStmt.setString(2, owner);

			// System.out.println(networkInsertStmt.toString()); // Debug network INSERT
			// statement

			// Execute escaped INSERT statement
			int networkRowsUpdated = networkInsertStmt.executeUpdate();

			// Get MySQL auto_increment ID
			Long networkId = null;
			// Based on StackOverflow answer from BalusC
			// https://stackoverflow.com/questions/1915166/how-to-get-the-insert-id-in-jdbc
			try (ResultSet autoGeneratedKeys = networkInsertStmt.getGeneratedKeys();) {
				if (autoGeneratedKeys.next()) {
					networkId = autoGeneratedKeys.getLong(1);
					System.out.println(networkId);
				} else {
					throw new SQLException("No network_id obtained. INSERT must have failed");
				}
			}

			// Close network INSERT resources
			networkInsertStmt.close();
			// System.out.println(networkRowsUpdated); // Debug

			// node -----------------------

			// Read CSV line-by-line
			// Expected CSV schema:
			// source,target,weight
			// weight is set to 1 if not present in CSV
			String line;
			while ((line = br.readLine()) != null) {

				// splitting CSV into its columns
				String[] s = line.split(",");

				String source, target;
				Double weight;

				// Set values from CSV line
				source = s[0];
				target = s[1];

				// weight default to 1 if no weight specified in CSV
				try {
					weight = Double.parseDouble(s[2]);
				} catch (Exception e) {
					weight = 1.0;
				}

				// To detect if source & target were unset by node INSERT
				int sourceId = -1, targetId = -1; // TODO Does java use exception handling for control flow?

				// To check for duplicate edges in edge table INSERT
				String edgeConcat = source + "," + target;

				// INSERT node when encountered for first time
				if (!nodes.contains(source)) { // Ensure each node is added only once

					// Prepare INSERT source node statement
					String nodeInsertQuery = "INSERT INTO node (node_label, network_id) values (?, ?)";
					PreparedStatement nodeInsertStmt = connection.prepareStatement(nodeInsertQuery);
					nodeInsertStmt.setString(1, source); // Set & escape parameters
					// TODO Can it be null? Throws exception if networkId can't be set
					nodeInsertStmt.setLong(2, networkId);

					// System.out.println(nodeInsertStmt.toString()); // Debug INSERT node statement
					// for source

					// Execute INSERT source node statement
					int nodeRowsUpdated = nodeInsertStmt.executeUpdate();

					// Close INSERT source node resources
					nodeInsertStmt.close();

					// Add node to nodes ArrayList
					nodes.add(source);
				}

				// Either way, Get id of source node for edge table INSERT
				String sourceNodeIdQuery = "SELECT node_id FROM node " + "WHERE network_id = ? AND node_label = ?";
				PreparedStatement sourceNodeIdStmt = connection.prepareStatement(sourceNodeIdQuery);
				sourceNodeIdStmt.setLong(1, networkId);
				sourceNodeIdStmt.setString(2, source);
				// System.out.println(souceNodeIdStmt.toString()); // Debug get node_id query
				// for source

				// Execute get source node node_id query
				ResultSet sourceNodeIdRs = sourceNodeIdStmt.executeQuery();
				sourceNodeIdRs.next(); // cursor starts before row 1
				sourceId = sourceNodeIdRs.getInt("node_id");

				// Close get source node node_id query resources
				sourceNodeIdRs.close();
				sourceNodeIdStmt.close();

				// INSERT node when encountered for first time
				if (!nodes.contains(target)) { // Ensure each node is added only once

					// Prepare target node INSERT statement
					String nodeInsertQuery = "INSERT INTO node (node_label, network_id) values (?, ?)";
					PreparedStatement nodeInsertStmt = connection.prepareStatement(nodeInsertQuery);
					nodeInsertStmt.setString(1, target); // Set & escape parameters
					nodeInsertStmt.setLong(2, networkId);
					// System.out.println(nodeInsertStmt.toString()); // Debug INSERT node statement
					// for target

					// Execute INSERT target node statement
					int nodeRowsUpdated = nodeInsertStmt.executeUpdate();

					// Close INSERT target node resources
					nodeInsertStmt.close();

					// Add node to nodes ArrayList to detect duplicates in next loop iteration
					nodes.add(target);
				}

				// Either way, Get id of target node for edge table INSERT
				String targetNodeIdQuery = "SELECT node_id FROM node " + "WHERE network_id = ? AND node_label = ?";
				PreparedStatement targetNodeIdStmt = connection.prepareStatement(sourceNodeIdQuery);
				targetNodeIdStmt.setLong(1, networkId);
				targetNodeIdStmt.setString(2, target);
				// System.out.println(targetNodeIdStmt.toString()); // Debug get node_id query
				// for target

				// Execute get get target node node_id query
				ResultSet targetNodeIdRs = targetNodeIdStmt.executeQuery();
				targetNodeIdRs.next(); // cursor starts before row 1
				targetId = targetNodeIdRs.getInt("node_id");

				// Close get target node node_id resources
				targetNodeIdRs.close();
				targetNodeIdStmt.close();

				// edge -----------------------

				// Ensure source and target were INSERTed
				// & ensure each edge is added only once
				if (!edges.contains(edgeConcat)) { // && sourceId != -1 && targetId != -1)

					// Prepare edge INSERT statement
					String edgeInsertQuery = "INSERT INTO edge (network_id, source_id, target_id, weight) "
							+ "VALUES (?, ?, ?, ?)";
					PreparedStatement edgeInsertStmt = connection.prepareStatement(edgeInsertQuery);
					edgeInsertStmt.setLong(1, networkId);
					edgeInsertStmt.setInt(2, sourceId); // Set & escape parameters
					edgeInsertStmt.setInt(3, targetId);
					edgeInsertStmt.setDouble(4, weight);
					// System.out.println(edgeInsertStmt.toString()); // Debug INSERT edge statement

					// Execute edge INSERT statement
					int nodeRowsUpdated = edgeInsertStmt.executeUpdate();

					// Close edge INSERT statement resources
					edgeInsertStmt.close();

					// Add edge to edgeConcat ArrayList to detect duplicates in next loop iteration
					edges.add(edgeConcat);

				} else {
					// TODO inform user that row # was skipped due to duplicate edge
				}

			} // end read csv line-by-line

			// try-with-resource auto-close
			// br.close();
			// connection.close(); // TODO learn about pooled connection closing

		} catch (SQLException | IOException e) {
			e.printStackTrace();
		}

		// Time this method
		long endTime = System.nanoTime();
		long totalTime = endTime - startTime;
		System.out.println("insertNetworkFromCSVFile (no batch): " + totalTime);
	}

	// Alternative to above

	// Read with opencsv Library 3.1
	// Reference: http://opencsv.sourceforge.net/
	public List<String[]> toStringArray(String filePath) throws IOException {

		try (CSVReader csvReader = new CSVReader(new FileReader(filePath));) {
			List<String[]> allRows = csvReader.readAll();
			System.out.println(allRows.toString());

			return allRows;
		} // TODO Implement CSVReader solution?
	}

	public void insertFromListOfArrays(String networkName, List<String[]> listOfArrays) {

	}

	public void batchInsert(String networkName, InputStream fileStream) throws SQLException, IOException {

	}
}
