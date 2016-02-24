package com.babeeta.butterfly.application.third.resource;

public enum MessageContentType {
	TYPE_BINARY("binary"),
	TYPE_TEXT("text"),
	TYPE_URL("url"),
	TYPE_AUDIO("audio"),
	TYPE_VIDEO("video"),
	TYPE_IMAGE("image"),
	TYPE_MAP("map"),
	TYPE_APK("apk"),
	TYPE_PHONE("phone"),
	TYPE_JSON("json"),
	TYPE_ARCHIVE("archive");

	private String tag;

	private MessageContentType(String tag) {
		this.tag = tag;
	}

	public String getTag() {
		return tag;
	}

	public static MessageContentType getByTag(String tag) {
		if (tag.equalsIgnoreCase(MessageContentType.TYPE_BINARY.getTag())) {
			return MessageContentType.TYPE_BINARY;
		} else if (tag.equalsIgnoreCase(MessageContentType.TYPE_TEXT.getTag())) {
			return MessageContentType.TYPE_TEXT;
		} else if (tag.equalsIgnoreCase(MessageContentType.TYPE_URL.getTag())) {
			return MessageContentType.TYPE_URL;
		} else if (tag.equalsIgnoreCase(MessageContentType.TYPE_AUDIO.getTag())) {
			return MessageContentType.TYPE_AUDIO;
		} else if (tag.equalsIgnoreCase(MessageContentType.TYPE_VIDEO.getTag())) {
			return MessageContentType.TYPE_VIDEO;
		} else if (tag.equalsIgnoreCase(MessageContentType.TYPE_IMAGE.getTag())) {
			return MessageContentType.TYPE_IMAGE;
		} else if (tag.equalsIgnoreCase(MessageContentType.TYPE_MAP.getTag())) {
			return MessageContentType.TYPE_MAP;
		} else if (tag.equalsIgnoreCase(MessageContentType.TYPE_APK.getTag())) {
			return MessageContentType.TYPE_APK;
		} else if (tag.equalsIgnoreCase(MessageContentType.TYPE_PHONE.getTag())) {
			return MessageContentType.TYPE_PHONE;
		} else if (tag.equalsIgnoreCase(MessageContentType.TYPE_JSON.getTag())) {
			return MessageContentType.TYPE_JSON;
		} else if (tag.equalsIgnoreCase(MessageContentType.TYPE_ARCHIVE
				.getTag())) {
			return MessageContentType.TYPE_ARCHIVE;
		} else {
			return null;
		}
	}
}
