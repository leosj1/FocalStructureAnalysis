package myclasses;

import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Comparator;
import java.util.Map;

import au.com.bytecode.opencsv.CSVReader;

import java.util.Iterator;
//import com.opencsv.CSVReader;

import java.sql.ResultSet;

/**
 *
 * @author Fatih Şen
 */
public class NetworkLoader {

    public NetworkLoader(){

    }

    public static NetworkGraph LoadFile(String fileName){
        NetworkGraph output = null;
        try
        {
            String extension = "";

            int i = fileName.lastIndexOf('.');
            if (i > 0) {
                extension = fileName.substring(i+1);
            }

            System.out.println(extension);

            //Load the related file according its type
            if(extension.equals("gdf")) //.gdf file
                output = LoadGDFFile(fileName);
            else if(extension.equals("net")) //pajek .net file
            {
                LoadPajekDotNetFile(fileName);
            }
            else if(extension.equals("csv")) //.csv file
            {
                output = LoadCSVFile(fileName);
            }
        }
        catch(Exception e)
        {
            System.out.println(e);
        }

       return output;
     }

    public static NetworkGraph LoadFile(javax.servlet.http.HttpServletRequest request){
        NetworkGraph output = null;

        try
        {
            output = LoadGDFFile(request);
        }
        catch(Exception e)
        {
            System.out.println(e);
        }

        return output;
    }

    public static NetworkGraph LoadGDFFile(javax.servlet.http.HttpServletRequest request){

      StringBuffer sb = null;
      String strLine = "";
      String contentType = request.getContentType();
      String[] lineArr;
      NetworkGraph nGraph = null;
      Hashtable<String,String> vertexLabelTable = new Hashtable<String, String>();

      if ((contentType != null) && (contentType.indexOf("multipart/form-data") >= 0))
      {
        try{
            ArrayList<String> vertexList = new ArrayList<String>();
            ArrayList<String> edgeList = new ArrayList<String>();
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
            boolean readingNodes = true;
            boolean readingEdges = false;

            //Read File Line By Line
            while ((strLine = br.readLine()) != null)   {
                if (strLine.trim().length() == 0)
                  continue;
                //sb.append(strLine);

                System.out.println(strLine);

                   if(strLine.startsWith("nodedef")){
                        readingNodes = true;
                        readingEdges = false;
                   }
                   else if(readingNodes && !strLine.startsWith("edgedef"))
                   {
                        lineArr = strLine.split(",");
                        vertexList.add(lineArr[0]);
                        if(lineArr[1] == null)//if the label is not provided in the dataset then set it to node's id
                           lineArr[1] = lineArr[0];
                        vertexLabelTable.put(lineArr[0],lineArr[1]);
                    }
                    else if(strLine.startsWith("edgedef")){
                        readingNodes = false;
                        readingEdges = true;
                    }
                    else if(readingEdges)
                    {
                        //lineArr = line.split(",");
                        edgeList.add(strLine);
                    }
            }

            //nGraph = new NetworkGraph(vertexList, edgeList, ",");
            nGraph = new NetworkGraph(vertexLabelTable, edgeList, ",");

            System.out.flush();
            br.close();

        }catch(IOException ex){
          System.out.println(ex);
        }
      }

      return nGraph;
    }


     public static NetworkGraph LoadGDFFile(String fileName){
            NetworkGraph nGraph = null;
            Hashtable<String,String> vertexLabelTable = new Hashtable<String, String>();

            try
            {
                ArrayList<String> vertexList = new ArrayList<String>();
                ArrayList<String> edgeList = new ArrayList<String>();
                //InputStream input = this.getClass().getResourceAsStream(fileName);
                //File webappFileSystemPath = new File(request.getSession().getServletContext().getRealPath("/"));
                BufferedReader input = new BufferedReader(new FileReader(fileName));
                String line = "";
                String[] lineArr;

                boolean readingNodes = true;
                boolean readingEdges = false;
                while ((line=input.readLine()) != null)
                {
                   System.out.println(line);

                   if(line.startsWith("nodedef")){
                        readingNodes = true;
                        readingEdges = false;
                   }
                   else if(readingNodes && !line.startsWith("edgedef"))
                   {
                        lineArr = line.split(",");
                        vertexList.add(lineArr[0]);
                        if(lineArr[1] == null)//if the label is not provided in the dataset then set it to node's id
                           lineArr[1] = lineArr[0];
                        vertexLabelTable.put(lineArr[0],lineArr[1]);
                    }
                    else if(line.startsWith("edgedef")){
                        readingNodes = false;
                        readingEdges = true;
                    }
                    else if(readingEdges)
                    {
                        //lineArr = line.split(",");
                        edgeList.add(line);
                    }
                }

                //nGraph = new NetworkGraph(vertexList, edgeList, ",");
                nGraph = new NetworkGraph(vertexLabelTable, edgeList, ",");

                System.out.flush();
                input.close();
            }
            catch(IOException e)
            {
                System.out.println(e);

                //nGraph = new NetworkGraph();
                //nGraph.errorMessage = e.getMessage();
            }

            return nGraph;
     }

    public static NetworkGraph LoadCSVFile(String fileName){
            NetworkGraph nGraph = null;
            Hashtable<String,String> vertexLabelTable = new Hashtable<String, String>();
            ArrayList<String> edgeList = new ArrayList<String>();
            Hashtable<String,String> edgeListTable = new Hashtable<String, String>();

            CSVReader reader = null;
            String[] nextLine;

            try{
                //Get the CSVReader instance with specifying the delimiter to be used
                reader = new CSVReader(new FileReader(fileName),',');

                //Read one line at a time
                while ((nextLine = reader.readNext()) != null)
                {
                    //if(!vertexLabelTable.containsKey(nextLine[0])) //commented because it takes a lot time. Also, there is no need to check for hashtable, because if there is the same data then it won't add
                        vertexLabelTable.put(nextLine[0],nextLine[0]);//Add the source node

                    //if(!vertexLabelTable.containsKey(nextLine[1]))
                        vertexLabelTable.put(nextLine[1],nextLine[1]);//Add the target node

                    String strEdge = nextLine[0]+","+nextLine[1];
                    edgeListTable.put(strEdge, strEdge);

                    //if(!edgeList.contains(strEdge))
                        //edgeList.add(strEdge);
                }

                ArrayList<String> edgeListArr = new ArrayList<String>(edgeListTable.values());
                edgeList = edgeListArr;

                nGraph = new NetworkGraph(vertexLabelTable, edgeList, ",");
            }
            catch(IOException e){

                System.out.println(e);
            }
            finally {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return nGraph;
     }

    //To do
    public static void LoadPajekDotNetFile(String fileName){

     }

     /*
      *@author ASP
      * Constructs a NetworkGraph with a
      *     Hashtable {name=label, ... , } and ArrayList
      *     ArrayList [source_id,target_id,weight, ... , ]
      * Parses all data as Strings to accommodate downstream algorithm assumptions
      */
     public static NetworkGraph LoadResultSet(Hashtable<String,String> vertexLabelTable,
         		 ArrayList<String> edgeList) {

             NetworkGraph nGraph = null;

     //        try {
     //            while(table.next()) {
     //
     //                // Hashtable { name="label", ... , }
     //                String id = table.getString("id");
     //                String label = table.getString("label");
     //                // Entry into hashtable
     //                vertexLabelTable.put(id, label);
     //
     //                // edgeList [ source_id,target_id,weight,un/directed, x,x,x,x ]
     //                String source_id = table.getString("source_id");
     //                String target_id = table.getString("target_id");
     //                String weight = table.getString("weight");
     //                // Assemble proper line
     //                String line = source_id + "," + target_id + "," + weight;
     //                // Append line to edgeList
     //                edgeList.add(line);
     //            }

                 nGraph = new NetworkGraph(vertexLabelTable, edgeList, ",");

     //        } catch(Exception e) {
     //            System.out.println(e);
     //        }

             return nGraph;
             //return edgeList.toString();

          }
          /*
           * @author ASP
           * Assumes the following table row: (name, label)
           * Parses all data as Strings to accommodate downstream algorithm assumptions
           */
          public static Hashtable<String,String> LoadVertexResultSet(ResultSet table) {

         	 Hashtable<String,String> vertexLabelTable = new Hashtable<String,String>();

         	 	try {
         	 		while(table.next()) {

                     // Hashtable { name="label", ... , }
                     String id = table.getString("name");
                     String label = table.getString("label");
                     // Entry into hashtable
                     vertexLabelTable.put(id, label);
         	 		}
         	 	}

         	 	catch (Exception e) {
         	 		System.out.println(e);
         	 	}

         	 	return vertexLabelTable;
          }

          /*
           * @author ASP
           * Assumes the follwoing table row: (source_id, target_id, weight)
           * Does not support directed graphs
           * Parses all data as Strings to accommodate downstream algorithm assumptions
           */
          public static ArrayList<String> LoadEdgeResultSet(ResultSet table) {

         	 	ArrayList<String> edgeList = new ArrayList<String>();
         	 	try {
                  while(table.next()) {

                      // edgeList [ source_id,target_id,weight,un/directed, x,x,x,x ]
                      String source_id = table.getString("source_id");
                      String target_id = table.getString("target_id");
                      String weight = table.getString("weight");
                      // Assemble proper line
                      String line = source_id + "," + target_id + "," + weight;
                      // Append line to edgeList
                      edgeList.add(line);
                  }

              }

         	 	catch (Exception e) {
         	 		System.out.println(e);
         	 	}

         	 	return edgeList;
          }
}
