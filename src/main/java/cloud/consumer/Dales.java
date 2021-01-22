package cloud.consumer;
import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.correlation.Covariance;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Dales
{
    public static void main2(String[]args)
    {
        //RemoveBoundary();
        //findMinMax();
        //PartitionTilesFeaturesIntoGrid obj = new PartitionTilesFeaturesIntoGrid();
        //obj.partitionFeatureSet("","");
        //NormalizePoint();
        SplitSpacev3();
    }

    public static void main(String[]args)
    {
        String folderName = "5110";
        String fileName = "5110_54320";
        ExecutorService executor= Executors
                .newFixedThreadPool(10);
        for (int i = 1; i <= 10; i++)
        {
            try
            {
                ///home/ubuntu/output/5110/parts

                String inputFileName = "/home/ubuntu/output/"
                        +folderName
                        +"/parts/"
                        + fileName+ "_part"
                        + Integer.toString(i)
                        +".txt";
                String outputFileName = "/home/ubuntu/output/"
                        +folderName
                        +"/feats/"
                        + fileName + "_feat_opt_sec"
                        + Integer.toString(i)
                        + ".txt";
                executor
                        .execute(new FeatureThread(inputFileName, outputFileName, 0));
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
        executor.shutdown();

        /*
        for (int i = 1; i <= 10 ; i++)
        {
            String inputFileName = "/Users/rpncmac2/Downloads/dales_txt/train/"
                    +folderName+"/parts/"
                    + fileName+ "_part"
                    + Integer.toString(i)
                    +".txt";

            String outputFileName = "/Users/rpncmac2/Downloads/dales_txt/train/"
                    +folderName+"/feat/"
                    + fileName + "_feat"
                    + Integer.toString(i)
                    + ".txt";
            GenerateFeaturesOptimalAvg(inputFileName, outputFileName, 0);
        }
        */
    }

    public static void findMinMax()
    {
        long startTime = System.nanoTime();
        String inputFile = "E:\\PCL_CLASSIFIER\\dales_semantic_segmentation_txt\\dales_txt\\test\\5080_54400.txt";
        String outputFile = "E:\\PCL_CLASSIFIER\\dales_semantic_segmentation_txt\\dales_txt\\test\\5080_54400_range.txt";

        File file =
                new File(inputFile);
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
                index++;
            }
        };
        long stopTime = System.nanoTime();
        System.out.println("***************************");
        System.out.print("Max X : "+ maxX + " ");
        System.out.print("Max Y : "+ maxY + " ");
        System.out.print("Max Z : "+ maxZ + " ");
        System.out.println("***************************");
        System.out.print("Min X : "+ minX + " ");
        System.out.print("Min Y : "+ minY + " ");
        System.out.print("Min Z : "+ minZ + " ");
        System.out.println("***************************");
        String time = ((stopTime - startTime)/1000000000) + " Seconds";
        try
        {
            FileWriter fileWriter = new FileWriter(outputFile);
            String str =  "Max X : "+ maxX + System.lineSeparator()
                    + "Max Y : "+ maxY + System.lineSeparator()
                    + "Max Z : "+ maxZ + System.lineSeparator()
                    + "Min X : "+ minX + System.lineSeparator()
                    + "Min Y : "+ minY + System.lineSeparator()
                    + "Min Z : "+ minZ + System.lineSeparator()
                    + System.lineSeparator()
                    + "Time Took To Process : "
                    + time
                    + System.lineSeparator()
                    + "Total Points : "
                    + index;

            fileWriter.write(str);
            fileWriter.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void NormalizePoint()
    {
        String line = "";
        int count = 0;
        try
        {
            long startTime = System.nanoTime();

            File file =
                    new File("/home/ubuntu/5110/5110_54320.txt");
            File file2 =
                    new File("/home/ubuntu/5110/5110_54320.labels");
            FileWriter writer =
                    new FileWriter("/home/ubuntu/5110/5110_54320_norm.txt");

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

            float xMin = 250.005f;
            float yMin = 250.015f;
            float zMin = 43.93f;
            while (sc.hasNextLine() && sc2.hasNextLine())
            {
                line = sc.nextLine();
                count++;
                String label = sc2.nextLine();

                String input[] = line.split(" ");

                if (input.length < 3) continue;

                float x = Float.parseFloat(input[0]);
                float y = Float.parseFloat(input[1]);

                float z = Float.parseFloat(input[2]);

                float xNorm = ((x - xMin));
                float yNorm = ((y - yMin));
                float zNorm = ((z - zMin));
                int l = Integer.parseInt(label);

                String str = xNorm + " " + yNorm + " " + zNorm + " " + x + " " + y + " " + z + " " + l;
                writer.write(str + System.lineSeparator());
                writer.flush();
            }

            long stopTime = System.nanoTime();
            String time = ((stopTime - startTime) / 1000000000) + " Seconds";
            System.out.println("DONE NORMALIZING POINTS in : " + time);
        }
        catch (Exception ex)
        {
           System.out.println("LINE : "+  line +"  Count :"+ count);
            ex.printStackTrace();
        }
    }

    public static void SplitSpacev2()
    {
        try
        {
            long startTime = System.nanoTime();
            float maxDiameter = 10f;

            float maxX = 500.01f;

            int maxNumberOfPartition = (int)(maxX/maxDiameter);

            int requiredPartition = 10;
            if (requiredPartition > maxNumberOfPartition)
            {
                throw new Exception("Partiton cannot be done");
            }

            FileWriter [] fileWriters = new FileWriter[10];
            String folderName = "5110";
            String fileName = "5110_54320";

            File file =
                    new File("/home/ubuntu/"
                            +folderName
                            +"/"
                            +fileName
                            +"_norm.txt");

            for (int i = 1; i <=10 ; i++)
            {
                FileWriter writer =
                        new FileWriter("/home/ubuntu/output/"
                                + folderName
                                + "/parts/"
                                + fileName
                                + "_part"
                                +String.valueOf(i)
                                +".txt");
                fileWriters[i-1] = writer;
            }

            Scanner sc = new Scanner(file);


            while (sc.hasNextLine())
            {
                String line = sc.nextLine();
                String input[] = line.split(" ");
                if (input.length < 7)
                {
                    System.out.println(line);
                }
                float xnorm = Float.parseFloat(input[0]);
                float ynorm = Float.parseFloat(input[1]);
                float znorm = Float.parseFloat(input[2]);
                float x = Float.parseFloat(input[3]);
                float y = Float.parseFloat(input[4]);
                float z = Float.parseFloat(input[5]);
                int label = Integer.parseInt(input[6]);

                float delta_x = 10L;

                if (xnorm <= (50 + delta_x))
                {
                    int isBoundary = 0;
                    if (xnorm > 50)
                    {
                        isBoundary = 1;
                    }
                    fileWriters[0]
                            .write(xnorm+" "+ynorm+" "+znorm+" "+x+" "+y+" "+z+" "+label+" "+isBoundary+ System.lineSeparator());
                    fileWriters[0].flush();
                }

                if (xnorm >= (50 - delta_x) &&  xnorm <= (100 + delta_x))
                {
                    int isBoundary = 0;
                    if (xnorm < 50 || xnorm > 100)
                    {
                        isBoundary = 1;
                    }
                    fileWriters[1]
                            .write(xnorm+" "+ynorm+" "+znorm+" "+x+" "+y+" "+z+" "+label+" "+isBoundary+ System.lineSeparator());
                    fileWriters[1].flush();
                }

                if (xnorm >= (100 - delta_x) &&  xnorm <= (150 + delta_x))
                {
                    int isBoundary = 0;
                    if (xnorm < 100 || xnorm > 150)
                    {
                        isBoundary = 1;
                    }
                    fileWriters[2]
                            .write(xnorm+" "+ynorm+" "+znorm+" "+x+" "+y+" "+z+" "+label+" "+isBoundary+ System.lineSeparator());
                    fileWriters[2].flush();
                }


                if (xnorm >= (150 - delta_x) &&  xnorm <= (200 + delta_x))
                {
                    int isBoundary = 0;
                    if (xnorm < 150 || xnorm > 200)
                    {
                        isBoundary = 1;
                    }
                    fileWriters[3]
                            .write(xnorm+" "+ynorm+" "+znorm+" "+x+" "+y+" "+z+" "+label+" "+isBoundary+ System.lineSeparator());
                    fileWriters[3].flush();
                }


                if (xnorm >= (200 - delta_x) &&  xnorm <= (250 + delta_x))
                {
                    int isBoundary = 0;
                    if (xnorm < 200 || xnorm > 250)
                    {
                        isBoundary = 1;
                    }
                    fileWriters[4]
                            .write(xnorm+" "+ynorm+" "+znorm+" "+x+" "+y+" "+z+" "+label+" "+isBoundary+ System.lineSeparator());
                    fileWriters[4].flush();
                }


                if (xnorm >= (250 - delta_x) &&  xnorm <= (300 + delta_x))
                {
                    int isBoundary = 0;
                    if (xnorm < 250 || xnorm > 300)
                    {
                        isBoundary = 1;
                    }
                    fileWriters[5]
                            .write(xnorm+" "+ynorm+" "+znorm+" "+x+" "+y+" "+z+" "+label+" "+isBoundary+ System.lineSeparator());
                    fileWriters[5].flush();
                }

                if (xnorm >= (300 - delta_x) &&  xnorm <= (350 + delta_x))
                {
                    int isBoundary = 0;
                    if (xnorm < 300 || xnorm > 350)
                    {
                        isBoundary = 1;
                    }
                    fileWriters[6]
                            .write(xnorm+" "+ynorm+" "+znorm+" "+x+" "+y+" "+z+" "+label+" "+isBoundary+ System.lineSeparator());
                    fileWriters[6].flush();
                }

                if (xnorm >= (350 - delta_x) &&  xnorm <= (400 + delta_x))
                {
                    int isBoundary = 0;
                    if (xnorm < 350 || xnorm > 400)
                    {
                        isBoundary = 1;
                    }
                    fileWriters[7]
                            .write(xnorm+" "+ynorm+" "+znorm+" "+x+" "+y+" "+z+" "+label+" "+isBoundary+ System.lineSeparator());
                    fileWriters[7].flush();
                }

                if (xnorm >= (400 - delta_x) &&  xnorm <= (450 + delta_x))
                {
                    int isBoundary = 0;
                    if (xnorm < 400 || xnorm > 450)
                    {
                        isBoundary = 1;
                    }
                    fileWriters[8]
                            .write(xnorm+" "+ynorm+" "+znorm+" "+x+" "+y+" "+z+" "+label+" "+isBoundary+ System.lineSeparator());
                    fileWriters[8].flush();
                }

                if (xnorm >= (450 - delta_x) &&  xnorm <= (501 + delta_x))
                {
                    int isBoundary = 0;
                    if (xnorm < 450 || xnorm > 501)
                    {
                        isBoundary = 1;
                    }
                    fileWriters[9]
                            .write(xnorm+" "+ynorm+" "+znorm+" "+x+" "+y+" "+z+" "+label+" "+isBoundary+ System.lineSeparator());
                    fileWriters[9].flush();
                }
            }

            for(int i = 0; i < 10; i++)
            {
                fileWriters[i].close();
            }

            long stopTime = System.nanoTime();
            String time = ((stopTime - startTime) / 1000000000) + " Seconds";
            System.out.println("DONE SPACE SPLITTING in : " + time);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void SplitSpacev3()
    {
        try
        {
            long startTime = System.nanoTime();
            float maxDiameter = 10f;

            float maxX = 500.03f;

            int maxNumberOfPartition = (int)(maxX/maxDiameter);

            int requiredPartition = 10;
            if (requiredPartition > maxNumberOfPartition)
            {
                throw new Exception("Partiton cannot be done");
            }

            float delta_x = maxDiameter;
            float partitionSize = 50L;

            // Partition Range to Search
            FileWriter [] fileWriters = new FileWriter[10];
            String folderName = "5110";
            String fileName = "5110_54320";

            File file =
                    new File("/home/ubuntu/"
                            +folderName
                            +"/"
                            +fileName
                            +"_norm.txt");

            for (int i = 1; i <= requiredPartition ; i++)
            {
                FileWriter writer =
                        new FileWriter("/home/ubuntu/output/"
                                + folderName
                                + "/parts/"
                                + fileName
                                + "_part"
                                + Integer.toString(i)
                                + ".txt");
                fileWriters[i-1] = writer;
            }

            Scanner sc = new Scanner(file);

            while (sc.hasNextLine())
            {
                String line = sc.nextLine();
                String input[] = line.split(" ");
                if (input.length < 7)
                {
                    System.out.println(line);
                }
                float xnorm = Float.parseFloat(input[0]);
                float ynorm = Float.parseFloat(input[1]);
                float znorm = Float.parseFloat(input[2]);
                float x = Float.parseFloat(input[3]);
                float y = Float.parseFloat(input[4]);
                float z = Float.parseFloat(input[5]);
                int label = Integer.parseInt(input[6]);

                for (int i = 1; i <= requiredPartition; i++)
                {
                    float check1 = ((partitionSize * (i - 1)) - delta_x);
                    float check2 = ((partitionSize * i) + delta_x);
                    float boundaryCheck1 = partitionSize * (i - 1);
                    float boundaryCheck2 = partitionSize * i;

                    if (i == 1)
                    {
                        if (xnorm <= check2)
                        {
                            int isBoundary = 0;
                            if (xnorm > boundaryCheck2)
                            {
                                isBoundary = 1;
                            }
                            fileWriters[i - 1]
                                    .write(xnorm + " " + ynorm + " " + znorm + " " + x + " " + y + " " + z + " " + label + " " + isBoundary + System.lineSeparator());
                            fileWriters[i - 1].flush();
                        }
                    }
                    else
                    {
                        if (i == requiredPartition)
                        {
                            boundaryCheck2 += 2;
                        }

                        if (xnorm >= check1 && xnorm <= check2)
                        {
                            int isBoundary = 0;
                            if (xnorm < boundaryCheck1 || xnorm > boundaryCheck2)
                            {
                                isBoundary = 1;
                            }
                            fileWriters[i - 1]
                                    .write(xnorm + " " + ynorm + " " + znorm + " " + x + " " + y + " " + z + " " + label + " " + isBoundary + System.lineSeparator());
                            fileWriters[i - 1].flush();
                        }
                    }
                }
            }

            for(int i = 0; i < requiredPartition; i++)
            {
                fileWriters[i].close();
            }

            long stopTime = System.nanoTime();
            String time = ((stopTime - startTime) / 1000000000) + " Seconds";
            System.out.println("DONE SPACE SPLITTING in : " + time);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void RemoveBoundary()
    {
        String fileName = "E:\\PCL_CLASSIFIER\\Profex\\feat5080\\5080_54470_feat_sec4.txt";
        String outputFileName = "E:\\PCL_CLASSIFIER\\Profex\\feat5080\\5080_54470_feat_sec4_wb.txt";
        File file =
            new File(fileName);

        Scanner sc = null;
        try
        {
            sc = new Scanner(file);
            FileWriter writer =
                new FileWriter(outputFileName);
            writer.write("x,y,z,cl,cp,cs,anisotropy,changeOfCurvature," +
                             "eigenentropy,omnivariance,sumOfEigenValues," +
                             "optimalRadius,hdiff,hsd,density,label"+
                             System.lineSeparator());
            sc.nextLine();
            while (sc.hasNextLine())
            {
                String line = sc.nextLine();
                String input[] = line.split(",");

                float x = Float.parseFloat(input[0]);
                float y = Float.parseFloat(input[1]);
                float z = Float.parseFloat(input[2]);
                float cl = Float.parseFloat(input[3]);
                float cp = Float.parseFloat(input[4]);
                float cs = Float.parseFloat(input[5]);
                float anisotropy = Float.parseFloat(input[6]);
                float changeOfCurvature = Float.parseFloat(input[7]);
                float eigenentropy = Float.parseFloat(input[8]);
                float omnivariance = Float.parseFloat(input[9]);
                float sumOfEigenValues = Float.parseFloat(input[10]);
                float optimalRadius = Float.parseFloat(input[11]);
                float hdiff = Float.parseFloat(input[12]);
                float hsd = Float.parseFloat(input[13]);
                float density = Float.parseFloat(input[14]);
                int label = Integer.parseInt(input[15]);
                // it is inside boundary
                if ((Math.abs(500.04 - x) <= 2.0) || (Math.abs(500.03 - y) <= 2.0))
                {

                }
                else
                {
                    writer.write( x
                                      + ","
                                      + y
                                      + ","
                                      + z
                                      + ","
                                      + cl
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
                                      + sumOfEigenValues
                                      + ","
                                      + optimalRadius
                                      + ","
                                      + hdiff
                                      + ","
                                      + hsd
                                      + ","
                                      + density
                                      + ","
                                      + label
                                      + System.lineSeparator());

                    writer.flush();
                }
            }
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
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

            writer.write("cl,cp,cs,anisotropy,changeOfCurvature,eigenentropy,omnivariance,height,label"+ System.lineSeparator());
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
                    float avgHeight = rgp.getZo();
                    while(rgp.getX() <= endIndex_X && k < regionPoints.size())
                    {
                        if ((rgp.getX() >= startIndex_X && rgp.getX() <= endIndex_X)
                            && (rgp.getY() >= startIndex_Y && rgp.getY() <= endIndex_Y)
                            && (rgp.getZ() >= startIndex_Z && rgp.getZ() <= endIndex_Z))
                        {
                            points.add(new Point(rgp.getXo(),
                                                 rgp.getYo(),
                                                 rgp.getZo()));

                            avgHeight += rgp.getZo();
                        }
                        k++;
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
                            avgHeight += rgp.getZo();
                        }
                        k--;
                        rgp = regionPoints.get(k);
                    }

                    avgHeight = avgHeight/points.size();
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
                                         + avgHeight
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

    public static void GenerateFeaturesOptimalAvg(String inputFileName, String outputFileName, int startIndex)
    {
        try
        {
            File file =
                new File(inputFileName);
            FileWriter writer =
                new FileWriter(outputFileName);
            Scanner sc = new Scanner(file);
            List<RegionPointv2> regionPoints = new ArrayList<>();
            while (sc.hasNextLine())
            {
                String line = sc.nextLine();
                String input[] = line.split(" ");
                float xnorm = Float.parseFloat(input[0]);
                float ynorm = Float.parseFloat(input[1]);
                float znorm = Float.parseFloat(input[2]);
                float x = Float.parseFloat(input[3]);
                float y = Float.parseFloat(input[4]);
                float z = Float.parseFloat(input[5]);
                int label = Integer.parseInt(input[6]);
                int isBoundary = Integer.parseInt(input[7]);
                if (label != 0)
                    regionPoints
                        .add(new RegionPointv2(xnorm, ynorm, znorm, x, y, z, label, isBoundary));
            }

            Collections.sort(regionPoints, new Comparator<RegionPointv2>()
            {
                @Override
                public int compare(RegionPointv2 o1, RegionPointv2 o2)
                {
                    if (o1.getX() > o2.getX())
                        return 1;
                    else
                    {
                        if (o1.getX() == o2.getX())
                            return 0;
                        else
                            return -1;
                    }
                }
            });

            writer.write("x,y,z,cl,cp,cs,anisotropy,changeOfCurvature,eigenentropy,omnivariance,height,label"+ System.lineSeparator());
            for (int i = startIndex; i < regionPoints.size(); i++)
            {
                RegionPointv2 regionPoint = regionPoints.get(i);
                if (regionPoint.getIsboundary() == 0)
                {
                    int index = Collections.binarySearch(regionPoints, regionPoint, new Comparator<RegionPointv2>()
                    {
                        @Override
                        public int compare(RegionPointv2 o1, RegionPointv2 o2)
                        {
                            if (o1.getX() > o2.getX())
                                return 1;
                            else
                            {
                                if (o1.getX() == o2.getX())
                                    return 0;
                                else
                                    return -1;
                            }
                        }
                    });

                    //Features
                    double cl = 0;
                    double cp = 0;
                    double cs = 0;
                    double omnivariance = 0;
                    double anisotropy = 0;
                    double eigenentropy = 0;
                    double changeOfCurvature = 0;
                    float avgHeight =0;

                    float[] radi = new float[3];
                    radi[0] = 0.5f;
                    radi[1] = 0.7f;
                    radi[2] = 0.9f;
                    for(int j = 0; j < radi.length; j++)
                    {
                        float startIndex_X = regionPoint.getX() - radi[j];
                        float startIndex_Y = regionPoint.getY() - radi[j];
                        float startIndex_Z = regionPoint.getZ() - radi[j];

                        float endIndex_X = regionPoint.getX() + radi[j];
                        float endIndex_Y = regionPoint.getY() + radi[j];
                        float endIndex_Z = regionPoint.getZ() + radi[j];

                        List<Point> points = new ArrayList<Point>();
                        // lets check the Start and Endpoint of the Point as we
                        // Are searching for the Points
                        int k = index;
                        RegionPointv2 rgp = regionPoints.get(k);
                        avgHeight = rgp.getZo();
                        while (rgp.getX() <= endIndex_X && k < regionPoints.size())
                        {
                            if ((rgp.getX() >= startIndex_X && rgp.getX() <= endIndex_X)
                                && (rgp.getY() >= startIndex_Y && rgp.getY() <= endIndex_Y)
                                && (rgp.getZ() >= startIndex_Z && rgp.getZ() <= endIndex_Z))
                            {
                                points.add(new Point(
                                    rgp.getXo(),
                                    rgp.getYo(),
                                    rgp.getZo()
                                ));

                                avgHeight += rgp.getZo();
                            }
                            k++;
                            rgp = regionPoints.get(k);
                        }

                        k = index;
                        rgp = regionPoints.get(k);
                        while (rgp.getX() >= startIndex_X && k > 0)
                        {
                            if ((rgp.getX() >= startIndex_X && rgp.getX() <= endIndex_X)
                                && (rgp.getY() >= startIndex_Y && rgp.getY() <= endIndex_Y)
                                && (rgp.getZ() >= startIndex_Z && rgp.getZ() <= endIndex_Z))
                            {
                                points.add(new Point(
                                    rgp.getXo(),
                                    rgp.getYo(),
                                    rgp.getZo()
                                ));
                                avgHeight += rgp.getZo();
                            }
                            k--;
                            rgp = regionPoints.get(k);
                        }

                        avgHeight += avgHeight / points.size();
                        // Now Save the Points into the Cassandra as the List of Points if we want
                        // Else let us calculate the cs, cl, cp values...
                        double[][] matrix = new double[points.size()][3];

                        if (points.size() < 3)
                        {
                            continue;
                        }

                        int matrixIdx = 0;
                        for (Point neighbour : points)
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
                        cl += ((eigenValues[0] - eigenValues[1]) / eigenValues[0]);
                        cp += ((eigenValues[1] - eigenValues[2]) / eigenValues[0]);
                        cs += (eigenValues[2] / eigenValues[0]);
                        omnivariance += Math.cbrt(eigenValues[0] * eigenValues[1] * eigenValues[2]);
                        anisotropy += ((eigenValues[0] - eigenValues[2]) / eigenValues[0]);
                        eigenentropy += (-1 * (eigenValues[0] * Math.log(eigenValues[0])
                            + eigenValues[1] * Math.log(eigenValues[1])
                            + eigenValues[2] * Math.log(eigenValues[2])));
                        changeOfCurvature += (eigenValues[2] / (eigenValues[0] + eigenValues[1] + eigenValues[2]));
                    }

                    if (!(Double.isNaN(cl) ||
                        Double.isNaN(cp) ||
                        Double.isNaN(cs) ||
                        Double.isNaN(omnivariance) ||
                        Double.isNaN(anisotropy) ||
                        Double.isNaN(eigenentropy) ||
                        Double.isNaN(changeOfCurvature) ||
                        cl == 0 ||
                        cp == 0 ||
                        cs == 0)
                    )
                    {
                        cl = cl/3;
                        cp = cp/3;
                        cs = cs /3;
                        anisotropy = anisotropy/3;
                        changeOfCurvature = changeOfCurvature/3;
                        eigenentropy = eigenentropy/3;
                        omnivariance = omnivariance/3;
                        avgHeight = avgHeight/3;

                        writer.write( regionPoint.getXo()
                                          + ","
                                          + regionPoint.getYo()
                                          + ","
                                          + regionPoint.getZo()
                                          + ","
                                          + cl
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
                                          + avgHeight
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

    public static void GenerateFeaturesOptimalScale(String inputFileName, String outputFileName, int startIndex)
    {
        try
        {
            File file =
                new File(inputFileName);
            FileWriter writer =
                new FileWriter(outputFileName);
            Scanner sc = new Scanner(file);
            List<RegionPointv2> regionPoints = new ArrayList<>();
            while (sc.hasNextLine())
            {
                String line = sc.nextLine();
                String input[] = line.split(" ");
                float xnorm = Float.parseFloat(input[0]);
                float ynorm = Float.parseFloat(input[1]);
                float znorm = Float.parseFloat(input[2]);
                float x = Float.parseFloat(input[3]);
                float y = Float.parseFloat(input[4]);
                float z = Float.parseFloat(input[5]);
                int label = Integer.parseInt(input[6]);
                int isBoundary = Integer.parseInt(input[7]);
                if (label != 0)
                    regionPoints
                        .add(new RegionPointv2(xnorm, ynorm, znorm, x, y, z, label, isBoundary));
            }

            Collections.sort(regionPoints, new Comparator<RegionPointv2>()
            {
                @Override
                public int compare(RegionPointv2 o1, RegionPointv2 o2)
                {
                    if (o1.getX() > o2.getX())
                        return 1;
                    else
                    {
                        if (o1.getX() == o2.getX())
                            return 0;
                        else
                            return -1;
                    }
                }
            });

            writer.write("x,y,z,cl,cp,cs,anisotropy,changeOfCurvature,eigenentropy,omnivariance,sumOfEigenValues,optimalRadius,label"+ System.lineSeparator());
            for (int i = startIndex; i < regionPoints.size(); i++)
            {
                RegionPointv2 regionPoint = regionPoints.get(i);
                if (regionPoint.getIsboundary() == 0)
                {
                    int index = Collections.binarySearch(regionPoints, regionPoint, new Comparator<RegionPointv2>()
                    {
                        @Override
                        public int compare(RegionPointv2 o1, RegionPointv2 o2)
                        {
                            if (o1.getX() > o2.getX())
                                return 1;
                            else
                            {
                                if (o1.getX() == o2.getX())
                                    return 0;
                                else
                                    return -1;
                            }
                        }
                    });

                    //Features
                    double cl = 0;
                    double cp = 0;
                    double cs = 0;
                    double omnivariance = 0;
                    double anisotropy = 0;
                    double eigenentropy = 0;
                    double changeOfCurvature = 0;
                    double sumOfEigenValues = 0;
                    float avgHeight =0;

                    float[] radi = new float[]
                        {
                            0.2f,
                            0.3f,
                            0.4f,
                            0.5f,
                            0.6f,
                            0.8f,
                            0.9f,
                            1.0f,
                            1.1f,
                            1.2f,
                            1.3f,
                            1.4f,
                            1.5f,
                            1.6f,
                            1.7f,
                            1.8f,
                            1.9f,
                            2.0f,
                            0f
                        };

                    float optimalRadius = 0;
                    double lastEigenentropy = 0;
                    boolean isOptimalScale = false;
                    for(int j = 0; j < radi.length; j++)
                    {
                        if (radi[j] == 0)
                        {
                            isOptimalScale = true;
                            radi[j] = optimalRadius;
                        }

                        float startIndex_X = regionPoint.getX() - radi[j];
                        float startIndex_Y = regionPoint.getY() - radi[j];
                        float startIndex_Z = regionPoint.getZ() - radi[j];

                        float endIndex_X = regionPoint.getX() + radi[j];
                        float endIndex_Y = regionPoint.getY() + radi[j];
                        float endIndex_Z = regionPoint.getZ() + radi[j];

                        List<Point> points = new ArrayList<Point>();
                        // lets check the Start and Endpoint of the Point as we
                        // Are searching for the Points
                        int k = index;
                        RegionPointv2 rgp = regionPoints.get(k);
                        avgHeight = rgp.getZo();
                        while (rgp.getX() <= endIndex_X && k < regionPoints.size())
                        {
                            if ((rgp.getX() >= startIndex_X && rgp.getX() <= endIndex_X)
                                && (rgp.getY() >= startIndex_Y && rgp.getY() <= endIndex_Y)
                                && (rgp.getZ() >= startIndex_Z && rgp.getZ() <= endIndex_Z))
                            {
                                points.add(new Point(
                                    rgp.getXo(),
                                    rgp.getYo(),
                                    rgp.getZo()
                                ));

                                avgHeight += rgp.getZo();
                            }
                            k++;
                            rgp = regionPoints.get(k);
                        }

                        k = index;
                        rgp = regionPoints.get(k);
                        while (rgp.getX() >= startIndex_X && k > 0)
                        {
                            if ((rgp.getX() >= startIndex_X && rgp.getX() <= endIndex_X)
                                && (rgp.getY() >= startIndex_Y && rgp.getY() <= endIndex_Y)
                                && (rgp.getZ() >= startIndex_Z && rgp.getZ() <= endIndex_Z))
                            {
                                points.add(new Point(
                                    rgp.getXo(),
                                    rgp.getYo(),
                                    rgp.getZo()
                                ));
                                avgHeight += rgp.getZo();
                            }
                            k--;
                            rgp = regionPoints.get(k);
                        }

                        avgHeight = avgHeight / points.size();
                        // Now Save the Points into the Cassandra as the List of Points if we want
                        // Else let us calculate the cs, cl, cp values...
                        double[][] matrix = new double[points.size()][3];

                        if (points.size() < 3)
                        {
                            continue;
                        }

                        int matrixIdx = 0;
                        for (Point neighbour : points)
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
                        Arrays.sort(eigenValues);
                        // lambda1 >= lambda2 >= lambda3 >= 0
                        double lambda1 = eigenValues[2];
                        double lambda2 = eigenValues[1];
                        double lambda3 = eigenValues[0];

                        //Features
                        if (isOptimalScale)
                        {
                            cl = ((lambda1 - lambda2) / lambda1);
                            cp = ((lambda2 - lambda3) / lambda1);
                            cs = (lambda3 / lambda1);
                            omnivariance = Math.cbrt(lambda1 * lambda2 * lambda3);
                            anisotropy = ((lambda1  - lambda3) / lambda1);
                            eigenentropy = -1 * (lambda1 * Math.log(lambda1)
                                + lambda2 * Math.log(lambda2)
                                + lambda3 * Math.log(lambda3));
                            sumOfEigenValues = lambda1 + lambda2 + lambda3;
                            changeOfCurvature = (lambda3 / (lambda1 + lambda2 + lambda3));
                        }
                        else
                        {
                            // Shannon Entropy
                            sumOfEigenValues = lambda1 + lambda2 + lambda3;
                            double e1 = lambda1/sumOfEigenValues;
                            double e2 = lambda2/sumOfEigenValues;
                            double e3 = lambda3/sumOfEigenValues;

                            eigenentropy = -1 * (e1 * Math.log(e1)
                                + e2 * Math.log(e2)
                                + e3 * Math.log(e3));

                            if (lastEigenentropy == 0)
                            {
                                lastEigenentropy = eigenentropy;
                                optimalRadius = radi[j];
                            }
                            else if (eigenentropy <= lastEigenentropy)
                            {
                                lastEigenentropy = eigenentropy;
                                optimalRadius = radi[j];
                            }
                        }
                    }

                    if (!(Double.isNaN(cl) ||
                        Double.isNaN(cp) ||
                        Double.isNaN(cs) ||
                        Double.isNaN(omnivariance) ||
                        Double.isNaN(anisotropy) ||
                        Double.isNaN(eigenentropy) ||
                        Double.isNaN(changeOfCurvature) ||
                        cl == 0 ||
                        cp == 0 ||
                        cs == 0)
                    )
                    {
                        writer.write( regionPoint.getXo()
                                          + ","
                                          + regionPoint.getYo()
                                          + ","
                                          + regionPoint.getZo()
                                          + ","
                                          + cl
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
                                          + sumOfEigenValues
                                          + ","
                                          + optimalRadius
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
