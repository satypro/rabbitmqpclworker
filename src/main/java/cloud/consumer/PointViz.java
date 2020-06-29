package cloud.consumer;

public class PointViz
{
    private long x;
    private long y;
    private long z;
    private int label;

    public int getBoundary()
    {
        return boundary;
    }

    public void setBoundary(int boundary)
    {
        this.boundary = boundary;
    }

    private int boundary;

    public PointViz(long x, long y, long z, int label, int boundary)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.label = label;
        this.boundary = boundary;
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

    public int getLabel()
    {
        return label;
    }

    public void setLabel(int label)
    {
        this.label = label;
    }
}