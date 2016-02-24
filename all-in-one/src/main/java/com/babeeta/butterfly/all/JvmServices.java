package com.babeeta.butterfly.all;

import com.babeeta.butterfly.all.dev.DevRouterServer;
import com.babeeta.butterfly.all.gateway.GatewayRouterJvmServer;
import com.babeeta.butterfly.all.gateway.GatewayRouterServer;
import com.babeeta.butterfly.all.gateway.GatewaySecondaryRouterJvmServer;
import com.babeeta.butterfly.all.monitor.DevJMX;
import com.babeeta.butterfly.all.monitor.GatewayJMX;
import com.babeeta.butterfly.router.jvm.DNSImpl;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;

/**
 * Created by IntelliJ IDEA. User: yinchong Date: 10-12-7 Time: 上午11:48 To
 * change this template use File | Settings | File Templates.
 */
public class JvmServices {
	private final static Logger logger = LoggerFactory
			.getLogger(JvmServices.class);

	/**
	 * 启动DevRouter Netty server 启动GatewayRouter Netty server 启动GatewayRouter JVM
	 * server 启动GatewaySecondaryRouter JVM server
	 * 
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		logger.info("Start services...");

		Options options = new Options();

		Option option = new Option("m", true, "MongoDB地址端口");
		option.setRequired(false);
		option.setArgName("host:port");
		option.setArgs(2);
		option.setValueSeparator(':');
		options.addOption(option);

		option = new Option("d", true, "dev路由服务域名");
		option.setRequired(false);
		option.setArgName("dev");
		options.addOption(option);

		option = new Option("g", true, "gateway路由服务域名");
		option.setRequired(false);
		option.setArgName("gateway.dev");
		options.addOption(option);

		option = new Option("gs", true, "二级gateway路由服务域名,多个用逗号分隔,对应dn参数顺序");
		option.setRequired(false);
		option.setArgName("gateway.secondary");
		option.setArgs(2);
		option.setValueSeparator(',');
		options.addOption(option);

		option = new Option("dn", true, "二级gatewya路由对应的数据库名,多个用逗号分隔,对应gs参数顺序");
		option.setRequired(false);
		option.setArgName("dbName");
		option.setArgs(2);
		option.setValueSeparator(',');
		options.addOption(option);

		try {
			CommandLine cl = new GnuParser().parse(options, args);
			ServerContext serverContext = new ServerContext(cl);
			bootstrap(serverContext);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("java ....JVM router server", options);
		}
	}

	private static void bootstrap(final ServerContext serverContext)
			throws Exception {

		try {
			DNSImpl.getDefaultInstance();// 初始化单例类，避免并发产生多个实例
			DevRouterServer.bootstrap(serverContext.getDevRouter(),
					serverContext.getMongo());
			GatewayRouterServer.bootstrap(serverContext.getGatewayRouter(),
					serverContext.getGatewaySecondaryRouter());
			GatewayRouterJvmServer.bootstrap(serverContext.getGatewayRouter(),
					serverContext.getGatewaySecondaryRouter());

			int len = serverContext.getGatewaySecondaryRouterDB().size();
			for (int i = 0; i < len; i++) {
				GatewaySecondaryRouterJvmServer.bootstrap(String
						.valueOf(serverContext.getGatewaySecondaryRouter().get(
								i)),
						serverContext.getMongo(),
						String.valueOf(serverContext
								.getGatewaySecondaryRouterDB().get(i)));
			}

			logger.info("Services is started.");

            startMonitor();

		} catch (Exception e) {
			e.printStackTrace();
			logger.info("Service is stop.");
			System.exit(0);
		}
	}

    private static void startMonitor() {
        MBeanServer mBeanServer =  ManagementFactory.getPlatformMBeanServer();
        DevJMX devMBean = new DevJMX();
        GatewayJMX gatewayMBean = new GatewayJMX();
        try {
            ObjectName devName = new ObjectName("com.babeeta.butterfly.all.monitor:name=DevJMX");
            ObjectName gatewayName = new ObjectName("com.babeeta.butterfly.all.monitor:name=GatewayJMX");
            mBeanServer.registerMBean(devMBean, devName);
            mBeanServer.registerMBean(gatewayMBean, gatewayName);
            logger.info("Big Router JMX Server is started.");
        } catch (Exception e) {
            logger.info("AuthenticationJMX Server error:" + e.getMessage());
        }
    }
}
