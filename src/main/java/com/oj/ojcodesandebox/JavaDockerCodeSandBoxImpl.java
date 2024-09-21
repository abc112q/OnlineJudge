package com.oj.ojcodesandebox;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.*;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.command.ExecStartResultCallback;
import com.oj.ojcodesandebox.model.ExecuteCodeRequest;
import com.oj.ojcodesandebox.model.ExecuteCodeResponse;
import com.oj.ojcodesandebox.model.ExecuteMessage;
import com.oj.ojcodesandebox.model.JudgeInfo;
import com.oj.ojcodesandebox.utils.ProcessUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author Ariel
 * Docker实现模板方法类
 */
@Component
public class JavaDockerCodeSandBoxImpl extends JavaCodeSandBoxTemplate{

    private static final long TIME_OUT = 5000L;

    private static final Boolean FIRST_INIT = true;


    /**
     * 只有runCode方法的具体实现与javaNativce的实现不同 所以只覆盖这个方法即可
     * @param inputList
     * @param userCodeFile
     * @return
     */
    @Override
    public List<ExecuteMessage> runCode(List<String> inputList, File userCodeFile) {
        String userCodeParentPath = userCodeFile.getParentFile().getAbsolutePath();
        // todo 创建容器(自定义容器 在已有镜像的基础上扩充，比如拉取现成的包含jdk的java环境) ,然后把文件复制到容器内
        DockerClient dockerClient = DockerClientBuilder.getInstance().build();
        // 1.拉取镜像
        String image = "openjdk:8-alpine";
        if(FIRST_INIT){
            PullImageCmd pullImageCmd = dockerClient.pullImageCmd(image);
            PullImageResultCallback pullImageResultCallback = new PullImageResultCallback() {
                @Override
                public void onNext(PullResponseItem item) {
                    System.out.println("下载镜像："+item.getStatus());
                    // 镜像在下载的时候，每个阶段就会触发一个onnext方法
                    super.onNext(item);
                }
            };
            // 接收异步参数是因为下载镜像的的时间比较长 用于得知镜像下载完毕后要去做什么事情 await阻塞直到镜像下载完才会执行下一步
            try {
                pullImageCmd.exec(pullImageResultCallback).awaitCompletion();
            } catch (InterruptedException e) {
                System.out.println("拉取镜像异常");
                e.printStackTrace();
            }
            System.out.println("下载完成");
        }

        //  2.创建容器  创建一个可交互的容器 能接收多次输入并且输出
        CreateContainerCmd containerCmd = dockerClient.createContainerCmd(image);
        // 创建完容器就复制用户代码文件 而不是启动容器后再复制 这样就如果文件复制失败 容器就启动不起来
        HostConfig hostConfig = new HostConfig();
        // 将本地存放代码的目录 映射到容器中
        hostConfig.setBinds(new Bind(userCodeParentPath,new Volume("/app")));
        // 限制内存实际上就是限制了用户代码的内存
        hostConfig.withMemory(100 * 1000 * 1000L);
        hostConfig.withMemorySwap(0L);
        hostConfig.withCpuCount(1L);
        // 安全管理配置json linux内核限制的可以执行的系统调用
        hostConfig.withSecurityOpts(Arrays.asList("seccomp = 安全管理配置字符串"));
        CreateContainerResponse createContainerResponse = containerCmd
                .withHostConfig(hostConfig)
                // 限制网络（安全） 不想让用户操作容器联网刷带宽
                .withNetworkDisabled(true)
                // 禁止用户操作root根目录
                .withReadonlyRootfs(true)
                // 这三个命令的作用是把docker容器与本地的终端进行连接 可以让我们输入也能获得输出
                .withAttachStderr(true)
                .withAttachStdin(true)
                .withAttachStdout(true)
                // 设置交互终端 相当于在后台启动了一个命令行 通过一个守护线程实时监控读取输入
                .withTty(true)
                // 启动容器时自动执行的命令
                .withCmd("echo","hello docker")
                .exec();
        System.out.println(createContainerResponse);
        // 获取容器id
        String ContainerId = createContainerResponse.getId();
        // 启动容器 (使用容器id
        dockerClient.startContainerCmd(ContainerId).exec();
        List<ExecuteMessage> executeMessageList = new ArrayList<>();
        // todo 执行命令获取结果
        //拼接命令 一定要把命令作为数组传递进去，否则linux独到的就不是一个个参数 而认为成一个字符串
        for(String inputArgs : inputList){
            StopWatch stopWatch = new StopWatch();
            String[] inputArgsArray = inputArgs.split(" ");
            String[] cmdArray = ArrayUtil.append(new String[]{"java","-cp","/app","Main",},inputArgsArray);
            ExecCreateCmdResponse execCreateCmdResponse = dockerClient.execCreateCmd(ContainerId)
                    .withCmd(cmdArray)
                    // 为了获得控制台的输出
                    .withAttachStderr(true)
                    .withAttachStdin(true)
                    .withAttachStdout(true)
                    .exec();
            System.out.println("执行的命令为："+execCreateCmdResponse);
            ExecuteMessage executeMessage = new ExecuteMessage();
            final String[] message = {null};
            final String[] errorMessage = {null};
            long time = 0L;
            // 默认超时
            final boolean[] timeout = {true};
            ExecStartResultCallback execStartResultCallback = new ExecStartResultCallback() {
                @Override
                public void onComplete(){
                    // 方法如果可以五秒执行完毕，会回调触发这个方法 设置为false代表未超时
                    timeout[0] = false;
                    super.onComplete();
                }
                @Override
                public void onNext(Frame frame){
                    StreamType streamType = frame.getStreamType();
                    if(streamType.equals(StreamType.STDERR)){
                        errorMessage[0] = new String(frame.getPayload());
                        System.out.println("错误信息"+ errorMessage[0]);
                    }else{
                        message[0] = new String(frame.getPayload());
                        System.out.println("输出结果"+ message[0]);
                    }
                    super.onNext(frame);
                }
            };
            final long[] maxMemory = {0L};
            // 获取占用内存 (制定一个周期 定期获取程序内存)  先获取执行时的容器状态
            StatsCmd statsCmd = dockerClient.statsCmd(ContainerId);
            final ResultCallback<Statistics> statisticsResultCallback = statsCmd.exec(new ResultCallback<Statistics>() {
                @Override
                public void onNext(Statistics statistics) {
                    System.out.println("内存占用" + statistics.getMemoryStats().getUsage());
                    maxMemory[0] = Math.max(statistics.getMemoryStats().getUsage(), maxMemory[0]);
                }


                @Override
                public void close() throws IOException {

                }

                @Override
                public void onStart(Closeable closeable) {

                }


                @Override
                public void onError(Throwable throwable) {

                }

                @Override
                public void onComplete() {

                }

            });
            statsCmd.exec(statisticsResultCallback);
            try {
                stopWatch.start();
                dockerClient.execStartCmd(execCreateCmdResponse.getId())
                        .exec(execStartResultCallback)
                        // 阻塞等待程序执行完毕 超时参数
                        .awaitCompletion(TIME_OUT, TimeUnit.MICROSECONDS);
                stopWatch.stop();
                time = stopWatch.getLastTaskTimeMillis();
                statsCmd.close();
            } catch (InterruptedException e) {
                System.out.println("程序异常执行");
                e.printStackTrace();
            }
            executeMessage.setMessage(message[0]);
            executeMessage.setErrorMessage(errorMessage[0]);
            executeMessage.setTime(time);
            executeMessage.setMemory(maxMemory[0]);
            executeMessageList.add(executeMessage);
        }
        return executeMessageList;
    }

}
