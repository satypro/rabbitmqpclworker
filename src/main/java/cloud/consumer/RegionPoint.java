package cloud.consumer;

public class RegionPoint
{
    private long regionId;
    private long pointId;
    private long morton;
    private int label;
    private int isboundary;
    private long x;
    private long y;
    private long z;

    private float xo;
    private float yo;
    private float zo;

    public RegionPoint(long regionId, long pointId, long morton, long x, long y, long z, float xo, float yo, float zo, int label, int isboundary)
    {
        this.regionId = regionId;
        this.pointId = pointId;
        this.x = x;
        this.y = y;
        this.z = z;
        this.setXo(xo);
        this.setYo(yo);
        this.setZo(zo);
        this.morton = morton;
        this.label = label;
        this.isboundary = isboundary;
    }

    public long getRegionId()
    {
        return regionId;
    }

    public void setRegionId(long regionId)
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

    public long getX()
    {
        return x;
    }

    public void setX(long x)
    {
        this.x = x;
    }

    public long getY()
    {
        return y;
    }

    public void setY(long y)
    {
        this.y = y;
    }

    public long getZ()
    {
        return z;
    }

    public void setZ(long z)
    {
        this.z = z;
    }

    public long getMorton()
    {
        return morton;
    }

    public void setMorton(long morton)
    {
        this.morton = morton;
    }

    public float getXo()
    {
        return xo;
    }

    public void setXo(float xo)
    {
        this.xo = xo;
    }

    public float getYo()
    {
        return yo;
    }

    public void setYo(float yo)
    {
        this.yo = yo;
    }

    public float getZo()
    {
        return zo;
    }

    public void setZo(float zo)
    {
        this.zo = zo;
    }

    public int getLabel()
    {
        return label;
    }

    public void setLabel(int label)
    {
        this.label = label;
    }

    public int getIsboundary()
    {
        return isboundary;
    }

    public void setIsboundary(int isboundary)
    {
        this.isboundary = isboundary;
    }
}