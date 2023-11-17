# 构建说明

该项目使用`GNU make`进行构建过程。

已经安装了`make`的话，可以跳过第一步。

## 步骤 1：安装`make`

### Windows

> 推荐使用 chocolatey（Windows 的包管理器）来更轻松地开始。
>
> 安装[chocolatey](https://chocolatey.org/install)。

##### chocolatey:

运行 `choco install make` 通过 chocolatey 安装`GNU make`。

##### 非 chocolatey:

我现在有点懒，所以你只能自己搞定了。

### Mac

> 最简单的方法是使用 Homebrew 安装`make`。

##### Homebrew:

`brew install make`

## 步骤 2：运行代码

一旦安装了`make`，运行代码就很简单了。只需导航到项目目录并运行`make`。

当运行正确时，命令行将类似于以下内容：

```shell
javac -d bin src/app/*.java --module-path ./utils/lib/ --add-modules javafx.controls
java -cp ./src/app:bin:.src/app --module-path ./utils/lib/ --add-modules javafx.controls app.Main
```
