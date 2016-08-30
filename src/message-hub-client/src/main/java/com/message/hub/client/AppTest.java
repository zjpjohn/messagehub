package com.message.hub.client;


import com.message.hub.client.processer.ISubscribeProcesser;
import com.message.hub.server.contract.HubMessage;

import java.io.IOException;

/**
 * Created by shi on 7/8/2016.
 */
public class AppTest {
    public static void main(String[] arg) throws InterruptedException, IOException {
        ClientManger.instance().init("127.0.0.1:10991");

        ClientManger client = ClientManger.instance();
        client.sub("test", "test", new suber());


        run(client);
    }

    private static void run(ClientManger client) throws InterruptedException {
        while (true) {
            suber su = new suber();
            su.setMsg("hello world");
            client.pub("test", "test",su );
            Thread.sleep(1000);
        }
    }

    public static class suber implements ISubscribeProcesser {

        @Override
        public void action(HubMessage msg) {
            System.out.println(msg.getMessage());
        }

        private String msg;

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }
    }
}
