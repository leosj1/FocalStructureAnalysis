<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%--
    Author: Fatih Şen
    Author: Andrew Pyle axpyle@ualr.edu MS Information Science 2018
    License: MIT
--%>

<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8">
    <title>Graph Analysis Platform</title>

    <!-- jQuery -->
    <script src="https://code.jquery.com/jquery-3.3.1.min.js"
            integrity="sha256-FgpCb/KJQlLNfOu91ta32o/NMZxltwRo8QtmkMRdAu8="
            crossorigin="anonymous">
    </script>
    
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

  <body>

    <%-- Logo, Title, and links --%>
    <div class="headerContainer">
      <div class="header">

        <div>
          <img src="images/mercek_logo.png" class="logo">
        </div>

        <div class="slogan">
          Visualize and Analyze Your Network Data
        </div>

        <div class="nav">
            <ul>
              <%-- FIXME This logout mechanism is bad --%>
              <li>Welcome, ${pageContext.request.userPrincipal.name}</li>
              <li> 
                <form name="logout" action="logout" method="post">
                  <button>Logout</button> 
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
          <div id="colorDescription" class="visItem">
            Nodes of the same color represent a focal structure. Grey nodes aren't part of a Focal Structure.
          </div>
        </div> <%-- #divVisualFeatures --%>

        <%-- Loading bar & Error output --%>
        <%-- TODO More lightweight loading indicator --%>
        <div id="loadingBar" class="graphInfoItems">
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

        <%-- D3.js graph viz canvas --%>
        <div id="d3vis" class="d3Style"></div>
      </div>

      <%-- Network chooser dropdown, buttons, upload file --%>
      <div id="menu">
        <div id="existingNetwork" class="formBox" style="height:390px;">
        
          <form id="ExistingNetworkForm">
            <div>
              <h2>Network</h2>
            </div>
            <%-- MySQL Query --%>
            <select id="networkList" name="networkList">
              <!-- <option value="placeholder" selected="true">Select a network</option>-->
              <c:forEach items="${networkIds.keys()}" var="networkId">
                <option value="${networkId}">${networkIds.get(networkId)}</option>
              </c:forEach>
            </select>
            <%-- TODO Retrieve & cache FSA groups--%>
            <input id="applyFSAUnweighted" class="FSAButtons" type="button" value="Apply FSA Unweighted" />
            <input id="applyFSAWeighted" class="FSAButtons" type="button" value="Apply FSA Weighted" />
          </form>
          
          <%-- Graph Info --%>
          <h2>Network Data</h2>
          <table id="graphInfo">
          </table>
          
          <%-- Download CSV Link --%>
          <a id="CSVExport" download="NetworkFSA.csv">Download FSA Analysis CSV</a>
          
          <%-- Explode FSA Groups --%>
          <form id="networkViewForm">
            <h2>Network View</h2>
            <div>
              <input type="radio" id="networkViewAll" name="networkView" value="all" checked>
              <label for="networkViewAll">All Nodes</label>
              <input type="radio" id="networkViewFSAOnly" name="networkView" value="fsaOnly">
              <label for="networkViewFSAOnly">FSA Groups Only</label>
            </div>
          </form>
          
        </div>
        
        <!--Upload CSV to server  -->
        <c:if test="${pageContext.request.isUserInRole('auth')}">
        
          <div class="formBox">
            <h2>Upload CSV</h2>
            <p id="csvFormatIcon" style="margin:0; cursor: pointer;">&#9432; CSV Format Info</p>
            <form id="uploadForm" method="post" action="upload-csv" enctype="multipart/form-data">
              <label id="uploadFormLabel" for="uploadForm"></label>
              <input name="networkName" type="text" maxlength="30" placeholder="Name your Network" required />
              <input id="filePicker" name="userCSV" type="file" accept="text/csv" required />
              <label for="uploadProgress" id="uploadProgressLabel"></label>
              <progress id="uploadProgress" value=0 max=1></progress>
              <input id="uploadSubmit" type="submit" value="Upload CSV" />
            </form>
          </div>
        </c:if>
        
        
      </div><!-- menu -->

    </div><!-- container -->

    <%-- Legal notice --%>
    <div id="footer">
      Copyright © Team COSMOS at University of Arkansas at Little Rock
    </div>
    <!-- Custom JS -->
    <script src="js/fsa_main.js" charset="utf-8"></script>
    <c:if test="${pageContext.request.isUserInRole('auth')}">
      <script src="js/upload_csv.js" charset="utf-8"></script>
    </c:if>
  </body>
</html>
