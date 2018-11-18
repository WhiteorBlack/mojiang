package cn.idcby.jiajubang.Bean;

import java.io.Serializable;

import cn.idcby.jiajubang.utils.StringUtils;

public class Version implements Serializable{
	public String OrderIndex;//版本号
	public String Uri;//下载地址
	public String IsMust;//是否必须  1是0否
	public String Memo;//修复内容

	public int getOrderIndex() {
		return StringUtils.convertString2Count(OrderIndex);
	}

	public void setOrderIndex(String orderIndex) {
		OrderIndex = orderIndex;
	}

	public String getUri() {
		return Uri;
	}

	public void setUri(String uri) {
		Uri = uri;
	}

	public boolean isMust() {
		return "1".equals(IsMust);
	}

	public void setIsMust(String isMust) {
		IsMust = isMust;
	}

	public String getMemo() {
		return Memo;
	}

	public void setMemo(String memo) {
		Memo = memo;
	}


//	// 下载地址
//	private String Url;
//	// 新版本号
//	private String Ver;
//	// 文件大小
//	private int FileSize;
//	// 新版说明
//	private String NewInfo;
//	// 新版说明(为转换格式的)
//	private String NewInfo2;
//	// 是否强制 1:必须升级、0：非必须
//	private String IsQiangZhi;
//	public String getUrl() {
//		return Url;
//	}
//	public void setUrl(String url) {
//		Url = url;
//	}
//	public String getVer() {
//		return Ver;
//	}
//	public void setVer(String ver) {
//		Ver = ver;
//	}
//	public int getFileSize() {
//		return FileSize;
//	}
//	public void setFileSize(int fileSize) {
//		FileSize = fileSize;
//	}
//	public String getNewInfo() {
//		return NewInfo;
//	}
//	public void setNewInfo(String newInfo) {
//		NewInfo = newInfo;
//	}
//	public String getIsQiangZhi() {
//		return IsQiangZhi;
//	}
//	public void setIsQiangZhi(String isQiangZhi) {
//		IsQiangZhi = isQiangZhi;
//	}
//
//	public String getNewInfo2() {
//		return NewInfo2;
//	}
//	public void setNewInfo2(String newInfo2) {
//		NewInfo2 = newInfo2;
//	}
//	public static List<Version> parseList(String s) {
//		try {
//			return new Gson().fromJson(s, new TypeToken<List<Version>>() {
//			}.getType());
//		} catch (Exception e) {
//			return null;
//		}
//	}
//
//	public static Version parse(String s) {
//		try {
//			return new Gson().fromJson(s, Version.class);
//		} catch (Exception e) {
//			return null;
//		}
//
//	}
}
