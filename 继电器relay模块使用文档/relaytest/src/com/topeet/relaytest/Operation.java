package com.topeet.relaytest;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Timer;
import java.util.TimerTask;

import android.util.Log;

public class Operation {

	private final static String TAG = "com.example.wifi"; 
    
    public final static String COMMAND_SU       = "su";  
    public final static String COMMAND_SH       = "sh";  
    public final static String COMMAND_EXIT     = "exit\n";  
    public final static String COMMAND_LINE_END = "\n"; 
	
	private static boolean mHaveRoot = false; 
	
	 public static class CommandResult {  
	        public int result = -1;  
	        public String errorMsg;  
	        public String successMsg;  
	    }  
	 
	 //自己选择是否有root 权限执行命令-一条
	 public static CommandResult execCommand(String command, boolean isRoot) {  
	        String[] commands = {command};  
	        return execCommand(commands, isRoot);  
	    }  
	  
	 //自己选择是否有root 权限执行命令-多条
	 public static CommandResult execCommand(String[] commands, boolean isRoot) {  
	        CommandResult commandResult = new CommandResult();  
	        if (commands == null || commands.length == 0) return commandResult;  
	        Process process = null;  
	        DataOutputStream os = null;  
	        BufferedReader successResult = null;  
	        BufferedReader errorResult = null;  
	        StringBuilder successMsg = null;  
	        StringBuilder errorMsg = null;  
	        try {  
	            process = Runtime.getRuntime().exec(isRoot ? COMMAND_SU : COMMAND_SH);  
	            os = new DataOutputStream(process.getOutputStream());  
	            for (String command : commands) {  
	                if (command != null) {  
	                    os.write(command.getBytes());  
	                    os.writeBytes(COMMAND_LINE_END);  
	                    os.flush();  
	                }  
	            }  
	            os.writeBytes(COMMAND_EXIT);  
	            os.flush();  
	            commandResult.result = process.waitFor();  
	            //获取错误信息  
	            successMsg = new StringBuilder();  
	            errorMsg = new StringBuilder();  
	            successResult = new BufferedReader(new InputStreamReader(process.getInputStream()));  
	            errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));  
	            String s;  
	            while ((s = successResult.readLine()) != null) successMsg.append(s);  
	            while ((s = errorResult.readLine()) != null) errorMsg.append(s);  
	            commandResult.successMsg = successMsg.toString();  
	            commandResult.errorMsg = errorMsg.toString();  
	            Log.i(TAG, commandResult.result + " | " + commandResult.successMsg  
	                    + " | " + commandResult.errorMsg);  
	        } catch (IOException e) {  
	            String errmsg = e.getMessage();  
	            if (errmsg != null) {  
	                Log.e(TAG, errmsg);  
	            } else {  
	                e.printStackTrace();  
	            }  
	        } catch (Exception e) {  
	            String errmsg = e.getMessage();  
	            if (errmsg != null) {  
	                Log.e(TAG, errmsg);  
	            } else {  
	                e.printStackTrace();  
	            }  
	        } finally {  
	            try {  
	                if (os != null) os.close();  
	                if (successResult != null) successResult.close();  
	                if (errorResult != null) errorResult.close();  
	            } catch (IOException e) {  
	                String errmsg = e.getMessage();  
	                if (errmsg != null) {  
	                    Log.e(TAG, errmsg);  
	                } else {  
	                    e.printStackTrace();  
	                }  
	            }  
	            if (process != null) process.destroy();  
	        }  
	        return commandResult;  
	    }  
	

	 
	 
    // 执行命令但不关注结果输出 
    public static int execRootCmdSilent(String cmd)
 { 
        int result = -1; 
        DataOutputStream dos = null; 
         
        try { 
            Process p = Runtime.getRuntime().exec("su"); 
            dos = new DataOutputStream(p.getOutputStream()); 
             
            Log.i(TAG, cmd); 
            dos.writeBytes(cmd + "\n"); 
            dos.flush(); 
            dos.writeBytes("exit\n"); 
            dos.flush(); 
            p.waitFor(); 
            result = p.exitValue(); 
        } catch (Exception e) { 
            e.printStackTrace(); 
        } finally { 
            if (dos != null) { 
                try { 
                    dos.close(); 
                } catch (IOException e) { 
                    e.printStackTrace(); 
                } 
            } 
        } 
        //delay(100);
        return result; 
    } 

    // 执行命令并且输出结果 
    public static String execRootCmd(String cmd) 
    { 
        String result = ""; 
        DataOutputStream dos = null; 
        DataInputStream dis = null; 
         
        try { 
            Process p = Runtime.getRuntime().exec("su");// 经过Root处理的android系统即有su命令 
            dos = new DataOutputStream(p.getOutputStream()); 
            dis = new DataInputStream(p.getInputStream()); 
 
            Log.i(TAG, cmd); 
            dos.writeBytes(cmd + "\n"); 
            dos.flush(); 
            dos.writeBytes("exit\n"); 
            dos.flush(); 
            String line = null; 
            while ((line = dis.readLine()) != null) { 
                Log.d("result", line); 
                result += line; 
            } 
            p.waitFor(); 
        } catch (Exception e) { 
            e.printStackTrace(); 
        } finally { 
            if (dos != null) { 
                try { 
                    dos.close(); 
                } catch (IOException e) { 
                    e.printStackTrace(); 
                } 
            } 
            if (dis != null) { 
                try { 
                    dis.close(); 
                } catch (IOException e) { 
                    e.printStackTrace(); 
                } 
            } 
        } 
        //delay(1000);
        return result; 
    } 
 
    
    public static synchronized String run(String[] cmd, String workdirectory)
    	    throws IOException {
    	    StringBuffer result = new StringBuffer();
    	    try {
    	    // 创建操作系统进程（也可以由Runtime.exec()启动）
    	    // Runtime runtime = Runtime.getRuntime();
    	    // Process proc = runtime.exec(cmd);
    	    // InputStream inputstream = proc.getInputStream();
    	    ProcessBuilder builder = new ProcessBuilder(cmd);
    	    InputStream in = null;
    	    // 设置一个路径（绝对路径了就不一定需要）
    	    if (workdirectory != null) {
    	    // 设置工作目录（同上）
    	    builder.directory(new File(workdirectory));
    	    // 合并标准错误和标准输出
    	    builder.redirectErrorStream(true);
    	    // 启动一个新进程
    	    Process process = builder.start();
    	    // 读取进程标准输出流
    	    in = process.getInputStream();
    	    byte[] re = new byte[1024];
    	    while (in.read(re) != -1) {
    	    result = result.append(new String(re));
    	    }
    	    }
    	    // 关闭输入流
    	    if (in != null) {
    	    in.close();
    	    }
    	    } catch (Exception ex) {
    	    ex.printStackTrace();
    	    }
    	    return result.toString();
    	    } 
    
    
    
    //获取wifi名的字符串数组
    public static String[]  getESSID(String wifiInfo) 
    {
    	int begin=wifiInfo.indexOf("ESSID:");
    	int end=wifiInfo.indexOf("Mode:");
    	String str[]=new String[20];
    	int i=1;//str数组第0位，存放一个数字，表示有多少个wifi
    	while(begin!=-1&&(end-begin)>8)
    	{
	    	String tstr=wifiInfo.substring(begin+7,end);
	    	str[i]=tstr.substring(0,tstr.indexOf("\""));
	    	wifiInfo=wifiInfo.substring(end+5);
	    	begin=wifiInfo.indexOf("ESSID:");
	    	end=wifiInfo.indexOf("Mode:");
	    	++i;
    	}
    	str[0]=(i-1)+"";
    	return str;
    }

    //检查当前开发板是否获得root权限
    public static boolean haveRoot() 
    { 
        if (!mHaveRoot) { 
            int ret = execRootCmdSilent("echo test"); // 通过执行测试命令来检测 
            if (ret != -1) { 
                Log.i(TAG, "have root!"); 
                mHaveRoot = true; 
            } else { 
                Log.i(TAG, "not root!"); 
            } 
        } else { 
            Log.i(TAG, "mHaveRoot = true, have root!"); 
        } 
        return mHaveRoot; 
    } 
    
    //利用上列函数返回wifi名的数组
    public static String[]  getWifiName()
    {
    	haveRoot(); 
    	execRootCmdSilent("insmod /data/mt7601Usta.ko"); 
    	execRootCmdSilent("netcfg wlan0 up"); 
    	execRootCmdSilent("netcfg eth0 down");
    	String str[]=getESSID(execRootCmd("iwlist wlan0 scan"));
    	int wificount = Integer.parseInt(str[0]);
    	String[] SSID= new String[wificount];
    	for(int i=0;i<wificount;i++)
    	{
    		SSID[i]=str[i+1];
    	}
    	return SSID;
    }
    
    
    
    public static void delay(int ms){  
    	 try {  
             Thread.currentThread();  
             Thread.sleep(ms);  
         } catch (InterruptedException e) {  
             e.printStackTrace();  
         }   
     }    
    
}
