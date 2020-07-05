package cloud.consumer;

public class RegionPointv2
{
    private float x;
    private float y;
    private float z;
    private float xo;
    private float yo;
    private float zo;
    private int label;
    private int isboundary;

    public RegionPointv2(float x, float y, float z, float xo, float yo, float zo, int label, int isboundary)
    {
        this.setX(x);
        this.setY(y);
        this.setZ(z);
        this.setXo(xo);
        this.setYo(yo);
        this.setZo(zo);
        this.setLabel(label);
        this.setIsboundary(isboundary);
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