/**
 * Copyright (c) 2011-2016 All Rights Reserved.
 */
package com.panjin.backend.trace.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.*;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Set;

/**
 *
 *
 * @author panjin
 * @version $Id: NetUtils.java 2016年7月21日 下午5:07:59 $
 */
public abstract class NetUtils {

    private static final Logger LOG = LoggerFactory.getLogger(NetUtils.class);

    private static InetAddress inetAddress = getInetAddress();

    public static void main(String[] args) throws SocketException {

        InetAddress netAddress = getInetAddress();
        System.out.println("host ip:" + getHostIp(netAddress));
        System.out.println("host name:" + getHostName(netAddress));
        System.out.println("Server host ip:" + getServerIp());
        System.out.println("Real ip:" + getServerIp());
        System.out.println("Server host name:" + getServerHostName());
        Properties properties = System.getProperties();
        Set<String> set = properties.stringPropertyNames(); // 获取java虚拟机和系统的信息。
        for (String name : set) {
            System.out.println(name + ":" + properties.getProperty(name));
        }
    }

    /**
     * 获取服务器IP地址
     * @return IP地址
     */
    public static String getServerIp() {
        return getHostIp(inetAddress);
    }

    /**
     * 获取服务器hostname
     * @return hostname
     */
    public static String getServerHostName() {
        return getFullHostName(inetAddress);
    }

    /**
     * 获取本机IP
     * 取云主机IP有问题
     * @return IP地址
     * @deprecated
     * @see NetUtils#getServerIp()
     */
    public static String getIPAddress() {

        Enumeration allNetInterfaces;
        try {
            allNetInterfaces = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {

            LOG.error("get local ip error !", e);
            return getRealIp();
        }

        InetAddress ip;
        while (allNetInterfaces.hasMoreElements()) {
            NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
            Enumeration addresses = netInterface.getInetAddresses();
            while (addresses.hasMoreElements()) {
                ip = (InetAddress) addresses.nextElement();
                if (ip != null && ip instanceof Inet4Address) {
                    return ip.getHostAddress();
                }
            }
        }

        return null;
    }

    /**
     * 获取主机名
     * 
     * @return 主机名
     * @deprecated
     * @see NetUtils#getServerHostName()
     */
    public static String getHostName() {
        Enumeration allNetInterfaces;
        try {
            allNetInterfaces = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {

            LOG.error("get local ip error !", e);
            return null;
        }

        InetAddress ip;
        while (allNetInterfaces.hasMoreElements()) {
            NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
            Enumeration addresses = netInterface.getInetAddresses();
            while (addresses.hasMoreElements()) {
                ip = (InetAddress) addresses.nextElement();
                if (ip != null && ip instanceof Inet4Address) {
                    return ip.getHostName();
                }
            }
        }

        return null;
    }

    /**
     * 获取机器的真实IP
     * 
     * @return
     * @throws
     */
    public static String getRealIp() {
        try {
            String localip = null;// 本地IP，如果没有配置外网IP则返回它
            String netip = null;// 外网IP

            Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress ip;

            boolean finded = false;// 是否找到外网IP
            while (netInterfaces.hasMoreElements() && !finded) {
                NetworkInterface ni = netInterfaces.nextElement();
                Enumeration<InetAddress> address = ni.getInetAddresses();
                while (address.hasMoreElements()) {
                    ip = address.nextElement();
                    if (!ip.isSiteLocalAddress() && !ip.isLoopbackAddress() && !ip.getHostAddress().contains(":")) {// 外网IP
                        netip = ip.getHostAddress();
                        finded = true;

                        break;

                    } else if (ip.isSiteLocalAddress() && !ip.isLoopbackAddress()
                               && !ip.getHostAddress().contains(":")) {// 内网IP
                        localip = ip.getHostAddress();
                    }
                }
            }

            if (netip != null && !"".equals(netip)) {
                return netip;
            } else {
                return localip;
            }
        } catch (Exception e) {
            LOG.error("load local ip error ", e);
            return null;
        }
    }

    private static InetAddress getInetAddress() {

        try {
            return InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            LOG.error("get InetAddress fail.", e);
        } catch (Exception e) {
            LOG.error("get InetAddress fail.", e);
        }
        return null;

    }

    private static String getHostIp(InetAddress netAddress) {
        if (null == netAddress) {
            return null;
        }
        String ip = netAddress.getHostAddress(); // get the ip address
        return ip;
    }

    private static String getHostName(InetAddress netAddress) {
        if (null == netAddress) {
            return null;
        }
        String name = netAddress.getHostName(); // get the host address
        return name;
    }

    private static String getFullHostName(InetAddress netAddress) {
        if (null == netAddress) {
            return null;
        }
        String name = netAddress.getCanonicalHostName(); // get full host address
        return name;
    }

}
