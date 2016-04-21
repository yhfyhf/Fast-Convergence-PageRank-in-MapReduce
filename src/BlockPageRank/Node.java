package BlockPageRank;

/**
 * Created by Christina on 4/16/20.
 */
public class Node {
    private String id;
    private float oldPageRank;
    private float newPageRank = 0;
    private float nextPageRank = 0; // nextPageRank = newPageRank / (number of desNodeInBlock)
    private String desNodeIds;
    private String desNodeInBlock = "";
    private String desNodeOutBlock = "";
    private int degree;

    public Node(String id) {
        this.id = id;
    }

    public void setDesNodeIds(String desNodeIds) {
        this.desNodeIds = desNodeIds;
        this.degree = desNodeIds.split(",").length;
    }

    public void setOldPageRank(float oldPageRank) {
        this.oldPageRank = oldPageRank;
    }

    public void addNewPageRank(float nextNewPageRank) {
        newPageRank += nextNewPageRank;
    }

    public void setDesNodeInBlock(String desNodeInBlock) {
        this.desNodeInBlock = desNodeInBlock;
    }

    public void setDesNodeOutBlock(String desNodeOutBlock) {
        this.desNodeOutBlock = desNodeOutBlock;
    }

    public void setNewPageRank(float nextPageRank) {
        this.nextPageRank = nextPageRank;
    }

    public String getId() {
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

    public String getDesNodeInBlock() {
        return desNodeInBlock;
    }

    public String getDesNodeOutBlock() {
        return desNodeOutBlock;
    }

    public int getDegree() {
        return degree;
    }

}
