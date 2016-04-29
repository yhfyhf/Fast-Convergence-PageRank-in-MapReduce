package RandomBlockPageRank;

/**
 * Created by Christina on 4/20/16.
 */
public class PageRankMapper extends BlockPageRank.PageRankMapper {
    @Override
    protected String getBlockId(String nodeId) {
        return Conf.Conf.getBlockIdRandom(nodeId);
    }
}
