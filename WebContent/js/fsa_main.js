/**
 * JS page designed to work with alternate Servlet architecture fsa_index.jsp
 * @author Andrew Pyle <axpyle@ualr.edu> MS-Information Science 2018
 */


$(document).ready(function() {
//alert('here');
  /**
   * Page defaults
   */
  $("#loadingBar").hide();

  var visDiv = "div#d3vis";
  var networkJson = null;  // global scope for current network in memory
  var greyHexCode = "#808080"
  // var colorScale = d3.scaleOrdinal(d3.schemeCategory20);
  var colorScale = d3.scaleOrdinal([
    "#94002d",
    "#008fe6",  // Colors from i want hue
    "#3d9400",  // Developed by Mathieu Jacomy
    "#d9b600",  // at the Sciences-Po Medialab
    "#babeff",  // http://tools.medialab.sciences-po.fr/iwanthue/
    "#ff50a5",
    "#ffaad8",
    "#ebffe3",
    "#374500",
    "#4b0051",
    "#3f0600",
    "#b9ffb7",
    "#01e986",
    "#01cedd",
    "#01645d",
    "#002b66",
    "#f62a56",
    "#8246cb",
    "#bc5300",
    "#ffa26e"]);

  // Load Default Network
  loadNetworkAjax("load/fsa/unweighted");

  // Custom error message to throw if FSA groups are changed from
  // increment-b-5 to increment-by-1 server-side
  var FSAGroupError = new Error("Custom FSA Error from Andrew Pyle." +
    " This Error means that the stopgap mutation bandaid to" +
    " transform the FSA Group numbers from an increment-by-5 space" +
    " to an increment-by-1 space yielded a non-Integer number." +
    " This is likely because someone has finally changed the FSA" +
    " algorithm from incrementing the FSA groups by 5 to" +
    " incrementing them by 1 Congratulations! Just remove the call" +
    " to normalizeFSAGroup() on loadNetworkAjax() success.");

  /**
   * Apply Load Network Event Handlers
   */
  
  // TODO Make this the only database query
  // TODO Apply FSA properly (weighted/unweighted) the first time

  $("#networkList").change(function() {
      loadNetworkAjax("load/fsa/unweighted");
  });

  $("#applyFSAUnweighted").click(function() {
      loadNetworkAjax("load/fsa/unweighted");
  });

  $("#applyFSAWeighted").click(function() {
      loadNetworkAjax("load/fsa/weighted");
  });


    /**
     * Function Definitions
     */

    // Mutates Node Object in place
    function normalizeFSAGroup(nodeObj) {
        if (typeof nodeObj.group === 'number') {
            nodeObj.group === 0 ? 0 : nodeObj.group /= 5;

            // Compatibility check
            if (!Number.isInteger(nodeObj.group)) {
                throw FSAGroupError;
            }

        } else {
            throw TypeError('FSA Group from server is not a number.');
        }
    }

    function loadNetworkAjax(url) {

        // Ensure user can't submit a request before the first one returns.
        $(".FSAButtons").prop("disabled", true);

        if ($("#networkList").val() != "placeholder") {

            $("#loadingBar").show();

            var request = $.ajax({
              type: "GET",
              url: url,
              data: {
                "networkId": $("#networkList").val()
              },
              dataType: "json",

              error: function(response) {
                console.log(response);
                alert("Error Loading FSA, Please login");
              },

              success: function(response) {

                // Mutate Node FSA Groups
                // from [0,5,10,...,N] -> [0,1,2,...,N]
                response.nodes.forEach(function(nodeObj) {
                    normalizeFSAGroup(nodeObj);
                });

                // alert("Ajax success");  // Debug
                // $("div#d3vis").empty();
                drawForceDirectedGraph(response, visDiv, colorScale);

                // Info Table
                var tbl = $("table#graphInfo");
                tbl.empty() // Prevent repeating rows

                var graphInfo = response.graphInfo[0];
                var keys = Object.keys(graphInfo);
                // console.log(graphInfo, keys);  // Debug
                $.each(graphInfo, function(key, val) {
                    // Only show responseMessage if has value
                    if (val != "") {
                        tbl.append(
                            $('<tr>').append(
                                $('<th>').text(key),
                                $('<td>').text(val)
                            )
                        )
                    }
                });

                // Network Text Export
                var nodeArr = response.nodes
                var lines = [];
                nodeArr.forEach(function(val, idx) {
                    if (val.group != 0) {
                        lines.push([val.name, val.group]);
                    }
                });

                // In-Place Sort by FSA Group.
                // Lowest Non-Zero Group First.
                lines.sort(function(a, b) {
                    // console.log(a, b);  // Debug
                    return a[1] - b[1];
                })

                var csvContent = lines.join('\n');  // CSV string

                // Hardcoded CSV Header
                var csvHeader = 'Node' + ',' + 'FSA Group' + '\n';
                var csvFileStr = csvHeader + csvContent;
                // console.log(csvFileStr);  // Debug

                // Download Link
                link = $("#CSVExport");
                link.attr(
                    'href', 'data:text/csv); charset=utf-8,' +
                    encodeURIComponent(csvFileStr)
                );
              },

              complete: function() {
                  // Re-enable buttons once request returns
                  $(".FSAButtons").prop("disabled", false);
                  $("#loadingBar").hide();
              }

            });

            $("#loadingBar").hide();
        }
    }


  /**
   * D3 Force-Directed Graph
   * param: networkJson (JSON): nodes & links arrays
   * param: div (DOM selector string): div to draw SVG
   * param: colorScale (Array): D3 color scale to apply
   */
  function drawForceDirectedGraph(networkJson, div, colorScale) {
      
      // Total Data
      var nodeData = networkJson.nodes;
      var linkData = networkJson.links;
      
      // Check Network View Radio Buttons
      if ($('input#networkViewFSAOnly').is(':checked')) { // Render Exploded FSA Groups
          
          // Explode FSA Groups from entire network
          renderExplodedGraph(networkJson, colorScale);
      
      } else { // Render Entire Network
      
          renderGraph(nodeData, linkData, colorScale);
      
      }


    function renderGraph(nodeData, linkData, colorScale) {
        // Clear div
        $(div).empty();
      
//    // Data
//    var nodeData = networkJson.nodes;
//    var linkData = networkJson.links;

    // Set up SVG div
    var width = "100%";
    var height = 900;  // TODO use viewport-based measurements

    var svg = d3.select(div)  // "div#svgHere"
        .attr("class", "svg-container")
        // .attr("style", "background-color: #eee;") // in CSS
      .append("svg")
        .attr("class", "svg-content")
        .attr("width", width)  // TODO Remove hardcode
        .attr("height", height); // TODO Remove hardcode

    // Separate layer for network so nodes and links zoom together
    var gNetworkBase = svg.append("g")
        .attr("class", "networkBase");

    // Draw links first so they're underneath nodes
    //alert('graph')
    
    var links = gNetworkBase.append("g")
        .attr("class", "links")
      .selectAll("line")  // SVG native element: line
        .data(linkData)
      .enter()  // TODO function() for exploded focal structures use .update()
        .append("line")
        .attr("stroke-width", 2);

    // Drag Nodes
    var drag = d3.drag()
        .on("start", function(d) {
            if (!d3.event.active) {
                simulation.alphaTarget(0.9).restart();
            }
            d.fx = d.x;
            d.fy = d.y;
        })
        .on("drag", function(d) {
            d.fx = d3.event.x;
            d.fy = d3.event.y;
        })
        .on("end", function(d) {
            if (!d3.event.active) {
                simulation.alphaTarget(0);
            }
            d.fx = null;
            d.fy = null;
        });

    // Draw nodes on top of links
    console.log("data", nodeData)
    var nodes = gNetworkBase.append("g")
        .attr("class", "nodes")
      .selectAll("circle")  // SVG native element: circle
        .data(nodeData)
      .enter()
        .append("circle")
        .attr("r", 10)
        .attr("fill", applyFSAColor)
        .call(drag);

    var nodeLabels = gNetworkBase.append("g")
        .attr("class", "nodeLabels")
      .selectAll("text")
        .data(nodeData)
      .enter()
        .append("text")
        .attr("dx", -5) // Slight offset to be in center
        .attr("dy", 5)  // FIXME Make this appear better
        .attr("visibility", "hidden")
        .text(function(d) { return d.name })
        // FIXME node labels scaled by zoom?
        .call(drag);

    var weightLabels = gNetworkBase.append("g")
        .attr("class", "weightLabels")
      .selectAll("text")
        .attr("class", "weightLabels")
        .data(linkData)
      .enter()
        .append("text")
        .attr("dx", -5) // Slight offset to be in center
        .attr("dy", 5)  // FIXME Make this appear better
        .attr("visibility", "hidden")
        .text(function(d) { return d.value });

    // d3.forceSimulation() creates simulation entities which will be
    // used to manipulate the position of the DOM entities on each tick
    var simulation = d3.forceSimulation()
        .nodes(nodeData)

        // Forces
        .force("charge", d3.forceManyBody())  // Nodes repels other nodes
        .force("center", d3.forceCenter(  // Creates center coordinates
            d3.select("svg.svg-content").node().getBoundingClientRect().width / 2,   // FIXME proper SVG boundaries
            d3.select("svg.svg-content").node().getBoundingClientRect().height / 2
         ))
        .force("links", d3.forceLink(linkData))  // Links have a length
        .force("collision", d3.forceCollide(nodes.attr("r"))) // Force radius == default node radius
        .force("xGravity", d3.forceX().strength(0.06))  // like gravity but
        .force("yGravity", d3.forceY().strength(0.06))  // toward origin

        // Decay Parameters
        .alphaDecay(0.08)  // default = 0.0228 = 1 - pow(0.001, 1 / 300)
        //.velocityDecay(0.2)  // default = 0.4

        // Tick
        .on("tick", function() {
            // Update SVG entity positions according to simulation
            nodes.attr("cx", function(d) { return d.x; })
                 .attr("cy", function(d) { return d.y; });

            links.attr("x1", function(d) { return d.source.x; })
                 .attr("y1", function(d) { return d.source.y; })
                 .attr("x2", function(d) { return d.target.x; })
                 .attr("y2", function(d) { return d.target.y; });

            nodeLabels.attr("x", function(d) { return d.x })
                      .attr("y", function(d) { return d.y });

            weightLabels
                .attr("x", function(d) {
                    return (d.source.x + d.target.x) / 2; })
                .attr("y", function(d) {
                    return (d.source.y + d.target.y) / 2; });
        });

    // Zoom & Pan entire SVG
    var zoom = d3.zoom().on("zoom", function() {
        // transform entire g element below nodes and links
        gNetworkBase.attr("transform", d3.event.transform)
    });
    zoom(svg); // TODO Apply with D3 .call()

    // Apply Visual Option Settings on Network Load
    applyVisualOptions();  // 0.
    

    /**
     * Visual Options
     */

    // Umbrella function for applying all visual options
    function applyVisualOptions() {
        nodeLabelState();
        weightLabelState();
        fsaColorState();
        linkWidthState();
        nodeRadiusState();
    }
    
    // Show Node Labels
    function nodeLabelState() {
        if ($("#chkCaption").is(":checked")) {
            nodeLabels.style("visibility", "visible");
        } else {
            nodeLabels.style("visibility", "hidden");
        }
    }
    
    // Checkbox event handler
    $("#chkCaption").change(function(event) {
        nodeLabelState();
    });


    // Show Weight labels
    function weightLabelState() {
        if ($("#chkEdgeWeight").is(":checked")) {
            weightLabels.style("visibility", "visible");
        } else {
            weightLabels.style("visibility", "hidden");
        }
    }

    // Checkbox event handler
    $("#chkEdgeWeight").change(function(event) {
        weightLabelState();
    });
    

    // Hide FSA Colors
    function fsaColorState() {
        if (!$("#chkNodeColor").is(":checked")) {
            nodes.attr("fill", applyFSAColor);
        } else {
            nodes.attr("fill", greyHexCode);
        }
    }

    function applyFSAColor(d) {  // D3-style .attr function
        // Grey if not in focal structure
        if (d.group === 0) { return greyHexCode }
        // Color if in focal structure
        else { return colorScale(d.group) }
    }

    // Checkbox event handler
    $("#chkNodeColor").change(function(event) {
        fsaColorState();
    });

    
    // Link Width Proportional to Weight
    function linkWidthScale(domainMax) {
        return d3.scaleLinear().domain([1,domainMax]).range([2,15])
    }

    function linkWidthState() {
        // Calculate Max Link width for scaling
        var maxLinkValue = linkData.reduce(function(acc, cv) {
            return Math.max(acc, cv.value);
        }, 0)
        
        // D3-style .attr function to set link width
        var scaleLinkWidths = linkWidthScale(maxLinkValue);

        if ($("#chkLinkWidth").is(":checked")) {
            links.attr("stroke-width", function(d) {
                return scaleLinkWidths(d.value); 
            });
        } else {
            links.attr("stroke-width", 2);
        }
    }

    // Checkbox event handler
    $("#chkLinkWidth").change(function(event) {
        linkWidthState();
    });
    
    
    // Node Size Proportional to Degree
    function sumIncidentEdges(nodeData, linkData) {
        inc = [];
        nodeData.forEach(function(element, index) {
            inc.push(0)
            linkData.forEach(function(element) {
                if (element.source.index === index ||
                        element.target.index === index) {
                    inc[index] += 1;
                }
            })
        })
        return inc;
    }

    function getMaxIncidentEdges(arr) {
        return arr.reduce(function(a, b) { return Math.max(a,b); })
    }

    function nodeRadiusScale(domainMax) {
        return d3.scaleLinear().domain([1,domainMax]).range([5,20])
    }

    function nodeRadiusState() {

        // Calculate incident edge data for scaling
        var incidentEdgeArray = sumIncidentEdges(nodeData, linkData);
        var scaleNodes = nodeRadiusScale(
                getMaxIncidentEdges(incidentEdgeArray));

        if ($("#chkNodeRadius").is(":checked")) {
            nodes.attr("r", function(d) {
                return scaleNodes(incidentEdgeArray[d.index]);
            });
        } else {
            nodes.attr("r", 10);
        }
    }

    // Checkbox event handler
    $("#chkNodeRadius").change(function() {
        nodeRadiusState();
    })

  }
      
    // Explode FSA Groups from entire network
    function renderExplodedGraph(networkJson, colorScale){

      // Get "Exploded" data
      var fsaNetwork = filterFSAGroups(networkJson);
      
      // Split for renderGraph params
      var fsaNodes = fsaNetwork.nodes;
      var fsaLinks = fsaNetwork.links;
      
      // Draw FSA Groups only
      renderGraph(fsaNodes, fsaLinks, colorScale);
    }
    
    function filterFSAGroups(networkJson) {
        // Deep copy of networkJson to avoid mutation
        var fsaNetworkJson = JSON.parse(JSON.stringify(networkJson));
        
        // Filter FSA Nodes
        var fsaNodes = fsaNetworkJson.nodes.filter(function(val) {
               return val.group != 0;
        });
        
        // List of FSA node names
        fsaNodeNameList = [];
        fsaNodes.forEach(function(val) {
            fsaNodeNameList.push(val.name);
        });
        
        // Mutate JSON: key on names, rather than indices
        fsaNetworkJson.links.forEach(function(val) {
            val.source = fsaNetworkJson.nodes[val.source].name;
            val.target = fsaNetworkJson.nodes[val.target].name;
        });
        
        
        // Remove non-FSA group links
        var fsaLinks = fsaNetworkJson.links.filter(function(val) {
            return isIntraGroupLink(val.source, val.target, fsaNetworkJson.nodes);
        });
        
        // Source & Target are in same group, and group is not 0
        function isIntraGroupLink(sourceNodeName, targetNodeName, nodesArray) {
            sourceGroup = nodesArray.find(function(val) {
                return val.name === sourceNodeName; 
            }).group;
            
            targetGroup = nodesArray.find(function(val) {
                return val.name === targetNodeName;
             }).group;
            
            return sourceGroup === targetGroup && sourceGroup != 0;
        }
        
        // Re-mutate links: key on indices, not names
        fsaLinks.forEach(function(val) {
//            val.source = fsaNodes.indexOf(val.source);
//            val.target = fsaNodes.indexOf(val.target);
            
            val.source = fsaNodes.findIndex(function(obj) {
                return obj.name === val.source;
            });
            
            val.target = fsaNodes.findIndex(function(obj) {
                return obj.name === val.target;
            })
            

        });
        
        
//        // Subset Data
//        var fsaNodes = [
//            {'name': 'echo', 'group':0},
//            {'name': 'PolitiDitz', 'group':0},
//            {'name': 'vinyardsaker', 'group':1}
//        ];
//        
//        var fsaLinks = [
//            {"source":0,"value":7.0,"target":1},
//            {"source":1,"value":7.0,"target":0},
//            {"source":1,"value":7.0,"target":2},
//        ];
        
        return { nodes: fsaNodes,
                 links: fsaLinks };
    }
  }

});
