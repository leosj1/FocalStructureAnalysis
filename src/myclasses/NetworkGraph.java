/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package myclasses;

import java.util.List;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;
//import org.json.simple.JSONObject;
//import org.json.simple.JSONArray;

import org.json.JSONObject;
import org.json.JSONArray;

/**
 *
 * @author Fatih Şen
 */
public class NetworkGraph{
        private NetworkGraph nGraph = null;
        private NetworkNode sourceNode = null;
        private NetworkNode targetNode = null;
        private List<NetworkNode> nNodes = new ArrayList<NetworkNode>();
        private List<NetworkEdge> nEdges = new ArrayList<NetworkEdge>();
        private int nodeCount = 0;
        private int edgeCount = 0;
        private NetworkEdge nEdge = null;
        private NetworkNode nNode = null;
        private List<String> strNodeIds;
        private List<String> strNodeLabels;
        Hashtable<String,String> nodeLabelTable;
        private List<String> strEdges;
        private Hashtable<String, ArrayList<String>> neighborsTable = new Hashtable<String,ArrayList<String>>();
        private ArrayList<String> neighborsList = new ArrayList<String>();
        private int viewId = -1;//primary key
        private String level = "";

        private int iterationId = -1;
        private int graphId = -1;
        private boolean isLeafNode = false;
        private String nodeIds = "";
        private String nodeLabels = "";
        private String edges = "";
        private String edgeSeparator = "";
        private double avgCoeff = 0.0;
        private int superGraphNodeCount = 0;
        private int superGraphEdgeCount = 0;
        private double density = 0.0;
        private double maxEdgeWeight = 0.0;
        private String responseMessage = "";

        private boolean isMerged = false;

        public String errorMessage = "";

        public NetworkGraph(){

        }

        //public NetworkGraph(String eSeparator){
          //  this.edgeSeparator = eSeparator;
        //}


        public NetworkGraph(List<String> strNodeIds, List<String> strEdges , String edgeSeparator){
            this.strNodeIds = strNodeIds;
            this.strEdges = strEdges;
            this.setEdgeSeperator(edgeSeparator);
            setupGraphFromText();
        }

        public NetworkGraph(Hashtable<String,String> nodeLabelTable, List<String> strEdges , String edgeSeparator){
            List<String> listNodeIds = new ArrayList<String>();
            for (String nodeId : nodeLabelTable.keySet()) {
                listNodeIds.add(nodeId);
            }
            this.strNodeIds = listNodeIds;

            this.strEdges = strEdges;
            this.nodeLabelTable = nodeLabelTable;
            this.setEdgeSeperator(edgeSeparator);
            setupGraphFromText();
        }

        public NetworkGraph(List<String> strNodeIds, List<String> strNodeLabels, List<String> strEdges){
            this.strNodeIds = strNodeIds;
            this.strNodeLabels = strNodeLabels;
            this.strEdges = strEdges;
            setupGraphFromTextWithLabels();
        }

        public NetworkGraph(NetworkGraph hGraph){
            this.nGraph = hGraph;

            setupGraphFromGraph();
        }

        private void setupGraphFromGraph()
        {
            for(NetworkNode n:this.nGraph.getNodes())
            {
                nNode = new NetworkNode(n);
                nNodes.add(nNode);
            }

            for(NetworkEdge e:this.nGraph.getEdges())
            {
                sourceNode = e.getSourceNode();
                targetNode = e.getTargetNode();
                NetworkEdge hEdge = new NetworkEdge(this,sourceNode, targetNode);
                nEdges.add(hEdge);
            }

            this.nodeCount = this.nNodes.size();//this.hGraph.getNodeCount();
            this.edgeCount = this.nEdges.size();//this.hGraph.getEdgeCount();
        }

        private void setupGraphFromText(){

            Set<String> keys = this.nodeLabelTable.keySet();
            for(String n:keys){ //String n:this.strNodeIds
                nNode = new NetworkNode(n,nodeLabelTable.get(n));//n,n-->previous code
                nNodes.add(nNode);

                neighborsTable.put(nNode.getNodeId(), new ArrayList<String>());
            }

            String[] EdgeArr;

            try{
            for(String e:this.strEdges){
               EdgeArr = e.split(this.edgeSeparator);

               if(EdgeArr[0] == "" || EdgeArr[0] == null || EdgeArr[1] == "" || EdgeArr[1] == null)
                   throw new Exception("The format of the input is not correct. Please fix it and try again!");

               //Check out the edge weight
               double edgeWeight;
               if(EdgeArr.length < 3)
                   edgeWeight = 1.0;
               else if(EdgeArr[2] == "" || EdgeArr[2] == null){
                   edgeWeight = 1.0;
               }
               else{
                   try{
                        edgeWeight = Double.parseDouble(EdgeArr[2]);
                   }
                   catch(Exception ex){
                        edgeWeight = 1.0;
                   }
               }

               nEdge = new NetworkEdge(this,EdgeArr[0],EdgeArr[1], edgeWeight);
               nEdges.add(nEdge);

               //keep the neighbors of the nodes
               if(neighborsTable.containsKey(EdgeArr[0]))
                    neighborsTable.get(EdgeArr[0]).add(EdgeArr[1]);

               if(neighborsTable.containsKey(EdgeArr[1]))
                    neighborsTable.get(EdgeArr[1]).add(EdgeArr[0]);
            }

            }
            catch(Exception ex){

            }

            this.nodeCount = this.nNodes.size();
            this.edgeCount = this.nEdges.size();

            setMaxEdgeWeight();
        }

        private void setupGraphFromTextWithLabels(){

            for(int i=0;i<strNodeIds.size();i++){
                nNode = new NetworkNode(strNodeIds.get(i),strNodeLabels.get(i));
                nNodes.add(nNode);
            }

            String[] EdgeArr;
            for(String e:this.strEdges){
               EdgeArr = e.split(this.edgeSeparator);
               nEdge = new NetworkEdge(this,EdgeArr[0],EdgeArr[1]);
               nEdges.add(nEdge);

               //keep the neighbors of the nodes
               neighborsTable.get(EdgeArr[0]).add(EdgeArr[1]);
               neighborsTable.get(EdgeArr[1]).add(EdgeArr[0]);
            }

            this.nodeCount = this.nNodes.size();
            this.edgeCount = this.nEdges.size();
        }

        public void addNode(NetworkNode n){
            nNode = new NetworkNode();
            nNode.copyNode(n);
            nNodes.add(nNode);
        }

        public void addEdge(NetworkEdge e){
            nEdge = new NetworkEdge();
            nEdge.copyEdge(e);
            if(!nEdges.contains(nEdge))
                nEdges.add(nEdge);
        }

        public void setNodeCount(int count){
            this.nodeCount = count;
        }
        public int getNodeCount(){
            //return this.nodeCount;
            //return this.nNodes.size();
            return this.nodeLabelTable.size();
        }

        public void setEdgeCount(int count){
            this.edgeCount = count;
        }
        public int getEdgeCount(){
            //return this.edgeCount;
            return this.nEdges.size();
        }

        public void setNetworkGraph(NetworkGraph nGraph){
            this.nGraph = nGraph;
        }

        public NetworkGraph getNetworkGraph(){
            return this.nGraph;
        }

        public void setEdgeSeperator(String eSeparator){
            this.edgeSeparator = eSeparator;
        }
        public String getEdgeSeperator(){
            return this.edgeSeparator;
        }

        public void setViewId(int vId){
            this.viewId = vId;
        }
        public int getViewId(){
            return this.viewId;
        }

        public void setIterationId(int itId){
            this.iterationId = itId;
        }
        public int getIterationId(){
            return this.iterationId;
        }

        public void setGraphId(int gId){
            this.graphId = gId;
        }
        public int getGraphId(){
            return this.graphId;
        }

        public void setIsLeafNode(boolean isLeafN){
            this.isLeafNode = isLeafN;
        }
        public boolean getIsLeafNode(){
            return this.isLeafNode;
        }

        public void setAvgCoeff(double avgC){
            this.avgCoeff = avgC;
        }
        public double getAvgCoeff(){
            return this.avgCoeff;
        }

        public void setSuperGraphNodeCount(int gNodeCount){
            this.superGraphNodeCount = gNodeCount;
        }
        public int getSuperGraphNodeCount(){
            return this.superGraphNodeCount;
        }

        public void setSuperGraphEdgeCount(int gEdgeCount){
            this.superGraphEdgeCount = gEdgeCount;
        }
        public int getSuperGraphEdgeCount(){
            return this.superGraphEdgeCount;
        }

        public void setDensity(double d){
            this.density = d;
        }
        public double getDensity(){
            return this.density;
        }

        public void setLevel(String l){
            this.level = l;
        }
        public String getLevel(){
            return this.level;
        }

        public void setIsMerged(boolean m){
            this.isMerged = m;
        }
        public boolean getIsMerged(){
            return this.isMerged;
        }

        public List<NetworkNode> getNodes(){
            return nNodes;
        }

        public List<NetworkEdge> getEdges(){
            return nEdges;
        }

        public void setNodeIds(String nIds){
            this.nodeIds = nIds;
        }
        public String getNodeIds(){
            return this.nodeIds;
        }

        public List<String> getStrNodeIds(){
            return this.strNodeIds;
        }

        public void setNodeLabels(String nLabels){
            this.nodeLabels = nLabels;
        }
        public String getNodeLabels(){
            return this.nodeLabels;
        }

        public void setGraphEdges(String gEdges){
            this.edges = gEdges;
        }
        public String getGraphEdges(){
            return this.edges;
        }

        public boolean isEdge(NetworkNode node1, NetworkNode node2){
            if(this.nGraph.neighborsTable.get(node1.getNodeId()).contains(node2.getNodeId()) ||
                    this.nGraph.neighborsTable.get(node2.getNodeId()).contains(node1.getNodeId()))
                return true;
            else
                return false;
        }

        public boolean isEdge(String node1Id, String node2Id){
            boolean isEdge = false;
            try{
            if(this.neighborsTable.get(node1Id).contains(node2Id) ||
                    this.neighborsTable.get(node2Id).contains(node1Id))
                isEdge = true;
            else
                isEdge = false;
            }
            catch(Exception ex){
                System.out.println("Error in isEdge function: " + ex.toString());
            }

            return isEdge;
        }

        public double getEdgeWeight(String node1Id, String node2Id){
            double eWeight = 0.0;
            for(NetworkEdge e:this.getEdges())
            {
                if((e.getSourceNode().getNodeId().equals(node1Id) && e.getTargetNode().getNodeId().equals(node2Id)) ||
                        (e.getSourceNode().getNodeId().equals(node2Id) && e.getTargetNode().getNodeId().equals(node1Id))){

                    eWeight = e.getEdgeWeight();
                    return eWeight;
                }
            }

            return eWeight;
        }

        public void setMaxEdgeWeight(){
            for(NetworkEdge e:this.getEdges())
            {
                if(this.maxEdgeWeight < e.getEdgeWeight())
                    this.maxEdgeWeight = e.getEdgeWeight();
            }
        }

        public double getMaxEdgeWeight(){
            return this.maxEdgeWeight;
        }

        //Node Ids seperated with commas
        public String getNodeIdsAsString(){
            String ids = "";
            int N = this.getNodeCount();
            for(int i=0;i<N;i++){
                if(i==(N-1))
                    ids = ids + getNodes().get(i).getNodeId();
                else
                    ids = ids + getNodes().get(i).getNodeId() + ",";
            }

            return ids;
        }

        //Node Labels separated with commas
        public String getNodeLabelsAsString(){
            String labels = "";
            int N = this.getNodeCount();
            for(int i=0;i<N;i++){
                if(i==(N-1))
                    labels = labels + this.getNodes().get(i).getNodeLabel();
                else
                    labels = labels + this.getNodes().get(i).getNodeLabel() + ",";
            }

            return labels;
        }

        //Edges seperated with commas
        public String getEdgesAsString(){
            String edges = "";
            int E = this.getEdgeCount();
            for(int i=0;i<E;i++){
                if(i==(E-1))
                    edges = edges + this.getEdges().get(i).getSourceNode().getNodeId() + this.edgeSeparator + this.getEdges().get(i).getTargetNode().getNodeId();
                else
                    edges = edges + this.getEdges().get(i).getSourceNode().getNodeId() + this.edgeSeparator + this.getEdges().get(i).getTargetNode().getNodeId() + ",";
            }

            return edges;
        }

        public void setResponseMessage(String msg){
            this.responseMessage = msg;
        }
        public String getResponseMessage(){
            return this.responseMessage;
        }

        public ArrayList<String> GetNeighbors(NetworkNode node){
            return neighborsTable.get(node.getNodeId());
        }

        public String ToJSONString(){

             Hashtable<String,Integer> nodesTable = new Hashtable<String,Integer>();
             String jsonStr = "{";

                 jsonStr += "'nodes':[";
                 for(int i=0; i < this.nGraph.getNodeCount(); i++){
                     jsonStr += "{";
                     jsonStr += "'name':'" + this.nGraph.getNodes().get(i).getNodeLabel() + "', 'group':1" ;
                     nodesTable.put(this.nGraph.getNodes().get(i).getNodeId(), i );
                     jsonStr += "}";
                     if(i != this.nGraph.getNodeCount()-1)
                         jsonStr += ",";
                 }
                 jsonStr += "],";

                 jsonStr += "'links':[";
                 for(int i=0; i < this.nGraph.getEdgeCount(); i++){
                     Integer sourceNodeId = nodesTable.get(this.nGraph.getEdges().get(i).getSourceNode().getNodeId());
                     Integer targetSourceId = nodesTable.get(this.nGraph.getEdges().get(i).getTargetNode().getNodeId());
                     jsonStr += "{";
                     jsonStr += "'source':" + sourceNodeId  + ",'target':" + targetSourceId + ",'value':1";
                     jsonStr += "}";
                     if(i != this.nGraph.getEdgeCount()-1)
                         jsonStr += ",";
                 }
                 jsonStr += "]";

             jsonStr += "}";

             return jsonStr;
        }

        public String ToJSONString(Hashtable<String,Integer> fStructures){

             Hashtable<String,Integer> nodesTable = new Hashtable<String,Integer>();
             String jsonStr = "{";

                 jsonStr += "'nodes':[";
                 for(int i=0; i < this.nGraph.getNodeCount(); i++){
                     String nodeId = this.nGraph.getNodes().get(i).getNodeId();
                     jsonStr += "{";
                     jsonStr += "'name':'" + this.nGraph.getNodes().get(i).getNodeLabel() + "', 'group':" + fStructures.get(nodeId);
                     nodesTable.put(nodeId, i );
                     jsonStr += "}";
                     if(i != this.nGraph.getNodeCount()-1)
                         jsonStr += ",";
                 }
                 jsonStr += "],";

                 jsonStr += "'links':[";
                 for(int i=0; i < this.nGraph.getEdgeCount(); i++){
                     Integer sourceNodeId = nodesTable.get(this.nGraph.getEdges().get(i).getSourceNode().getNodeId());
                     Integer targetSourceId = nodesTable.get(this.nGraph.getEdges().get(i).getTargetNode().getNodeId());
                     jsonStr += "{";
                     jsonStr += "'source':" + sourceNodeId  + ",'target':" + targetSourceId + ",'value':1";
                     jsonStr += "}";
                     if(i != this.nGraph.getEdgeCount()-1)
                         jsonStr += ",";
                 }
                 jsonStr += "]";

             jsonStr += "}";

             System.out.println(jsonStr);

             return jsonStr;
        }

        // FIXME Duplicate code from ToJSONObject() signature
        public JSONObject ToJSONObject(Hashtable<String,Integer> fStructures) throws Exception{
            Hashtable<String,Integer> nodesTable = new Hashtable<String,Integer>();
            JSONObject jsonObject = new JSONObject();
            double edgeWeight = 1.0;
            String msg= this.nGraph.getResponseMessage(); // ASP

            JSONArray nodesArray = new JSONArray();
            for(int i=0; i < this.nGraph.getNodeCount(); i++){
                JSONObject nodesArrayJsonObject = new JSONObject();
                String nodeId = this.nGraph.getNodes().get(i).getNodeId();
                nodesArrayJsonObject.put("name", this.nGraph.getNodes().get(i).getNodeLabel());
                nodesArrayJsonObject.put("group", fStructures.get(nodeId));
                nodesTable.put(nodeId, i );
                nodesArray.put(nodesArrayJsonObject);
            }
            jsonObject.put("nodes", nodesArray);

            JSONArray linksArray = new JSONArray();
            for(int i=0; i < this.nGraph.getEdgeCount(); i++){
                Integer sourceNodeId = nodesTable.get(this.nGraph.getEdges().get(i).getSourceNode().getNodeId());
                Integer targetSourceId = nodesTable.get(this.nGraph.getEdges().get(i).getTargetNode().getNodeId());

                //get the edge weight
                edgeWeight = this.nGraph.getEdges().get(i).getEdgeWeight();

                JSONObject linksArrayJsonObject = new JSONObject();
                linksArrayJsonObject.put("source", sourceNodeId);
                linksArrayJsonObject.put("target", targetSourceId);
                linksArrayJsonObject.put("value", edgeWeight);
                linksArray.put(linksArrayJsonObject);
            }
            jsonObject.put("links", linksArray);
            
            // ASP ------
            JSONArray graphInfoArray = new JSONArray();
            JSONObject graphInfoJSONObject = new JSONObject();
            graphInfoJSONObject.put("numNodes", this.nGraph.getNodeCount());
            graphInfoJSONObject.put("numEdges", this.nGraph.getEdgeCount());
            graphInfoJSONObject.put("responseMessage", msg);
            graphInfoArray.put(graphInfoJSONObject);
            jsonObject.put("graphInfo",graphInfoArray);
            // ASP ------

            return jsonObject;
        }

        public JSONObject ToJSONObject() throws Exception{//Use this when only loading a graph
            Hashtable<String,Integer> nodesTable = new Hashtable<String,Integer>();
            JSONObject jsonObject = new JSONObject();//main json object
            double edgeWeight = 1.0;
            String msg= this.nGraph.getResponseMessage();

            JSONArray graphInfoArray = new JSONArray();
            JSONObject graphInfoJSONObject = new JSONObject();
            graphInfoJSONObject.put("numNodes", this.nGraph.getNodeCount());
            graphInfoJSONObject.put("numEdges", this.nGraph.getEdgeCount());
            graphInfoJSONObject.put("responseMessage", msg);
            graphInfoArray.put(graphInfoJSONObject);
            jsonObject.put("graphInfo",graphInfoArray);

            if(msg.equals("")){
                JSONArray nodesArray = new JSONArray();
                for(int i=0; i < this.nGraph.getNodeCount(); i++){
                    JSONObject nodesArrayJsonObject = new JSONObject();
                    String nodeId = this.nGraph.getNodes().get(i).getNodeId();
                    nodesArrayJsonObject.put("name", this.nGraph.getNodes().get(i).getNodeLabel());
                    nodesArrayJsonObject.put("group", 0);
                    nodesTable.put(nodeId, i );
                    nodesArray.put(nodesArrayJsonObject);
                }
                jsonObject.put("nodes", nodesArray);

                JSONArray linksArray = new JSONArray();
                for(int i=0; i < this.nGraph.getEdgeCount(); i++){
                    Integer sourceNodeId = nodesTable.get(this.nGraph.getEdges().get(i).getSourceNode().getNodeId());
                    Integer targetSourceId = nodesTable.get(this.nGraph.getEdges().get(i).getTargetNode().getNodeId());

                    //get the edge weight
                    edgeWeight = this.nGraph.getEdges().get(i).getEdgeWeight();

                    JSONObject linksArrayJsonObject = new JSONObject();
                    linksArrayJsonObject.put("source", sourceNodeId);
                    linksArrayJsonObject.put("target", targetSourceId);
                    linksArrayJsonObject.put("value", edgeWeight);
                    linksArray.put(linksArrayJsonObject);
                }
                jsonObject.put("links", linksArray);
            }

            return jsonObject;
        }

}
