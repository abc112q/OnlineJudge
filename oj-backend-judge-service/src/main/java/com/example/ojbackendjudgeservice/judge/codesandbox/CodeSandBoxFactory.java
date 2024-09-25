package com.example.ojbackendjudgeservice.judge.codesandbox;

import com.example.ojbackendjudgeservice.judge.codesandbox.impl.ExampleCodeSandboxImpl;
import com.example.ojbackendjudgeservice.judge.codesandbox.impl.RemoteCodeSandboxImpl;
import com.example.ojbackendjudgeservice.judge.codesandbox.impl.ThirdPartyCodeSandBoxImpl;

/**
 * @author Ariel
 * 使用静态工厂模式
 * 根据不同类别创建指定的代码沙箱实 例
 */
public class CodeSandBoxFactory {
    public static CodeSandbox newInstance(String type){
        switch (type){
            case "remote":
                return new RemoteCodeSandboxImpl();
            case "thirdParty":
                return new ThirdPartyCodeSandBoxImpl();
            default:
                return new ExampleCodeSandboxImpl();
        }
    }

}
