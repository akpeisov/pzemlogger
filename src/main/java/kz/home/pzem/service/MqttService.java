package kz.home.pzem.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kz.home.pzem.entity.PzemData;
import kz.home.pzem.repository.PzemDataRepository;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class MqttService {

    @Autowired
    private PzemDataRepository repository;

    private final IMqttClient mqttClient;

    private static final Logger logger = LoggerFactory.getLogger(MqttService.class);

    public MqttService(@Value("${mqtt.broker.url}") String brokerUrl,
                       @Value("${mqtt.client.id}") String clientId,
                       @Value("${mqtt.user}") String username,
                       @Value("${mqtt.password}") String password) throws MqttException {
        this.mqttClient = new MqttClient(brokerUrl, clientId);
        //logger.info(String.format("username %s, password %s", username, password));
        MqttConnectOptions options = setUpConnectionOptions(username, password); //new MqttConnectOptions();
        options.setCleanSession(true);
        mqttClient.connect(options);
        subscribeToTopic("PZEM/jsondata");
    }
    private static MqttConnectOptions setUpConnectionOptions(String username, String password) {
        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(true);
        connOpts.setUserName(username);
        connOpts.setPassword(password.toCharArray());
        return connOpts;
    }
    public void subscribeToTopic(String topic) throws MqttException {
        mqttClient.subscribe(topic, (topic1, msg) -> {
            String payload = new String(msg.getPayload());
            handleIncomingMessage(payload);
        });
    }

    private void handleIncomingMessage(String payload) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(payload);

            PzemData data = new PzemData();
            data.setDeviceId(jsonNode.get("id").asInt());
            data.setVoltage(jsonNode.get("voltage").asDouble());
            data.setCurrent(jsonNode.get("current").asDouble());
            data.setPower(jsonNode.get("power").asDouble());
            data.setEnergy(jsonNode.get("energy").asLong());
            data.setFrequency(jsonNode.get("frequency").asDouble());
            data.setPowerfactor(jsonNode.get("powerfactor").asDouble());
            data.setTimestamp(LocalDateTime.now());

            repository.save(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

