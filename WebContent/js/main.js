/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

 /**
  * UI & Visualization Section
  */
var baseurl = app_url;

// Do after page load
$(document).ready(function(){
  // Declare all program variables
  var width = 700,
      height = 900,
      graph,
      processed,  // NOTE ? Global variable: algorithm has been applied
      node,
      path,
      svg,
      linkedByIndex = {},
      nodeColor,
      nFile,
      linktext,
      nodeStyle = "circle", // default
      edgeStyle = "curvy",  // default
      rectWidth=20,
      rectHeight=10,
      squareWidth=15,
      r=8;

  // ------Set page defaults-----------
  // Get selected network from dropdown
  var sel = $("#network_file" ).val();

  // Hide graph loading bar
  $("#squaresWaveG").hide();

  // Disable algorithms since no network is selected
  // TODO Why not just set defaults and enable if valid selection made?
  if(sel == '0'){
    $('#divVisualFeatures').hide();
    $('#divResultLabel').hide();
    $("#btnFSA").prop("disabled",true);
    $("#btnClusteringCoefficient").prop("disabled",true);
    $("#btnDegreeCentrality").prop("disabled",true);
    $("#btnPageRank").prop("disabled",true);
    $("#btnBetweenness").prop("disabled",true);
    $("#btnModularity").prop("disabled",true);
    $("#btnSimilarity").prop("disabled",true);
    $("#btnDijkstra").prop("disabled",true);
    $("#d3vis").empty();
  }
  // ------End page defaults-----------

  // Event handler for network dropdown
  // hits "Internal Error: null in #divVisualFeatures" on change event
  $("#network_file").change(function(){

    // Hide network visual option selectors
    $('#divVisualFeatures').hide();

    // Name of selected network file
    nFile = $("#network_file" ).val();

    // Show loading bar. NOTE Assuming that a graph will be processed
    // TODO Move to better place.
    $("#squaresWaveG").show();

    // Disable algorithms since no network is selected
    // TODO remove unused buttons
    if(nFile == '0'){

      $('#divVisualFeatures').hide();
      $('#divResultLabel').hide();
      $("#btnFSA").prop("disabled",true);
      $("#btnClusteringCoefficient").prop("disabled",true);
      $("#btnDegreeCentrality").prop("disabled",true);
      $("#btnPageRank").prop("disabled",true);
      $("#btnBetweenness").prop("disabled",true);
      $("#btnModularity").prop("disabled",true);
      $("#btnSimilarity").prop("disabled",true);
      $("#btnDijkstra").prop("disabled",true);
      $("#d3vis").empty();

    }

    // Process selected network file
    else {
      processed = false;
      // Clear visualization canvas
      $("#d3vis").empty();

      // Retrieve network file as JSON from server by AJAX call
      // TODO Replace with MySQL query
      var req = $.ajax({
        type: "GET",
        url: "ajaxrequest.jsp?network_file="+nFile+"&process=false",
        contentType: "application/json; charset=utf-8",
        dataType: "json",

        success: function(response){
          // Store graph returned from network file
          graph = response;  // type == JSON
          // Enable Algorithms
          // TODO remove unused buttons
          $("#btnFSA").prop("disabled",false);
          $("#btnClusteringCoefficient").prop("disabled",false);
          $("#btnDegreeCentrality").prop("disabled",false);
          $("#btnPageRank").prop("disabled",false);
          $("#btnBetweenness").prop("disabled",false);
          $("#btnModularity").prop("disabled",false);
          $("#btnSimilarity").prop("disabled",false);
          $("#btnDijkstra").prop("disabled",false);
          //alert(graph.graphInfo[0]["numNodes"]);
        },

        error: function(response){
          // Network visual option selectors
          $("#divVisualFeatures").hide();
          // Display text from HTML response in loading bar div
          $("#divGraphInfo").html(response.responseText);
        }

      });

      // Create basic, all-grey network viz when AJAX call completes
      $.when(req).done(function(){

        // Create SVG of network
        applyd3dotjs(graph, processed);  // processed == false?

        // Show network visual option selectors for viz output
        $('#divVisualFeatures').show();

        // Display network info: # nodes, # edges, etc.
        var graphInfoHtml = getGraphInfo(graph, processed);
        $("#divGraphInfo").html(graphInfoHtml);

        // Hide loading bar
        $("#squaresWaveG").hide();

      });

    } // else (selection made in dropdown)

    // Commented out by Fatih
    // TODO Implement export graph
    $("#btnExportGraph").click(function(){
      var url = "uploaded_files/" + nFile;
      oIFrm = document.getElementById('myIFrm');
      oIFrm.src = url;
    });

  }); // network dropdown onchange event

  // TODO remove hack for #network_dropdown_db
//Event handler for network dropdown
// hits "Internal Error: null in #divVisualFeatures" on change event
$("#network_file_db").change(function(){

  // Hide network visual option selectors
  $('#divVisualFeatures').hide();

  // Name of selected network file
  nFile = $("#network_file_db" ).val();

  // Show loading bar. NOTE Assuming that a graph will be processed
  // TODO Move to better place.
  $("#squaresWaveG").show();

  // Disable algorithms since no network is selected
  // TODO remove unused buttons
  if(nFile == '0'){

    $('#divVisualFeatures').hide();
    $('#divResultLabel').hide();
    $("#btnFSA").prop("disabled",true);
    $("#btnClusteringCoefficient").prop("disabled",true);
    $("#btnDegreeCentrality").prop("disabled",true);
    $("#btnPageRank").prop("disabled",true);
    $("#btnBetweenness").prop("disabled",true);
    $("#btnModularity").prop("disabled",true);
    $("#btnSimilarity").prop("disabled",true);
    $("#btnDijkstra").prop("disabled",true);
    $("#d3vis").empty();

  }

  // Process selected network file
  else {
    processed = false;
    // Clear visualization canvas
    $("#d3vis").empty();

    // Retrieve network file as JSON from server by AJAX call
    // TODO Replace with MySQL query
    // Retrieve network file as JSON from server by AJAX call
    // TODO Replace with MySQL query
    var req = $.ajax({
      type: "POST",
      // url: "ajaxrequest.jsp?network_file="+nFile+"&process=false",
      url: "fsa/load?network_name=" + nFile,
      contentType: "application/json; charset=utf-8",
      dataType: "json",

      success: function(response){
        // TODO Remove test
        alert("db_test ajax success");


        // Store graph returned from network file
        graph = response;  // type == JSON
        // Enable Algorithms
        // TODO remove unused buttons
        $("#btnFSA").prop("disabled",false);
        $("#btnClusteringCoefficient").prop("disabled",false);
        $("#btnDegreeCentrality").prop("disabled",false);
        $("#btnPageRank").prop("disabled",false);
        $("#btnBetweenness").prop("disabled",false);
        $("#btnModularity").prop("disabled",false);
        $("#btnSimilarity").prop("disabled",false);
        $("#btnDijkstra").prop("disabled",false);
        //alert(graph.graphInfo[0]["numNodes"]);
      },

      error: function(response){
        alert("db_test ajax error") // TODO Remove debug
        // Network visual option selectors
        $("#divVisualFeatures").hide();
        // Display text from HTML response in loading bar div
        $("#divGraphInfo").html(response.responseText);
      }

    });

    // Create basic, all-grey network viz when AJAX call completes
    $.when(req).done(function(){

      // Create SVG of network
      applyd3dotjs(graph, processed);  // processed == false?

      // Show network visual option selectors for viz output
      $('#divVisualFeatures').show();

      // Display network info: # nodes, # edges, etc.
      var graphInfoHtml = getGraphInfo(graph, processed);
      $("#divGraphInfo").html(graphInfoHtml);

      // Hide loading bar
      $("#squaresWaveG").hide();

    });

  } // else (selection made in dropdown)

  // Commented out by Fatih
  // TODO Implement export graph
  $("#btnExportGraph").click(function(){
    var url = "uploaded_files/" + nFile;
    oIFrm = document.getElementById('myIFrm');
    oIFrm.src = url;
  });

}); // network dropdown onchange event

  // Button click event handler
  $("#btnFSA").click(function() {
    // Retrieve selected network name from dropdown
    nFile = $("#network_file" ).val();
    // NOTE why is there an alert here? Probably debug tool
    alert(nFile);
    // Clear vizualization Canvas
    $("#d3vis").empty();
    // Show loading bar
    $("#squaresWaveG").show();

    processed = true;
    var graphReady = false;

    // Apply FSA algorithm AJAX call
    // TODO what are the parameters in this url for?
    var req = $.ajax({
      type: "GET",
      url: "ajaxrequest.jsp?network_file="+nFile+"&process=true",
      contentType: "application/json; charset=utf-8",
      dataType: "json",
      success: function(response){
        // Store response JSON of network with FSA applied
        graph = response;

      },
      error: function(response){

        // Show error message from HTTP response
        $("#divVisualFeatures").hide();
        $("#divGraphInfo").html(response.responseText);

      }
    });

    // Create colored, FSA network viz when AJAX call completes
    $.when(req).done(function(){
    //$.done(function(){
    //d3.json("ajaxrequest.jsp?network_file="+nFile, function(error, graph) {

      //if(graphReady)
      // Generate network SVG with D3.js
      applyd3dotjs(graph,processed);

      // Show network visual option selectors
      $('#divVisualFeatures').show();

      // Show network info: # nodes, # edges, etc.
      var graphInfoHtml = getGraphInfo(graph,processed);
      $("#divGraphInfo").html(graphInfoHtml);

      //var fsaResults = getFSAResults(graph);
      //$("#divGraphInfo").html(fsaResults);

      // Hide loading bar
      $("#squaresWaveG").hide();

    }); //End of .done(function(graph) {

  });//FSA button click event
  // NOTE Only difference from #btnFSA is addition of new HTTP parameter
  // in AJAX request: processName=fsaw

  // TODO UI control selector fix
  //Button click event handler
  $("#servletFSA").click(function() {
    // Retrieve selected network name from dropdown
    nFile = $("#network_file_db" ).val();
    // NOTE why is there an alert here? Probably debug tool
    alert(nFile);
    // Clear vizualization Canvas
    $("#d3vis").empty();
    // Show loading bar
    $("#squaresWaveG").show();

    processed = true;
    var graphReady = false;

    // Apply FSA algorithm AJAX call
    // TODO what are the parameters in this url for?
    var req = $.ajax({
      type: "POST",
      url: "load/fsa?network_name="+nFile,
      contentType: "application/json; charset=utf-8",
      dataType: "json",
      success: function(response){
    	  	alert("ajax success");
        // Store response JSON of network with FSA applied
        graph = response;

      },
      error: function(response){
    	  	alert("ajax failure");
        // Show error message from HTTP response
        $("#divVisualFeatures").hide();
        $("#divGraphInfo").html(response.responseText);

      }
    });

    // Create colored, FSA network viz when AJAX call completes
    $.when(req).done(function(){
    //$.done(function(){
    //d3.json("ajaxrequest.jsp?network_file="+nFile, function(error, graph) {

      //if(graphReady)
      // Generate network SVG with D3.js
      applyd3dotjs(graph,processed);

      // Show network visual option selectors
      $('#divVisualFeatures').show();

      // Show network info: # nodes, # edges, etc.
      var graphInfoHtml = getGraphInfo(graph,processed);
      $("#divGraphInfo").html(graphInfoHtml);

      //var fsaResults = getFSAResults(graph);
      //$("#divGraphInfo").html(fsaResults);

      // Hide loading bar
      $("#squaresWaveG").hide();

    }); //End of .done(function(graph) {
  }); // servletFSA button click event

  // TODO Remove Test Button click event handlers
  $("#servletFSAWeighted").click(function() {
    // Retrieve selected network name from dropdown
    nFile = $("#network_file_db" ).val();
    // NOTE why is there an alert here? Probably debug tool
    alert(nFile);
    // Clear vizualization Canvas
    $("#d3vis").empty();
    // Show loading bar
    $("#squaresWaveG").show();

    processed = true;
    var graphReady = false;

    // Apply FSA algorithm AJAX call
    // TODO what are the parameters in this url for?
    var req = $.ajax({
      type: "POST",
      url: "load/fsa/weighted?network_name="+nFile,
      contentType: "application/json; charset=utf-8",
      dataType: "json",
      success: function(response){
    	  	alert("ajax success");
        // Store response JSON of network with FSA applied
        graph = response;

      },
      error: function(response){
    	  	alert("ajax failure");
        // Show error message from HTTP response
        $("#divVisualFeatures").hide();
        $("#divGraphInfo").html(response.responseText);

      }
    });

    // Create colored, FSA network viz when AJAX call completes
    $.when(req).done(function(){
    //$.done(function(){
    //d3.json("ajaxrequest.jsp?network_file="+nFile, function(error, graph) {

      //if(graphReady)
      // Generate network SVG with D3.js
      applyd3dotjs(graph,processed);

      // Show network visual option selectors
      $('#divVisualFeatures').show();

      // Show network info: # nodes, # edges, etc.
      var graphInfoHtml = getGraphInfo(graph,processed);
      $("#divGraphInfo").html(graphInfoHtml);

      //var fsaResults = getFSAResults(graph);
      //$("#divGraphInfo").html(fsaResults);

      // Hide loading bar
      $("#squaresWaveG").hide();

    }); //End of .done(function(graph) {

  });//FSA Weighted button click event

  // TODO Remove Test Button clisk event handlers
  $("#ProcessCSVServletPOST").click(function() {
    var req = $.ajax({
      type: "POST",
      url: "process_csv",
      contentType: "text/plain; charset=utf-8",
      dataType: "text",
      success: function(response){
        alert("ajax success");
      },
      error: function(response){
        alert("ajax failure");
        // Show error message from HTTP response
      }
    });
  });

  $("#btnFSAW").click(function() {
    // Retrieve selected network name from dropdown
    nFile = $("#network_file" ).val();
    // Clear viz canvas
    $("#d3vis").empty();
    // Show loading Bar
    // TODO abstract function for these repetitive tasks
    $("#squaresWaveG").show();

    // NOTE Adjust global state variables
    processed = true;
    var graphReady = false;

    // Apply FSA Weighted algorithm AJAX call
    var req = $.ajax({
    type: "GET",
    url: "ajaxrequest.jsp?network_file="+nFile+"&process=true&processName=fsaw",
    contentType: "application/json; charset=utf-8",
    dataType: "json",
    success: function(response){
      // Store JSON of FSA Weighted network
      graph = response;

      },
      error: function(response){
        // Show HTTP response error message
        $("#divVisualFeatures").hide();
        $("#divGraphInfo").html(response.responseText);
      }
    });

    // // Create colored, FSA Weighted network viz when AJAX call completes
    $.when(req).done(function(){
    //$.done(function(){
    //d3.json("ajaxrequest.jsp?network_file="+nFile, function(error, graph) {

      //if(graphReady)
      // Create SVG of network in D3.js
      applyd3dotjs(graph,processed);
      // Show network visual option selectors
      $('#divVisualFeatures').show();
      // Show network info: # nodes, # edges, etc.
      var graphInfoHtml = getGraphInfo(graph,processed);
      $("#divGraphInfo").html(graphInfoHtml);

      //var fsaResults = getFSAResults(graph);
      //$("#divGraphInfo").html(fsaResults);
      // Hide loading bar
      $("#squaresWaveG").hide();

    }); //End of .done(function(graph) {

  });//FSA Weight button click event

  // NOTE This function only exists to assign a local path to nFile rather than
  //      an option from the hardcoded HTML dropdown
  // TODO Replace it for a unified SQL solution
  $( "#btnFSA2" ).click(function() {  //this is used after uploading a file

    // Assign an escaped local path to upload
    nFile = $("#myfile" ).val();   //returns C:/fakepath/filename.extension
    nFile = nFile.split('\\').pop().split('/').pop();
    //alert(nFile);
    // Clear viz canvas
    $("#d3vis").empty();
    // Show loading Bar
    $("#squaresWaveG").show();
    // Adjust Global state variables. TODO Bad
    var graph;
    processed = true;

    var req = $.ajax({
      type: "GET",
      url: "ajaxrequest.jsp?network_file_uploaded="+nFile+"&process=true",
      contentType: "application/json; charset=utf-8",
      dataType: "json",
      success: function(response){
        // Shows #divResultLabel because it it is default hidden until a
        // dropdown selection is made
        $('#divResultLabel').show();
        graph = response;

      }
    });

    $.when(req).done(function(){
    //$.done(function(){
    //d3.json("ajaxrequest.jsp?network_file="+nFile, function(error, graph) {

      // NOTE: ASP: Is the above commented out d3 function call needed?
      applyd3dotjs(graph,processed);

      var graphInfoHtml = getGraphInfo(graph,processed);
      $("#divGraphInfo").html(graphInfoHtml);
      $("#squaresWaveG").hide();

    }); //End of .done(function(graph) {

  });//2nd FSA button click event

  $("#nodeStyle").change(function() {

      nodeShapeStyle();
  });

  $("#edgeStyle").change(function() {

      edgeShapeStyle();
  });

  // Functions to create visualization
  // TODO Write docstrings for these
  // NOTE Are these copied without attribution?
  function nodeShapeStyle(){

    svg.selectAll("circle.node").remove();
    svg.selectAll("rect.node").remove();
    nodeStyle = $("#nodeStyle").val();

    if(nodeStyle=="circle"){

      node.append("circle")
        .attr("class", "node")

        .attr("r", r)
    }
    else if(nodeStyle == "rectangle"){

      node.append("rect")
        .attr("class", "node")

        .attr("width", rectWidth)
        .attr("height", rectHeight)
    }
    else if(nodeStyle == "square"){

      node.append("rect")
        .attr("class", "node")
        .attr("width", squareWidth)
        .attr("height", squareWidth)
    }
  }

  function edgeShapeStyle(){

    svg.selectAll("path").remove();
    svg.selectAll("line").remove();

    edgeStyle = $("#edgeStyle").val();



    if(edgeStyle=="curvy"){
      path = svg.selectAll("path")
        .data(graph.links)
        .enter().append("path")
        .attr("class", "link")

      path.attr("d", function(d) {

        var dx = d.target.x - d.source.x,
          dy = d.target.y - d.source.y;
          dr = Math.sqrt(dx * dx + dy * dy);

          return "M" +
            d.source.x + "," +
            d.source.y + "A" +
            dr + "," + dr + " 0 0,1 " +
            d.target.x + "," +
            d.target.y;
       });

      path.attr("id",function(d,i) { return "linkId_" + i; })
        .style("stroke-width", function(d) { return Math.sqrt(d.value); });

      linktext = svg.selectAll("g.linklabelholder")
                              .data(force.links())
                              .enter().append("g")
                              .append("text")
                              .attr("class", "linklabelholder")
                              .attr("dx", 35)
                              .attr("dy", 0)
                              .append("textPath")
                              .attr("xlink:href",function(d,i) { return "#linkId_" + i;})
                              .text(function(d,i) { return "" + d.value; });

    }
    else if(edgeStyle == "straight"){

      /*path = svg.selectAll("line")
              .data(graph.links)
              .enter().append("line")

      path.attr("class", "link")
      .attr("id",function(d,i) { return "linkId_" + i; })
      .attr("x1", function(d) { return d.source.x; })
      .attr("y1", function(d) { return d.source.y; })
      .attr("x2", function(d) { return d.target.x; })
      .attr("y2", function(d) { return d.target.y; })
      .style("stroke-width", function(d) { return Math.sqrt(d.value); });*/

      path = svg.selectAll("path")
        .data(graph.links)
        .enter().append("path")
        .attr("class", "link")

        .attr("d", function(d) {

        return "M" +
          d.source.x + "," +
          d.source.y + "L" +
          d.target.x + "," +
          d.target.y;
        });

        path.attr("id",function(d,i) { return "linkId_" + i; })
          .style("stroke-width", function(d) { return Math.sqrt(d.value); });

        linktext = svg.selectAll("g.linklabelholder")
                        .data(force.links())
                        .enter().append("g")
                        .append("text")
                        .attr("class", "linklabelholder")
                        .attr("dx", 35)
                        .attr("dy", 0)
                        .append("textPath")
                        .attr("xlink:href",function(d,i) { return "#linkId_" + i;})
                        .text(function(d,i) { return "" + d.value; });
    }//else if line

  } // function edgeShapeStyle()

  function getGraphInfo(g, isProcessed){

    var graphInfoDiv = "<div>Number of Nodes: " + g.graphInfo[0]["numNodes"]+"</div>";
    graphInfoDiv += "<div>Number of Edges: " + g.graphInfo[0]["numEdges"]+"</div>";
    graphInfoDiv += "<div><a href='uploaded_files/" + nFile + "' id='btnExportGraph' >";
    graphInfoDiv += "Export Graph</a></div>";

    //alert(graphInfoDiv);

    if(isProcessed) { //if fsa algorithm applied!
      graphInfoDiv += "<div><a href='FSA_Results/fsa_output.txt' id='linkFSAResults' >Export FSA Results</a></div>";
    }

    return graphInfoDiv;
  }

  //Get the visualization using d3.js library!
  function applyd3dotjs(graph, isProcessed){

    linkedByIndex = {}
    //nodeColors = {};
    graph.links.forEach(function(d) {
      linkedByIndex[d.source + "," + d.target] = 1;
    });

    var color = d3.scale.category20();

    var force = d3.layout.force()
                .charge(-250)
                .linkDistance(50)
                .size([width, height])
                .on("tick", tick);

    svg = d3.select("#d3vis").append("svg")
          .attr("width", "100%") //width
          .attr("height", height) //height
          .attr('pointer-events', 'all')
          .append("svg:g")
          .call(d3.behavior.zoom().on("zoom", redraw))
          .append("svg:g");

    //add marker for directed graphs
    /*svg.append("defs").selectAll("marker")
    .enter().append("marker")
      .attr("id", function(d) { return d; })
      .attr("viewBox", "0 -5 10 10")
      .attr("refX", 15)
      .attr("refY", -1.5)
      .attr("markerWidth", 6)
      .attr("markerHeight", 6)
      .attr("orient", "auto");
    */

    force.nodes(graph.nodes).links(graph.links).start();

    if(edgeStyle=="straight") {
      path = svg.selectAll("path").data(graph.links).enter().append("svg:path")
    }

    else {
      path = svg.selectAll("path")
      .data(graph.links)
      .enter().append("svg:path")
    }

    path.attr("class", "link")
        .attr("id",function(d,i) { return "linkId_" + i; })
        .style("stroke-width", function(d) { return Math.sqrt(d.value); });

    linktext = svg.selectAll("g.linklabelholder")
                  .data(force.links())
                  .enter().append("g")
                  .append("text")
                  .attr("class", "linklabelholder")
                  .attr("dx", 35)
                  .attr("dy", 0)
                  .append("textPath")
                  .attr("xlink:href",function(d,i) { return "#linkId_" + i;})
                  .text(function(d,i) { return "" + d.value; });

    node = svg.selectAll(".node")
              .data(graph.nodes)
              .enter()
              .append('g')
              .classed('node', true)


    node.append("title")
        .text(function(d) { return d.name; });

    nodeShapeStyle();

    // TODO report number of focal structures to user as text as well as colors
    node.style("fill", function(d) {
      if(isProcessed) {
        if(d.group != 0) {

         nodeColor = color(d.group);
         //nodeColors[d.index] = nodeColor;//d.group;
         return nodeColor;//'#1c77e9';//'#1f77b4'; //color(d.group);
        }

        else {
         nodeColor = '#808080' ;
         //nodeColors[d.index] = nodeColor;
         return nodeColor;//'#1c77e9';
        }

      }
       else {
          nodeColor = '#808080';
          //nodeColors[d.index] = nodeColor;
          return nodeColor;
       }
    })
    .on("mouseover", fade(.1))
    .on("mouseout", normalizeNodes())
    .call(force.drag);

    showNodeCaptions();

    showEdgeWeights();

    $( "#chkCaption" ).on( "click", function() {
      showNodeCaptions();
    });

    $( "#chkEdgeWeight" ).on( "click", function() {
      showEdgeWeights();
    });

    function showNodeCaptions(){

      if($('#chkCaption').is(':checked')){

          node.append("text")
              .attr("class", function(d){ return "nodetext"})
              .attr('fill', 'black')
           // .attr("dx", 12)
           // .attr("dy", ".35em")
              .text(function(d) { return d.name; });
      }

      else {//remove the label if unchecked
          svg.selectAll("text.nodetext").remove();
      }
    }

    function showEdgeWeights(){

      if($('#chkEdgeWeight').is(':checked')){
        linktext = svg.selectAll("g.linklabelholder")
                      .data(force.links())
                      .enter().append("g")
                      .append("text")
                      .attr("class", "linklabelholder")
                      .attr("dx", 35)
                      .attr("dy", 0)
                      .append("textPath")
                      .attr("xlink:href", function(d,i) {
                        return "#linkId_" + i;
                        })
                      .text(function(d,i) { return "" + d.value; });
      }
      else {
        svg.selectAll("text.linklabelholder").remove();
      }

    }

    function redraw() {

      //console.log("here", d3.event.translate, d3.event.scale);
      svg.attr("transform","translate(" + d3.event.translate + ")" + " scale(" + d3.event.scale + ")");
    }

    //source: http://www.d3noob.org/2013/03/d3js-force-directed-graph-example-basic.html
    function tick() {

      //edgeStyle = "straight";
      if(edgeStyle == "curvy") {

        path.attr("d", function(d) {

          var dx = d.target.x - d.source.x,
              dy = d.target.y - d.source.y;
              dr = Math.sqrt(dx * dx + dy * dy);

          return "M" +
              d.source.x + "," +
              d.source.y + "A" +
              dr + "," + dr + " 0 0,1 " +
              d.target.x + "," +
              d.target.y;
        });

      }

      else { //if line
      path.attr("d", function(d) {

        return "M" +
            d.source.x + "," +
            d.source.y + "L" +
            d.target.x + "," +
            d.target.y;

        });
      }

      // link label
      linktext.attr("transform", function(d) {
        return "translate(" + (d.source.x + d.target.x) / 2 + ","
                + (d.source.y + d.target.y) / 2 + ")";
        });

      // nodes
      node.attr("transform", function(d) {
          return "translate(" + d.x + "," + d.y + ")";
        })

      if(nodeStyle == "circle"){

        node.attr("cx", function(d) { return d.x; })
            .attr("cy", function(d) { return d.y; });
      }

      if(nodeStyle == "rectangle"){

          node.attr("x", function(d) { return d.x+rectWidth/2; })
              .attr("y", function(d) { return d.y+rectHeight/2; });
      }

    } // function tick()

    //source: http://bl.ocks.org/christophermanning/1625629
    function fade(opacity, showText) {
      return function(d,i) {

        svg.selectAll(".node").style("fill-opacity", function(o) {
            var isNodeConnectedBool = isNodeConnected(d, o);
            var thisOpacity = isNodeConnectedBool ? 1 : opacity;
            //alert(thisOpacity);
            return thisOpacity;
          });


            path.style("stroke-opacity", function(o) {
                return o.source === d || o.target === d ? 1 : opacity;
              });

        }

    }//end of fade function

    //source: http://bl.ocks.org/christophermanning/1625629
    function normalizeNodes(node) {

      return function(d,i) {
          selectedLabelIndex = null;
          svg.selectAll(".link").style("stroke-opacity", 1);
          svg.selectAll(".node").style("stroke-opacity", .6)
             .style("fill-opacity", 1.0).style("stroke-width", 1.5);
        }

    }

    //source: http://bl.ocks.org/christophermanning/1625629
    function isNodeConnected(a, b) {
      return linkedByIndex[a.index + "," + b.index] || linkedByIndex[b.index + "," + a.index] || a.index == b.index;
    }
  }


  /**
   * File Uploader Section
   */
  //For file uploader
  var options = {
    beforeSend : function() {

      $("#progressbox").show();
      $("#squaresWaveG").show();
      // clear everything
      $("#progressbar").width('0%');
      $("#message").empty();
      $("#percent").html("0%");

    },
    uploadProgress : function(event, position, total, percentComplete) {

      $("#progressbar").width(percentComplete + '%');
      $("#percent").html(percentComplete + '%');
      // change message text to red after 50%
      if (percentComplete > 50) {

        $("#message").html("<font color='red'>File Upload is in progress!</font>");

      }
    },

    success : function() {
      $("#progressbar").width('100%');
      $("#percent").html('100%');
    },

    complete : function(response) {
      //$("#message").html(response.responseText);
      $("#message").html("<font color='blue'>Your file has been uploaded!</font>");
      $("#btnFSA2").prop("disabled",false);
      $("#btnClusteringCoefficient2").prop("disabled",false);
      $("#btnDegreeCentrality2").prop("disabled",false);
      $("#btnPageRank2").prop("disabled",false);
      $("#btnBetweenness2").prop("disabled",false);
      $("#btnModularity2").prop("disabled",false);
      $("#btnSimilarity2").prop("disabled",false);
      $("#btnDijkstra2").prop("disabled",false);

      loadBrowsedFile();

      $("#squaresWaveG").hide();
    },

    error : function() {
      $("#message").html(
          "<font color='red'> ERROR: unable to upload files</font>"
        );
    }

  };  // var options


  $("#UploadForm").ajaxForm(options);

  $("#btnUpload").prop("disabled",true);
  $("#btnFSA2").prop("disabled",true);
  $("#btnClusteringCoefficient2").prop("disabled",true);
  $("#btnDegreeCentrality2").prop("disabled",true);
  $("#btnPageRank2").prop("disabled",true);
  $("#btnBetweenness2").prop("disabled",true);
  $("#btnModularity2").prop("disabled",true);
  $("#btnSimilarity2").prop("disabled",true);
  $("#btnDijkstra2").prop("disabled",true);

  function checkFileFormat(){
    var ext = $('#myfile').val().split('.').pop().toLowerCase();

    if($.inArray(ext, ['gdf','csv','net']) == -1){
      return false;
    }

    else {
      return true;
    }
    //alert('invalid extension!');

  }

  $("#myfile").change(function() {

    if(!checkFileFormat()){
        //alert('invalid extension!');
        $("#message").html("<font color='red'>Invalid extensions! You can only upload files with .gdf, .csv and .net-pajek formats</font>");
        $("#btnUpload").prop("disabled",true);
    }

    else {
        $("#btnUpload").prop("disabled",false);
    }

  });

  $("#UploadForm").submit(function(e) {

    if(!checkFileFormat()){

      //alert('invalid extension!');
      $("#message").html(
        "<font color='red'>Invalid extensions! You can only upload files with .gdf, .csv and .net-pajek formats</font>"
      );
      e.preventDefault();

    }

  });

  function loadBrowsedFile(){ //making ready for upload

    nFile = $("#myfile" ).val();   //returns C:/fakepath/filename.extension
    //get only the filename.extension from the path
    nFile = nFile.split('\\').pop().split('/').pop();
    processed = false;
    //$("#d3vis").empty();
    
    alert('getting--');
    alert( nFile)
    $.ajax({
      type: "GET",
      url: "ajaxrequest.jsp?network_file_uploaded="+nFile+"&process=false",
      //url: baseurl+"upload-csv",
      contentType: "application/json; charset=utf-8",
      dataType: "json",
      /*data: {
    	  networkName: nFile,
    	  process:"False",
    	  ownerName:"seun"
		},*/
      success: function(response){
        graph = response;
        //$("#fsa_strategy").prop("disabled",false);
      },
      error: function(response){
    	  alert('uploading error')
    	  //console.log('uploading error', response)
        $("#divVisualFeatures").hide();
        $("#divGraphInfo").html(response.responseText);

      }

     });
    
   
    
    
    $.when(req).done(function(){
    	alert('uploading 3')
      applyd3dotjs(graph, processed);

      $('#divVisualFeatures').show();
      $("#btnFSA2").prop("disabled",false);
      $("#btnClusteringCoefficient2").prop("disabled",false);
      $("#btnDegreeCentrality2").prop("disabled",false);
      $("#btnPageRank2").prop("disabled",false);
      $("#btnBetweenness2").prop("disabled",false);
      $("#btnModularity2").prop("disabled",false);
      $("#btnSimilarity2").prop("disabled",false);
      $("#btnDijkstra2").prop("disabled",false);

       var graphInfoHtml = getGraphInfo(graph,processed);
       $("#divGraphInfo").html(graphInfoHtml);

    });

  } // function loadBrowsedFile()


  //some useful definition of functions
  if (typeof String.prototype.startsWith != 'function') {
    // see below for better implementation!
    String.prototype.startsWith = function (str){
      return this.indexOf(str) == 0;
    };
  }

}); // document ready
