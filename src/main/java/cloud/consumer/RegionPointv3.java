package cloud.consumer;

public class RegionPointv3
{
    private float x;
    private float y;
    private float z;
    private int label;

    public RegionPointv3(float x, float y, float z, int label)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.label = label;
    }

    public float getX()
    {
        return x;
    }

    public void setX(float x)
    {
        this.x = x;
    }

    public float getY()
    {
        return y;
    }

    public void setY(float y)
    {
        this.y = y;
    }

    public float getZ()
    {
        return z;
    }

    public void setZ(float z)
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
