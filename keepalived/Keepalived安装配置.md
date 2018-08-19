# Keepalived 安装配置



```
vim /etc/sysconfig/selinux

#SELINUX=enforcing                #注释掉
#SELINUXTYPE=targeted             #注释掉
SELINUX=disabled                  #增加
```



```
vim /etc/sysconfig/iptables
-A INPUT -s 192.168.13.0/24 -d 224.0.0.18 -j ACCEPT
-A INPUT -s 192.168.13.0/24 -p vrrp -j ACCEPT
```



## 安装依赖软件包

```
### yum 安装
yum install keepalived

### 源码编译安装
yum install -y openssl-devel libnl libnl-devel libnfnetlink-devel

wget http://www.keepalived.org/software/keepalived-1.2.13.tar.gz

tar -zvxf keepalived-1.2.13.tar.gz

cd keepalived-1.2.13

./configure --prefix=/usr/local/keepalived

make && make install

cp /root/keepalived-1.2.13/keepalived/etc/init.d/keepalived.init /etc/rc.d/init.d/keepalived

cp /usr/local/keepalived/etc/sysconfig/keepalived /etc/sysconfig/

mkdir /etc/keepalived/

cp /usr/local/keepalived/etc/keepalived/keepalived.conf /etc/keepalived/

cp /usr/local/keepalived/sbin/keepalived /usr/sbin/

echo "/etc/init.d/keepalived start" >> /etc/rc.local

chmod +x /etc/rc.d/init.d/keepalived

chkconfig keepalived on

service keepalived start

service keepalived stop

service keepalived restart
```



## 二、配置

```
! Configuration File for keepalived

global_defs {
   notification_email {
     acassen@firewall.loc
     failover@firewall.loc
     sysadmin@firewall.loc
   }
   notification_email_from Alexandre.Cassen@firewall.loc
   smtp_server 127.0.0.1
   smtp_connect_timeout 30
   router_id LVS_DEVEL
}

vrrp_script chk_haproxy {                           
  script "killall -0 haproxy"
  interval 1
  weight -10
  rise 1
  fall 2
}

vrrp_instance VI_1 {
    state MASTER
    interface eth0
    virtual_router_id 51
    priority 100
    advert_int 1
    authentication {
        auth_type PASS
        auth_pass 1111
    }
    track_script {
        chk_haproxy
    }
    virtual_ipaddress {
        192.168.13.200
    }
    notify_master "/etc/keepalived/notify.sh master"
    notify_backup "/etc/keepalived/notify.sh backup"
    notify_fault "/etc/keepalived/notify.sh fault"
}
```



```
## slave
! Configuration File for keepalived

global_defs {
   notification_email {
     acassen@firewall.loc
     failover@firewall.loc
     sysadmin@firewall.loc
   }
   notification_email_from Alexandre.Cassen@firewall.loc
   smtp_server 127.0.0.1
   smtp_connect_timeout 30
   router_id LVS_DEVEL
}

vrrp_script chk_haproxy {                           
  script "killall -0 haproxy"
  interval 1
  weight -5
  rise 1
  fall 2
}

vrrp_instance VI_1 {
    state BACKUP
    interface eth0
    virtual_router_id 51
    priority 99
    advert_int 1
    authentication {
        auth_type PASS
        auth_pass 1111
    }
    track_script {
        chk_haproxy
    }
    virtual_ipaddress {
        192.168.13.200
    }
    notify_master "/etc/keepalived/notify.sh master"
    notify_backup "/etc/keepalived/notify.sh backup"
    notify_fault "/etc/keepalived/notify.sh fault"
}
```



```
#!/bin/bash
A=`ps -C haproxy --no-header | wc -l`
if [ $A -eq 0 ];then
/usr/local/haproxy/sbin/haproxy -f /usr/local/haproxy/haproxy-dynamic.cfg
sleep 3
if [ `ps -C haproxy --no-header | wc -l ` -eq 0 ];then
killall keepalived
fi
fi
```



```
#!/bin/bash
#
contact='carlosxiaocc@163.com'
notify() {
 mailsubject="$(hostname) to be $1, vip floating"
 mailbody="$(date +'%F %T'): vrrp transition, $(hostname) changed to be $1"
 echo "$mailbody" | mail -s "$mailsubject" $contact
}
case $1 in
master)
 notify master
 ;;
backup)
 notify backup
 ;;
fault)
 notify fault
 ;;
*)
 echo "Usage: $(basename $0) {master|backup|fault}"
 exit 1
 ;;
esac
```

