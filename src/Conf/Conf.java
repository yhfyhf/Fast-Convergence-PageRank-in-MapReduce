package Conf;

/**
 * Created by Christina on 4/16/16.
 */
public class Conf {
    public static final String FILE_NAME = "simplepagerank_";

    public static final int NODEINFO = 0;
    public static final int NEXTPAGERANK = 1;
    public static final int EDGE_INCBLOCK = 3;
    public static final int NEXTPAGERANK_FROM_INBLOCK = 4;
    public static final int NEXTPAGERANK_FROM_OUTBLOCK = 5;


    public static final int BE = 111;
    public static final int BC = 101;



    public static final int NODES_NUM = 685230;
    public static final int BLOCKS_NUM = 68;
    public static final int MAPREDUCE_ITERATION = 8;
    public static final float EPSILON = 0.001f;

    public static final float DAMPING_FACTOR = 0.85f;
    public static final float RANDOM_JUMP_FACTOR = ((1 - DAMPING_FACTOR) / NODES_NUM);
    public static final int MULTIPLE = 1000000;
    public static final float RESIDUAL_ERROR = 0.001f;
    public static final float INBLOCK_ITERRATION = 30;


    private static final int[] BLOCK_BOUNDARIES = { 0, 10328, 20373, 30629, 40645,
            50462, 60841, 70591, 80118, 90497, 100501, 110567, 120945,
            130999, 140574, 150953, 161332, 171154, 181514, 191625, 202004,
            212383, 222762, 232593, 242878, 252938, 263149, 273210, 283473,
            293255, 303043, 313370, 323522, 333883, 343663, 353645, 363929,
            374236, 384554, 394929, 404712, 414617, 424747, 434707, 444489,
            454285, 464398, 474196, 484050, 493968, 503752, 514131, 524510,
            534709, 545088, 555467, 565846, 576225, 586604, 596585, 606367,
            616148, 626448, 636240, 646022, 655804, 665666, 675448, 685230 };

    public static int getBlockId(int nodeId) {
        int blockid = (int) Math.floor(nodeId / 10000);
        int boundary = BLOCK_BOUNDARIES[blockid];
        if (nodeId < boundary) {
            blockid--;
        }

        return blockid;
    }

    public static int getBlockIdRandom(int nodeId) {
        return String.valueOf(nodeId).hashCode() % BLOCKS_NUM;
    }
}
