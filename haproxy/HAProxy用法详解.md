# HAProxy用法详解

[TOC]

## 一、HAProxy简介

1. HAProxy 是一款提供高可用性、负载均衡以及基于TCP（第四层）和HTTP（第七层）应用的代理软件，支持虚拟主机，它是免费、快速并且可靠的一种解决方案。 HAProxy特别适用于那些负载特大的web站点，这些站点通常又需要会话保持或七层处理。HAProxy运行在时下的硬件上，完全可以支持数以万计的 并发连接。并且它的运行模式使得它可以很简单安全的整合进您当前的架构中， 同时可以保护你的web服务器不被暴露到网络上。 
2. HAProxy 实现了一种事件驱动、单一进程模型，此模型支持非常大的并发连接数。多进程或多线程模型受内存限制 、系统调度器限制以及无处不在的锁限制，很少能处理数千并发连接。事件驱动模型因为在有更好的资源和时间管理的用户端(User-Space) 实现所有这些任务，所以没有这些问题。此模型的弊端是，在多核系统上，这些程序通常扩展性较差。这就是为什么他们必须进行优化以 使每个CPU时间片(Cycle)做更多的工作。 
3. HAProxy 支持连接拒绝 : 因为维护一个连接的打开的开销是很低的，有时我们很需要限制攻击蠕虫（attack bots），也就是说限制它们的连接打开从而限制它们的危害。 这个已经为一个陷于小型DDoS攻击的网站开发了而且已经拯救了很多站点，这个优点也是其它负载均衡器没有的。
4. HAProxy 支持全透明代理（已具备硬件防火墙的典型特点）: 可以用客户端IP地址或者任何其他地址来连接后端服务器. 这个特性仅在Linux 2.4/2.6内核打了cttproxy补丁后才可以使用. 这个特性也使得为某特殊服务器处理部分流量同时又不修改服务器的地址成为可能。 

## 二、安装配置HAProxy

### 安装HAProxy

```shell
yum install -y haproxy #直接使用RPM来安装

rpm -qi haproxy ## 查看安装版本

rpm -ql haproxy ## 查看安装版本的文件以及路径
```

### 源码编译方式安装

```shell
sudo yum -y install gcc make

## 创建运行haproxy时，使用的用户。在此我们使用haproxy这个用户，而且此用户不能登录到系统
sudo useradd -m haproxy

## 下载源代码
wget https://www.haproxy.org/download/1.8/src/haproxy-1.8.13.tar.gz
tar -xzvf haproxy-1.8.13.tar.gz
cd haproxy-1.8.13/
cat README
## 编译
make TARGET=linux26 ARCH=x86_64 PREFIX=/usr/local/haproxy
## 安装
sudo make install PREFIX=/usr/local/haproxy

ll /usr/local/haproxy/
```



### 配置文件详解

**haproxy配置分为五部分**

* global ：全局配置主要用于设定义全局参数，属于进程级的配置，通常和操作系统配置有关 
* default:  配置默认参数，这些参数可以被用到frontend，backend，Listen组件，在此部分中设置的参数值，默认会自动引用到下面的frontend、backend、listen部分中，因引，某些参数属于公用的配置，只需要在defaults部分添加一次即可。而如果frontend、backend、listen部分也配置了与defaults部分一样的参数，Defaults部分参数对应的值自动被覆盖

* frontend: 接收请求的前端虚拟节点，Frontend可以更加规则直接指定具体使用后端的backend) frontend是在haproxy 1.3版本以后才引入的一个组件，同时引入的还有backend组件。通过引入这些组件，在很大程度上简化了haproxy配置文件的复杂性。forntend可以根据ACL规则直接指定要使用的后端backend
* backend: 在HAProxy1.3版本之前，HAProxy的所有配置选项都在这个部分中设置。为了保持兼容性，haproxy新的版本依然保留了listen组件配置试。两种配置方式任选一中 
* listen: (Fronted和backend的组合体) 比如haproxy实例状态监控部分配置 

#### global

​	

```shell
global
        log 127.0.0.1 local3        #定义haproxy日志输出设置
        log 127.0.0.1   local1 notice        
        #log loghost    local0 info #定义haproxy 日志级别
        ulimit-n 82000              #设置每个进程的可用的最大文件描述符
        maxconn 20480               #默认最大连接数
        chroot /usr/local/haproxy   #chroot运行路径
        uid 99                      #运行haproxy 用户 UID
        gid 99                      #运行haproxy 用户组gid
        daemon                      #以后台形式运行harpoxy
        nbproc 1                    #设置进程数量
        pidfile /usr/local/haproxy/run/haproxy.pid  #haproxy 进程PID文件
        #debug                      #haproxy调试级别，建议只在开启单进程的时候调试
        #quiet
```

1. log：全局的日志配置，local0是日志输出设置，info表示日志级别（err，waning，info，debug） 
2. maxconn：设定每个HAProxy进程可接受的最大并发连接数，此选项等同于linux命令选项”ulimit -n” 
3. chroot ：修改haproxy的工作目录至指定的目录并在放弃权限之前执行chroot()操作,可以提升haproxy的安全级别，不过需要注意的是要确保指定的目录为空目录且任何用户均不能有写权限； 
4. daemon：让haproxy以守护进程的方式工作于后台，其等同于“-D”选项的功能，当然，也可以在命令行中以“-db”选项将其禁用； 
5. nbproc ：指定启动的haproxy进程个数，只能用于守护进程模式的haproxy；默认只启动一个进程，鉴于调试困难等多方面的原因，一般只在单进程仅能打开少数文件描述符的场景中才使用多进程模式； 
6. pidfile：将haproxy的进程写入pid文件。 
7. ulimit-n：设定每进程所能够打开的最大文件描述符数目，默认情况下其会自动进行计算，因此不推荐修改此选项 
8. stats socket <path>定义统计信息保存位置。 

#### default

**用于设置配置默认参数，这些参数可以被用到frontend，backend，Listen组件；**

**此部分中设置的参数值，默认会自动引用到下面的frontend、backend、listen部分中，因引，某些参数属于公用的配置，只需要在 defaults部分添加一次即可。而如果frontend、backend、listen部分也配置了与defaults部分一样的参 数，Defaults部分参数对应的值自动被覆盖**

```shell
defaults
        log    global         #引入global定义的日志格式
        mode    http          #所处理的类别(7层代理http，4层代理tcp)
        maxconn 50000         #最大连接数
        option  httplog       #日志类别为http日志格式
        option  httpclose     #每次请求完毕后主动关闭http通道
        option  dontlognull   #不记录健康检查日志信息
        option  forwardfor    #如果后端服务器需要获得客户端的真实ip，需要配置的参数，
                              可以从http header 中获取客户端的IP
        retries 3             #3次连接失败就认为服务器不可用，也可以通过后面设置
        
        option redispatch  
#《---上述选项意思是指serverID 对应的服务器挂掉后，强制定向到其他健康的服务器, 当使用了 cookie时，
haproxy将会将其请求的后端服务器的serverID插入到cookie中，以保证会话的SESSION持久性；而此时，如果
后端的服务器宕掉了,但是客户端的cookie是不会刷新的，如果设置此参数，将会将客户的请求强制定向到另外一个
后端server上，以保证服务的正常---》
        
        stats refresh 30       #设置统计页面刷新时间间隔
        option abortonclose    #当服务器负载很高的时候，自动结束掉当前队列处理比较久的连接
        balance roundrobin     #设置默认负载均衡方式，轮询方式
        #balance source        #设置默认负载均衡方式，类似于nginx的ip_hash      
        #contimeout 5000        #设置连接超时时间
        #clitimeout 50000       #设置客户端超时时间
        #srvtimeout 50000       #设置服务器超时时间
        timeout http-request    10s  #默认http请求超时时间
        timeout queue           1m   #默认队列超时时间
        timeout connect         10s  #默认连接超时时间
        timeout client          1m   #默认客户端超时时间
        timeout server          1m   #默认服务器超时时间
        timeout http-keep-alive 10s  #默认持久连接超时时间
        timeout check           10s  #设置心跳检查超时时间
```

1. `mode  http` 设置haproxy的运行模式，有三种｛http|tcp|health｝。注意：如果haproxy中还要使用4层的应用（mode tcp）的话，不建议在此定义haproxy的运行模式。

   设置HAProxy实例默认的运行模式有tcp、http、health三种可选：

   tcp模式：在此模式下，客户端和服务器端之前将建立一个全双工的连接，不会对七层报文做任何检查，默认为tcp模式，经常用于SSL、SSH、SMTP等应用。
   http模式：在此模式下，客户端请求在转发至后端服务器之前将会被深度分板，所有不与RFC格式兼容的请求都会被拒绝。
   health：已基本不用了

2. `log   global` 设置日志继承全局配置段的设置。 

3. `option httplog ` 表示开始打开记录http请求的日志功能。 

4. `option dontlognull`  如果产生了一个空连接，那这个空连接的日志将不会记录。 

5.  `option http-server-close ` 打开http协议中服务器端关闭功能，使得支持长连接，使得会话可以被重用，使得每一个日志记录都会被记录 

6. `option forwardfor except 127.0.0.0/8`  如果上游服务器上的应用程序想记录客户端的真实IP地址，haproxy会把客户端的IP信息发送给上游服务器，在HTTP请求中添加”X-Forwarded-For”字段,但当是haproxy自身的健康检测机制去访问上游服务器时是不应该把这样的访问日志记录到日志中的，所以用except来排除127.0.0.0，即haproxy身 

7. `option redispatch`  当与上游服务器的会话失败(服务器故障或其他原因)时，把会话重新分发到其他健康的服务器上,当原来故障的服务器恢复时，会话又被定向到已恢复的服务器上。还可以用”retries”关键字来设定在判定会话失败时的尝试连接的次数。 

8. `retries 3`  向上游服务器尝试连接的最大次数，超过此值就认为后端服务器不可用。 

9. `option abortonclose`  当haproxy负载很高时，自动结束掉当前队列处理比较久的链接 

10. `timout http-request 10s`  客户端发送http请求的超时时间 

11. `timeout queue 1m`  当上游服务器在高负载响应haproxy时，会把haproxy发送来的请求放进一个队列中，timeout queue定义放入这个队列的超时时间 

12. `timeout connect 5s`  haproxy与后端服务器连接超时时间，如果在同一个局域网可设置较小的时间 

13. `timeout client 1m`  定义客户端与haproxy连接后，数据传输完毕，不再有数据传输，即非活动连接的超时时间。 

14. `timeout server 1m`  定义haproxy与上游服务器非活动连接的超时时间。 

15. `timeout http-keep-alive 10s`  设置新的http请求连接建立的最大超时时间，时间较短时可以尽快释放出资源，节约资源 

16. `timeout check 10s`  健康检测的时间的最大超时时间。 

17. `maxconn 3000`  最大并发连接数。 

18. `contimeout 5000`  设置成功连接到一台服务器的最长等待时间，默认单位是毫秒，新版本的haproxy使用timeout connect替代，该参数向后兼容。 

19. `clitimeout 3000`  设置连接客户端发送数据时的成功连接最长等待时间，默认单位是毫秒，新版本haproxy使用timeout client替代。该参数向后兼容。 

20. `srvtimeout 3000`  设置服务器端回应客户度数据发送的最长等待时间，默认单位是毫秒，新版本haproxy使用timeout server替代。该参数向后兼容 

21. `balance roundrobin`  设置负载算法为：轮询算法rr 

    1. roundrobin：基于权重进行的轮叫算法，在服务器的性能分布经较均匀时这是一种最公平的，最合量的算法 
    2. static-rr：也是基于权重时行轮叫的算法，不过此算法为静态方法，在运行时调整其服务权重不会生效 
    3. source：是基于请求源IP的算法，此算法对请求的源IP时行hash运算，然后将结果与后端服务器的权理总数相除后转发至某台匹配的后端服务器，这种方法可以使用一个客户端IP的请求始终转发到特定的后端服务器 
    4. leastconn：此算法会将新的连接请求转发到具有最少连接数目的后端服务器。在会话时间较长的场景中推荐使用此算法。例如数据库负载均衡等。此算法不适合会话较短的环境，如基于http的应用 
    5. uri：此算法会对部分或整个URI进行hash运算，再经过与服务器的总权重要除，最后转发到某台匹配的后端服务器上 
    6. uri_param：此算法会椐据URL路径中的参数时行转发，这样可以保证在后端真实服务器数量不变时，同一个用户的请求始终分发到同一台机器上 
    7. hdr：此算法根据httpd头时行转发，如果指定的httpd头名称不存在，则使用roundrobin算法进行策略转发 
    8. rdp-cookie(name)：示根据据cookie(name)来锁定并哈希每一次TCP请求 

#### frontend

**frontend是在haproxy 1.3版本以后才引入的一个组件，同时引入的还有backend组件。通过引入这些组件，在很大程度上简化了haproxy配置文件的复杂性。frontend根据任意 HTTP请求头内容做ACL规则匹配,然后把请求定向到相关的backend**

```shell
frontend http_80_in
    bind 0.0.0.0:80    #设置监听端口，即haproxy提供的web服务端口，和lvs的vip 类似
    mode http          #http 的7层模式
    log global         #应用全局的日志设置
    option httplog     #启用http的log
    option httpclose   #每次请求完毕后主动关闭http通道，HAproxy不支持keep-alive模式     
    option forwardfor         #如果后端服务器需要获得客户端的真实IP需要配置此参数，将可以从
HttpHeader中获得客户端IP
    default_backend wwwpool   #设置请求默认转发的后端服务池，
```

1. `frontend http_80_in`  定义一个名为http_80_in的frontend 
2. `bind 0.0.0.0:80`  定义haproxy前端部分监听的端口。 
3. `mode http`  定义为http模式 
4. `log global` 继承global中log的定义 
5. `option forwardfor`  使后端server获取到客户端的真实IP 

#### backend

**用来定义后端服务集群的配置，真实服务器，一个Backend对应一个或者多个实体服务器** 

```shell
 backend wwwpool         #定义wwwpool服务器组。
 mode http           #http的7层模式
 option  redispatch
 option  abortonclose
 balance source      #负载均衡的方式，源哈希算法
 cookie  SERVERID    #允许插入serverid到cookie中，serverid后面可以定义
 option  httpchk GET /test.html   #心跳检测
 server web1 10.1.1.2:80 cookie 2 weight 3 check inter 2000 rise 2 fall 3 maxconn 8
```

1. `cookie`  表示充许向cookie插入SERVERID,每台服务器的SERVERID可以下面的server关键字中使用cookie关键字定义 
2. `option httpchk`  此选项表示启用HTTP的服务状态检测功能。 HAProxy作为一个专业的负载均衡器，并且它支持对backend部分指定的后端服务节点的 健康检查，以保证在后端的backend中某个节点不能服务时，把从frontend端进来的客户端请求分配至backend中其他健康节点上，从而保证 整体服务的可用性 
3. `method`  表示HTTP请求的方式，常用的有OPTIONS、GET、HEAD几种方式。 一般健康检查可以采用HEAD方式进行，而不是采用GET方式，这是因为HEAD方式没有数据返回，仅检查Response的HEAD是不是状态码200。因此，相对于GET，HEAD方式更快，更简单 
4. `uri`  表示要检测的URL地址，通过执行此URL，可以获取后端服务器的运行状态，在正常情况下返回状态码200，返回其他状态码均为异常状态 
5. `version`  指定心跳检测时的HTTP的版本号 
6. `server`  用来定义多台后端真实服务器,不能用于defaults和frontend部分,格式为:`server name address:port param`
7. `name`  为后端真实服务器指定一个内部名称，随便这下义一个即可 
8. `address`：后端真实服务器的iP地址或主机名。 
9. `port`：指定连接请求发往真实服务器时的目标端口，在未设定时，将使用客户端请求时的同一端口 
10. `param`：为后端服务器设定的一系列参数，可用参数非常多 
11. `check`：表示启用对此后端服务器执行健康检查 
12. `inter`：设置健康状态检查的时间间隔，单位为毫秒
13. `rise`：设置人故障状态转换至正常状态需要成功检查的次数，如 rise 2：表示2次检查正确就认为此服务器可用 
14. `fall`：设置后端服务器从正常状态转换为不可用状态需要检查的次数，如 fall 3表示3 次检查失败就认为此服务器不可用 
15. `cookie`：为指定的后端服务器设定cookie值，此外指定的值将在请求入站时被检查，第一次为此值挑选的后端服务器将在后续的请求中一直被选中，其目的在于实现持久连接的功能 
16. `cookie server1`：表示web1的serverid为server1 
17. `weigth`：设置后端真实服务器的权重，默认为1，最大值为256，设置为0表示不参与负载均衡。 
18. `maxconn`：设定每个backend中server进程可接受的最大并发连接数，此选项等同于linux命令选项”ulimit -n” 
19. `backup`：设置后端真实服务器的备份服器，仅仅在后端所有真实服务器均不可用的情况下才启用 

#### listen

**常常用于状态页面监控，以及后端server检查，是Fronted和backend的组合体**

```shell
listen  admin_status           #Frontend和Backend的组合体,监控组的名称，按需自定义名称 
    bind 0.0.0.0:8888              #监听端口 
    mode http                      #http的7层模式 
    log 127.0.0.1 local3 err       #错误日志记录 
    stats refresh 5s               #每隔5秒自动刷新监控页面 
    stats uri /admin?stats         #监控页面的url访问路径 
    stats realm itnihao\ welcome   #监控页面的提示信息 
    stats auth admin:admin         #监控页面的用户和密码admin,可以设置多个用户名 
    stats auth admin1:admin1       #监控页面的用户和密码admin1 
    stats hide-version             #隐藏统计页面上的HAproxy版本信息  
    stats admin if TRUE            #手工启用/禁用,后端服务器(haproxy-1.4.9以后版本)


```

**PS: 代理配置段主要有以下几部分配置块:**

1. Frontend:定义面向客户的监听的地址和端口,以及关联的后端的服务器组 
2. Backend:后端服务器组的定义
3. Listen:组合的方式直接定义frontend及相关的backend
4. Defaults:默认的配置。其中listen配置块可以直接使用frontend和backend中任意一项参数配置， 比如acl语法配置以及server语法配置参数

## 三、ACL

haproxy的ACL用于实现基于请求报文的首部、响应报文的内容或其它的环境状态信息来做出转发决策，这大大增强了其配置弹性。其配置法则通常分为两步，首先去定义ACL，即定义一个测试条件，而后在条件得到满足时执行某特定的动作，如阻止请求或转发至某特定的后端。定义ACL的语法格式如下 

```shell
acl <aclname> <criterion> [flags] [operator] <value> ...
```

<aclname>：ACL名称，区分字符大小写，且其只能包含大小写字母、数字、-(连接线)、_(下划线)、.(点号)和:(冒号)；haproxy中，acl可以重名，这可以把多个测试条件定义为一个共同的acl；

<criterion>：测试标准，即对什么信息发起测试；测试方式可以由[flags]指定的标志进行调整；而有些测试标准也可以需要为其在之前指定一个操作符[operator]；

[flags]：目前haproxy的acl支持的标志位有3个：

-i：不区分中模式字符的大小写；

-f：从指定的文件中加载模式；

--：标志符的强制结束标记，在模式中的字符串像标记符时使用；

<value>：acl测试条件支持的值有以下四类：

整数或整数范围：如1024:65535表示从1024至65535；仅支持使用正整数(如果出现类似小数的标识，其为通常为版本测试)，且支持使用的操作符有5个，分别为eq、ge、gt、le和lt；

字符串：支持使用“-i”以忽略字符大小写，支持使用“\”进行转义；如果在模式首部出现了-i，可以在其之前使用“–”标志位；

正则表达式：其机制类同字符串匹配；

IP地址及网络地址；

同一个acl中可以指定多个测试条件，这些测试条件需要由逻辑操作符指定其关系。条件间的组合测试关系有三种：“与”(默认即为与操作)、“或”(使用“||”操作符)以及“非”(使用“!”操作符)



## 四、配置案列

### http服务配置

```shell
global
    log         127.0.0.1 local2
    chroot      /var/empty
    pidfile     /var/run/haproxy.pid
    maxconn     20000
    user        haproxy
    group       haproxy
    daemon
    spread-checks 2
defaults
    mode                    http
    log                     global
    option                  httplog
    option                  dontlognull
    option http-server-close
    option forwardfor       except 127.0.0.0/8
    option                  redispatch
    timeout http-request    2s
    timeout queue           3s
    timeout connect         1s
    timeout client          10s
    timeout server          2s
    timeout http-keep-alive 10s
    timeout check           2s
    maxconn                 18000 

frontend http-in
    bind             *:80
    mode             http
    log              global
    capture request  header Host len 20
    capture request  header Referer len 60
    default_backend  static_group

backend static_group
    balance            roundrobin
    option             http-keep-alive
    http-reuse         safe
    option httpchk     GET /index.html
    http-check expect  status 200
    server staticsrv1  192.168.13.43:80 check rise 1 maxconn 5000
    server staticsrv2  192.168.13.44:80 check rise 1 maxconn 5000

listen report_stats
        bind *:8081
        stats enable
        stats hide-version
        stats uri    /hastats
        stats realm  "pls enter your name"
        stats auth   admin:admin
        stats admin  if TRUE
```

### 动静分离示例完整示例

```shell
global
    log         127.0.0.1 local2
    chroot      /var/empty
    pidfile     /var/run/haproxy.pid
    maxconn     20000
    user        haproxy
    group       haproxy
    daemon
    spread-checks 2
defaults
    mode                    http
    log                     global
    option                  httplog
    option                  dontlognull
    option http-server-close
    option forwardfor       except 127.0.0.0/8
    option                  redispatch
    timeout http-request    2s
    timeout queue           3s
    timeout connect         1s
    timeout client          10s
    timeout server          2s
    timeout http-keep-alive 10s
    timeout check           2s
    maxconn                 18000 

frontend http-in
    bind             *:80
    mode             http
    log              global
    capture request  header Host len 20
    capture request  header Referer len 60
    acl url_static   path_beg  -i /static /images /stylesheets
    acl url_static   path_end  -i .jpg .jpeg .gif .png .ico .bmp .css .js
    acl url_static   path_end  -i .html .htm .shtml .shtm .pdf .mp3 .mp4 .rm .rmvb .txt
    acl url_static   path_end  -i .zip .rar .gz .tgz .bz2 .tgz

    use_backend      static_group   if url_static
    default_backend  dynamic_group

backend static_group
    balance            roundrobin
    option             http-keep-alive
    http-reuse         safe
    option httpchk     GET /index.html
    http-check expect  status 200
    server staticsrv1  192.168.13.43:80 check rise 1 maxconn 5000
    server staticsrv2  192.168.13.44:80 check rise 1 maxconn 5000

backend dynamic_group
    cookie appsrv insert nocache
    balance roundrobin
    option http-server-close
    option httpchk     GET /index
    http-check expect  status 200
    server appsrv1 192.168.13.43:8080  check rise 1 maxconn 3000 cookie appsrv1
    server appsrv2 192.168.13.44:8080  check rise 1 maxconn 3000 cookie appsrv2

listen report_stats
        bind *:8081
        stats enable
        stats hide-version
        stats uri    /hastats
        stats realm  "pls enter your name"
        stats auth   admin:admin
        stats admin  if TRUE
```

### 负载均衡MySQL服务的配置示例

```shell
#---------------------------------------------------------------------
# Global settings
#---------------------------------------------------------------------
global
# to have these messages end up in /var/log/haproxy.log you will
# need to:
#
# 1) configure syslog to accept network log events. This is done
# by adding the '-r' option to the SYSLOGD_OPTIONS in
# /etc/sysconfig/syslog
#
# 2) configure local2 events to go to the /var/log/haproxy.log
# file. A line like the following can be added to
# /etc/sysconfig/syslog
#
# local2.* /var/log/haproxy.log
#
log 127.0.0.1 local2
chroot /var/lib/haproxy
pidfile /var/run/haproxy.pid
maxconn 4000
user haproxy
group haproxy
daemon
defaults
mode tcp
log global
option httplog
option dontlognull
retries 3
timeout http-request 10s
timeout queue 1m
timeout connect 10s
timeout client 1m
timeout server 1m
timeout http-keep-alive 10s
timeout check 10s
maxconn 600
listen stats
mode http
bind 0.0.0.0:1080
stats enable
stats hide-version
stats uri /haproxyadmin?stats
stats realm Haproxy\ Statistics
stats auth admin:admin
stats admin if TRUE
frontend mysql
bind *:3306
mode tcp
log global
default_backend mysqlservers
backend mysqlservers
balance leastconn
server dbsrv1 192.168.1.111:3306 check port 3306 intval 2 rise 1 fall 2 maxconn 300
server dbsrv2 192.168.1.112:3306 check port 3306 intval 2 rise 1 fall 2 maxconn 300
```



## 五、Haproxy + keepalived高可用

vim /etc/sysconfig/selinux 

`#SELINUX=enforcing                #注释掉`

`#SELINUXTYPE=targeted             #注释掉`

SELINUX=disabled                  #增加