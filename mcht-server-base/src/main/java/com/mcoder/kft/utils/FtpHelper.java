package com.mcoder.kft.utils;

import com.alibaba.fastjson.JSON;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

/**
 * @author 1194688733@qq.com
 * @Description: TODO
 * @date 2019-01-09 17:43:50
 */
public class FtpHelper {

    private static final Logger logger = LoggerFactory.getLogger(FtpHelper.class);
    private static final String DEFAULT_PROTOCOL = "sftp";

    private static final String STRICT_HOSTKEY_CHECKING = "StrictHostKeyChecking";
    private static final String DEFALUT_STRICT_HOSTKEY_CHECKING = "no";

    public static List<String> listFileNames(String host, int port, String username, String password, String dir) {
        List<String> list = new ArrayList<String>();
        ChannelSftp sftp = null;
        Channel channel = null;
        Session sshSession = null;
        try {
            JSch jsch = new JSch();
            sshSession = jsch.getSession(username, host, port);
            sshSession.setPassword(password);
            Properties sshConfig = new Properties();
            sshConfig.put(STRICT_HOSTKEY_CHECKING, DEFALUT_STRICT_HOSTKEY_CHECKING);
            sshSession.setConfig(sshConfig);
            sshSession.connect();
            channel = sshSession.openChannel(DEFAULT_PROTOCOL);
            channel.connect();
            sftp = (ChannelSftp) channel;
            Vector<?> vector = sftp.ls(dir);
            for (Object item : vector) {
                ChannelSftp.LsEntry entry = (ChannelSftp.LsEntry) item;
                list.add(entry.getFilename());
            }
        } catch (Exception e) {
            logger.info("[read files from {} ] exception {}", dir, e.getMessage());
        } finally {
            closeChannel(sftp);
            closeChannel(channel);
            closeSession(sshSession);
        }
        return list;
    }

    public static boolean downloadFileToByte(String host, int port, String username, String password, String dir, String fileName, String destFile) {
        Session session = null;
        Channel channel = null;
        ChannelSftp chSftp = null;
        try {
            JSch jsch = new JSch();
            session = jsch.getSession(username, host, port);
            session.setPassword(password);
            session.setTimeout(100000);
            Properties sshConfig = new Properties();
            sshConfig.put(STRICT_HOSTKEY_CHECKING, DEFALUT_STRICT_HOSTKEY_CHECKING);
            session.setConfig(sshConfig);
            session.connect();
            channel = session.openChannel(DEFAULT_PROTOCOL);
            channel.connect();
            chSftp = (ChannelSftp) channel;

            String ftpFilePath = fileName.startsWith(dir) ? fileName : dir.concat(fileName);

            chSftp.get(ftpFilePath, destFile);
            return true;
        } catch (Exception e) {
            logger.error("[read file {} from {} ] exception", fileName, dir, e);
            return false;
        } finally {
            closeChannel(chSftp);
            closeChannel(channel);
            closeSession(session);
        }
    }


    private static void closeChannel(Channel channel) {
        if (channel != null) {
            if (channel.isConnected()) {
                channel.disconnect();
            }
        }
    }

    private static void closeSession(Session session) {
        if (session != null) {
            if (session.isConnected()) {
                session.disconnect();
            }
        }
    }

    public static void main(String[] args) {
        List<String> fileNameList = listFileNames("218.17.35.123", 22, "2018102500097289", "admin1234", "/reconfiles");

        System.out.println(JSON.toJSONString(fileNameList));
       /* fileNameList = fileNameList.stream().filter(s -> s.startsWith("test") && s.endsWith(".txt")).collect(Collectors.toList());

        System.out.println(fileNameList);*/

        //uploadFile("172.30.0.115", 22, "test", "Test@f7p", "/channel/20180729", "test4.txt", new ByteArrayInputStream("你好".getBytes()));

    }
}
