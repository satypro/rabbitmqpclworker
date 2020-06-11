package cloud.consumer;

public class PointFeature
{
    private long index;
    private double cl;
    private double cp;
    private double cs;
    private double omnivariance;
    private double anisotropy;
    private double eigenentropy;
    private double changeOfCurvature;

    public PointFeature(
            long index,
            double cl,
            double cp,
            double cs,
            double omnivariance,
            double anisotropy,
            double eigenentropy,
            double changeOfCurvature)
    {
        this.setIndex(index);
        this.setCp(cp);
        this.setCs(cs);
        this.setCl(cl);
        this.setOmnivariance(omnivariance);
        this.setAnisotropy(anisotropy);
        this.setEigenentropy(eigenentropy);
        this.setChangeOfCurvature(changeOfCurvature);
    }

    public double getCl()
    {
        return cl;
    }

    public void setCl(double cl)
    {
        this.cl = cl;
    }

    public double getCp()
    {
        return cp;
    }

    public void setCp(double cp)
    {
        this.cp = cp;
    }

    public double getCs()
    {
        return cs;
    }

    public void setCs(double cs)
    {
        this.cs = cs;
    }

    public double getOmnivariance()
    {
        return omnivariance;
    }

    public void setOmnivariance(double omnivariance)
    {
        this.omnivariance = omnivariance;
    }

    public double getAnisotropy()
    {
        return anisotropy;
    }

    public void setAnisotropy(double anisotropy)
    {
        this.anisotropy = anisotropy;
    }

    public double getEigenentropy()
    {
        return eigenentropy;
    }

    public void setEigenentropy(double eigenentropy)
    {
        this.eigenentropy = eigenentropy;
    }

    public double getChangeOfCurvature()
    {
        return changeOfCurvature;
    }

    public void setChangeOfCurvature(double changeOfCurvature)
    {
        this.changeOfCurvature = changeOfCurvature;
    }

    public long getIndex()
    {
        return index;
    }

    public void setIndex(long index)
    {
        this.index = index;
    }
}
