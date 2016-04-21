package BlockPageRank;

/**
 * Created by Christina on 4/16/20.
 */
public class Node {
    private String id;
    private float oldPageRank;
    private float newPageRank = 0;
    private String desNodeIds;
    private String desNodeInBlock = "";
    private String desNodeOutBlock = "";

    public Node(String id) {
        this.id = id;
    }

    public void setDesNodeIds(String desNodeIds) {
        this.desNodeIds = desNodeIds;
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

    public String getDesNodeInBlock() {
        return desNodeInBlock;
    }

    public String getDesNodeOutBlock() {
        return desNodeOutBlock;
    }

}
