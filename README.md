# shiro-assistant
shiro助手 自动生成资源权限的数据库结构 半自动化管理权限 实现开发阶段和维护阶段的数据一致性

帮助qq群210722852

本项目是提取自butterfly框架中的权限模块的一部分 在此遵循apache2协议开源

项目依赖 springMVC3.0以上版本
        shiro 1.2.3以上版本
        slf4j
实例使用了hibernate4 你可以根据情况实现其他dao层

主要功能：
1自动生成资源权限的数据库结构 
2开发阶段和维护阶段的权限数据的一致性

解决思路：
当spring容器启动完时，扫描所有使用@Controller注解的类，分析里面是否使用了@SysRes注解标注了资源的类型 资源的名字 资源的有效性等信息
有的话提取这些信息 结合@RequestMapping和@RequiresPermissions注解分析 资源的url和权限字符串 综合处理保存到数据库

注意事项：
1@SysRes注解必须配合@RequiresPermissions使用在@Controller类上
2@SysRes注解在类上时，如果没有@RequestMapping是可以的，有的话也是可以的，工具会自动处理url串
3请在spring配置文件中配置SysResHandlerSpringContextListener时注入generateFlag开关 true为生成数据到数据库 默认为false，一般项目开发完成时，生成一次即可
4一般生成前请先在数据库表中插入一条记录为树根id=0
5主键和id顺序递增自动生成，如果系统希望手动维护id号，请在开发阶段在注解上表明（注意维护唯一性，不建议手动），或者在数据库插入少量的自定义数据时，id请大于10000起编号10000以以下
 为工具使用编号。
6工具生成数据后，请手动在数据库维护parentId 和parentIds 因为id为自动生成 无法生成pid的依赖关系 需要手动配置！



