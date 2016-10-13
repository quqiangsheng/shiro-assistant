package com.max256.butterfly.common.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * SysResource 系统资源表
 * 用于存储受保护的资源信息
 * 进行权限拦截 fbf
 * 
 */
@Entity
@Table(name = "SYS_RESOURCE")
public class SysResource implements Serializable {
	
	private static final long serialVersionUID = 5367150089836913993L;// 序列化id
	//枚举
	public static enum ResourceType {
		menu("菜单"), 
		button("按钮");

		private final String info;

		private ResourceType(String info) {
			this.info = info;
		}

		public String getInfo() {
			return info;
		}
	}

	
	/*
	 * //静态字符串 区分菜单还是按钮 用于限制type字段 public static String MENU="menu"; public
	 * static String BUTTON="button";
	 */
	private String uuid;// 主键
	private String id; // 编号
	private String name; // 资源节点名称
	private ResourceType type = ResourceType.menu; // 资源类型 菜单或者按钮 默认是菜单
	private String url; // 访问资源路径
	private String permission; // 权限字符串
	private String parentId; // 父编号
	private String parentIds; // 父编号列表

	private String isValid;// 是否停用

	public SysResource() {
		super();

	}

	public SysResource(String uuid, String id, String name, ResourceType type,
			String url, String permission, String parentId, String parentIds,
			String isValid) {
		super();
		this.uuid = uuid;
		this.id = id;
		this.name = name;
		this.type = type;
		this.url = url;
		this.permission = permission;
		this.parentId = parentId;
		this.parentIds = parentIds;
		this.isValid = isValid;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SysResource other = (SysResource) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (isValid == null) {
			if (other.isValid != null)
				return false;
		} else if (!isValid.equals(other.isValid))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (parentId == null) {
			if (other.parentId != null)
				return false;
		} else if (!parentId.equals(other.parentId))
			return false;
		if (parentIds == null) {
			if (other.parentIds != null)
				return false;
		} else if (!parentIds.equals(other.parentIds))
			return false;
		if (permission == null) {
			if (other.permission != null)
				return false;
		} else if (!permission.equals(other.permission))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		if (uuid == null) {
			if (other.uuid != null)
				return false;
		} else if (!uuid.equals(other.uuid))
			return false;
		return true;
	}

	// 资源编号
	@Column(name = "ID", length = 255)
	public String getId() {
		return id;
	}

	// 是否停用本条资源
	@Column(name = "IS_VALID")
	public String getIsValid() {
		return isValid;
	}

	// 资源名字
	@Column(name = "NAME", length = 255)
	public String getName() {
		return name;
	}

	// 资源所属父资源id
	@Column(name = "PARENT_ID")
	public String getParentId() {
		return parentId;
	}

	// 资源所属全部父资源id字符串
	@Column(name = "PARENT_IDS")
	public String getParentIds() {
		return parentIds;
	}

	// 资源权限字符串
	@Column(name = "PERMISSION")
	public String getPermission() {
		return permission;
	}

	// 资源类型
	@Column(name = "TYPE", length = 255)
	@Enumerated(value = EnumType.STRING)
	public ResourceType getType() {
		return type;
	}

	// 资源URL地址
	@Column(name = "URL")
	public String getUrl() {
		return url;
	}

	// 主键 手动指定32位
	@Id
	@Column(name = "UUID", unique = true, nullable = false, length = 32)
	/*@GeneratedValue(generator = "uuidGenerator")
	@GenericGenerator(name = "uuidGenerator", strategy = "org.hibernate.id.UUIDGenerator")*/
	public String getUuid() {
		return uuid;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((isValid == null) ? 0 : isValid.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((parentId == null) ? 0 : parentId.hashCode());
		result = prime * result
				+ ((parentIds == null) ? 0 : parentIds.hashCode());
		result = prime * result
				+ ((permission == null) ? 0 : permission.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((url == null) ? 0 : url.hashCode());
		result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
		return result;
	}

	/**
	 * 判断是否为根节点 
	 * 因为parentId为String类型 用equals来判断 
	 * 只能有一个根节点所有资源权限由这一个根节点发展而来
	 * 根节点id=0 uuid=0 根节点的父节点parentId="-1" 这样的节点为根节点
	 * @return boolean
	 */
	@Transient
	public boolean isRootNode() {
		return parentId.equals("-1");
	}


	/**
	 * 此方法是用来 在选中本节点时 添加子节点时设置parents
	 * 把自己作为父节点的同时把父节点的所有有父节点 也加进来 形成一个节点串 如 :祖祖节点/祖节点/父节点/本节点 用“/”来分割
	 * 
	 * @return
	 */
	public String makeSelfAsParentIds() {
		//如果是直接在根节点添加子节点 需要特殊处理ParentIds
		if(isRootNode()){
			return  "0/";
		}
		//不是根节点时
		return getParentIds() + getId() + "/";
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setIsValid(String isValid) {
		this.isValid = isValid;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public void setParentIds(String parentIds) {
		this.parentIds = parentIds;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

	public void setType(ResourceType type) {
		this.type = type;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	@Override
	public String toString() {
		return "SysResource [uuid=" + uuid + ", id=" + id + ", name=" + name
				+ ", type=" + type + ", url=" + url + ", permission="
				+ permission + ", parentId=" + parentId + ", parentIds="
				+ parentIds + ", isValid=" + isValid + "]";
	}

}
