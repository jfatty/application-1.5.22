package com.babeeta.butterfly.application.app.service.impl;

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

import com.babeeta.butterfly.application.app.service.NamedService;

public class NamedServiceImpl implements NamedService {

	private String appName;
	private static final String APP_NAME_FILE = "name";
	private static final Logger log = LoggerFactory
			.getLogger(NamedServiceImpl.class);

	@Override
	public String getAppName() {
		return appName;
	}

	@Override
	public void nameNewNode(ServletContext servletContext) {

		String ranomUUIDappName = UUID.randomUUID().toString().replaceAll("-",
				"");

		this.appName=ranomUUIDappName;
		
		writeAppNameToFile(servletContext, ranomUUIDappName);

		log.info("name new node to {} success.", ranomUUIDappName);
	}

	private void writeAppNameToFile(ServletContext servletContext,
			String appName) {

		File nameFile = getNameFile(servletContext);

		nameFile.deleteOnExit();

		OutputStream outputStream = null;
		try {
			nameFile.createNewFile();
			outputStream = new FileOutputStream(nameFile);
			outputStream.write(appName.getBytes());
		} catch (IOException e) {
			log.error("wirte appName " + appName + " to file "
					+ nameFile.getAbsolutePath() + " failed.", e);
		} finally {
			try {
				outputStream.close();
			} catch (IOException e) {
				log.error(e.getMessage(), e);
			}
		}

		log.info("write appName {} to file {} success.", appName, nameFile
				.getAbsolutePath());
	}

	private String readAppNameFromFile(ServletContext servletContext) {
		File nameFile = getNameFile(servletContext);
		if (nameFile == null) {
			log.info("app name file is null");
			return null;
		}
		if(!nameFile.exists()){
			log.info("app name file not exists");
			return null;
		}

		InputStream inputStream = null;
		byte[] bts = null;

		try {
			inputStream = new FileInputStream(nameFile);
			bts = new byte[inputStream.available()];
			inputStream.read(bts);

		} catch (FileNotFoundException e) {
			log.info("appName file " + nameFile.getAbsolutePath()
					+ "not found.", e);
			return null;
		} catch (IOException e) {
			log.info("read app name from file " + nameFile.getAbsolutePath()
					+ " failed.", e);
			return null;
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				log.error(e.getMessage(), e);
			}
		}

		appName = new String(bts);

		log.debug("read app name from file {} success,app name is {}", nameFile
				.getAbsolutePath(), appName);

		return appName;
	}

	private File getNameFile(ServletContext servletContext) {
		String webRootPath = servletContext.getRealPath("/");
		File webRoot = new File(webRootPath);
		File nameFile = new File(webRoot.getParentFile().getAbsolutePath()
				+ File.separator + APP_NAME_FILE);
		return nameFile;
	}

	@Override
	public boolean hasName(ServletContext servletContext) {
		return StringUtils.isNotBlank(readAppNameFromFile(servletContext));
	}

}
