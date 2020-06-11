package cloud.consumer;

public class PointCloud
{
    private String regionId;
    private long mortonCode;
    private long pointId;
    private double distance;

    public PointCloud(long mortonCode, String regionId, long pointId)
    {
        this.mortonCode = mortonCode;
        this.regionId = regionId;
        this.pointId = pointId;
    }

    public long getMortonCode()
    {
        return mortonCode;
    }

    public void setMortonCode(long mortonCode)
    {
        this.mortonCode = mortonCode;
    }

    public double getDistance()
    {
        return distance;
    }

    public void setDistance(double distance)
    {
        this.distance = distance;
    }

    public String getRegionId()
    {
        return regionId;
    }

    public void setRegionId(String regionId)
    {
        this.regionId = regionId;
    }

    public long getPointId()
    {
        return pointId;
    }

    public void setPointId(long pointId)
    {
        this.pointId = pointId;
    }
}