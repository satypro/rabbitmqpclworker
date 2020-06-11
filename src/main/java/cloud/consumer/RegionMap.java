package cloud.consumer;

import java.util.List;

public class RegionMap
{
    public String regionCode;
    public List<String> childrens;

    public List<String> getChildren()
    {
        return childrens;
    }

    public void setChildren(List<String> childrens)
    {
        this.childrens = childrens;
    }

    public String getRegionCode()
    {
        return regionCode;
    }

    public void setRegionCode(String regionCode)
    {
        this.regionCode = regionCode;
    }
}