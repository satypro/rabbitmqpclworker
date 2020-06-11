package cloud.consumer;

import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import com.google.gson.Gson;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ShutdownSignalException;
import org.json.simple.parser.JSONParser;

public class QueueConsumer extends EndPoint implements Runnable, Consumer
{
    PointCloudRepository pointCloudRepository =  CassandraFactory.getPointCloudRepository();
    Gson gson = new Gson();

    public QueueConsumer(String endPointName) throws IOException, TimeoutException
    {
        super(endPointName);
    }

    public void run()
    {
        try
        {
            channel.basicConsume(endPointName, false,this);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void handleConsumeOk(String consumerTag)
    {
        System.out.println("Consumer "+consumerTag +" registered");
    }

    public void handleDelivery(String consumerTag, Envelope env,
                               AMQP.BasicProperties props, byte[] body) throws IOException
    {
        try
        {
            String content  = new String(body);
            Object obj = new JSONParser().parse(content);
            String jsonObject = (String)obj;
            Message message = gson.fromJson(jsonObject, Message.class);

            Long mortonCode = message.getMortonCode();
            String regionCode = message.getRegionCode();
            int k = message.getK();

            List<RegionMap> selectedRegionMap = PointCloudRepository.regionMaps
                    .stream()
                    .filter(regionMap -> regionMap.regionCode.equals(regionCode))
                    .collect(Collectors.toList());

            if (selectedRegionMap.size() > 0)
            {
                List<String> childrens = selectedRegionMap
                        .get(0)
                        .childrens;

                List<PointCloud> potentialCandidate = new ArrayList<>();
                for (String code : childrens)
                {
                    if (PointCloudRepository.pointCloudStore.containsKey(code))
                    {
                        List<PointCloud> pointClouds = PointCloudRepository
                                .pointCloudStore.get(code);
                        if (pointClouds.size() > 0)
                        {
                            String[] regionIdSplit = code.split(":");
                            Long startMortonCode = Long.parseLong(regionIdSplit[0]);
                            Long endMortonCode = Long.parseLong(regionIdSplit[1]);
                            if (startMortonCode <= mortonCode && mortonCode <= endMortonCode)
                            {
                                int index = Collections
                                        .binarySearch(pointClouds,
                                                new PointCloud(mortonCode, "", 0L),
                                                new Comparator<PointCloud>()
                                                {
                                                    @Override
                                                    public int compare(PointCloud o1, PointCloud o2)
                                                    {
                                                        if (o1.getMortonCode() > o2.getMortonCode())
                                                            return 1;
                                                        if (o1.getMortonCode() == o2.getMortonCode())
                                                            return 0;

                                                        return -1;
                                                    }
                                                });

                                int size = pointClouds.size();
                                int startIndex = index - k > 0 ? index - k : 0;
                                int endIndex = size - index > k ? index + k : size - 1;

                                potentialCandidate.addAll(pointClouds.subList(startIndex, endIndex));
                            }
                            else
                            {
                                if (pointClouds.size() < k*2)
                                {
                                    potentialCandidate.addAll(pointClouds.subList(0, pointClouds.size()));
                                } else
                                {
                                    potentialCandidate
                                            .addAll(pointClouds.subList(0, k));

                                    potentialCandidate
                                            .addAll(pointClouds.subList(pointClouds.size() - k, pointClouds.size()));
                                }
                            }
                        }
                    }
                }

                // Now Store these Points in the Cassandra against the Query (MortonCode , Set<potentialCandidate>)
                List<Long> points = potentialCandidate
                        .stream()
                        .map(p -> p.getPointId())
                        .collect(Collectors.toList());

                pointCloudRepository
                        .UpdateKnnResult(mortonCode, points);
            }

            long deliveryTag = env.getDeliveryTag();
            channel.basicAck(deliveryTag, false);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void handleCancel(String consumerTag) {}
    public void handleCancelOk(String consumerTag) {}
    public void handleRecoverOk(String consumerTag) {}
    public void handleShutdownSignal(String consumerTag, ShutdownSignalException arg1) {}
}