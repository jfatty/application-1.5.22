package com.babeeta.butterfly.application.third.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import javax.servlet.ServletContext;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class NamedServiceImpl implements NamedService{


	private String nodeName;
	private static final String NODE_NAME_FILE = "name";
	private static final Logger log = LoggerFactory
			.getLogger(NamedServiceImpl.class);

	@Override
	public String getNodeName() {
		return nodeName;
	}

	@Override
	public String nameNewNode(ServletContext servletContext) {

		String ranomUUIDNodeName = UUID.randomUUID().toString().replaceAll("-",
				"");

		this.nodeName=ranomUUIDNodeName;
		
		writeNodeNameToFile(servletContext, ranomUUIDNodeName);

		log.info("name new node to {} success.", ranomUUIDNodeName);
		return nodeName;
	}

	private void writeNodeNameToFile(ServletContext servletContext,
			String nodeName) {

		File nameFile = getNameFile(servletContext);

		nameFile.deleteOnExit();

		OutputStream outputStream = null;
		try {
			nameFile.createNewFile();
			outputStream = new FileOutputStream(nameFile);
			outputStream.write(nodeName.getBytes());
		} catch (IOException e) {
			log.error("wirte node " + nodeName + " to file "
					+ nameFile.getAbsolutePath() + " failed.", e);
		} finally {
			try {
				outputStream.close();
			} catch (IOException e) {
				log.error(e.getMessage(), e);
			}
		}

		log.info("write node name {} to file {} success.", nodeName, nameFile
				.getAbsolutePath());
	}

	private String readNodeNameFromFile(ServletContext servletContext) {
		File nameFile = getNameFile(servletContext);
		if (nameFile == null) {
			log.info("node name file is null");
			return null;
		}
		if(!nameFile.exists()){
			log.info("node name file not exists");
			return null;
		}

		InputStream inputStream = null;
		byte[] bts = null;

		try {
			inputStream = new FileInputStream(nameFile);
			bts = new byte[inputStream.available()];
			inputStream.read(bts);

		} catch (FileNotFoundException e) {
			log.info("node Name file " + nameFile.getAbsolutePath()
					+ "not found.", e);
			return null;
		} catch (IOException e) {
			log.info("read node name from file " + nameFile.getAbsolutePath()
					+ " failed.", e);
			return null;
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				log.error(e.getMessage(), e);
			}
		}

		String nodeName = new String(bts);

		log.debug("read node name from file {} success,node name is {}", nameFile
				.getAbsolutePath(), nodeName);

		return nodeName;
	}

	private File getNameFile(ServletContext servletContext) {
		String webRootPath = servletContext.getRealPath("/");
		File webRoot = new File(webRootPath);
		File nameFile = new File(webRoot.getParentFile().getAbsolutePath()
				+ File.separator + NODE_NAME_FILE);
		return nameFile;
	}

	@Override
	public boolean hasName(ServletContext servletContext) {
		return StringUtils.isNotBlank(readNodeNameFromFile(servletContext));
	}


}
