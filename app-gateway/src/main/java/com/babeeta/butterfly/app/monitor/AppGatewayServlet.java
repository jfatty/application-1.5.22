package com.babeeta.butterfly.app.monitor;

import com.babeeta.butterfly.MessageRouting;
import com.google.protobuf.ByteString;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by IntelliJ IDEA.
 * User: XYuser
 * Date: 11-1-28
 * Time: 上午10:23
 * To change this template use File | Settings | File Templates.
 */
public class AppGatewayServlet extends HttpServlet {

    public static final MessageRouting.Message MSG_HEARTBEAT = MessageRouting.Message.newBuilder()
            .setUid("heartbeat")
            .setDate(-1)
            .setFrom("")
            .setTo("")
            .setContent(ByteString.EMPTY)
            .build();

    public void doGet(final HttpServletRequest request, final HttpServletResponse response)
            throws IOException, ServletException {
        Socket socket = new Socket();
        String domain = System.getProperty("domain", "0.gateway.app");
        try {
            socket.connect(new InetSocketAddress(domain, 5757));
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            MSG_HEARTBEAT.writeDelimitedTo(out);
            out.flush();
            out.close();
        } finally {
            socket.close();
        }
        response.getWriter().write("OK");
        response.getWriter().flush();
        response.getWriter().close();
    }
}
