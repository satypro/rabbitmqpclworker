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
    public static void main(String[]args)
    {
        //findMinMax();
        // NormalizePointsv2();
        //SplitSpacev2();

        String folderName = "5080";
        String fileName = "5080_54435";
        ExecutorService executor= Executors
                .newFixedThreadPool(15);

        for (int i = 1; i <= 10; i++)
        {
            try
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
        String inputFile = "/Users/rpncmac2/Downloads/dales_txt/test/5150/5150_54325.txt";
        String outputFile = "/Users/rpncmac2/Downloads/dales_txt/test/5150/5150_54325_range.txt";

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

    public static void NormalizePoints()
    {
        File file =
            new File("/Users/rpncmac2/Downloads/dales_txt/train/5080/5080_54435.txt");
        File file2 =
            new File("/Users/rpncmac2/Downloads/dales_txt/train/5080/5080_54435.labels");
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
        float xMin = 250.025f;
        float yMin = 250.02f;
        float zMin = 24.19f;
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
            int l = Integer.parseInt(label);

            lines.add(xNorm+" "+yNorm+" "+zNorm+" "+x+" "+y+" "+z+" "+l);
            index++;
        }

        FileWriter writer = null;
        try
        {
            writer = new FileWriter("E:\\PCL_CLASSIFIER\\dales_data\\5150\\normalized.txt");
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

    public static void NormalizePointsv2()
    {
        try
        {
            long startTime = System.nanoTime();

            File file =
                    new File("/Users/rpncmac2/Downloads/dales_txt/test/5150/5150_54325.txt");
            File file2 =
                    new File("/Users/rpncmac2/Downloads/dales_txt/test/5150/5150_54325.labels");
            FileWriter writer =
                    new FileWriter("/Users/rpncmac2/Downloads/dales_txt/test/5150/5150_54325_norm.txt");

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

            float xMin = 250.015f;
            float yMin = 250.02f;
            float zMin = 29.325f;
            while (sc.hasNextLine() && sc2.hasNextLine())
            {
                String line = sc.nextLine();
                String label = sc2.nextLine();
                String input[] = line.split(" ");

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
            ex.printStackTrace();
        }
    }

    public static void SplitSpace()
    {
        try
        {
            File file =
                new File("E:\\PCL_CLASSIFIER\\dales_data\\5150\\normalized.txt");
            FileWriter writer1 =
                new FileWriter("E:\\PCL_CLASSIFIER\\dales_data\\5150\\com_part1.txt");
            FileWriter writer2 =
                new FileWriter("E:\\PCL_CLASSIFIER\\dales_data\\5150\\com_part2.txt");
            FileWriter writer3 =
                new FileWriter("E:\\PCL_CLASSIFIER\\dales_data\\5150\\com_part3.txt");
            FileWriter writer4 =
                new FileWriter("E:\\PCL_CLASSIFIER\\dales_data\\5150\\com_part4.txt");
            FileWriter writer5 =
                new FileWriter("E:\\PCL_CLASSIFIER\\dales_data\\5150\\com_part5.txt");

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
                if (xnorm <= (100000 + delta_x))
                {
                    int isBoundary = 0;
                    if (xnorm > 100000)
                    {
                        isBoundary = 1;
                    }
                    writer1
                        .write(xnorm+" "+ynorm+" "+znorm+" "+x+" "+y+" "+z+" "+label+" "+isBoundary+ System.lineSeparator());
                }

                if (xnorm >= (100000 - delta_x) &&  xnorm <= (200000 + delta_x))
                {
                    int isBoundary = 0;
                    if (xnorm < 100000 || xnorm > 200000)
                    {
                        isBoundary = 1;
                    }
                    writer2
                        .write(xnorm+" "+ynorm+" "+znorm+" "+x+" "+y+" "+z+" "+label+" "+isBoundary+ System.lineSeparator());
                }

                if (xnorm >= (200000 - delta_x) &&  xnorm <= (300000 + delta_x))
                {
                    int isBoundary = 0;
                    if (xnorm < 200000 || xnorm > 300000)
                    {
                        isBoundary = 1;
                    }
                    writer3
                        .write(xnorm+" "+ynorm+" "+znorm+" "+x+" "+y+" "+z+" "+label+" "+isBoundary+ System.lineSeparator());
                }

                if (xnorm >= (300000 - delta_x) &&  xnorm <= (400000 + delta_x))
                {
                    int isBoundary = 0;
                    if (xnorm < 300000 || xnorm > 400000)
                    {
                        isBoundary = 1;
                    }
                    writer4
                        .write(xnorm+" "+ynorm+" "+znorm+" "+x+" "+y+" "+z+" "+label+" "+isBoundary+ System.lineSeparator());
                }

                if (xnorm >= (400000 - delta_x) &&  xnorm <= (500000 + delta_x))
                {
                    int isBoundary = 0;
                    if (xnorm < 400000)
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

    public static void SplitSpacev2()
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

            FileWriter [] fileWriters = new FileWriter[10];
            String folderName = "";
            String fileName = "";

            File file =
                    new File("/Users/rpncmac2/Downloads/dales_txt/train/"
                            +folderName
                            +"/"
                            +fileName
                            +"_norm.txt");

            for (int i = 1; i <=10 ; i++)
            {
                FileWriter writer =
                        new FileWriter("/Users/rpncmac2/Downloads/dales_txt/train/"
                                + folderName
                                + "/parts/"
                                + fileName
                                + "_part1.txt");
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

                if (xnorm >= (50 - delta_x) &&  xnorm <= (10 + delta_x))
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

            float delta_x = 10L;
            float partitionSize = 50L;

            // Partition Range to Search
            FileWriter [] fileWriters = new FileWriter[10];
            String folderName = "";
            String fileName = "";

            File file =
                    new File("/Users/rpncmac2/Downloads/dales_txt/train/"
                            +folderName
                            +"/"
                            +fileName
                            +"_norm.txt");

            for (int i = 1; i <= requiredPartition ; i++)
            {
                FileWriter writer =
                        new FileWriter("/Users/rpncmac2/Downloads/dales_txt/train/"
                                + folderName
                                + "/parts/"
                                + fileName
                                + "_part1.txt");
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

    public static void fixfile(String inputFileName, String outputFileName)
    {
        try
        {
            File file =
                    new File(inputFileName);
            FileWriter writer =
                    new FileWriter(outputFileName);
            Scanner sc = new Scanner(file);

            String line = sc.nextLine();
            writer.write("x,y,z,cl,cp,cs,anisotropy,changeOfCurvature,eigenentropy,omnivariance,height,label"+ System.lineSeparator());
            while (sc.hasNextLine())
            {
                line = sc.nextLine();
                String input[] = line.split(",");
                String stt[] = input[0].split(" ");
                float x = Float.parseFloat(stt[0]);
                float y = Float.parseFloat(stt[1]);
                float z = Float.parseFloat(stt[2]);
                float cl = Float.parseFloat(stt[3]);

                float cp = Float.parseFloat(input[1]);
                float cs = Float.parseFloat(input[2]);
                float anisotropy = Float.parseFloat(input[3]);
                float changeOfCurvature = Float.parseFloat(input[4]);
                float eigenentropy = Float.parseFloat(input[5]);
                float omnivariance = Float.parseFloat(input[6]);
                float avgHeight = Float.parseFloat(input[7]);
                int label = Integer.parseInt(input[8]);

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
                        + avgHeight
                        + ","
                        + label
                        + System.lineSeparator());
                writer.flush();
            }
            writer.close();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}