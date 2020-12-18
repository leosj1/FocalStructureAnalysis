/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package myclasses;


/**
 *
 * @author Fatih Şen
 */
public class NetworkEdge {
        private NetworkNode sourceNode = null;
        private NetworkNode targetNode = null;
        private NetworkGraph nGraph = null;
        private double edgeWeight = 1.0;

        NetworkEdge(){

        }

        NetworkEdge(NetworkGraph nGraph, NetworkNode sNode, NetworkNode tNode){
            this.nGraph = nGraph;
            sourceNode = new NetworkNode(sNode);
            targetNode = new NetworkNode(tNode);
        }

        NetworkEdge(NetworkGraph nGraph,String sNodeId, String tNodeId){
            this.nGraph = nGraph;
            sourceNode =  new NetworkNode(sNodeId,sNodeId);
            targetNode = new NetworkNode(tNodeId,tNodeId);
        }

        NetworkEdge(NetworkGraph nGraph,String sNodeId, String tNodeId, double eWeight){
            this.nGraph = nGraph;
            sourceNode =  new NetworkNode(sNodeId,sNodeId);
            targetNode = new NetworkNode(tNodeId,tNodeId);
            this.edgeWeight = eWeight;
        }

        public void copyEdge(NetworkEdge e){
            this.setSourceNode(e.getSourceNode());
            this.setTargetNode(e.getTargetNode());
        }

        public void setSourceNode(NetworkNode sNode){
            this.sourceNode = sNode;
        }
        public NetworkNode getSourceNode(){
            return sourceNode;
        }

        public void setTargetNode(NetworkNode tNode){
            this.targetNode = tNode;
        }
        public NetworkNode getTargetNode(){
            return targetNode;
        }

        public void setEdgeWeight(double eWeight){
            this.edgeWeight = eWeight;
        }
        public double getEdgeWeight(){
            return this.edgeWeight;
        }


}
