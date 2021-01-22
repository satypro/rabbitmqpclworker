package cloud.consumer;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Scanner;

class Voxel
{
    float xMin;
    float xMax;
    float yMin;
    float yMax;
}

public class PartitionTilesFeaturesIntoGrid
{
    float []xRange = new float[]
    {
        250.015f,
        275.015f,
        300.015f,
        325.015f,
        350.015f,
        375.015f,
        400.015f,
        425.015f,
        450.015f,
        475.015f,
        500.015f,
        525.015f,
        550.015f,
        575.015f,
        600.015f,
        625.015f,
        650.015f,
        675.015f,
        700.015f,
        725.015f,
        750.046f,
    };

    float []yRange = new float[]
    {
        250.015f,
        275.015f,
        300.015f,
        325.015f,
        350.015f,
        375.015f,
        400.015f,
        425.015f,
        450.015f,
        475.015f,
        500.015f,
        525.015f,
        550.015f,
        575.015f,
        600.015f,
        625.015f,
        650.015f,
        675.015f,
        700.015f,
        725.015f,
        750.046f,
    };

    public void partitionFeatureSet(String inputFileName, String outputFileLocation)
    {
        ArrayList<Voxel> voxels = new ArrayList<>();

        for(int i = 0 ; i < xRange.length - 1; i++)
        {
            for(int j=0; j < yRange.length -1; j++)
            {
                Voxel voxel = new Voxel();
                voxel.xMin = xRange[i];
                voxel.xMax = xRange[i+1];
                voxel.yMin = yRange[j];
                voxel.yMax = yRange[j+1];
                voxels.add(voxel);
                System.out.println(     "XMIN : "+   voxel.xMin
                                       +" XMAX : " + voxel.xMax
                                       +" YMIN : " + voxel.yMin
                                       +" YMAX : " + voxel.yMax);
            }
        }

        //System.out.println(voxels.size());

        try
        {
            System.out.println(inputFileName);
            outputFileLocation = "E:\\PCL_CLASSIFIER\\5080\\AVG\\output";
            File file =
                new File("E:\\PCL_CLASSIFIER\\5080\\AVG\\combined.txt");
            File file2 = new File("E:\\PCL_CLASSIFIER\\5080\\AVG\\combined2.txt");

            ArrayList<FileWriter> fileWriters = new ArrayList<>();

            for(int i = 0; i < voxels.size(); i++)
            {
                FileWriter fileWriter = new FileWriter(outputFileLocation
                                   +"/voxel_"
                                   + Integer.toString(i)
                                   +"_"
                                   +"multiscale.txt");

                fileWriter.write("x,y,z,cl,cp,cs,anisotropy,changeOfCurvature," +
                                 "eigenentropy,omnivariance,sumOfEigenValues," +
                                 "optimalRadius,hdiff,hsd,density,label"+
                                 System.lineSeparator());

                fileWriters.add(fileWriter);
            }

            Scanner sc = new Scanner(file);
            while (sc.hasNextLine())
            {
                Feature feature = new Feature();
                String line = sc.nextLine();
                String input[] = line.split(",");
                feature.x = Float.parseFloat(input[0]);
                feature.y = Float.parseFloat(input[1]);
                feature.z = Float.parseFloat(input[2]);
                feature.cl = Float.parseFloat(input[3]);
                feature.cp = Float.parseFloat(input[4]);
                feature.cs = Float.parseFloat(input[5]);
                feature.anisotropy = Double.parseDouble(input[6]);
                feature.changeOfCurvature = Double.parseDouble(input[7]);
                feature.eigenentropy = Double.parseDouble(input[8]);
                feature.omnivariance = Double.parseDouble(input[9]);
                feature.sumOfEigenValues = Double.parseDouble(input[10]);
                feature.optimalRadius = Double.parseDouble(input[11]);
                feature.hdiff = Float.parseFloat(input[12]);
                feature.hsd = Float.parseFloat(input[13]);
                feature.density = Float.parseFloat(input[14]);
                feature.label = Integer.parseInt(input[15]);

                for(int i = 0; i < voxels.size(); i++)
                {
                    Voxel voxel = voxels.get(i);
                    if ((feature.x >= voxel.xMin && feature.x < voxel.xMax)
                        &&
                        (feature.y >= voxel.yMin && feature.y < voxel.yMax)
                    )
                    {
                        FileWriter fileWriter = fileWriters.get(i);
                        fileWriter.write( feature.x
                                                       + ","
                                                       + feature.y
                                                       + ","
                                                       + feature.z
                                                       + ","
                                                       + feature.cl
                                                       + ","
                                                       + feature.cp
                                                       + ","
                                                       + feature.cs
                                                       + ","
                                                       + feature.anisotropy
                                                       + ","
                                                       + feature.changeOfCurvature
                                                       + ","
                                                       + feature.eigenentropy
                                                       + ","
                                                       + feature.omnivariance
                                                       + ","
                                                       + feature.sumOfEigenValues
                                                       + ","
                                                       + feature.optimalRadius
                                                       + ","
                                                       + feature.hdiff
                                                       + ","
                                                       + feature.hsd
                                                       + ","
                                                       + feature.density
                                                       + ","
                                                       + feature.label
                                                       + System.lineSeparator());
                        fileWriter.flush();
                    }
                }
            }

            Scanner sc2 = new Scanner(file2);
            while (sc2.hasNextLine())
            {
                Feature feature = new Feature();
                String line = sc2.nextLine();
                String input[] = line.split(",");
                feature.x = Float.parseFloat(input[0]);
                feature.y = Float.parseFloat(input[1]);
                feature.z = Float.parseFloat(input[2]);
                feature.cl = Float.parseFloat(input[3]);
                feature.cp = Float.parseFloat(input[4]);
                feature.cs = Float.parseFloat(input[5]);
                feature.anisotropy = Double.parseDouble(input[6]);
                feature.changeOfCurvature = Double.parseDouble(input[7]);
                feature.eigenentropy = Double.parseDouble(input[8]);
                feature.omnivariance = Double.parseDouble(input[9]);
                feature.sumOfEigenValues = Double.parseDouble(input[10]);
                feature.optimalRadius = Double.parseDouble(input[11]);
                feature.hdiff = Float.parseFloat(input[12]);
                feature.hsd = Float.parseFloat(input[13]);
                feature.density = Float.parseFloat(input[14]);
                feature.label = Integer.parseInt(input[15]);

                for(int i = 0; i < voxels.size(); i++)
                {
                    Voxel voxel = voxels.get(i);
                    if ((feature.x >= voxel.xMin && feature.x < voxel.xMax)
                        &&
                        (feature.y >= voxel.yMin && feature.y < voxel.yMax)
                    )
                    {
                        FileWriter fileWriter = fileWriters.get(i);
                        fileWriter.write( feature.x
                                                      + ","
                                                      + feature.y
                                                      + ","
                                                      + feature.z
                                                      + ","
                                                      + feature.cl
                                                      + ","
                                                      + feature.cp
                                                      + ","
                                                      + feature.cs
                                                      + ","
                                                      + feature.anisotropy
                                                      + ","
                                                      + feature.changeOfCurvature
                                                      + ","
                                                      + feature.eigenentropy
                                                      + ","
                                                      + feature.omnivariance
                                                      + ","
                                                      + feature.sumOfEigenValues
                                                      + ","
                                                      + feature.optimalRadius
                                                      + ","
                                                      + feature.hdiff
                                                      + ","
                                                      + feature.hsd
                                                      + ","
                                                      + feature.density
                                                      + ","
                                                      + feature.label
                                                      + System.lineSeparator());
                        fileWriter.flush();
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}