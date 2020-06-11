package cloud.consumer;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;

public class CassandraConnector {

    private Cluster cluster;

    private Session session;

    public void connect()
    {
        Cluster.Builder b = Cluster.builder()
                .addContactPoint("192.168.29.100")
                .addContactPoint("192.168.29.120");
        cluster = b.build();

        session = cluster.connect();
    }

    public Session getSession() {
        return this.session;
    }

    public void close() {
        session.close();
        cluster.close();
    }
}