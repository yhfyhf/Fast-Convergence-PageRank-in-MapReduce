package BlockPageRank;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Christina on 4/16/20.
 */
public class Node {
    private int id;
    private float oldPageRank;
    private float newPageRank = 0;
    private float nextPageRank = 0; // nextPageRank = newPageRank / (number of desNodeInBlock)
    private float pageRankFromOutBlock = 0;
    private String desNodeIds = "";
    private String desNodeInBlock = "";
    private int degree = 0;

    private List<Integer> BE = new ArrayList<>();
    private float BC = 0.0f;

    public List<Integer> getBE() {
        return BE;
    }

    public void addBE(int u) {
        BE.add(u);
    }


    public float getBC() {
        return BC;
    }

    public void addBC(float BC) {
        this.BC += BC;
    }


    public Node(int id) {
        this.id = id;
    }

    public void setDesNodeIds(String desNodeIds) {
        this.desNodeIds = desNodeIds;
        String[] arr = desNodeIds.split(",");
        if (arr[0].trim().isEmpty()) {
            this.degree = 0;
        } else {
            this.degree = arr.length;
        }
    }

    public void setOldPageRank(float oldPageRank) {
        this.oldPageRank = oldPageRank;
    }

    public void setNewPageRank(float newPageRank) {
        this.newPageRank = newPageRank;
    }

    public void addNewPageRank(float nextNewPageRank) {
        newPageRank += nextNewPageRank;
    }

    public void setDesNodeInBlock(String desNodeInBlock) {
        this.desNodeInBlock = desNodeInBlock;
    }

    public void addPageRankFromOutBlock(float pageRankFromOutBlock) {
        this.pageRankFromOutBlock += pageRankFromOutBlock;
    }

    public void setNextPageRank(float nextPageRank) {
        this.nextPageRank = nextPageRank;
    }

    public int getId() {
        return id;
    }

    public String getDesNodeId() {
        return desNodeIds;
    }

    public float getNewPageRank() {
        return newPageRank;
    }

    public float getOldPageRank() {
        return oldPageRank;
    }

    public float getNextPageRank() {
        return nextPageRank;
    }

    public float getPageRankFromOutBlock() {
        return pageRankFromOutBlock;
    }

    public String getDesNodeInBlock() {
        return desNodeInBlock;
    }

    public int getDegree() {
        return degree;
    }

    public String toString() {
        return "nodeId:" + id;
    }

}
