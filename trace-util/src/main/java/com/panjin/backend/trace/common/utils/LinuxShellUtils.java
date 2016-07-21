/**
 * Copyright (c) 2011-2016 All Rights Reserved.
 */
package com.panjin.backend.trace.common.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * linux 脚本执行工具类
 *
 * @author panjin
 * @version $Id: LinuxShellUtils.java 2016年7月21日 下午4:51:26 $
 */
public abstract class LinuxShellUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(LinuxShellUtils.class);

    /**
     * 执行指定的shell脚本
     * 
     * @param shellScript
     *            shell脚本
     * @return 脚本的执行结果
     */
    public static String executeShell(String shellScript) {
        StringBuilder result = new StringBuilder(30);

        BufferedReader bufferedReader = null;
        try {
            Process process = Runtime.getRuntime().exec(new String[] { "/bin/bash", "-c", shellScript, });
            process.waitFor();

            bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream(), "utf-8"));

            String temp;
            while ((temp = bufferedReader.readLine()) != null) {
                result.append(temp);
            }

        } catch (Throwable e) {
            LOGGER.error("execute shell error . command : {}", shellScript);
            LOGGER.error("execute shell error . command :", e);

        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    LOGGER.info("close shell inputstream error !");
                }
            }
        }

        if (result.length() == 0) {
            return Strings.EMPTY;
        }

        return result.toString();

    }

    public static void main(String[] args) {
        String hostname = LinuxShellUtils.executeShell("hostname");
        System.out.println(hostname);
    }
}
