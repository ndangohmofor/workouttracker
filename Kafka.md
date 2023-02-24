#vCreating a kafka messaging server on ubuntu:

1. Install and configure a server running ubuntu.

2. Kafka is written in Java and Scala and requires jre 1.7 and above to run it.
sudo apt-get update
sudo apt-get install default-jre

3. Zookeeper is required since kafka uses it as the broker for the kafka nodes
sudo apt-get install zookeeperd

4. Confirm that zookeeper has been successfully installed:
telnet localhost 2181

5. In the telnet session, type
ruok

Response should be:
imok

6. Create a service account for managing kafka:
sudo adduser kafka
sudo adduser kafka sudo

7. Switch to the new user account and download and install the kafka binaries
su -l kafka
mkdir ~/Downloads
"https://downloads.apache.org/kafka/2.8.2/kafka_2.13-2.8.2.tgz " -o ~/Downloads/kafka.tgz
NB: alwas check the download page for apache for the latest version of kafka to download.

8. Make a new directory for extracting the kafka binaries and switch to the directory
mkdir ~/kafka
cd ~/kafka

9. Extract the downloaded kafka binaries into the new directory
tar -xvzf ~/Downloads/kafka.tgz --strip 1

10. Configure a directory for storing log files. 
Ensure to create the directory before starting the kafka service
From inside the directory where you extracted kafka, run:
vim config/server.properties
Add the below entry into the config file:
log.dirs=/home/kafka/kafka/logs

11. Configure zookeeper and kafka to be managed by the systemd service, so that kafka can be started/stopped using the systemd utility. Create file /etc/systemd/system/zookeeper.service. 
sudo vim /etc/systemd/system/zookeeper.service.

Add the below into the file:
[Unit]
Requires=network.target remote-fs.target
After=network.target remote-fs.target

[Service]
Type=simple
User=kafka
ExecStart=/home/kafka/kafka/bin/zookeeper-server-start.sh /home/kafka/kafka/config/zookeeper.properties
ExecStop=/home/kafka/kafka/bin/zookeeper-server-stop.sh
Restart=on-abnormal

[Install]
WantedBy=multi-user.target


sudo vim /etc/systemd/system/kafka.service

Add the below into the file:
[Unit]
Requires=zookeeper.service
After=zookeeper.service

[Service]
Type=simple
User=kafka
ExecStart=/bin/sh -c '/home/kafka/kafka/bin/kafka-server-start.sh /home/kafka/kafka/config/server.properties > /home/kafka/kafka/kafka.log 2>&1'
ExecStop=/home/kafka/kafka/bin/kafka-server-stop.sh
Restart=on-abnormal

[Install]
WantedBy=multi-user.target


12. Enable Kafka and zooker to start up at system book
sudo systemctl enable zookeeper
sudo systemctl enable kafka

13. Start kafka and confirm successful startup
sudo systemctl start kafka
sudo systemctl status kafka

14. Test that the kafka server is running by checking that there is a process listening on port 9092
telnet localhost 9092

15. To test that the messaging is working as expected, you can create a topic on zookeeper, and then use the producer script to produce a message to the topic and then use the consumer script to see if you can consume the same message.
Create topic - Topic name is WorkoutTopic:
~/kafka/bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic WorkoutTopic

Produce a message into the topic - message here is "Hello Kafka"
echo "Hello, Kafka" | ~/kafka/bin/kafka-console-producer.sh --broker-list localhost:9092 --topic WorkoutTopic
 
Try consuming from the topic and confirm if you can see the same message:
~/kafka/bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic WorkoutTopic --from-beginning

16. The last step will be to secure the kafka server by removing sudo access from the kafka user, and disable it's password to prevent people from logging in directly with the user accounts
sudo deluser kafka sudo

Now we can setup our Email service to work asynchronously by configuring a producer and consumer for email messaging.
