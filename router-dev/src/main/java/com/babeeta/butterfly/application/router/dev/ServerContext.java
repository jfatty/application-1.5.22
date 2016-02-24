package com.babeeta.butterfly.application.router.dev;

import java.net.UnknownHostException;

import org.apache.commons.cli.CommandLine;

import com.mongodb.Mongo;
import com.mongodb.MongoException;

/**
 * 服务器环境
 * 
 * @author leon
 * 
 */
public class ServerContext {
	final String mongoHost;
	final int mongoPort;
	final String domain = "dev";
	final String localAddress;
	final Mongo mongo;

	public ServerContext(CommandLine commandLine) throws UnknownHostException,
			MongoException {
		mongoHost = commandLine.getOptionValues("m")[0];
		mongoPort = Integer.valueOf(commandLine.getOptionValues("m")[1]);
		localAddress = commandLine.getOptionValue("l");
		mongo = new Mongo(mongoHost, mongoPort);        
	}
}
