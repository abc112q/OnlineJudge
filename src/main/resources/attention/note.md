###使用utf-8格式编译
【测试代码在testCode中，直接open in terminal】

"C:\Program Files\Java\jdk1.8.0_151\bin\javac" -encoding utf-8 .\Main.java 
> 当系统有两个java版本需要确保运行和编译、以及maven必须使用同一个jdk版本
> 实在不行就像上面直接强制使用jdk1.8来javac编译
> 甚至调整了terminal为cmd而不是shell
> 如果直接用javac -encoding utf-8 .\SimpleCompute.java进行编译再用java运行
> 就会出现编译和运行的版本不一致 从而报错
###使用java运行
java -cp . Main 1 2

为了统一处理用户输入的代码，我们将用户输入代码的类统一命名为Main
