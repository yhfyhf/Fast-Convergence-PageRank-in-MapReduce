package Conf;

/**
 * Created by Christina on 4/16/16.
 */
public class Conf {
    public static final String NODEINFO = "nodeInfo";
    public static final String NEXTPAGERANK = "nextPageRank";
    public static final String EDGE_OUTCBLOCK = "edgeInBlock";
    public static final String EDGE_INCBLOCK = "edgeOutBlock";
    public static final String FILE_NAME = "simplepagerank_";

    public static final int NODES_NUM = 685230;
    public static final int ITERATIONS_NUM = 6;
    private static final int[] BLOCK_BOUNDARIES = { 0, 10328, 20373, 30629, 40645,
            50462, 60841, 70591, 80118, 90497, 100501, 110567, 120945,
            130999, 140574, 150953, 161332, 171154, 181514, 191625, 202004,
            212383, 222762, 232593, 242878, 252938, 263149, 273210, 283473,
            293255, 303043, 313370, 323522, 333883, 343663, 353645, 363929,
            374236, 384554, 394929, 404712, 414617, 424747, 434707, 444489,
            454285, 464398, 474196, 484050, 493968, 503752, 514131, 524510,
            534709, 545088, 555467, 565846, 576225, 586604, 596585, 606367,
            616148, 626448, 636240, 646022, 655804, 665666, 675448, 685230 };

    public static String getBlockId(String nodeId) {
        int nodeIdInt = Integer.parseInt(nodeId);
        int blockId = (int) Math.floor(nodeIdInt / 10000);
        blockId = nodeIdInt < BLOCK_BOUNDARIES[blockId] ? blockId - 1 : blockId;
        return String.valueOf(blockId);
    }

}
