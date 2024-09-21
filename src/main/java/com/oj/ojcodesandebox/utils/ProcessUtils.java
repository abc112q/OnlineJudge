package com.oj.ojcodesandebox.utils;

import com.oj.ojcodesandebox.model.ExecuteMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.StopWatch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Ariel
 * 编译java文件中的代码，并返回编译后控制台的输出结果的工具类
 */
public class ProcessUtils {
    /**
     * 执行进程并获取信息
     * @param compileProcess
     * @param opName
     * @return
     */
    public static ExecuteMessage runProcessAndGetMessage(Process compileProcess, String opName){
        ExecuteMessage executeMessage = new ExecuteMessage();
       try{
           // 每个用例都需要单独计算时间
           StopWatch stopWatch = new StopWatch();
           stopWatch.start();
           // 等待程序执行，直到执行完毕后会返回一个退出码 来判断程序是正常退出(0)还是异常退出(1)
           int exitValue = compileProcess.waitFor();
           executeMessage.setExitCode(exitValue);
           if(exitValue == 0){
               System.out.println(opName+"成功");
               // 编译成功我们希望拿到控制台的输出  先拿到进程对应的输入流，上面的控制台程序会把执行正常结果写入这个输入流 我们从输入流中读取即可
               BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(compileProcess.getInputStream()));
               // 拼接输出
               List<String> outputStrList = new ArrayList<>();
               // 按行读取控制台输出（比如程序执行结果）
               String compileOutputLine;
               while ((compileOutputLine = bufferedReader.readLine()) != null){
                   // 换行符看起来更好看
                   outputStrList.add(compileOutputLine);
               }
               executeMessage.setMessage(StringUtils.join(outputStrList,"\n"));
           } else {
               System.out.println(opName+"失败,错误码："+ exitValue);
               BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(compileProcess.getInputStream()));
               // 按行读取控制台输出（比如程序执行结果） 拼接输出
               List<String> outputStrList = new ArrayList<>();
               // 按行读取控制台输出（比如程序执行结果）
               String compileOutputLine;
               while ((compileOutputLine = bufferedReader.readLine()) != null){
                   outputStrList.add(compileOutputLine);
               }
               executeMessage.setMessage(StringUtils.join(outputStrList,"\n"));
                // 拼接输出
               List<String> errorOutputStrList = new ArrayList<>();
               // 如果存在错误信息执行这段 按行读取控制台输出（比如程序执行结果）
               String errorCompileOutputLine;
               while ((errorCompileOutputLine = bufferedReader.readLine()) != null){
                   // 换行符看起来更好看
                   errorOutputStrList.add(errorCompileOutputLine);
               }
               executeMessage.setErrorMessage(StringUtils.join(errorOutputStrList,"\n"));
           }
           stopWatch.stop();
           long lastTaskTimeMillis = stopWatch.getLastTaskTimeMillis();
           executeMessage.setTime(lastTaskTimeMillis);
       }catch (Exception e){
            e.printStackTrace();
       }
        return executeMessage;
    }

}
