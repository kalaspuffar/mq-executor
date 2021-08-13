package org.ea;

public interface ConfigConst {
    String REQUEST_QUEUE_NAME = "rabbitmq_request_queue";
    String RESPONSE_QUEUE_NAME = "rabbitmq_response_queue";
    String WORKER_NAME = "rabbitmq_worker_name";
    String HOSTS = "rabbitmq_hosts";
    String PORT = "rabbitmq_port";
    Object USERNAME = "rabbitmq_username";
    Object PASSWORD = "rabbitmq_password";
    Object VHOST = "rabbitmq_vhost";
}
