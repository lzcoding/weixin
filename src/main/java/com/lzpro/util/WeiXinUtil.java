package com.lzpro.util;

import com.alibaba.fastjson.JSONObject;
import com.lzpro.model.AccessToken;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WeiXinUtil {


	/**
	 * 直接获取token实体，并存入session
	 * @return
	 */
	public static AccessToken getAccessToken(){
		AccessToken accessToken = new AccessToken();
		String url = Constant.ACCESS_TOKEN_URL.replace("APPID",Constant.APPID).replace("APPSECRET",Constant.APPSECRET);
		JSONObject jsonObject = HttpClientUtil.doGetStr(url);
		if(jsonObject != null){
			accessToken.setToken(jsonObject.getString("access_token"));
			accessToken.setExpiresIn(jsonObject.getInteger("expires_in"));
			long validMilliSeconds = System.currentTimeMillis()+(accessToken.getExpiresIn()*1000);
			accessToken.setValidTime(validMilliSeconds);
		}
		return accessToken;
	}


	/**
	 * 获取session中的token
	 * @param request
	 * @return
	 */
	public static AccessToken getTimeAccessToken(HttpServletRequest request){
		AccessToken accessToken = (AccessToken) request.getSession().getAttribute(Constant.SESSION_WEIXIN_TOKEN_KEY);
		if( accessToken != null){
			long currentMilliSeconds = System.currentTimeMillis();
			if(accessToken.getValidTime() <= currentMilliSeconds){
				return accessToken;
			}
		}
		accessToken = getAccessToken();
		request.getSession().setAttribute(Constant.SESSION_WEIXIN_TOKEN_KEY,accessToken);
		return accessToken;
	}



	/**
	 * 解析微信发来的请求（XML）
	 *
	 * @param inputStream
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, String> parseXml(InputStream inputStream) throws Exception {

		if (inputStream == null){
			return null;
		}
		Map<String, String> map = new HashMap <String, String>();// 将解析结果存储在HashMap中
		SAXReader reader = new SAXReader();// 读取输入流
		Document document = reader.read(inputStream);
		Element root = document.getRootElement();// 得到xml根元素
		List<Element> elementList = root.elements();// 得到根元素的所有子节点
		for (Element e : elementList) {        // 遍历所有子节点
			map.put(e.getName(), e.getText());
		}
		inputStream.close();        // 释放资源
		inputStream = null;

		return map;
	}


}
