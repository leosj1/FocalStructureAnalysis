/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package myclasses;

/**
 *
 * @author Fatih Şen
 */
public class NetworkNode {
        private NetworkNode node = null;
        private String nodeId = "";
        private String nodeLabel = "";
        private NetworkGraph nGraph = null;

        NetworkNode(){

        }

        NetworkNode(NetworkNode n){
            this.node = n;
        }

        NetworkNode(String nId, String nLabel){
            this.nodeId = nId;
            this.nodeLabel = nLabel;
        }

        NetworkNode(NetworkGraph nGraph,String nId){
            this.nGraph = nGraph;
            this.nodeId = nId;
        }

        NetworkNode(NetworkGraph hGraph,NetworkNode n){
            this.nGraph = hGraph;
            this.node = n;
        }

        public void copyNode(NetworkNode node){
            this.setNode(node.getNode());
            this.setNodeId(node.getNodeId());
            this.setNodeLabel(node.getNodeLabel());
        }

        public void setNode(NetworkNode n){
            this.node = n;
        }

        public NetworkNode getNode(){
            return node;
        }

        public void setNodeId(String nId){
            this.nodeId = nId;
        }

        public String getNodeId(){
            return nodeId;
        }

        public void setNodeLabel(String nLabel){
            this.nodeLabel = nLabel;
        }

        public String getNodeLabel(){
            return nodeLabel;
        }



}
