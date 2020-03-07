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
	 
	 //�Լ�ѡ���Ƿ���root Ȩ��ִ������-һ��
	 public static CommandResult execCommand(String command, boolean isRoot) {  
	        String[] commands = {command};  
	        return execCommand(commands, isRoot);  
	    }  
	  
	 //�Լ�ѡ���Ƿ���root Ȩ��ִ������-����
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
	            //��ȡ������Ϣ  
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
	

	 
	 
    // ִ���������ע������ 
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

    // ִ������������� 
    public static String execRootCmd(String cmd) 
    { 
        String result = ""; 
        DataOutputStream dos = null; 
        DataInputStream dis = null; 
         
        try { 
            Process p = Runtime.getRuntime().exec("su");// ����Root�����androidϵͳ����su���� 
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
    	    // ��������ϵͳ���̣�Ҳ������Runtime.exec()������
    	    // Runtime runtime = Runtime.getRuntime();
    	    // Process proc = runtime.exec(cmd);
    	    // InputStream inputstream = proc.getInputStream();
    	    ProcessBuilder builder = new ProcessBuilder(cmd);
    	    InputStream in = null;
    	    // ����һ��·��������·���˾Ͳ�һ����Ҫ��
    	    if (workdirectory != null) {
    	    // ���ù���Ŀ¼��ͬ�ϣ�
    	    builder.directory(new File(workdirectory));
    	    // �ϲ���׼����ͱ�׼���
    	    builder.redirectErrorStream(true);
    	    // ����һ���½���
    	    Process process = builder.start();
    	    // ��ȡ���̱�׼�����
    	    in = process.getInputStream();
    	    byte[] re = new byte[1024];
    	    while (in.read(re) != -1) {
    	    result = result.append(new String(re));
    	    }
    	    }
    	    // �ر�������
    	    if (in != null) {
    	    in.close();
    	    }
    	    } catch (Exception ex) {
    	    ex.printStackTrace();
    	    }
    	    return result.toString();
    	    } 
    
    
    
    //��ȡwifi�����ַ�������
    public static String[]  getESSID(String wifiInfo) 
    {
    	int begin=wifiInfo.indexOf("ESSID:");
    	int end=wifiInfo.indexOf("Mode:");
    	String str[]=new String[20];
    	int i=1;//str�����0λ�����һ�����֣���ʾ�ж��ٸ�wifi
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

    //��鵱ǰ�������Ƿ���rootȨ��
    public static boolean haveRoot() 
    { 
        if (!mHaveRoot) { 
            int ret = execRootCmdSilent("echo test"); // ͨ��ִ�в������������ 
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
    
    //�������к�������wifi��������
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
