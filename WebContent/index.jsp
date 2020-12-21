<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%@page import="myclasses.NetworkLoader"%>
<%@page import="util.*"%>
<%@page import="authentication.*"%>
<%@ page import="java.io.*"%>
<%@page import="java.util.*"%>
<%@ page import="myclasses.NetworkGraph"%>
<%@ page import="myclasses.NetworkLoader"%>
<%@ page import="myclasses.FocalStructures"%>
<%@ page import="edu.ualr.fsa.*"%>
<%@ page import="org.json.simple.JSONObject"%>
<%@ page import="org.json.simple.parser.JSONParser"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>


<!DOCTYPE html>
<html>
<head>
<!-- For asynchronous browser based operations-->
<script src="http://code.jquery.com/jquery-1.10.1.min.js"></script>
<!-- For the file uploader -->
<script src="http://malsup.github.com/jquery.form.js"></script>

<script src="page_dependencies/baseurl.js?v=908"></script>
<!-- Custom JS -->
<!-- <script src="js/main.js" charset="utf-8"></script> -->




<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<title>Graph Analysis Platform</title>
<!-- jQuery -->
<script src="https://code.jquery.com/jquery-3.3.1.min.js"
	integrity="sha256-FgpCb/KJQlLNfOu91ta32o/NMZxltwRo8QtmkMRdAu8="
	crossorigin="anonymous">
    </script>

<script
	src="https://cdnjs.cloudflare.com/ajax/libs/jquery.form/4.3.0/jquery.form.min.js"
	integrity="sha512-YUkaLm+KJ5lQXDBdqBqk7EVhJAdxRnVdT2vtCzwPHSweCzyMgYV/tgGF4/dCyqtCC2eCphz0lRQgatGVdfR0ww=="
	crossorigin="anonymous"></script>
<!-- jQuery Fallback to Google CDN -->
<script>window.jQuery || document.write(
      '<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js" '+
        'integrity="sha256-FgpCb/KJQlLNfOu91ta32o/NMZxltwRo8QtmkMRdAu8=" '+
        'crossorigin="anonymous">' +
      '<\/script>')
    </script>

<!-- jQuery Fallback to Local -->
<script>window.jQuery || document.write(
      '<script src="js/jquery-3.3.1.min.js" '+
        'integrity="sha256-FgpCb/KJQlLNfOu91ta32o/NMZxltwRo8QtmkMRdAu8=" '+
        'crossorigin="anonymous">' +
     '<\/script>')
    </script>

<!-- D3.js -->
    <script src="https://d3js.org/d3.v4.min.js"
            integrity="sha256-hYXbQJK4qdJiAeDVjjQ9G0D6A0xLnDQ4eJI9dkm7Fpk="
            crossorigin="anonymous">
    </script>
    
    <!-- D3.js Fallback to Local -->
    <script>window.d3 || document.write(
      '<script src="js/d3.v4.min.js" ' +
        'integrity="sha256-hYXbQJK4qdJiAeDVjjQ9G0D6A0xLnDQ4eJI9dkm7Fpk=" ' +
        'crossorigin="anonymous">' +
      '<\/script>')
    </script>
    

    <!-- CSS -->
    <link rel="stylesheet" href="css/style.css"/>
    <link rel="stylesheet" href="css/upload_file.css"/>
    <link rel="stylesheet" href="css/loadspinner.css"/>
    <link rel="stylesheet" href="css/fsa_style.css"/>
  </head>
</head>

<body>
	<%-- TODO restore Authentication --%>
	<%
     Object email = (null == session.getAttribute("email")) ? "" : session.getAttribute("email");
     DbConnection dbconn = new DbConnection();
     NetworkDAO networkDAO = new NetworkDAO();
     List<ArrayList<String>> userinfo = dbconn.query("SELECT * FROM usercredentials where Email = '"+email+"'");
     String username = "";
     Hashtable<Integer, String> networkIds = new Hashtable<>();
     if (userinfo.size()<1) {
     	response.sendRedirect("login.jsp");
     }
     else{
    	 System.out.println("");
    	 username = userinfo.get(0).get(0);
    	 networkIds = networkDAO.getNetworks(username);
    	 //networkIds = (Hashtable<Integer, String>)session.getAttribute("networkIds");
     }
     
    %>

	<%-- Logo, Title, and links --%>
	<div class="headerContainer">
		<div class="header">

			<div>
				<img src="images/mercek_logo.png" class="logo">
			</div>

			<div class="slogan">Visualize and Analyze Your Network Data</div>

			<%-- <div class="nav">
				<ul>
					TODO: UI Decision: These go nowhere
					<li><a href="#">Home</a></li>
              <li><a href="#">About Us</a></li>
              <li><a href="#">Contact Us</a></li>
              <%if (userinfo.size()<1) { %>
					<li><a href="login.jsp">Login</a></li>
				<%}else{ %>
				<li><a href="logout.jsp">Logout</a></li>
				<%} %>
				</ul>
			</div> --%>
			
			<div class="nav">
            <ul>
              <%-- FIXME This logout mechanism is bad --%>
              <li>Welcome, <%=username %></li>
              <li> 
              <%if (userinfo.size()<1) { %>
                <form name="login" action="" method="post">
                  <button href="login.jsp">Login</button> 
                  <%}else{ %>
                  <form name="logout" action="logout" method="post">
                  <button>Logout</button> 
                  <%} %>
                </form>
              </li>
            </ul>
        </div>

		</div>
	</div>

	<%-- Interactive Content --%>
	<div id="container">

		<%-- Network Viz Canvas --%>
		<div id="content">

			<%-- Dropdowns & Checkboxes for graph visual attributes --%>
			<div id="divVisualFeatures" class="visFeatures">
				<div id="divLayoutType" class="visItem">
					Layout Type: <select id="layoutType" name="layoutType">
						<option value="forced">Forced Layout</option>
					</select>
				</div>

				<div id="divNodeStyle" class="visItem">
					Node Style: <select id="nodeStyle" name="nodeStyle">
						<option value="circle">Circle</option>
						<option value="square">Square</option>
						<option value="rectangle">Rectangle</option>
					</select>
				</div>

				<!--<div id="divNodeSize" class="visItem">
            Node Size:
            <select id="nodeSize" name="nodeSize">
              <option value="4">4</option>
              <option value="6">6</option>
              <option value="8">8</option>
              <option value="10" selected="true">10</option>
              <option value="12">12</option>
              <option value="14">14</option>
              <option value="16">16</option>
              <option value="18">18</option>
            </select>
          </div>-->

				<div id="divEdgeStyle" class="visItem">
					Edge Style: <select id="edgeStyle" name="edgeStyle">
						<option value="curvy">Curvy</option>
						<option value="straight">Straight</option>
					</select>
				</div>

				<!--<div id="divEdgeWidth" class="visItem">
            Edge Size:
            <select id="edgeWidth" name="edgeStyle">
              <option value="4">4</option>
              <option value="6">6</option>
              <option value="8" selected="true">8</option>
              <option value="10">10</option>
              <option value="12">12</option>
              <option value="14">14</option>
              <option value="16">16</option>
              <option value="18">18</option>
            </select>
          </div>-->

				<%-- TODO: This seems useful --%>
				<%-- <div id="divLabelSize" class="visItem">
            Label Font Size:
            <select id="labelSize" name="labelSize">
              <option value="12">12</option>
              <option value="14">14</option>
              <option value="16">16</option>
              <option value="18" selected="true">18</option>
              <option value="20">20</option>
              <option value="22">22</option>
              <option value="24">24</option>
            </select>
          </div> --%>

				<div id="divCaption" class="visItem">
					<input type="checkbox" id="chkCaption" name="chkCaption">
					Show Label
				</div>

				<div id="divWeight" class="visItem">
					<input type="checkbox" id="chkEdgeWeight" name="chkEdgeWeight">
					Show Weight
				</div>

				<div id="divColor" class="visItem">
					<input type="checkbox" id="chkNodeColor" name="chkNodeColor">
					Hide FSA Colors
				</div>
				<div id="divLink" class="visItem">
					<input type="checkbox" id="chkLinkWidth" name="chkLinkWidth">
					Link Width Proportional to Weight
				</div>
				<div id="divNode" class="visItem">
					<input type="checkbox" id="chkNodeRadius" name="chkNodeRadius">
					Node Size Proportional to Degree
				</div>

				<!--<div id="divDirectedNetwork" class="visItem">
            <input type="checkbox" id="chkDirectedNetwork"
              name="chkDirectedNetwork">
            Show Direction
          </div>-->
			</div>
			<%-- #divVisualFeatures --%>

			<%-- Loading bar & Error output.
        TODO Check CSS animations: gradient over time
        TODO Un-overload this div: error / loading bar / network info --%>
			<div id="divGraphInfo" class="graphInfoItems">
				<div id="squaresWaveG">
					<div id="squaresWaveG_1" class="squaresWaveG"></div>
					<div id="squaresWaveG_2" class="squaresWaveG"></div>
					<div id="squaresWaveG_3" class="squaresWaveG"></div>
					<div id="squaresWaveG_4" class="squaresWaveG"></div>
					<div id="squaresWaveG_5" class="squaresWaveG"></div>
					<div id="squaresWaveG_6" class="squaresWaveG"></div>
					<div id="squaresWaveG_7" class="squaresWaveG"></div>
					<div id="squaresWaveG_8" class="squaresWaveG"></div>
				</div>
			</div>

			<%-- Natural Language description of viz legend info --%>
			<div id="divResultLabel" class="visFeatures">
				<br>
				<div class="visItem">
					The nodes with a same color represent a focal structure, whereas
					the grey nodes are not included in a focal structure at all.
					<!--<a id="linkExportFocalStructures" href="">Export Focal structures</a>-->
				</div>
			</div>

			<%-- D3.js graph viz canvas --%>
			<div id="d3vis" class="d3Style"></div>

		</div>

		<%-- Network chooser dropdown, buttons, upload file --%>
		<div id="menu">
			<div id="existingNetwork" class="formBox" style="height: 390px;">
				<form id="ExistingNetworkForm">
					<div>Select an existing network!</div>

					<!-- <select id="network_file" name="network_file" style="width:100%;"> -->
					<select id="networkList" name="networkList">
						<%
              for(Integer x: networkIds.keySet()){
            	  request.setAttribute("networkId", x);
            	%>
						<option value="<%=x%>"><%= networkIds.get(x)%></option>
						<%}
              %>

						<!-- <option value="test.gdf">test.gdf</option>
              <option value="ecoli.gdf">E.coli Metabolic Network</option>
              <option value="Mutlu_network.gdf">Protein Complexes</option>
              <option value="zachary-karate.gdf">Zachary's Karate Club</option>
              <option value="polbooks.gdf">Books about US politics</option>
              <option value="BlogTagsNetwork.gdf">Blog Tags</option>
              <option value="GrahamPhillipsBlog.gdf">Ukraine Crisis Blog Network</option>
              <option value="ICSR-Network.gdf">ISIS Top Disseminators Network</option>
              
              <option value="10_09.csv">Saudi Women OctDriving Campaign-Day 9th</option>
              <option value="10_10.csv">Saudi Women OctDriving Campaign-Day 10th</option>
              <option value="10_13.csv">Saudi Women OctDriving Campaign-Day 13rd</option>
              <option value="10_14.csv">Saudi Women OctDriving Campaign-Day 14th</option>
              <option value="10_15.csv">Saudi Women OctDriving Campaign-Day 15th</option>
              <option value="10_16.csv">Saudi Women OctDriving Campaign-Day 16th</option>
              <option value="10_17.csv">Saudi Women OctDriving Campaign-Day 17th</option>
              <option value="10_18.csv">Saudi Women OctDriving Campaign-Day 18th</option>
              <option value="10_19.csv">Saudi Women OctDriving Campaign-Day 19th</option>
              <option value="10_20.csv">Saudi Women OctDriving Campaign-Day 20th</option>
              <option value="10_21.csv">Saudi Women OctDriving Campaign-Day 21st</option>
              <option value="10_22.csv">Saudi Women OctDriving Campaign-Day 22nd</option>
              <option value="10_23.csv">Saudi Women OctDriving Campaign-Day 23rd</option>
              <option value="10_24.csv">Saudi Women OctDriving Campaign-Day 24th</option>
              <option value="10_25.csv">Saudi Women OctDriving Campaign-Day 25th</option>
              <option value="10_26.csv">Saudi Women OctDriving Campaign-Day 26th</option>
              <option value="10_27.csv">Saudi Women OctDriving Campaign-Day 27th</option>
              <option value="10_28.csv">Saudi Women OctDriving Campaign-Day 28th</option>
              <option value="10_29.csv">Saudi Women OctDriving Campaign-Day 29th</option>
              <option value="10_30.csv">Saudi Women OctDriving Campaign-Day 30th</option> -->
					</select>

					<%-- TODO: This button seems to popup the filename of the selected dataset on the server  --%>
					<input id="btnFSA" type="button" value="Apply FSA" />
					<%-- This button seems to actually apply the algorithms --%>
					<%-- TODO: loading bar goes forever sometimes --%>
					<input id="btnFSAW" type="button"
						value="Apply FSA (For Weighted Graph)" />
					<%-- TODO: These buttons don't do anything --%>
					<input id="btnClusteringCoefficient" type="button"
						class="btnAlgorithm" value="Clustering Coefficient" /> <input
						id="btnDegreeCentrality" type="button" class="btnAlgorithm"
						value="Degree Centrality" /> <input id="btnPageRank"
						type="button" class="btnAlgorithm" value="Page Rank" /> <input
						id="btnBetweenness" type="button" class="btnAlgorithm"
						value="Betweenness" /> <input id="btnModularity" type="button"
						class="btnAlgorithm" value="Modularity" /> <input
						id="btnSimilarity" type="button" class="btnAlgorithm"
						value="Similarity" /> <input id="btnDijkstra" type="button"
						class="btnAlgorithm" value="Dijkstra's Algorithm" />
				</form>
			</div>

			<%-- TODO WHY inline styles??? --%>
			<div
				style="color: #000; text-align: center; margin: 0 auto; width: 100%">
				Or</div>

			<div class="formBox">
				<!-- <form id="UploadForm" action="" method="post"
					enctype="multipart/form-data">
					<div id="divNetworkType" style="float: left;">
						<label> Upload a network file! Only .gdf and .csv formats
							are supported! </label> <br />
						<input type="checkbox" id="chkNetworkType" name="chkNetworkType"> Directed Network
					</div>

					<input type="file" size="15" id="myfile" name="myfile"
						style="color: black;"> <input id="btnUpload" type="submit"
						value="Upload">

					<div id="progressbox" style="margin: 6px; margin-left: 1px;">
						<div id="progressbar"></div>
						<div id="percent">0%</div>
						<div id="message"></div>
					</div> -->
			<form id="uploadForm" method="post" action="upload-csv" enctype="multipart/form-data">
              <label id="uploadFormLabel" for="uploadForm"></label>
              <input name="networkName" type="text" maxlength="30" placeholder="Name your Network" required />
              <input name="ownerName" type="hidden" value="<%=username%>"/>
              <input id="filePicker" name="userCSV" type="file" accept="text/csv" required />
              <!-- <label for="uploadProgress" id="uploadProgressLabel"></label>
              <progress id="uploadProgress" value=0 max=1></progress> -->
              
              <input id="uploadSubmit" type="submit" value="Upload CSV" />
					<div id="progressbox" style="margin: 6px; margin-left: 1px;">
						<div id="progressbar"></div>
						<div id="percent">0%</div>
						<div id="message"></div>
					</div>
					<%-- TODO: repeated buttons seem to be used differently in main.js --%>
					<input id="btnFSA2" type="button" value="Apply FSA" /> <input
						id="btnClusteringCoefficient2" type="button" class="btnAlgorithm"
						value="Clustering Coefficient" /> <input
						id="btnDegreeCentrality2" type="button" class="btnAlgorithm"
						value="Degree Centrality" /> <input id="btnPageRank2"
						type="button" class="btnAlgorithm" value="Page Rank" /> <input
						id="btnBetweenness2" type="button" class="btnAlgorithm"
						value="Betweenness" /> <input id="btnModularity2" type="button"
						class="btnAlgorithm" value="Modularity" /> <input
						id="btnSimilarity2" type="button" class="btnAlgorithm"
						value="Similarity" /> <input id="btnDijkstra2" type="button"
						class="btnAlgorithm" value="Dijkstra's Algorithm" />
				</form>
			</div>
		</div>
		<!-- menu -->
	</div>
	<!-- container -->

	<%-- Legal notice --%>
	<div id="footer">Copyright © Team COSMOS at University of
		Arkansas at Little Rock</div>
</body>

<!-- TODO Remove Temp Custom JS -->
<script src="js/main.js" charset="utf-8"></script>
<script src="js/fsa_main.js" charset="utf-8"></script>

<%-- <script src="js/db_test.js" charset="utf-8"></script> --%>
</html>
