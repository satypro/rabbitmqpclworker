package cloud.consumer;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.SimpleStatement;

import java.util.*;

public class PointCloudRepository
{
    private static final String TABLE_NAME = "pointcloudnormalizedoctree";
    private static final String TABLE_NAME_2 = "pointcloudregions";
    private static final String TABLE_NAME_3 = "pointpartitionregions";
    private Session session;

    public static Map<String, List<PointCloud>> pointCloudStore = new HashMap<String, List<PointCloud>>();
    public static Map<Long, List<RegionPoint>>  regionPointsStore = new HashMap<>();
    public static Map<Long, List<PointCloudPartition>>  regionPartitionPointsStore = new HashMap<>();

    public static List<RegionMap> regionMaps = new ArrayList<RegionMap>();

    public PointCloudRepository(Session session)
    {
        this.session = session;
    }

    public List<PointCloud> getPointsByRegionId(String regionId)
    {
        StringBuilder sb = new StringBuilder("SELECT regionid, mortoncode, pointid FROM propelld.")
                    .append(TABLE_NAME)
                    .append(" WHERE regionid = '")
                    .append(regionId)
                    .append("';");

        final String query = sb.toString();
        ResultSet rs = session.execute(query);

        List<PointCloud> pointClouds = new ArrayList<PointCloud>();

        for (Row r : rs)
        {
            PointCloud pointCloud = new PointCloud(r.getLong("mortoncode"),
                    r.getString("regionid"),
                    r.getLong("pointid"));
            pointClouds.add(pointCloud);
        }
        return pointClouds;
    }

    public List<RegionPoint> getPointsByRegionId(long regionId)
    {
        StringBuilder sb = new StringBuilder("SELECT " +
                "regionid, morton, pointid, x, y, z, xo, yo, zo, label " +
                "FROM propelld.")
                .append(TABLE_NAME_2)
                .append(" WHERE regionid =")
                .append(regionId)
                .append(";");

        final String query = sb.toString();
        ResultSet rs = session.execute(query);

        List<RegionPoint> pointClouds = new ArrayList<RegionPoint>();

        for (Row r : rs)
        {
            RegionPoint pointCloud = new
                    RegionPoint(r.getLong("regionid"),
                    r.getLong("pointid"),
                    r.getLong("morton"),
                    r.getLong("x"),
                    r.getLong("y"),
                    r.getLong("z"),
                    r.getFloat("xo"),
                    r.getFloat("yo"),
                    r.getFloat("zo"),
                    r.getInt("label"));
            pointClouds.add(pointCloud);
        }

        Collections.sort(pointClouds, new Comparator<RegionPoint>()
        {
            @Override
            public int compare(RegionPoint o1, RegionPoint o2)
            {
                if (o1.getMorton() > o2.getMorton())
                    return 1;

                if (o1.getMorton() < o2.getMorton())
                    return -1;

                return 0;
            }
        });
        return pointClouds;
    }
    //PointCloudPartition
    public List<PointCloudPartition> getPointsPartitionsByRegionId(long regionId)
    {
        StringBuilder sb = new StringBuilder("SELECT regionid, pointid, x, y, z FROM propelld.")
                .append(TABLE_NAME_3)
                .append(" WHERE regionid =")
                .append(regionId)
                .append(";");

        final String query = sb.toString();
        ResultSet rs = session.execute(query);

        List<PointCloudPartition> pointClouds = new ArrayList<PointCloudPartition>();

        for (Row r : rs)
        {
            PointCloudPartition pointCloud = new
                    PointCloudPartition();
            pointCloud.setPointid(r.getLong("pointid"));
            pointCloud.setRegionid(r.getLong("regionid"));
            pointCloud.setX(r.getFloat("x"));
            pointCloud.setY(r.getFloat("x"));
            pointCloud.setZ(r.getFloat("x"));

            pointClouds.add(pointCloud);
        }
        return pointClouds;
    }

    public boolean UpdateKnnResult(Long mortonCode, List<Long> mortonCodes)
    {
        //knnresult
        // UPDATE propelld.knnresult SET knn = knn + {5, 6, 7} WHERE mortoncode = 123456789;
        SimpleStatement st = new SimpleStatement("UPDATE propelld.knnresult SET knn = knn + ? WHERE mortoncode = ?", mortonCodes, mortonCode);
        ResultSet rs = session.execute(st);
        return true;
    }
}