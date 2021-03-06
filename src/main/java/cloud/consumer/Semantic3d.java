package cloud.consumer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.correlation.Covariance;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Semantic3d
{
    public static void main(String[]args)
    {
         //findMinMax();
         //NormalizePoints();
         //SplitSpace();
        //GenerateJSONFile();
        /*
        System.out.println("untermaed : part5");
        String inputFileName = "E:\\PCL_CLASSIFIER\\bildstein_station3_xyz_intensity_rgb_verify\\ff\\com_part5.txt";
        String outputFileName = "E:\\PCL_CLASSIFIER\\bildstein_station3_xyz_intensity_rgb_verify\\ff\\com_part5_1000_part_optimal.csv";
        long radius = 200L;
        int startIndex = 0;
        GenerateFeaturesOptimal(inputFileName, outputFileName, radius, startIndex);
         */
    }

    public static void findMinMax()
    {
        File file =
            new File("E:\\PCL_CLASSIFIER\\bildstein_station3_xyz_intensity_rgb_verify\\training.txt");
        Scanner sc = null;
        try
        {
            sc = new Scanner(file);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

        float maxX = 0;
        float minX = 0;
        float maxY = 0;
        float minY = 0;
        float maxZ = 0;
        float minZ = 0;

        int index = 0;
        while (sc.hasNextLine())
        {
            String line = sc.nextLine();
            String input[] = line.split(" ");

            float x = Float.parseFloat(input[0]);
            float y = Float.parseFloat(input[1]);
            float z = Float.parseFloat(input[2]);

            if (index == 0)
            {
                index = 1;
                maxX = x;
                maxY = y;
                maxZ = z;

                minX = x;
                minY = y;
                minZ = z;
            }
            else
            {
                maxX = x > maxX ? x : maxX;
                maxY = y > maxY ? y : maxY;
                maxZ = z > maxZ ? z : maxZ;

                minX = x < minX ? x : minX;
                minY = y < minY ? y : minY;
                minZ = z < minZ ? z : minZ;
            }
        };

        System.out.println("***************************");
        System.out.print("Max X : "+ maxX + " ");
        System.out.print("Max Y : "+ maxY + " ");
        System.out.print("Max Z : "+ maxZ + " ");
        System.out.println("***************************");
        System.out.print("Min X : "+ minX + " ");
        System.out.print("Min Y : "+ minY + " ");
        System.out.print("Min Z : "+ minZ + " ");
        System.out.println("***************************");
    }

    public static void GenerateJSONFile()
    {
        File file =
            new File("E:\\PCL_CLASSIFIER\\dales_data\\5110\\Space_Partition\\com_part5.txt");
        Scanner sc = null;
        try
        {
            sc = new Scanner(file);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

        List<PointViz> pointVizs = new ArrayList<PointViz>();
        while (sc.hasNextLine())
        {
            String line = sc.nextLine();
            String input[] = line.split(" ");

            long x = Long.parseLong(input[0]);
            long y = Long.parseLong(input[1]);
            long z = Long.parseLong(input[2]);
            int  l = Integer.parseInt(input[6]);
            int boundary = Integer.parseInt(input[7]);
            if (pointVizs.size() < 1000000)
                pointVizs.add(new PointViz(x,y,z,l, boundary));
        };

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try
        {
            gson.toJson(pointVizs, new FileWriter("E:\\output.json"));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void NormalizePoints()
    {
        File file =
            new File("E:\\PCL_CLASSIFIER\\bildstein_station3_xyz_intensity_rgb_verify\\training.txt");
        File file2 =
            new File("E:\\PCL_CLASSIFIER\\bildstein_station3_xyz_intensity_rgb_verify\\label.txt");
        Scanner sc = null;
        Scanner sc2 = null;
        try
        {
            sc = new Scanner(file);
            sc2 = new Scanner(file2);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

        float p = 0.001f;
        float xMin = -82.302f;
        float yMin = -124.823f;
        float zMin = -12.591f;
        List<String> lines = new ArrayList<>();
        long index = 1;
        while (sc.hasNextLine() && sc2.hasNextLine())
        {
            String line = sc.nextLine();
            String label = sc2.nextLine();
            String input[] = line.split(" ");

            float x = Float.parseFloat(input[0]);
            float y = Float.parseFloat(input[1]);
            float z = Float.parseFloat(input[2]);

            int xNorm = (int) ((x - xMin)/p);
            int yNorm = (int) ((y - yMin)/p);
            int zNorm = (int) ((z - zMin)/p);
            int l = Integer.parseInt(label.trim());

            lines.add(xNorm+" "+yNorm+" "+zNorm+" "+x+" "+y+" "+z+" "+l);
            index++;
        }

        FileWriter writer = null;
        try
        {
            writer = new FileWriter("E:\\PCL_CLASSIFIER\\bildstein_station3_xyz_intensity_rgb_verify\\ff\\normalized.txt");
            for(String str: lines)
            {
                writer.write(str + System.lineSeparator());
            }
            writer.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        System.out.println("DONE NORMALIZING POINTS");
    }

    public static void SplitSpace()
    {
        try
        {
            File file =
                new File("E:\\PCL_CLASSIFIER\\bildstein_station3_xyz_intensity_rgb_verify\\ff\\normalized.txt");
            FileWriter writer1 =
                new FileWriter("E:\\PCL_CLASSIFIER\\bildstein_station3_xyz_intensity_rgb_verify\\ff\\com_part1.txt");
            FileWriter writer2 =
                new FileWriter("E:\\PCL_CLASSIFIER\\bildstein_station3_xyz_intensity_rgb_verify\\ff\\com_part2.txt");
            FileWriter writer3 =
                new FileWriter("E:\\PCL_CLASSIFIER\\bildstein_station3_xyz_intensity_rgb_verify\\ff\\com_part3.txt");
            FileWriter writer4 =
                new FileWriter("E:\\PCL_CLASSIFIER\\bildstein_station3_xyz_intensity_rgb_verify\\ff\\com_part4.txt");
            FileWriter writer5 =
                new FileWriter("E:\\PCL_CLASSIFIER\\bildstein_station3_xyz_intensity_rgb_verify\\ff\\com_part5.txt");

            Scanner sc = new Scanner(file);
            // Now Space Partitioning....
            while (sc.hasNextLine())
            {
                String line = sc.nextLine();
                String input[] = line.split(" ");
                long xnorm = Long.parseLong(input[0]);
                long ynorm = Long.parseLong(input[1]);
                long znorm = Long.parseLong(input[2]);
                float x = Float.parseFloat(input[3]);
                float y = Float.parseFloat(input[4]);
                float z = Float.parseFloat(input[5]);
                int label = Integer.parseInt(input[6]);

                // Now Decide Which Bucket x, y, z will go
                // 0-100000, 100000-200000, 200000-300000, 300000 - 400000, 400000 -- 500000
                long delta_x = 5000L;
                if (xnorm <= (95000  + delta_x))
                {
                    int isBoundary = 0;
                    if (xnorm > 95000)
                    {
                        isBoundary = 1;
                    }
                    writer1
                        .write(xnorm+" "+ynorm+" "+znorm+" "+x+" "+y+" "+z+" "+label+" "+isBoundary+ System.lineSeparator());
                }

                if (xnorm >= (95000  - delta_x) &&  xnorm <= (99000  + delta_x))
                {
                    int isBoundary = 0;
                    if (xnorm < 95000  || xnorm > 99000)
                    {
                        isBoundary = 1;
                    }
                    writer2
                        .write(xnorm+" "+ynorm+" "+znorm+" "+x+" "+y+" "+z+" "+label+" "+isBoundary+ System.lineSeparator());
                }

                if (xnorm >= (99000  - delta_x) &&  xnorm <= (104000  + delta_x))
                {
                    int isBoundary = 0;
                    if (xnorm < 99000  || xnorm > 104000)
                    {
                        isBoundary = 1;
                    }
                    writer3
                        .write(xnorm+" "+ynorm+" "+znorm+" "+x+" "+y+" "+z+" "+label+" "+isBoundary+ System.lineSeparator());
                }

                if (xnorm >= (104000  - delta_x) &&  xnorm <= (108000  + delta_x))
                {
                    int isBoundary = 0;
                    if (xnorm < 104000  || xnorm > 108000)
                    {
                        isBoundary = 1;
                    }
                    writer4
                        .write(xnorm+" "+ynorm+" "+znorm+" "+x+" "+y+" "+z+" "+label+" "+isBoundary+ System.lineSeparator());
                }

                if (xnorm >= (108000  - delta_x) &&  xnorm <= (300000  + delta_x))
                {
                    int isBoundary = 0;
                    if (xnorm < 108000)
                    {
                        isBoundary = 1;
                    }
                    writer5
                        .write(xnorm+" "+ynorm+" "+znorm+" "+x+" "+y+" "+z+" "+label+" "+isBoundary+ System.lineSeparator());
                }
            }
            writer1.close();
            writer2.close();
            writer3.close();
            writer4.close();
            writer5.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void GenerateFeatures(String inputFileName, String outputFileName, long radius, int startIndex)
    {
        try
        {
            File file =
                new File(inputFileName);
            FileWriter writer =
                new FileWriter(outputFileName);
            Scanner sc = new Scanner(file);
            List<RegionPoint> regionPoints = new ArrayList<>();
            while (sc.hasNextLine())
            {
                String line = sc.nextLine();
                String input[] = line.split(" ");
                long xnorm = Long.parseLong(input[0]);
                long ynorm = Long.parseLong(input[1]);
                long znorm = Long.parseLong(input[2]);
                float x = Float.parseFloat(input[3]);
                float y = Float.parseFloat(input[4]);
                float z = Float.parseFloat(input[5]);
                int label = Integer.parseInt(input[6]);
                int isBoundary = Integer.parseInt(input[7]);
                if (label != 0)
                regionPoints
                    .add(new RegionPoint(xnorm, ynorm, znorm, x, y, z, label, isBoundary));
            }

            writer.write("cl,cp,cs,anisotropy,changeOfCurvature,eigenentropy,omnivariance,label"+ System.lineSeparator());
            for (int i = startIndex; i < regionPoints.size(); i++)
            {
                RegionPoint regionPoint = regionPoints.get(i);
                if (regionPoint.getIsboundary() == 0)
                {
                    long startIndex_X =  regionPoint.getX() - radius;
                    long startIndex_Y =  regionPoint.getY() - radius;
                    long startIndex_Z =  regionPoint.getZ() - radius;

                    long endIndex_X = regionPoint.getX() + radius;
                    long endIndex_Y = regionPoint.getY() + radius;
                    long endIndex_Z = regionPoint.getZ() + radius;

                    List<Point> points = new ArrayList<Point>();
                    for (RegionPoint rgp: regionPoints)
                    {
                        if ((rgp.getX() >= startIndex_X && rgp.getX() <= endIndex_X)
                            && (rgp.getY() >= startIndex_Y && rgp.getY() <= endIndex_Y)
                            && (rgp.getZ() >= startIndex_Z && rgp.getZ() <= endIndex_Z))
                        {
                            points.add(new Point(rgp.getXo(),
                                                 rgp.getYo(),
                                                 rgp.getZo()));
                        }
                    }

                    // Now Save the Points into the Cassandra as the List of Points if we want
                    // Else let us calculate the cs, cl, cp values...

                    double[][] matrix = new double[points.size()][3];

                    if (points.size() < 3)
                    {
                        continue;
                    }

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
                    double eigenentropy = -1* (eigenValues[0]* Math.log(eigenValues[0])
                        + eigenValues[1]* Math.log(eigenValues[1])
                        + eigenValues[2]* Math.log(eigenValues[2]));
                    double changeOfCurvature = eigenValues[2]/(eigenValues[0] + eigenValues[1] + eigenValues[2]);

                    if (Double.isNaN(cl) ||
                        Double.isNaN(cp) ||
                        Double.isNaN(cs) ||
                        Double.isNaN(omnivariance) ||
                        Double.isNaN(anisotropy) ||
                        Double.isNaN(eigenentropy) ||
                        Double.isNaN(changeOfCurvature))
                    {
                    }
                    else
                    {
                        writer.write(cl
                                         + ","
                                         + cp
                                         + ","
                                         + cs
                                         + ","
                                         + anisotropy
                                         + ","
                                         + changeOfCurvature
                                         + ","
                                         + eigenentropy
                                         + ","
                                         + omnivariance
                                         + ","
                                         + regionPoint.getLabel()
                                         + System.lineSeparator());
                    }
                }
            }
            writer.close();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public static void GenerateFeaturesOptimal(String inputFileName, String outputFileName, long radius, int startIndex)
    {
        try
        {
            File file =
                new File(inputFileName);
            FileWriter writer =
                new FileWriter(outputFileName);
            Scanner sc = new Scanner(file);
            List<RegionPoint> regionPoints = new ArrayList<>();
            while (sc.hasNextLine())
            {
                String line = sc.nextLine();
                String input[] = line.split(" ");
                long xnorm = Long.parseLong(input[0]);
                long ynorm = Long.parseLong(input[1]);
                long znorm = Long.parseLong(input[2]);
                float x = Float.parseFloat(input[3]);
                float y = Float.parseFloat(input[4]);
                float z = Float.parseFloat(input[5]);
                int label = Integer.parseInt(input[6]);
                int isBoundary = Integer.parseInt(input[7]);
                if (label != 0)
                    regionPoints
                        .add(new RegionPoint(xnorm, ynorm, znorm, x, y, z, label, isBoundary));
            }

            Collections.sort(regionPoints, new Comparator<RegionPoint>()
            {
                @Override
                public int compare(RegionPoint o1, RegionPoint o2)
                {
                    return (int) (o1.getX() - o2.getX());
                }
            });

            writer.write("cl,cp,cs,anisotropy,changeOfCurvature,eigenentropy,omnivariance,label"+ System.lineSeparator());
            for (int i = startIndex; i < regionPoints.size(); i++)
            {
                RegionPoint regionPoint = regionPoints.get(i);
                if (regionPoint.getIsboundary() == 0)
                {
                    long startIndex_X =  regionPoint.getX() - radius;
                    long startIndex_Y =  regionPoint.getY() - radius;
                    long startIndex_Z =  regionPoint.getZ() - radius;

                    long endIndex_X = regionPoint.getX() + radius;
                    long endIndex_Y = regionPoint.getY() + radius;
                    long endIndex_Z = regionPoint.getZ() + radius;

                    List<Point> points = new ArrayList<Point>();
                    // lets check the Start and Endpoint of the Point as we
                    // Are searching for the Points
                    int index = Collections.binarySearch(regionPoints, regionPoint, new Comparator<RegionPoint>()
                    {
                        @Override
                        public int compare(RegionPoint o1, RegionPoint o2)
                        {
                            return (int) (o1.getX() - o2.getX());
                        }
                    });

                    int k = index;
                    RegionPoint rgp = regionPoints.get(k);
                    while(rgp.getX() <= endIndex_X && k < regionPoints.size())
                    {
                        if ((rgp.getX() >= startIndex_X && rgp.getX() <= endIndex_X)
                            && (rgp.getY() >= startIndex_Y && rgp.getY() <= endIndex_Y)
                            && (rgp.getZ() >= startIndex_Z && rgp.getZ() <= endIndex_Z))
                        {
                            points.add(new Point(rgp.getXo(),
                                                 rgp.getYo(),
                                                 rgp.getZo()));
                        }
                        k++;
                        if (k < regionPoints.size())
                            rgp = regionPoints.get(k);
                    }

                    k = index;
                    rgp = regionPoints.get(k);
                    while(rgp.getX() >= startIndex_X && k > 0)
                    {
                        if ((rgp.getX() >= startIndex_X && rgp.getX() <= endIndex_X)
                            && (rgp.getY() >= startIndex_Y && rgp.getY() <= endIndex_Y)
                            && (rgp.getZ() >= startIndex_Z && rgp.getZ() <= endIndex_Z))
                        {
                            points.add(new Point(rgp.getXo(),
                                                 rgp.getYo(),
                                                 rgp.getZo()));
                        }
                        k--;
                        if (k>=0)
                        rgp = regionPoints.get(k);
                    }

                    // Now Save the Points into the Cassandra as the List of Points if we want
                    // Else let us calculate the cs, cl, cp values...
                    double[][] matrix = new double[points.size()][3];

                    if (points.size() < 3)
                    {
                        continue;
                    }

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
                    double eigenentropy = -1* (eigenValues[0]* Math.log(eigenValues[0])
                        + eigenValues[1]* Math.log(eigenValues[1])
                        + eigenValues[2]* Math.log(eigenValues[2]));
                    double changeOfCurvature = eigenValues[2]/(eigenValues[0] + eigenValues[1] + eigenValues[2]);

                    if (Double.isNaN(cl) ||
                        Double.isNaN(cp) ||
                        Double.isNaN(cs) ||
                        Double.isNaN(omnivariance) ||
                        Double.isNaN(anisotropy) ||
                        Double.isNaN(eigenentropy) ||
                        Double.isNaN(changeOfCurvature))
                    {
                    }
                    else
                    {
                        writer.write(cl
                                         + ","
                                         + cp
                                         + ","
                                         + cs
                                         + ","
                                         + anisotropy
                                         + ","
                                         + changeOfCurvature
                                         + ","
                                         + eigenentropy
                                         + ","
                                         + omnivariance
                                         + ","
                                         + regionPoint.getLabel()
                                         + System.lineSeparator());
                    }
                }
            }
            writer.close();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}