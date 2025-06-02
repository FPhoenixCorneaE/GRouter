# GRouter

帮助 Android App 进行组件化改造的路由框架

[![](https://jitpack.io/v/FPhoenixCorneaE/GRouter.svg)](https://jitpack.io/#FPhoenixCorneaE/GRouter)

### 一、添加依赖

1. #### 添加 Jitpack.io 仓库

```kotlin
pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
        maven("https://jitpack.io")
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
}
```

2. #### 添加 grouter-plugin 插件，用于字节码注入自动生成 RouterRegister 类

> <font color="red">注意：插件仅支持 AGP 7.4+</font>

project build.gradle.kts:

```kotlin
buildscript {
    dependencies {
        classpath("com.github.FPhoenixCorneaE.GRouter:grouter-plugin:$latest")
    }
}
```

application build.gradle.kts:

```kotlin
plugins {
    id("grouter-plugin")
}
```

3. #### 添加 ksp 注解处理器以及 Router 依赖

```kotlin
plugins {
    id("com.google.devtools.ksp")
}

ksp {
    arg("gRouterModuleName", project.name)
    arg("gRouterDefaultScheme", "your scheme")
    arg("gRouterDefaultHost", "your host")
}

dependencies {
    ksp("com.github.FPhoenixCorneaE.GRouter:grouter-process:$latest")
    implementation("com.github.FPhoenixCorneaE.GRouter:grouter-api:$latest")
}
```

### 二、功能介绍

- [x] **支持直接解析标准URL进行跳转，参数在目标页面通过 Intent 获取**
- [x] **支持多模块工程使用**
- [x] **支持添加多个拦截器**
- [ ] **支持自定义拦截器顺序**
- [ ] **支持多种方式配置转场动画**
- [ ] **支持获取Fragment**
- [ ] **支持生成路由文档**


### 三、典型应用
1. #### 从外部URL映射到内部页面，以及参数传递与解析
2. #### 跨模块页面跳转，模块间解耦
3. #### 拦截跳转过程，处理登陆、埋点等逻辑
4. #### 跨模块API调用，通过控制反转来做组件解耦

### 四、基础用法
1. #### 添加注解
```kotlin
@Router(scheme = "deeplink", host = "grouter", path = "/second", description = "SecondActivity")
class SecondActivity : AppCompatActivity() {
}
```

2. #### 发起路由操作
```kotlin
GRouter.with(view.context)
    .url("deeplink://grouter/second")
    .start()
```

3. #### 添加混淆规则（待续）

### 五、进阶用法