package com.example.zsq.mynews.bean;

public class DataEntity {

	private String title;
	private String digest;
	private String imgsrc;
	private String url_3w;

	public String getUrl_3w() {
		return url_3w;
	}

	public void setUrl_3w(String url_3w) {
		this.url_3w = url_3w;
	}

	public DataEntity(String title, String digest, String imgsrc, String url_3w) {
		this.title = title;
		this.imgsrc = imgsrc;
		this.digest = digest;
		this.url_3w = url_3w;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDigest() {
		return digest;
	}

	public void setDigest(String digest) {
		this.digest = digest;
	}

	public String getImgsrc() {
		return imgsrc;
	}
	public void setImgSc(String imgsrc) {
		this.imgsrc = imgsrc;
	}
	public DataEntity(String title, String imgsrc) {
		super();
		this.title = title;
		this.imgsrc = imgsrc;
	}
	public DataEntity() {
		super();
	}
	
}
