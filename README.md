# localproxy
【小工具】局域网穿透到公网

# maven编译打包
mvn clean package

# 修改配置
1.local.properties
remote.ip 公网ip，例如阿里云服务器ip, 例如47.100.220.181
remote.helpdesk.port 默认可以不改， 要和remote.properties中的remote.helpdesk.port一样
target.ip 局域网内想被放出去的ip
target.port=局域网内想被放出去的端口

2.remote.properties
remote.access.port 通过公网访问的端口, 例如9527
remote.helpdesk.port 默认可以不改， 要和local.properties中的remote.helpdesk.port一样

# 运行
1.在公网服务器运行 bin/remote.sh
2.在局域网机器运行 local.bat
例如，代码本地tomcat发布web服务，这样访问 http://47.100.220.181:9527/xxx

# 流程图
![image](https://github.com/huoxing0303/localproxy/blob/master/src/main/resources/process.jpg)
