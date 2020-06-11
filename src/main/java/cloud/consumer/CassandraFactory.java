package cloud.consumer;

import com.datastax.driver.core.Session;

public class CassandraFactory
{
    private static Session session;
    private static PointCloudRepository pointCloudRepository;

    private CassandraFactory()
    {
    }

    public static Session getSession()
    {
        if (session != null)
        {
            return session;
        }

        CassandraConnector cassandraConnector = new CassandraConnector();
        cassandraConnector.connect();
        session = cassandraConnector.getSession();
        return session;
    }

    public static PointCloudRepository getPointCloudRepository()
    {
        if (pointCloudRepository != null)
        {
            return pointCloudRepository;
        }

        PointCloudRepository pointCloudRepository = new PointCloudRepository(getSession());
        return pointCloudRepository;
    }
}