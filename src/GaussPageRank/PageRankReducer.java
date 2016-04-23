package GaussPageRank;

import Conf.Conf;

import java.util.Map;

/**
 * Created by Christina on 4/20/16.
 */
public class PageRankReducer extends BlockPageRank.PageRankReducer {
    /**
     * newPageRank = nextPageRank from inBlock nodes + nextPageRank from outBlock nodes
     * The nextPageRank from outBlock nodes is constant, so reset the newPageRank = nextPageRank from outBlock
     * then add the nextPageRank from inBlock
     * */
    protected float iterateBlockOnce(Map<Integer, BlockPageRank.Node> nodesMap) {
        float residuals = 0;

        //set nextPageRank and newPageRank for each node
        for (BlockPageRank.Node node : nodesMap.values()) {
            //check if the node has desNode
            if (!node.getDesNodeId().isEmpty()) {
                node.setNextPageRank(node.getNewPageRank() / node.getDegree());
                node.setNewPageRank(node.getPageRankFromOutBlock());
            }
        }

        //get the updated newPageRank considering the nextPageRank from inBlock nodes
        for (BlockPageRank.Node srcNode : nodesMap.values()) {
            //check if the node has desNodeInBlock
            if (srcNode.getDesNodeInBlock().isEmpty()) {
                continue;
            }
            String[] desNodeIds = srcNode.getDesNodeInBlock().split(",");
            float nextPageRank = srcNode.getNextPageRank();

            for (String desNodeIdString : desNodeIds) {
                int desNodeId = Integer.valueOf(desNodeIdString);
                BlockPageRank.Node desNode = nodesMap.get(desNodeId);
                desNode.addNewPageRank(nextPageRank);
            }
        }

        //update newPageRank considering the damping factor and calculate the residual
        for (BlockPageRank.Node node : nodesMap.values()) {
            float updatedPageRank = node.getNewPageRank() * Conf.DAMPING_FACTOR + Conf.RANDOM_JUMP_FACTOR;
            node.setNewPageRank(updatedPageRank);
            float oldPageRank = nodesMap.get(node.getId()).getOldPageRank();
            float newPageRank = node.getNewPageRank();
            residuals += Math.abs(oldPageRank - newPageRank) / newPageRank;
        }

        //return the avg of residuals
        return residuals / nodesMap.size();
    }
}
