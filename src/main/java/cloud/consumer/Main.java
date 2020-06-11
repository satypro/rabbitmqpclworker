package cloud.consumer;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.correlation.Covariance;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

public class Main
{
    static Morton64 morton64 = new Morton64(2, 32);
    static PointCloudRepository pointCloudRepository =  CassandraFactory.getPointCloudRepository();
    static long regionId = 3L;

    public static void main(String[] args) throws Exception
    {
        LoadRegionPointsMorton(regionId);
    }

    public Main() throws Exception
    {
        QueueConsumer consumer = new QueueConsumer("Publish.Point.Queue.Region_Three.incoming.queue");
        Thread consumerThread = new Thread(consumer);
        consumerThread.start();
    }

    public static void LoadRegionMap()
    {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                .create();
        try
        {
            Type userListType = new TypeToken<ArrayList<RegionMap>>() {}
                .getType();
            List<RegionMap> regionMaps = gson
                    .fromJson(new FileReader("/Users/rpncmac2/Propelld/POC/NodeExample/src/Octree/regionpart.json"), userListType);
            PointCloudRepository pointCloudRepository =  CassandraFactory
                    .getPointCloudRepository();

            pointCloudRepository.regionMaps = regionMaps;
        }
        catch (Exception ex)
        {
            System.out.println("Loading error while region map");
        }
    }

    public static void LoadRegionPoints(long regionId)
    {
        PointCloudRepository pointCloudRepository =  CassandraFactory.getPointCloudRepository();
        List<PointCloudPartition> regionPoints = pointCloudRepository.getPointsPartitionsByRegionId(regionId);
        pointCloudRepository.regionPartitionPointsStore.put(regionId, regionPoints);
        System.out.println("Region one Loaded with : " +regionPoints.size() + " Many Points");
    }

    public static void LoadRegionPointsMorton(long regionId)
    {
        List<RegionPoint> regionPoints = pointCloudRepository.getPointsByRegionId(regionId);
        pointCloudRepository.regionPointsStore.put(regionId, regionPoints);
        System.out.println("Region one Loaded with : "
                + regionPoints.size()
                + " Many Points");

        // Now lets For each point, lets find the points inside a range Box
        // Having the Square Box where the center is the points....
        // Having radius R
        try
        {
            long startTime = System.nanoTime();

            List<String> lines = new ArrayList<>();
            for (RegionPoint regionPoint : regionPoints)
            {
                PointFeature pointFeature = findThePointsInBoxAndItsFeature(1000L, regionPoint, regionId);
                String str = pointFeature.getIndex()
                        +" "
                        + pointFeature.getCl()
                        +" "
                        + pointFeature.getCp()
                        +" "
                        + pointFeature.getCs()
                        +" "
                        + pointFeature.getAnisotropy()
                        +" "
                        + pointFeature.getChangeOfCurvature()
                        +" "
                        + pointFeature.getEigenentropy()
                        +" "
                        + pointFeature.getOmnivariance()
                        +" "
                        + regionPoint.getXo()
                        +" "
                        + regionPoint.getYo()
                        +" "
                        + regionPoint.getZo()
                        +" "
                        + regionPoint.getLabel();
                lines.add(str);
            }

            long elapsedTime = System.nanoTime() - startTime;
            System.out.println("Feature Generation took : " + elapsedTime/1000000);

            FileWriter fileWriter = new FileWriter("/Users/rpncmac2/Study/Research/features/region_three.txt");
            for (String line: lines)
            {
                fileWriter.write(line + System.lineSeparator());
            }

            fileWriter.close();
            elapsedTime = System.nanoTime() - elapsedTime;
            System.out.println("Writing into File Feature took : " + elapsedTime/1000000);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        System.out.println("DONE WRITING INTO THE REGION "+ regionId + " Features File");
    }

    public static PointFeature findThePointsInBoxAndItsFeature(long radius, RegionPoint regionPoint, long regionId)
    {
        List<RegionPoint> regionPoints = pointCloudRepository
                .regionPointsStore
                .get(regionId);

        long totalPoints = regionPoints.size();

        long startIndex_X =  regionPoint.getX() - radius;
        long startIndex_Y =  regionPoint.getY() - radius;

        long endIndex_X = regionPoint.getX() + radius;
        long endIndex_Y = regionPoint.getY() + radius;

        long startMortonCode = morton64.pack(startIndex_X, startIndex_Y);
        long endMortonCode = morton64.pack(endIndex_X, endIndex_Y);

        // Now we need to find the Points within this range
        // Start and End Morton Code...
        // We need to Do the Morton Code
        int index = Collections.binarySearch(regionPoints, regionPoint, new Comparator<RegionPoint>()
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

        int startIndex = index;
        List<Point> points = new ArrayList<Point>();

        while(startIndex < totalPoints)
        {
            RegionPoint rgp = regionPoints.get(startIndex);
            if (rgp.getMorton() <= endMortonCode)
            {
                points.add(new Point(rgp.getXo(),
                        rgp.getYo(),
                        rgp.getZo()));
            }
            else
            {
                break;
            }

            startIndex++;
        }

        startIndex = index;
        while(startIndex >= 0)
        {
            RegionPoint rgp = regionPoints.get(startIndex);
            if (rgp.getMorton() >= startMortonCode)
            {
                points.add(new Point(rgp.getXo(),
                        rgp.getYo(),
                        rgp.getZo()));
            }
            else
            {
                break;
            }

            startIndex--;
        }
        // Now Save the Points into the Cassandra as the List of Points if we want
        // Else let us calculate the cs, cl, cp values...

        double[][] matrix = new double[points.size()][3];

        int matrixIdx = 0;
        for(Point neighbour : points)
        {
            matrix[matrixIdx][0] = neighbour.getX();
            matrix[matrixIdx][1] = neighbour.getY();
            matrix[matrixIdx][2] = neighbour.getZ();
            matrixIdx++;
        }

        RealMatrix cov = new Covariance(MatrixUtils.createRealMatrix(matrix))
                .getCovarianceMatrix();

        double[] eigenValues = new EigenDecomposition(cov)
                .getRealEigenvalues();

        //Features
        double cl = (eigenValues[0] - eigenValues[1])/eigenValues[0];
        double cp = (eigenValues[1] - eigenValues[2])/eigenValues[0];
        double cs =  eigenValues[2]/eigenValues[0];
        double omnivariance = Math.cbrt(eigenValues[0]* eigenValues[1] * eigenValues[2]);
        double anisotropy = (eigenValues[0] - eigenValues[2])/eigenValues[0];
        double eigenentropy = -1* (eigenValues[0]*Math.log(eigenValues[0])
                + eigenValues[1]*Math.log(eigenValues[1])
                + eigenValues[2]*Math.log(eigenValues[2]));
        double changeOfCurvature = eigenValues[2]/(eigenValues[0] + eigenValues[1] + eigenValues[2]);

        // lets build Feature and then return the feature
        return new PointFeature(
                regionPoint.getPointId(),
                cl,
                cp,
                cs,
                omnivariance,
                anisotropy,
                eigenentropy,
                changeOfCurvature
        );
    }

    public static void main1(String[] args) throws Exception
    {
        PointCloudRepository pointCloudRepository =  CassandraFactory.getPointCloudRepository();
        LoadRegionMap();

        int count = 1;
        for (RegionMap regionMap:  pointCloudRepository.regionMaps)
        {
            System.out.println("Region No " + count);
            count++;
            for (String regionCode : regionMap.childrens)
            {
                List<PointCloud> pointCloudList = pointCloudRepository
                        .getPointsByRegionId(regionCode);
                if (pointCloudList.size() == 0)
                {
                    System.out.println("Nothing found for : " + regionCode);
                }
                else
                {
                    System.out.println("found for : " + regionCode + " No Of Points : " + pointCloudList.size());
                }

                // Sort the Morton Codes and then Store it....
                Collections.sort(pointCloudList, new Comparator<PointCloud>()
                {
                    @Override
                    public int compare(PointCloud o1, PointCloud o2)
                    {
                        if (o1.getMortonCode() - o2.getMortonCode() > 0)
                            return 1;
                        if (o1.getMortonCode() == o2.getMortonCode())
                        {
                            return  0;
                        }

                        return -1;
                    }
                });

                PointCloudRepository
                        .pointCloudStore
                        .put(regionCode, pointCloudList);

                /*
                for (PointCloud pointCloud : pointCloudList)
                {
                    if (PointCloudRepository.pointCloudStore.containsKey(pointCloud.getRegionId()))
                    {
                        PointCloudRepository.pointCloudStore
                                .get(pointCloud.getRegionId())
                                .add(pointCloud);
                    }
                    else
                    {
                        List<PointCloud> pointClouds = new ArrayList<PointCloud>();
                        pointClouds.add(pointCloud);
                        PointCloudRepository.pointCloudStore.put(pointCloud.getRegionId(), pointClouds);
                    }
                }
                 */
            }
        }

        System.out.println("Point Cloud Data Store Build... Complete !!!");

        // Load the Consumers
        new Main();
    }
}