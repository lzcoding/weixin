package com.lzpro.controller;

import com.lzpro.util.SignUtil;
import com.lzpro.util.WeiXinUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Map;

@Controller
@RequestMapping("/weixin")
public class WeiXinCheckController {

	/**
	 * 微信接入校验
	 * @param signature 微信加密签名
	 * @param timestamp 时间戳
	 * @param nonce 随机数
	 * @param echostr 随机字符串
	 * @return
	 */
	@RequestMapping(value = "/checkAccess",method = RequestMethod.GET)
	@ResponseBody
	public String check(String signature, String timestamp, String nonce, String echostr){

		if (SignUtil.checkSignature(signature, timestamp, nonce)) {
			System.out.println("=======请求校验成功======" + echostr);
			return echostr;
		}
		return "";
	}

	@RequestMapping(value = "/checkAccess",method = RequestMethod.POST)
	public void acceptMessage(HttpServletRequest request, HttpServletResponse response){
		try {
			request.setCharacterEncoding("UTF-8");
			response.setContentType("text/html;charset=UTF-8");
			PrintWriter out=response.getWriter();
			Map<String, String> map = WeiXinUtil.parseXml(request.getInputStream());
			System.out.println(map);
			String ToUserName = map.get("ToUserName");
			String FromUserName = map.get("FromUserName");
			String CreateTime = map.get("CreateTime");
			String MsgType = map.get("MsgType");
			String Content = map.get("Content");

			String MsgId = map.get("MsgId");


			String replyMsg = "<xml>"
					+ "<ToUserName><![CDATA["+FromUserName+"]]></ToUserName>"//回复用户时，这里是用户的openid；但用户发送过来消息这里是微信公众号的原始id
					+ "<FromUserName><![CDATA["+ToUserName+"]]></FromUserName>"//这里填写微信公众号 的原始id；用户发送过来时这里是用户的openid
					+ "<CreateTime>1531553112194</CreateTime>"//这里可以填创建信息的时间，目前测试随便填也可以
					+ "<MsgType><![CDATA[text]]></MsgType>"//文本类型，text，可以不改
					+ "<Content><![CDATA[就问你屌不屌]]></Content>"//文本内容，我喜欢你
					+ "<MsgId>1234567890123456</MsgId> "//消息id，随便填，但位数要够
					+ " </xml>";
//			System.out.println(replyMsg);//打印出来
			out.println(replyMsg);//回复
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
