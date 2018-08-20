package com.plf.weather.controller;

import java.net.URI;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

@RestController
public class WeatherController {

	/**
	 * 这个接口会返回未来三天的天气情况
	 * 
	 * @param city 要查询的城市，默认值为深圳
	 * @return com.alibaba.fastjson.JSON 对象，可以是对象或者数组
	 * @throws Exception
	 */
	@RequestMapping("getThreeDaysForecastByCity")
	//设置默认参数为深圳
	public JSON getThreeDaysForecastByCity(@RequestParam(defaultValue="深圳",required=false) String city) throws Exception {
		//创建httpClient
		CloseableHttpClient httpClient = HttpClients.createDefault();
		//创建uri构建对象
		URIBuilder uriBuilder = new URIBuilder("https://www.sojson.com/open/api/weather/json.shtml");
		//添加参数
		uriBuilder.addParameter("city", city);
		//构建uri
		URI uri = uriBuilder.build();
		System.out.println("uri:"+uri);
		//构建get请求
		HttpGet get = new HttpGet(uri);
		//执行get请求
		CloseableHttpResponse response = httpClient.execute(get);
		//得到状态码,200成功
		int statusCode = response.getStatusLine().getStatusCode();
		System.out.println("statusCode:"+statusCode);
		//得到响应体
		HttpEntity httpEntity = response.getEntity();
		//转为string
		String bodyString = EntityUtils.toString(httpEntity,"utf-8");
		System.out.println("bodyString:"+bodyString);
		//关闭资源
		response.close();
		httpClient.close();
		//------------------------------------------------------------------------
		//进行json解析,把字符串转为JSONObject
		JSONObject jsonObject = JSON.parseObject(bodyString);
		//得到状态码，这个接口请求过于频繁会失败
		int status = jsonObject.getIntValue("status");
		//请求成功则继续解析
		if(status==200) {
			//需要的数据在data下的forecast下，且是数组，所以使用JSONArray
			JSONArray forecastJsonArray = jsonObject.getJSONObject("data").getJSONArray("forecast");
			//我们只需要未来三天的天气，这个接口会返回今天及未来四天的，移除第四天，和今天，每删一个size减少1,注意下标
			forecastJsonArray.remove(4);
			forecastJsonArray.remove(0);
			System.out.println("forecastJson:"+forecastJsonArray);
			return forecastJsonArray;
		}
		return jsonObject;
	}
	

}
