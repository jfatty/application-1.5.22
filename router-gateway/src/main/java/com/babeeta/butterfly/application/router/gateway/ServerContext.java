package com.babeeta.butterfly.application.router.gateway;

import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.cli.CommandLine;

import com.mongodb.MongoException;

/**
 * 服务器环境
 * 
 * @author leon
 * 
 */
public class ServerContext {
	final String domain = "gateway.dev";
	final String localAddress;
	final List<String> secondaryDomainList;

	public ServerContext(CommandLine commandLine) throws UnknownHostException,
			MongoException {
		localAddress = commandLine.getOptionValue("l");
		secondaryDomainList = Collections.unmodifiableList(Arrays
				.asList(commandLine.getOptionValues("n")));
	}
}