package cloud.consumer;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public abstract class EndPoint
{
    protected Channel channel;
    protected Connection connection;
    protected String endPointName;

    public EndPoint(String endpointName) throws IOException, TimeoutException
    {
        this.endPointName = endpointName;
        ConnectionFactory factory = new ConnectionFactory();

        factory.setHost("localhost");

        connection = factory.newConnection();

        channel = connection.createChannel();

        Map<String, Object> args = new HashMap<String, Object>();
        args.put("x-dead-letter-exchange", "Publish.Point.Queue.incoming.exchange");
        args.put("x-dead-letter-routing-key", "Publish.Point.Queue.Region_Three.dead-letter.queue");

        String queue = channel.queueDeclare("Publish.Point.Queue.Region_Three.incoming.queue",
                true    , false, false, args).getQueue();

        channel.queueDeclare("Publish.Point.Queue.Region_Three.dead-letter.queue",
                true    , false, false, null);

        channel.queueBind(queue, "Publish.Point.Queue.incoming.exchange", "REGION_ONE");
    }

    public void close() throws IOException, TimeoutException
    {
        this.channel.close();
        this.connection.close();
    }
}
