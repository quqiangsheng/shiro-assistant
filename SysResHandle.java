package com.max256.butterfly.common.annotation;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.max256.butterfly.common.entity.SysResource;



/**
 * 资源管理注解 
 * 扫描器传入类的class对象分析是否包含资源权限
 * @author fbf
 * @since 2016年10月12日 下午4:34:37
 * @version V1.0
 * 
 */
public class SysResHandle {

	/**
	 * 根据Controller对象的class对象
	 * 扫描资源权限组装成List<SysResource>返回
	 * @param clazz 控制器的class对象
	 * @return List<SysResource>
	 */
	public static List<SysResource> getSysResourceListByClass(Class<?> clazz) {
		
		//标志位 判断controller上是否有url 有的话 方法上的RequestMapping的url需要加上类上的url拼成一起 放入方法上的SysResource
		boolean hasControllerRequestMapping=false;
		//控制器上的URL
		String  controllerURL="";
		//准备返回数据
		List<SysResource> list=new ArrayList<SysResource>();
		//1分析类上的注解
		if(clazz.isAnnotationPresent(Controller.class)//必须是Controller
				&& clazz.isAnnotationPresent(SysRes.class)//必须有自定义SysResource注解
				&& clazz.isAnnotationPresent(RequiresPermissions.class)){//必须有自定义RequiresPermissions注解
			//取得注解对象
			SysRes sysResource = (SysRes) clazz.getAnnotation(SysRes.class);
			RequiresPermissions requiresPermissions = (RequiresPermissions) clazz.getAnnotation(RequiresPermissions.class);
			//取得权限字符串  权限字符串只取一个
			String permissions = requiresPermissions.value()[0];
			//类上的RequestMapping不是必须的 有的话分析其url
			String URLString = "";//准备URLString
			if(clazz.isAnnotationPresent(RequestMapping.class)){
				RequestMapping requestMapping = (RequestMapping) clazz.getAnnotation(RequestMapping.class);
				String[] URLStrings = requestMapping.value();// url字符串														
				// 遍历出来每一个url逗号分隔
				for (int i = 0; i < URLStrings.length; i++) {
					URLString = URLString + URLStrings[i] + ",";
				}
				URLString = URLString.substring(0, URLString.length() - 1);// 截取不要最后一个标点
				//改变类上有url的标记
				hasControllerRequestMapping=true;
				//统一风格 类上url加/后缀
				if(URLString.length()>1&&StringUtils.endsWith(URLString, "/")){
					//url处理
					controllerURL=URLString;
				}
				if(URLString.length()>1&&!StringUtils.endsWith(URLString, "/")){
					//url处理
					controllerURL=URLString+"/";
				}
				//统一风格controllerURL前缀/开头
				if(controllerURL.length()>1&&!StringUtils.startsWith(controllerURL, "/")){
					//加上/
					controllerURL="/"+controllerURL;
				}
			}
			//准备生成资源对象
			SysResource newSysResource = new SysResource();
			newSysResource.setIsValid(sysResource.isValid());
			newSysResource.setType(sysResource.type());
			newSysResource.setUrl(controllerURL);
			newSysResource.setName(sysResource.name());
			newSysResource.setPermission(permissions);
			newSysResource.setParentId(sysResource.parentId());
			newSysResource.setParentIds(sysResource.parentIds());
			newSysResource.setId(sysResource.id());
			list.add(newSysResource);
		}
		
		//2分析方法上的注解
		//得到类里所有的方法
		Method[] methods = clazz.getDeclaredMethods();
		for (Method method : methods) {
			// 同时具有ReuestMapping SysResource 和RequiresPermissions注解才可以
			if (method.isAnnotationPresent(SysRes.class) && method.isAnnotationPresent(RequestMapping.class)
					&& method.isAnnotationPresent(RequiresPermissions.class)) {
				SysRes sysResource = (SysRes) method.getAnnotation(SysRes.class);
				RequestMapping requestMapping = (RequestMapping) method.getAnnotation(RequestMapping.class);
				RequiresPermissions requiresPermissions = (RequiresPermissions) method.getAnnotation(RequiresPermissions.class);
				String[] URLStrings = requestMapping.value();// url字符串														
				String URLString = "";
				// 遍历出来每一个url逗号分隔
				for (int i = 0; i < URLStrings.length; i++) {
					URLString = URLString + URLStrings[i] + ",";
				}
				URLString = URLString.substring(0, URLString.length() - 1);// 截取不要最后一个标点
				//分析类上是否有url
				if(hasControllerRequestMapping){
					//每个方法上的url需要和类上的拼传组成一个
					if(URLString.length()>1&&StringUtils.startsWith(URLString, "/")){
						//是否是以/开头的 是的话 去掉 因为类上的后缀已经加了url
						URLString=controllerURL+URLString.substring(1);
					}
					if(URLString.length()>1&&!StringUtils.startsWith(URLString, "/")){
						//是否是以/开头的 没的话 直接拼接
						URLString=controllerURL+URLString;
					}
					
				}else{
					//统一风格 以/开头 没有父url的情况下
					if(URLString.length()>1&&!StringUtils.startsWith(URLString, "/")){
						//加上/
						URLString="/"+URLString;
					}
				}

				String permissions = requiresPermissions.value()[0];// 权限字符串只取一个
				SysResource newSysResource = new SysResource();
				newSysResource.setIsValid(sysResource.isValid());
				newSysResource.setType(sysResource.type());
				newSysResource.setUrl(URLString);
				newSysResource.setName(sysResource.name());
				newSysResource.setPermission(permissions);
				newSysResource.setParentId(sysResource.parentId());
				newSysResource.setParentIds(sysResource.parentIds());
				newSysResource.setId(sysResource.id());
				list.add(newSysResource);
			}
			
		}
		return list;
	}
}
