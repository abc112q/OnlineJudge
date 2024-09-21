package com.oj.ojcodesandebox.Docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.*;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.api.model.Link;
import com.github.dockerjava.api.model.PullResponseItem;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.command.LogContainerResultCallback;

import java.util.List;

/**
 * @author Ariel
 * Docker基本操作
 * 如果启动失败：1.增加权限 2.重启虚拟机、远程开发环境、重启程序
 */
public class DockerDemo {
    /*
    public static void main(String[] args) throws InterruptedException {
        // 获取默认的DockerClient
        DockerClient dockerClient = DockerClientBuilder.getInstance().build();
        String image = "nginx:latest";
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
        pullImageCmd.exec(pullImageResultCallback).awaitCompletion();
        System.out.println("下载完成");
        //  创建容器
        CreateContainerCmd containerCmd = dockerClient.createContainerCmd(image);
        CreateContainerResponse createContainerResponse = containerCmd
                // 启动容器时自动执行的命令
                .withCmd("echo","hello docker")
                .exec();
        System.out.println(createContainerResponse);
        // 获取容器id
        String ContainerId = createContainerResponse.getId();
        // 查看容器状态 获取容器列表
        ListContainersCmd listContainersCmd = dockerClient.listContainersCmd();
        List<Container> containers = listContainersCmd.exec();
        for(Container container:containers){
            System.out.println(container);
        }
        // 启动容器 使用容器id
        dockerClient.startContainerCmd(ContainerId).exec();

        // 查看日志 异步回调 说明日志是一批一批输出的，等所有的批次都结束之后整体返回
        LogContainerResultCallback logContainerResultCallback = new LogContainerResultCallback() {
            @Override
            public void onNext(Frame item) {
                System.out.println("日志"+item.toString());
                super.onNext(item);
            }
        };
        dockerClient.logContainerCmd(ContainerId)
                .withStdErr(true)
                .withStdOut(true)
                .exec(logContainerResultCallback)
                .awaitCompletion(); //阻塞等待日志输出

        // 删除镜像
        dockerClient.removeImageCmd(image).exec();

        //删除镜像
        dockerClient.removeContainerCmd(ContainerId).exec();
        // 强制删除调用.withForce()即可
    }*/
}
