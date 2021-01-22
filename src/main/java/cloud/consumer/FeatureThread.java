package cloud.consumer;
import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.correlation.Covariance;
import java.io.File;
import java.io.FileWriter;
import java.util.*;

public class FeatureThread implements Runnable
{
    String inputFileName;
    String outputFileName;
    int startIndex;

    public FeatureThread(String inputFileName, String outputFileName, int startIndex)
    {
        this.inputFileName = inputFileName;
        this.outputFileName = outputFileName;
        this.startIndex = startIndex;
    }

    @Override
    public void run()
    {
        optimalScale_Original();
        //multiScaleAverage_Original();
    }

    public void optimalScale_Original()
    {
        try
        {
            System.out.println(inputFileName);
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

            writer.write("x,y,z,cl,cp,cs,anisotropy,changeOfCurvature," +
                             "eigenentropy,omnivariance,sumOfEigenValues," +
                             "optimalRadius,hdiff,hsd,density,label"+
                             System.lineSeparator());

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
                    float heightDiff = 0;
                    float heightStandardDeviation = 0;
                    float pointDensity = 0;
                    /*Feature Set Ends here*/
                    float maxHeight = 0;
                    float minHeight = 0;
                    float heightSum = 0;
                    float heightAvg = 0;
                    /*
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
                    */
                    float[] radi = new float[]
                    {
                        0.2f,
                        0.4f,
                        0.6f,
                        0.8f,
                        1.0f,
                        1.2f,
                        1.4f,
                        1.6f,
                        1.8f,
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
                        maxHeight = rgp.getZo();
                        minHeight = rgp.getZo();
                        heightSum = rgp.getZo();
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

                                if (rgp.getZo() >= maxHeight)
                                {
                                    maxHeight = rgp.getZo();
                                }

                                if (rgp.getZo() <= minHeight)
                                {
                                    minHeight = rgp.getZo();
                                }

                                heightSum += rgp.getZo();
                            }
                            k++;
                            if (k < regionPoints.size())
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

                                if (rgp.getZo() >= maxHeight)
                                {
                                    maxHeight = rgp.getZo();
                                }

                                if (rgp.getZo() <= minHeight)
                                {
                                    minHeight = rgp.getZo();
                                }
                                heightSum += rgp.getZo();
                            }
                            k--;
                            if ( k >= 0)
                                rgp = regionPoints.get(k);
                        }

                        // Now Save the Points into the Cassandra as the List of Points if we want
                        // Else let us calculate the cs, cl, cp values...
                        double[][] matrix = new double[points.size()][3];

                        if (points.size() < 3)
                        {
                            continue;
                        }
                        heightAvg = (heightSum/points.size());

                        int matrixIdx = 0;
                        for (Point neighbour : points)
                        {
                            matrix[matrixIdx][0] = neighbour.getX();
                            matrix[matrixIdx][1] = neighbour.getY();
                            matrix[matrixIdx][2] = neighbour.getZ();
                            matrixIdx++;

                            heightStandardDeviation += ((neighbour.getZ()  - heightAvg)*(neighbour.getZ()  - heightAvg));
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

                        sumOfEigenValues = lambda1 + lambda2 + lambda3;
                        //Features
                        if (isOptimalScale)
                        {
                            cl = ((lambda1 - lambda2) / lambda1);
                            cp = ((lambda2 - lambda3) / lambda1);
                            cs = (lambda3 / lambda1);
                            omnivariance = Math.cbrt(lambda1 * lambda2 * lambda3);
                            anisotropy = ((lambda1  - lambda3) / lambda1);
                            //sumOfEigenValues = lambda1 + lambda2 + lambda3;
                            changeOfCurvature = (lambda3 / (lambda1 + lambda2 + lambda3));
                            eigenentropy = -1 * (lambda1 * Math.log(lambda1)
                                + lambda2 * Math.log(lambda2)
                                + lambda3 * Math.log(lambda3));
                            heightDiff = maxHeight - minHeight;
                            pointDensity = (points.size()/(radi[j]*radi[j]*radi[j]));
                            heightStandardDeviation = (float) Math.sqrt((heightStandardDeviation/points.size()));
                        }
                        else
                        {
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
                                          + heightDiff
                                          + ","
                                          + heightStandardDeviation
                                          + ","
                                          + pointDensity
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

    public void multiScaleAverage_Original()
    {
        try
        {
            System.out.println(inputFileName);
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

            writer.write("x,y,z,cl,cp,cs,anisotropy,changeOfCurvature," +
                             "eigenentropy,omnivariance,sumOfEigenValues," +
                             "optimalRadius,hdiff,hsd,density,label"+
                             System.lineSeparator());

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
                    float heightDiff = 0;
                    float heightStandardDeviation = 0;
                    float pointDensity = 0;
                    /*Feature Set Ends here*/
                    float maxHeight = 0;
                    float minHeight = 0;
                    float heightSum = 0;
                    float heightAvg = 0;
                    float[] radi = new float[]
                        {
                            0.5f,
                            0.7f,
                            0.9f
                        };

                    float optimalRadius = 0;
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
                        maxHeight = rgp.getZo();
                        minHeight = rgp.getZo();
                        heightSum = rgp.getZo();
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

                                if (rgp.getZo() >= maxHeight)
                                {
                                    maxHeight = rgp.getZo();
                                }

                                if (rgp.getZo() <= minHeight)
                                {
                                    minHeight = rgp.getZo();
                                }

                                heightSum += rgp.getZo();
                            }
                            k++;
                            if (k < regionPoints.size())
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

                                if (rgp.getZo() >= maxHeight)
                                {
                                    maxHeight = rgp.getZo();
                                }

                                if (rgp.getZo() <= minHeight)
                                {
                                    minHeight = rgp.getZo();
                                }
                                heightSum += rgp.getZo();
                            }
                            k--;
                            if ( k >= 0)
                                rgp = regionPoints.get(k);
                        }

                        // Now Save the Points into the Cassandra as the List of Points if we want
                        // Else let us calculate the cs, cl, cp values...
                        double[][] matrix = new double[points.size()][3];

                        if (points.size() < 3)
                        {
                            continue;
                        }
                        heightAvg = (heightSum/points.size());

                        int matrixIdx = 0;
                        for (Point neighbour : points)
                        {
                            matrix[matrixIdx][0] = neighbour.getX();
                            matrix[matrixIdx][1] = neighbour.getY();
                            matrix[matrixIdx][2] = neighbour.getZ();
                            matrixIdx++;

                            heightStandardDeviation += ((neighbour.getZ()  - heightAvg)*(neighbour.getZ()  - heightAvg));
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

                        sumOfEigenValues = lambda1 + lambda2 + lambda3;
                        //Features
                        cl += ((lambda1 - lambda2) / lambda1);
                        cp += ((lambda2 - lambda3) / lambda1);
                        cs += (lambda3 / lambda1);
                        omnivariance += Math.cbrt(lambda1 * lambda2 * lambda3);
                        anisotropy += ((lambda1  - lambda3) / lambda1);
                        //sumOfEigenValues = lambda1 + lambda2 + lambda3;
                        changeOfCurvature += (lambda3 / (lambda1 + lambda2 + lambda3));
                        eigenentropy += -1 * (lambda1 * Math.log(lambda1)
                            + lambda2 * Math.log(lambda2)
                            + lambda3 * Math.log(lambda3));
                        heightDiff += maxHeight - minHeight;
                        pointDensity += (points.size()/(radi[j]*radi[j]*radi[j]));
                        heightStandardDeviation += (float) Math.sqrt((heightStandardDeviation/points.size()));
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
                        cl = cl/radi.length;
                        cp = cp/radi.length;
                        cs = cs /radi.length;
                        anisotropy = anisotropy/radi.length;
                        changeOfCurvature = changeOfCurvature/radi.length;
                        eigenentropy = eigenentropy/radi.length;
                        omnivariance = omnivariance/radi.length;
                        sumOfEigenValues = sumOfEigenValues/radi.length;
                        optimalRadius = optimalRadius/radi.length;
                        heightDiff = heightDiff/radi.length;
                        heightStandardDeviation = heightStandardDeviation/radi.length;
                        pointDensity = pointDensity/radi.length;

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
                                          + heightDiff
                                          + ","
                                          + heightStandardDeviation
                                          + ","
                                          + pointDensity
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

    public void multiscaleAverage_wb()
    {
        try
        {
            System.out.println(inputFileName);
            File file =
                new File(inputFileName);
            FileWriter writer =
                new FileWriter(outputFileName);
            Scanner sc = new Scanner(file);
            List<RegionPointv3> regionPoints = new ArrayList<>();
            while (sc.hasNextLine())
            {
                String line = sc.nextLine();
                String input[] = line.split(" ");
                float x = Float.parseFloat(input[0]);
                float y = Float.parseFloat(input[1]);
                float z = Float.parseFloat(input[2]);
                int label = Integer.parseInt(input[3]);
                if (label != 0)
                    regionPoints
                        .add(new RegionPointv3(x, y, z, label));
            }

            Collections.sort(regionPoints, new Comparator<RegionPointv3>()
            {
                @Override
                public int compare(RegionPointv3 o1, RegionPointv3 o2)
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

            //writer.write("x,y,z,cp,cl,cs,label"+System.lineSeparator());
            //Features
            double cl = 0;
            double cp = 0;
            double cs = 0;
            float[] radi = new float[]
                {
                    0.5f,
                    0.7f,
                    0.9f
                };

            for (int i = startIndex; i < regionPoints.size(); i++)
            {
                RegionPointv3 regionPoint = regionPoints.get(i);
                cl = 0;
                cp = 0;
                cs = 0;
                if (true)
                {
                    int index = Collections.binarySearch(regionPoints, regionPoint, new Comparator<RegionPointv3>()
                    {
                        @Override
                        public int compare(RegionPointv3 o1, RegionPointv3 o2)
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
                        RegionPointv3 rgp = regionPoints.get(k);

                        while (rgp.getX() <= endIndex_X && k < regionPoints.size())
                        {
                            if ((rgp.getX() >= startIndex_X && rgp.getX() <= endIndex_X)
                                && (rgp.getY() >= startIndex_Y && rgp.getY() <= endIndex_Y)
                                && (rgp.getZ() >= startIndex_Z && rgp.getZ() <= endIndex_Z))
                            {
                                points.add(new Point(
                                    rgp.getX(),
                                    rgp.getY(),
                                    rgp.getZ()
                                ));
                            }
                            k++;
                            if (k < regionPoints.size())
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
                                    rgp.getX(),
                                    rgp.getY(),
                                    rgp.getZ()
                                ));
                            }
                            k--;
                            if (k >= 0)
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

                        /*
                        gcl += ((values[2] - values[1]) / len); // CL
                        gcp += ((2 * (values[1] - values[0])) / len); // CS
                        gcs += (1 - (((values[2] - values[1]) / len) + ((2 * (values[1] - values[0])) / len))); // CP
                        */
                        double len = lambda1 + lambda2 + lambda3;
                        cl += ((lambda1 - lambda2) / len);
                        cs += ((2 * (lambda2 - lambda3)) / len);
                        cp += (1 - (((lambda1 - lambda2) / len) + ((2 * (lambda2 - lambda3)) / len)));
                    }

                    if (!(Double.isNaN(cl) ||
                        Double.isNaN(cp) ||
                        Double.isNaN(cs) ||
                        (cp == 0  || cl == 0 || cs == 0))
                    )
                    {
                        cp = cp/radi.length;
                        cl = cl/radi.length;
                        cs = cs/radi.length;

                        writer.write(   regionPoint.getX()
                                          + " "
                                          + regionPoint.getY()
                                          + " "
                                          + regionPoint.getZ()
                                          + " "
                                          + cp
                                          + " "
                                          + cl
                                          + " "
                                          + cs
                                          + " "
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

    public void optimalScale_wb()
    {
        try
        {
            System.out.println(inputFileName);
            File file =
                new File(inputFileName);
            FileWriter writer =
                new FileWriter(outputFileName);
            Scanner sc = new Scanner(file);
            List<RegionPointv3> regionPoints = new ArrayList<>();
            while (sc.hasNextLine())
            {
                String line = sc.nextLine();
                String input[] = line.split(" ");

                float x = Float.parseFloat(input[0]);
                float y = Float.parseFloat(input[1]);
                float z = Float.parseFloat(input[2]);
                int label = Integer.parseInt(input[3]);
                if (label != 0)
                    regionPoints
                        .add(new RegionPointv3(x, y, z, label));
            }

            Collections.sort(regionPoints, new Comparator<RegionPointv3>()
            {
                @Override
                public int compare(RegionPointv3 o1, RegionPointv3 o2)
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

            writer.write("x,y,z,cl,cp,cs,anisotropy,changeOfCurvature," +
                             "eigenentropy,omnivariance,sumOfEigenValues," +
                             "optimalRadius,hdiff,hsd,density,label"+
                             System.lineSeparator());

            for (int i = startIndex; i < regionPoints.size(); i++)
            {
                RegionPointv3 regionPoint = regionPoints.get(i);
                if (true)
                {
                    int index = Collections.binarySearch(regionPoints, regionPoint,
                                                         new Comparator<RegionPointv3>()
                    {
                        @Override
                        public int compare(RegionPointv3 o1, RegionPointv3 o2)
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
                    float heightDiff = 0;
                    float heightStandardDeviation = 0;
                    float pointDensity = 0;
                    /*Feature Set Ends here*/
                    float maxHeight = 0;
                    float minHeight = 0;
                    float heightSum = 0;
                    float heightAvg = 0;
                    /*
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
                    */
                    float[] radi = new float[]
                        {
                            0.2f,
                            0.4f,
                            0.6f,
                            0.8f,
                            1.0f,
                            1.2f,
                            1.4f,
                            1.6f,
                            1.8f,
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
                        RegionPointv3 rgp = regionPoints.get(k);
                        maxHeight = rgp.getZ();
                        minHeight = rgp.getZ();
                        heightSum = rgp.getZ();
                        while (rgp.getX() <= endIndex_X && k < regionPoints.size())
                        {
                            if ((rgp.getX() >= startIndex_X && rgp.getX() <= endIndex_X)
                                && (rgp.getY() >= startIndex_Y && rgp.getY() <= endIndex_Y)
                                && (rgp.getZ() >= startIndex_Z && rgp.getZ() <= endIndex_Z))
                            {
                                points.add(new Point(
                                    rgp.getX(),
                                    rgp.getY(),
                                    rgp.getZ()
                                ));

                                if (rgp.getZ() >= maxHeight)
                                {
                                    maxHeight = rgp.getZ();
                                }

                                if (rgp.getZ() <= minHeight)
                                {
                                    minHeight = rgp.getZ();
                                }

                                heightSum += rgp.getZ();
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
                                    rgp.getX(),
                                    rgp.getY(),
                                    rgp.getZ()
                                ));

                                if (rgp.getZ() >= maxHeight)
                                {
                                    maxHeight = rgp.getZ();
                                }

                                if (rgp.getZ() <= minHeight)
                                {
                                    minHeight = rgp.getZ();
                                }
                                heightSum += rgp.getZ();
                            }
                            k--;
                            rgp = regionPoints.get(k);
                        }

                        // Now Save the Points into the Cassandra as the List of Points if we want
                        // Else let us calculate the cs, cl, cp values...
                        double[][] matrix = new double[points.size()][3];

                        if (points.size() < 3)
                        {
                            continue;
                        }
                        heightAvg = (heightSum/points.size());

                        int matrixIdx = 0;
                        for (Point neighbour : points)
                        {
                            matrix[matrixIdx][0] = neighbour.getX();
                            matrix[matrixIdx][1] = neighbour.getY();
                            matrix[matrixIdx][2] = neighbour.getZ();
                            matrixIdx++;

                            heightStandardDeviation += ((neighbour.getZ()  - heightAvg)*(neighbour.getZ()  - heightAvg));
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

                        sumOfEigenValues = lambda1 + lambda2 + lambda3;
                        //Features
                        if (isOptimalScale)
                        {
                            cl = ((lambda1 - lambda2) / lambda1);
                            cp = ((lambda2 - lambda3) / lambda1);
                            cs = (lambda3 / lambda1);
                            omnivariance = Math.cbrt(lambda1 * lambda2 * lambda3);
                            anisotropy = ((lambda1  - lambda3) / lambda1);
                            //sumOfEigenValues = lambda1 + lambda2 + lambda3;
                            changeOfCurvature = (lambda3 / (lambda1 + lambda2 + lambda3));
                            eigenentropy = -1 * (lambda1 * Math.log(lambda1)
                                + lambda2 * Math.log(lambda2)
                                + lambda3 * Math.log(lambda3));
                            heightDiff = maxHeight - minHeight;
                            pointDensity = (points.size()/(radi[j]*radi[j]*radi[j]));
                            heightStandardDeviation = (float) Math.sqrt((heightStandardDeviation/points.size()));
                        }
                        else
                        {
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
                        writer.write( regionPoint.getX()
                                          + ","
                                          + regionPoint.getY()
                                          + ","
                                          + regionPoint.getZ()
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
                                          + heightDiff
                                          + ","
                                          + heightStandardDeviation
                                          + ","
                                          + pointDensity
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