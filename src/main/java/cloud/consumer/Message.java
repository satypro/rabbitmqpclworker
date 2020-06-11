package cloud.consumer;

public class Message
{
    private String regionCode;
    private Long mortonCode;
    private int k;
    private Long index;

    public String getRegionCode()
    {
        return regionCode;
    }

    public void setRegionCode(String regionCode)
    {
        this.regionCode = regionCode;
    }

    public Long getMortonCode()
    {
        return mortonCode;
    }

    public void setMortonCode(Long mortonCode)
    {
        this.mortonCode = mortonCode;
    }

    public int getK()
    {
        return k;
    }

    public void setK(int k)
    {
        this.k = k;
    }

    public Long getIndex()
    {
        return index;
    }

    public void setIndex(Long index)
    {
        this.index = index;
    }
}
