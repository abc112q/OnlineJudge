package com.oj.ojcodesandebox;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.dfa.FoundWord;
import cn.hutool.dfa.WordTree;
import com.oj.ojcodesandebox.model.ExecuteCodeRequest;
import com.oj.ojcodesandebox.model.ExecuteCodeResponse;
import com.oj.ojcodesandebox.model.ExecuteMessage;
import com.oj.ojcodesandebox.model.JudgeInfo;
import com.oj.ojcodesandebox.security.DefaultSecurityManager;
import com.oj.ojcodesandebox.security.DenySecurityManager;
import com.oj.ojcodesandebox.utils.ProcessUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * @author Ariel
 * 核心是使用程序代替人工在命令行输入命令
 * 要使用到进程管理类Process
 * input是输入用例
 * output则是程序运行后的实际输出
 */
public class JavaNativeCodeSandboxImpl implements CodeSandbox{

    private static final String GLOBAL_CODE_DIR_NAME = "tempCode";

    private static final String GLOBAL_JAVA_CLASS_NAME = "Main.java";

    private static final long TIME_OUT = 5000L;

    private static final List<String> blockList = Arrays.asList("Files","exec");

    private static final WordTree WORD_TREE;

    // 安全管理器所在的类路径
    private static final String SECURITY_MANAGER_PATH = "D:\\ProjectStudy\\OJ\\code-sandbox\\src\\main\\resources\\security";

    private static final String SECURITY_MANAGER_CLASS_NAME = "MySecurityManager";

    // 类加载之后就初始化字典树，而不是每次执行代码都加载一边黑名单
    static {
        // 使用字典树节约空间 提高效率
        WORD_TREE = new WordTree();
        WORD_TREE.addWords(blockList);
    }

    public static void main(String[] args) {
        JavaNativeCodeSandboxImpl javaNativeCodeSandbox = new JavaNativeCodeSandboxImpl();
        ExecuteCodeRequest executeCodeRequest = new ExecuteCodeRequest();
        executeCodeRequest.setInputList(Arrays.asList("1 2","3 4"));
        // 读取resource目录下的代码
        String code = ResourceUtil.readStr("testCode/simpleComputeArgs/Main.java", StandardCharsets.UTF_8);
        // String code = ResourceUtil.readStr("testCode/unsafe/TimeOut.java", StandardCharsets.UTF_8);
        executeCodeRequest.setCode(code);
        executeCodeRequest.setLanguage("java");
        javaNativeCodeSandbox.executeCode(executeCodeRequest);
    }

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest){
        // 使用java安全管理器 会对每个访问的资源都作出限制
        //  System.setSecurityManager(new DenySecurityManager());
        // 获取用户输入
        List<String> inputList = executeCodeRequest.getInputList();
        String code = executeCodeRequest.getCode();
        String language = executeCodeRequest.getLanguage();
        // todo 匹配用户输入的代码中是否包含危险代码 黑白名单 在使用安全管理器的时候注释掉
        FoundWord foundWord = WORD_TREE.matchWord(code);
        if(foundWord != null){
            System.out.println("用户代码包含敏感词汇"+foundWord.getFoundWord());
        }
        // 把用户代码保存为文件
        // 判断专门保存用户代码的tempCode文件夹是否存在 只有存在才能将文件写入
         String userDir = System.getProperty("user.dir");
         String globalCodePathName = userDir + File.separator + GLOBAL_CODE_DIR_NAME;
         if(!FileUtil.exist(globalCodePathName)){
             // 如果不存在创建根目录
             FileUtil.mkdir(globalCodePathName);
         }
         // 分级把用户的代码隔离存放  tempcode下面的子目录
         String userCodeParentPath = globalCodePathName + File.separator + UUID.randomUUID();
         // tempcode下面的子目录真正存放的用户代码文件
         String userCodePath = userCodeParentPath + File.separator + GLOBAL_JAVA_CLASS_NAME;
         // 将用户代码写入文件，返回用户代码文件
         File userCodeFile = FileUtil.writeString(code, userCodePath, "UTF-8");
         // System.out.println(userCodeFile);   这个输出的是文件的具体内容
        // todo 编译代码得到class文件 然后拼接编译结果
        String compiledCmd = String.format("\"C:\\Program Files\\Java\\jdk1.8.0_151\\bin\\javac\" -encoding utf-8 %s",userCodeFile.getAbsolutePath());
        try {
            // 就相当于在终端执行命令  （执行编译的命令）
            Process compileProcess = Runtime.getRuntime().exec(compiledCmd);
            ExecuteMessage executeMessage = ProcessUtils.runProcessAndGetMessage(compileProcess, "编译");
            System.out.println(executeMessage);
        } catch (IOException e) {
            return getErrorResponse(e);
        }
        // todo 编译出字节码之后执行字节码文件
        // -cp命令需要的是字节码的所属文件  执行代码 得到输出结果
        List<ExecuteMessage> executeMessageList = new ArrayList<>();
        // 执行每一条用例都有一个输出
        for(String inputArgs : inputList){
            // String runCmd = String.format("java -Xmx256m -Dfile.encoding=UTF-8 -cp %s Main %s", userCodeParentPath, inputArgs);
            String runCmd = String.format("java -Dfile.encoding=UTF-8 -cp %s;%s -Djava.security.manager=%s Main\n", userCodeParentPath,SECURITY_MANAGER_PATH, SECURITY_MANAGER_CLASS_NAME,inputArgs);
            try {
                // 执行运行命令 实际上这段代码是开启了一个新的线程
                Process runProcess = Runtime.getRuntime().exec(runCmd);
                // TODO 创建一个监控线程
                new Thread(()->{
                    // 先睡一个超时时间，如果睡醒了主线程还没有执行完毕就直接杀死
                    try {
                        Thread.sleep(TIME_OUT);
                        runProcess.destroy();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
                ExecuteMessage executeMessage = ProcessUtils.runProcessAndGetMessage(runProcess, "运行");
                executeMessageList.add(executeMessage);
                System.out.println(executeMessage);
            } catch (IOException e) {
                return getErrorResponse(e);
            }
        }
        // 整理输出
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        List<String> outputList = new ArrayList<>();
        // 取用例的最大值
        long maxRunTime = 0;
        for(ExecuteMessage executeMessage : executeMessageList){
            String errorMessage = executeMessage.getErrorMessage();
            if(StrUtil.isNotBlank(errorMessage)){
                // 用户提交的代码执行出错
                executeCodeResponse.setStatus(3);
                break;
            }
            // 将正确的信息添加到输出列表
            outputList.add(executeMessage.getMessage());
            if(executeMessage.getTime() != null){
                maxRunTime = Math.max(maxRunTime, executeMessage.getTime());
            }
        }
        // 程序正常执行 意味着输出列表长度等于执行信息列表
        if(outputList.size() == executeMessageList.size()){
            executeCodeResponse.setStatus(1);
        }
        executeCodeResponse.setOutputList(outputList);
        JudgeInfo judgeInfo = new JudgeInfo();
        // judgeInfo.setMemory();
        judgeInfo.setTime(maxRunTime);
        executeCodeResponse.setJudgeInfo(judgeInfo);
        // 文件清理  防止占用服务器内存不足
        if(userCodeFile.getParentFile() != null){
            final boolean del = FileUtil.del(userCodeParentPath);
            System.out.println("删除"+(del ? "成功" : "失败"));
        }
        // 错误处理 提升程序健壮性
        return executeCodeResponse;
    }

    /**
     * 获取错误响应 封装一个错误处理方法 程序抛出异常的时候就直接抛出错误响应
     * @param e
     * @return
     */
    private ExecuteCodeResponse getErrorResponse(Throwable e){
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        executeCodeResponse.setOutputList(new ArrayList<>());
        executeCodeResponse.setMessage(e.getMessage());
        // 代码沙箱内部出错，还没执行到用户代码
        executeCodeResponse.setStatus(2);
        executeCodeResponse.setJudgeInfo(new JudgeInfo());
        return executeCodeResponse;
    }
}
