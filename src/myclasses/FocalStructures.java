/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package myclasses;

import java.util.Hashtable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Collections;
import java.util.Comparator;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

/**
 *
 * @author Fatih Şen
 */
public class FocalStructures {

        Hashtable<String, Double> verticeClustCoeffTable = new Hashtable();
        Hashtable<String, Integer> degreeTable;
        private NetworkGraph nGraph;
        Hashtable<Integer, ArrayList<String>> focalStructureTable  = new Hashtable();
        FocalStructure[] focalStructures;
        int N;
        double totalClustCoeff = 0.00;
        int numCoeff = 0;
        Double outDegreeAvg = 0.0;
        public final AtomicInteger viewId = new AtomicInteger(1);
        public Hashtable<String,Integer> fStructuresIndexTable = new Hashtable<String,Integer>();
        public String FSAOutputFileName = "";


        public FocalStructures(){

            verticeClustCoeffTable = new Hashtable<String, Double>();
            fStructuresIndexTable = new Hashtable<String,Integer>();
        }

        public FocalStructures(NetworkGraph nGraph){

            verticeClustCoeffTable = new Hashtable<String, Double>();
            this.nGraph = nGraph;
            N = this.nGraph.getNodeCount();
            focalStructures = new FocalStructure[N];
            fStructuresIndexTable = new Hashtable<String,Integer>();
        }



        public String getOutput(){

		return "this is the focal structure output!";
	}

	public String getJSONOutput()
	{
			  String jsonStr = "{" +
				  		"'nodes':[" +
				  		"  {'name':'Myriel','group':1}," +
				  		"  {'name':'Napoleon','group':1}," +
				  		"  {'name':'Mlle.Baptistine','group':2} " +
				  	    "  ]," +
				  	    "  'links':[" +
				  	    "  {'source':1,'target':0,'value':1}," +
				  	    "  {'source':2,'target':0,'value':8}" +
				  	    "  ]" +
				  	    "  }";
			  return jsonStr;

	  }

        public void getFocalStructures()
        {
            getOutDegree();

            boolean weightedGraph = false;
            computeClusteringCoefficient(weightedGraph);

            obtainFocalStructures();

            displayFocalStructures(focalStructures,FSAOutputFileName);
        }

        public void getFocalStructuresForWeightedGraph(){
            getOutDegree();

            boolean weightedGraph = true;
            computeClusteringCoefficient(weightedGraph);

            obtainFocalStructures();

            displayFocalStructures(focalStructures,FSAOutputFileName);
        }

        public void obtainFocalStructures(){
            ArrayList<String> processedVertices = new ArrayList<String>();
            ArrayList<String> verticeList = new ArrayList<String>();
            ArrayList ccListforStatistics = new ArrayList(verticeClustCoeffTable.entrySet());

            // Put keys and values in to an arraylist using entryset
            ArrayList degreeList = new ArrayList(degreeTable.entrySet());

            // Sort the values based on values first and then keys.
            Collections.sort(degreeList, new MyComparator()); //comment this if you don't want to include sorting according to degree centrality

            // Show sorted results
            Iterator itr = degreeList.iterator();
            String key = "";
            while (itr.hasNext()) {
                Map.Entry e = (Map.Entry) itr.next();
                key = (String) e.getKey();
                verticeList.add(key);
            }


            itr = ccListforStatistics.iterator();
            Double valueClustCoeff = 0.0;
            ArrayList<Double> clustCoeffList = new ArrayList<Double>();
            while (itr.hasNext()) {
                Map.Entry e = (Map.Entry) itr.next();
                valueClustCoeff = (Double) e.getValue();
                clustCoeffList.add(valueClustCoeff);
            }
            Double[] clustCoeffListArr = new Double[clustCoeffList.size()];
            clustCoeffList.toArray(clustCoeffListArr);
            Statistics st = new Statistics(clustCoeffListArr);
            Double mean = st.getMean();



            //It is time to obtain the focal structures
            String key2 = "";
            int index = 0;
            for(int i=0; i < verticeList.size(); i++){

                ArrayList<Double> ccList = new ArrayList<Double>();

                key = verticeList.get(i);
                double clustCoeff1 = verticeClustCoeffTable.get(key);
                ccList.add(clustCoeff1);

                if(clustCoeff1 > 0.0 ){
                    if(!processedVertices.contains(key))
                    {
                        focalStructures[i] = new FocalStructure();
                        focalStructures[i].add(key);
                        focalStructures[i].setId(this.viewId+"");
                        this.viewId.getAndIncrement();
                        processedVertices.add(key);
                        index = i;
                    }
                    else{
                        //find the index of the vertice in the structure, i.e. find which structure id that vertex belongs to
                        index = findStructureIdOfTheVertice(key);
                    }
                }

                for(int j = i+1; j< verticeList.size(); j++){
                    key2 = verticeList.get(j);

                    boolean isEdge = this.nGraph.isEdge(key, key2);

                    if(isEdge)//if node and node2 are neighbors
                    {
                        if(!processedVertices.contains(key2)) //if the node2 is not in a focal structure
                        {
                            double clustCoeff2 = verticeClustCoeffTable.get(key2);
                            ccList.add(clustCoeff2);
                            Double[] clustCoeffListArr2 = new Double[ccList.size()];
                            ccList.toArray(clustCoeffListArr2);

                            if((clustCoeff1 < mean && clustCoeff2 < mean && clustCoeff2 > 0.0) || (clustCoeff1 > mean && clustCoeff2 > mean && clustCoeff2 > 0.0)){
                                //if(focalStructures[index] == null)//uncomment this if you don't want to include sorting according to degree centrality
                                    //focalStructures[index] = new FocalStructure();////uncomment this if you don't want to include sorting according to degree centrality
                                focalStructures[index].add(key2);//add the neighbornode to the structure in which the vertex is involved! key2 and key nodes are connected with each other!
                                processedVertices.add(key2);
                            }
                            else
                                ccList.remove(ccList.size()-1);
                        }
                    }
                }

            }


        }

        public void displayFocalStructures(FocalStructure[] cst, String fileName){

            //First put every node into same group
            for(String nodeId : this.nGraph.getStrNodeIds()){
                System.out.println("Node Id:" + "\t" + nodeId + "\t" + "Group Id:" + "\t" + 0);
                fStructuresIndexTable.put(nodeId, 0);
            }
            // NOTE Writing to this output file is hardcoded into the FSA algorithm here. Needs abstraction.
            // File file = null;
            // FileWriter fw = null;
            // BufferedWriter bw = null;
            // file = new File(fileName);

            // try {
            //     // if file doesnt exists, then create it
            //     if (!file.exists()) {
            //         file.createNewFile();
            //      }

            // FileOutputStream fos = new FileOutputStream(file);
            //
            // bw = new BufferedWriter(new OutputStreamWriter(fos));

                //Now overwrite the groups of the nodes in focal structures
                int focalStructureCounter = 5;
                for(int i=0; i<cst.length; i++){
                    if(cst[i]!=null)
                        if(cst[i].nodes.size() > 1){

                                for(String nodeId : cst[i].nodes){
                                    //System.out.println("Node Id: " + nodeId + ", Group Id: " + focalStructureCounter);
                                    fStructuresIndexTable.put(nodeId, focalStructureCounter);

                                    String content = "Node Id:" + "\t" + nodeId + "\t" +"Group Id:" + "\t" + focalStructureCounter;
                                    System.out.println(content);
                                    //bw.write(content);
                                    //bw.newLine();
                                }

                                focalStructureCounter += 5;
                        }
                }

            // bw.close();
            //} // closes try
            // catch(IOException io){
            //     System.out.println(io.toString());
            // }
    }


        public void getOutDegree(){
            int outDegree = 0;
            int totalOutDegree = 0;
            degreeTable = new Hashtable();

            for ( int j=0; j<this.nGraph.getNodes().toArray().length;j++) {
                NetworkNode n = this.nGraph.getNodes().get(j);
                outDegree = this.nGraph.GetNeighbors(n).size();
                totalOutDegree += outDegree;
                degreeTable.put(n.getNodeId(), outDegree);

                //degreeList.add(n.getId() + "," + hGraph.getDegree(n));
                System.out.println("OutDegree of Node " + n.getNodeId() + " is " + outDegree);
            }

            outDegreeAvg = (double)totalOutDegree/degreeTable.size();

            System.out.println("OutDegree: " + outDegreeAvg);

        }

        public void computeClusteringCoefficient(boolean wGraph){

            verticeClustCoeffTable = new Hashtable<String, Double>();

            NetworkNode node = null;
            double clustCoeff;
            double totalClustCoeff = 0.00;
            int numCoeff = 0;

            for ( int j=0; j<this.nGraph.getNodes().toArray().length;j++) {
                node = this.nGraph.getNodes().get(j);

                if(!wGraph)//for unweighted Graphs
                    clustCoeff = clusteringCoefficient(node);
                else
                    clustCoeff = clusteringCoefficientWithEdge(node);

                if(clustCoeff > 0){
                    numCoeff++;
                    totalClustCoeff += clustCoeff;
                }

                System.out.println("Vertice: " + node.getNodeId() + ", Clustering Coefficient: " + clustCoeff);

                verticeClustCoeffTable.put(node.getNodeId() , clustCoeff);
            }

            System.out.println("Average Coefficient Value: " + totalClustCoeff/numCoeff);

        }

        // Computes the clustering coefficient of a vertex. This is the
        // version that takes a vertex index as argument. It looks how
        // the neighbors of a node are connected with each other!
        public double clusteringCoefficient(NetworkNode n)
        {
                // initialize edges-in-neighborhood to 0
                int edgesInNbd = 0;

                ArrayList<String> neighborNodes = this.nGraph.GetNeighbors(n);
                int neighborLength = neighborNodes.size();

                // Scan pairs of neighbors and increment counter whenever
                // there is an edge
                for ( int j = 0; j < neighborLength; j++) {
                    String neighborNode1Id = neighborNodes.get(j);

                    for ( int i=0; i< j;i++) {
                        //Node neighborNode2 = hgraph.getNeighbors(n).toArray()[i];
                        String neighborNode2Id = neighborNodes.get(i);

                        if(this.nGraph.isEdge(neighborNode1Id, neighborNode2Id))
                               edgesInNbd++;
                    }
                }

                // if there are no neighbors or one neighbor then, clustering
                // coefficient is trivially defined to  be 1. Otherwise,
                // compute the ratio of number of edges in neighborhood to
                // the total number of pairs of neighbors
                if(neighborLength == 1)
                    return 0;
                else if(neighborLength == 0)
                    return 0;
                else
                    return (double)edgesInNbd/choose(neighborLength,2);

        }

        // Computes the clustering coefficient of a vertex. This is the
        // version that takes a vertex index as argument. It looks how well
        // the neighbors of a node are connected with each other!
        public double clusteringCoefficientWithEdge(NetworkNode n)
        {
            // initialize edges-in-neighborhood to 0
            double edgesInNbd = 0.0;
            double edgeWeight = 0.0;

            ArrayList<String> neighborNodes = this.nGraph.GetNeighbors(n);
            int neighborLength = neighborNodes.size();

            // Scan pairs of neighbors and increment counter whenever
            // there is an edge
            for ( int j = 0; j < neighborLength; j++) {
                String neighborNode1Id = neighborNodes.get(j);

                for ( int i=0; i< j;i++) {
                    //Node neighborNode2 = hgraph.getNeighbors(n).toArray()[i];
                    String neighborNode2Id = neighborNodes.get(i);

                    if(this.nGraph.isEdge(neighborNode1Id, neighborNode2Id)){
                           //Get the edge weight and normalize
                           edgeWeight = this.nGraph.getEdgeWeight(neighborNode1Id, neighborNode2Id)/this.nGraph.getMaxEdgeWeight();
                           edgesInNbd = edgesInNbd + edgeWeight;
                    }
                }
            }

            // if there are no neighbors or one neighbor then, clustering
            // coefficient is trivially defined to  be 1. Otherwise,
            // compute the ratio of number of edges in neighborhood to
            // the total number of pairs of neighbors
            if(neighborLength == 1)
                return 0;
            else if(neighborLength == 0)
                return 0;
            else
                return (double)edgesInNbd/choose(neighborLength,2);

        }

        public int findStructureIdOfTheVertice(String verticeId){
            int index = -1;
            for(int i=0; i<focalStructures.length; i++){
                if(focalStructures[i] != null)
                    if(focalStructures[i].nodes != null && focalStructures[i].nodes.contains(verticeId))
                        return i;
            }

            return -1;
        }

        //choose(n,k) = n! / (n-k)! k!
        //http://stackoverflow.com/questions/1678690/what-is-a-good-way-to-implement-choose-notation-in-java
        public static double choose(int x, int y)
        {
            if (y < 0 || y > x) return 0;
            if (y > x / 2)
            {
                // choose(n,k) == choose(n,n-k),
                // so this could save a little effort
                y = x - y;
            }

            double denominator = 1.0, numerator = 1.0;
            for (int i = 1; i <= y; i++)
            {
                denominator *= i;
                numerator *= (x + 1 - i);
            }
            return numerator / denominator;
        }

        //Sources
    //http://kbvstuff.blogspot.com/2010/06/sort-hashtable-by-value.html
    //http://mrtextminer.wordpress.com/2007/09/14/java-hashtable-sorted-by-values/
    static class MyComparator implements Comparator {
        public int compare(Object obj1, Object obj2) {
            int result = 0;
            Map.Entry e1 = (Map.Entry) obj1;
            Map.Entry e2 = (Map.Entry) obj2;// Sort based on values.

            Integer value1 = (Integer) e1.getValue();
            Integer value2 = (Integer) e2.getValue();

            if (value1.compareTo(value2) == 0) {
            String word1 = (String) e1.getKey();
            String word2 = (String) e2.getKey();

            result = word1.compareTo(word2);
            } else {
            // Sort values in a ascending order
            //result = value1.compareTo(value2);
            // Sort values in a descending order
            result= value2 .compareTo(value1);
            }
            return result;
        }

    }


    class FocalStructure{

        LinkedList<String> nodes;
        FocalStructures structures;
        LinkedList<String> edges;
        private String Id;

        public FocalStructure(){
            nodes = new LinkedList<String>();
            edges = new LinkedList<String>();
        }

        public FocalStructure(FocalStructures structures){
            nodes = new LinkedList<String>();
            edges = new LinkedList<String>();
            this.structures = structures;
        }

        public int size() {
            return nodes.size();
        }

        public boolean add(String node) {
            nodes.addLast(new String(node));
            return true;
        }

        public boolean add(String node,String node2) {
            nodes.addLast(new String(node));

            String edge = node + ";" + node2;
            edges.addLast(new String(edge));

            return true;
        }

        public boolean remove(String node) {
            boolean result = nodes.remove(new Integer(node));

            return result;
        }

        public String getNodes(){
            String nodeIds = "";
            for(int i=0; i<nodes.size(); i++){
                if(i==0)
                    nodeIds = nodes.get(i);
                else
                    nodeIds = nodeIds + "," + nodes.get(i);
            }

            return nodeIds;
        }

        public String getEdges(){
            String edgeIds = "";
            for(int i=0; i<edges.size(); i++){
                if(i==0)
                    edgeIds = edges.get(i);
                else
                    edgeIds = edgeIds + "," + edges.get(i);
            }

            return edgeIds;
        }

        public void setId(String vId){
            this.Id = vId;
        }

        public String getId(){
            return this.Id;
        }

    }

    //http://stackoverflow.com/questions/7988486/how-do-you-calculate-the-variance-median-and-standard-deviation-in-c-or-java
    public class Statistics
    {
        Double[] data;
        Double size;

        public Statistics(Double[] data)
        {
            this.data = data;
            size = Double.valueOf(data.length);
        }

        double getMean()
        {
            Double sum = 0.0;
            for(double a : data)
                sum += a;
                return sum/size;
        }

        double getVariance()
        {
            Double mean = getMean();
            Double temp = 0.0;
            for(double a :data)
                temp += (mean-a)*(mean-a);
                return temp/size;
        }

        double getStdDev()
        {
            return Math.sqrt(getVariance());
        }

        public Double median()
        {
               Double[] b = new Double[data.length];
               System.arraycopy(data, 0, b, 0, b.length);
               Arrays.sort(b);

               if (data.length % 2 == 0)
               {
                  return (b[(b.length / 2) - 1] + b[b.length / 2]) / 2.0;
               }
               else
               {
                  return b[b.length / 2];
               }
        }
    }
}
