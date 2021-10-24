package com.example.demo;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class Consumer {
    public static void main(String[] args) {
        thread(new HelloWorldConsumer(), false);
    }

    public static void thread(Runnable runnable, boolean daemon) {
        Thread brokerThread = new Thread(runnable);
        brokerThread.setDaemon(daemon);
        brokerThread.start();
    }

    public static class HelloWorldConsumer implements Runnable, ExceptionListener {
        public void run() {
            try {
                //Create a ConnectionFactory
                ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://0.0.0.0:61616");

                //Create a connection
                Connection connection = connectionFactory.createConnection();
                connection.start();

                connection.setExceptionListener(this);

                //Create session
                Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

                //Create the destination (Topic or Queue)
                Destination destination = session.createQueue("message.queue");

                //Create a MessageProducer from the Session to the Topic or Queue
                MessageConsumer consumer = session.createConsumer(destination);

                //Wait for a message
                Message message = consumer.receive(1000);

                if (message instanceof TextMessage) {
                    TextMessage textMessage = (TextMessage) message;
                    String text = textMessage.getText();
                    System.out.println("Received: " + message);
                }

                // Clean up
                consumer.close();
                session.close();
                connection.close();

            } catch (Exception e) {
                System.out.println("Caught" + e);
                e.printStackTrace();
            }
        }

        @Override
        public synchronized void onException(JMSException e) {
            System.out.println(" exception occurred. client shutdown.");
        }
    }
}
