# AndroidMVVM 架构文档

> 本文档描述项目的模块划分、整体架构、技术栈与外部依赖，随架构演进持续更新。

## 1. 项目概览

一个基于 **MVVM + Jetpack Compose** 的 Android 社交类应用，后端使用 **Supabase**（认证 + 数据库 + 存储），支持邮箱 OTP 登录与 Google 登录。项目采用 Gradle 多模块 + Version Catalog 管理依赖，通用 HTTP 基础设施已抽取为独立公共模块 `:http`。

| 项 | 值 |
|---|---|
| applicationId | `com.lokahe.androidmvvm` |
| minSdk / targetSdk / compileSdk | 26 / 36 / 36 |
| JVM Target | 11 |
| Kotlin | 2.0.21 |
| AGP | 8.13.2 |

---

## 2. 模块结构

```
AndroidMVVM/
├── app/        # 业务主模块（UI、业务逻辑、数据层）
├── http/       # 通用 HTTP 基础设施模块（无业务逻辑）
├── log/        # 统一日志模块（级别管理 + 可插拔输出端）
└── gradle/
    └── libs.versions.toml   # Version Catalog，统一管理依赖版本
```

### 2.1 `:app` — 业务模块

包名 `com.lokahe.androidmvvm`，按 MVVM 分层组织：

```
com.lokahe.androidmvvm/
├── MyApplication.kt          # @HiltAndroidApp 入口
├── data/
│   ├── remote/               # Api（常量）、ApiService（Retrofit 接口定义）
│   ├── local/                # Room（AppDatabase + DAO）、DataStore（UserManager 等）
│   ├── models/               # 数据模型：supabase/（业务模型）、auth/、本地实体（Person、Post、UserPreferences）
│   └── repository/           # HttpRepository（网络）、DataBaseRepository（Room）、PreferencesRepository（DataStore）
├── di/                       # Hilt 模块：NetWorkModule、DataBaseModule、PreferencesModule
├── viewmodels/               # BaseViewModel 及各业务 ViewModel（Main / Post / User / Settings）
├── ui/
│   ├── screens/              # Compose 页面 + Nav.kt（Navigation 3 路由定义）
│   ├── activites/            # MainActivity（单 Activity 入口）、SettingsActivity
│   ├── widget/               # 通用 Compose 组件
│   ├── preference/           # 自研 Preference（DataStore 绑定）组件库
│   ├── theme/                # Material 3 主题（支持动态取色 ColorSeed）
│   └── vectors/              # 自定义图标
├── data/auth/GoogleAuther.kt # Google 登录封装（Credential Manager）
└── utils / Const / Extensions / LocalProvider
```

### 2.2 `:http` — 通用 HTTP 基础设施模块

包名 `com.lokahe.http`，**不含任何业务逻辑**，可被其他模块/项目复用：

| 文件 | 职责 |
|---|---|
| `ApiResult.kt` | `ApiResult<T>` sealed class（Success / Error）与 `ApiError` 统一错误封装 |
| `SafeApiCall.kt` | `safeApiCall { }`：将 `Response<T>` 安全包装为 `Flow<ApiResult<T>>` |
| `ProgressRequestBody.kt` | 带上传进度的 RequestBody（File / Uri 两种实现） |
| `HttpConfig.kt` | HTTP 配置（baseUrl、超时、日志级别），由使用方注入 |
| `di/HttpModule.kt` | Hilt 模块，基于 `HttpConfig` 构建 `OkHttpClient` / `Retrofit`（单例） |

对消费者通过 `api(...)` 透传 Retrofit / OkHttp / Gson Converter，使用方无需重复声明这些依赖。

**依赖注入约定**：`:app` 通过自己的 Hilt 模块（`NetWorkModule`）提供 `HttpConfig`（baseUrl 来自 `Api.SPB_URL`）并创建业务 `ApiService`；OkHttp / Retrofit 的实例化由 `:http` 的 `HttpModule` 完成。

### 2.3 `:log` — 统一日志模块

包名 `com.lokahe.log`，零第三方依赖：

| 文件 | 职责 |
|---|---|
| `LogLevel.kt` | 日志级别枚举（V/D/I/W/E），级别过滤基础 |
| `LogConfig.kt` | 初始化配置：全局最小级别、总开关、输出端列表 |
| `LogSink.kt` | 输出端接口 + 内置 `LogcatSink`；MMKV 存储 / 崩溃上报后续实现此接口注册即可，调用方无感 |
| `L.kt` | 统一门面：`init(LogConfig)` 配置级别；`v/d/i/w/e` 提供立即求值与惰性求值（lambda）双版本 |
| `LogExtensions.kt` | `logD { }` 等扩展函数，自动以调用方类名为 Tag |

约定：初始化在 `MyApplication.onCreate`（debug 用 DEBUG 级别、release 用 WARN 级别）；其他模块只依赖 `L` 门面，禁止直接使用 `android.util.Log`。全项目存量 `Log.x` 调用已于 2026-10-15 全部迁移至 `L`，现仅 `LogSink.kt` 内置的 `LogcatSink` 直接调用 Android Log API（作为唯一输出适配器）。

### 2.4 模块依赖关系

```
:app ──implementation──▶ :http ──▶ Retrofit / OkHttp / Gson / Hilt
:app ──implementation──▶ :log
:http ──implementation──▶ :log
```

---

## 3. 整体架构（MVVM）

```
UI 层（Compose）
  MainActivity / Screens / Widgets
  Navigation 3 路由（lokahe://androidmvvm 深链）
        │ StateFlow / Compose State
        ▼
ViewModel 层
  BaseViewModel（公共逻辑：用户态、弹窗、偏好）
  MainViewModel / PostViewModel / UserViewModel / SettingsViewModel
        │ 调用
        ▼
Repository 层
  HttpRepository（网络） DataBaseRepository（Room） PreferencesRepository（DataStore）
        │                          │
        ▼                          ▼
 远端（Supabase）              本地存储
 Retrofit + OkHttp             Room / DataStore
（:http 模块提供）
```

关键模式：

- **统一网络结果**：Repository 返回 `Flow<ApiResult<T>>`，ViewModel 收集后区分成功/失败更新 UI（`ApiResult` 来自 `:http`）。
- **单 Activity + Compose 导航**：路由用 `@Serializable sealed interface Screen : NavKey` 定义（Navigation 3），支持 `lokahe://androidmvvm` 深链。
- **用户态**：`UserManager`（DataStore）持久化 Supabase 会话（token / refresh_token / 过期时间），`BaseViewModel.currentUser` 以 `StateFlow` 暴露给 UI。
- **主题**：Material 3 + Palette 动态取色（`ColorSeed`）。

---

## 4. 技术栈

| 类别 | 技术 | 版本 |
|---|---|---|
| 语言 | Kotlin | 2.0.21 |
| UI | Jetpack Compose（BOM）/ Material 3 | 2025.12.01 |
| 导航 | androidx.navigation3（runtime + ui） | 1.1.0-alpha04 |
| 路由序列化 | kotlinx-serialization-json | 1.6.3 |
| DI | Hilt + hilt-navigation-compose | 2.57.2 / 1.3.0 |
| 注解处理 | KSP | 2.0.21-1.0.27 |
| 网络 | Retrofit + Gson Converter | 2.9.0 |
| 网络 | OkHttp + Logging Interceptor | 4.12.0 |
| 协程 | kotlinx-coroutines-core | 1.9.0 |
| 本地存储 | Room（runtime/ktx/compiler） | 2.6.1 |
| 本地存储 | DataStore Preferences | 1.2.0 |
| 图片 | Coil (compose) | 2.7.0 |
| 兼容库 | AppCompat / Core-KTX / Lifecycle | 1.7.1 / 1.17.0 / 2.10.0 |
| 认证 | Credentials + Play Services Auth + Google ID | 1.5.0 / 21.5.0 / 1.2.0 |
| 其他 | Material Components / Palette / Icons-Extended | 1.13.0 / 1.0.0 / 1.7.8 |
| 测试 | JUnit / AndroidX Test / Espresso / Compose UI Test | 4.13.2 / 1.3.0 / 3.7.0 |
| 日志 | 自研 `:log` 模块（`L` 门面 + 可插拔 `LogSink`） | 无第三方依赖 |

所有版本集中在 `gradle/libs.versions.toml` 中管理。

---

## 5. 外部依赖（后端与服务）

| 服务 | 用途 | 配置来源 |
|---|---|---|
| Supabase（自托管） | 认证（邮箱 OTP / Google）、PostgREST 数据接口、存储 | `SPB_URL` + `ANON_KEY` |
| Google OAuth | Google 登录（Credential Manager + GoogleID） | `GOOGLE_WEB_CLIENT_ID` |
| Google Play Services | Credentials 回退支持 | — |

构建时通过 `local.properties` 或环境变量注入（`app/build.gradle.kts` 的 `buildConfigField`）：

```properties
# local.properties 示例
SPB_URL=https://your-supabase-host
ANON_KEY=eyJhbGciOi...
GOOGLE_WEB_CLIENT_ID=xxxx.apps.googleusercontent.com
```

---

## 6. 构建说明

```bash
# Android Studio 内直接构建即可；命令行需注意 JAVA_HOME
# 本机默认 JAVA_HOME 若为 JDK 8 会失败（AGP 8.x 要求 JDK 17+），
# 可在 gradle.properties 中固定：
# org.gradle.java.home=D\:\\Program Files\\Android\\Android Studio\\jbr

gradlew.bat :app:assembleDebug          # 完整打包
gradlew.bat :http:assembleDebug         # 仅构建 :http 模块
```

---

## 7. 架构演进记录

| 日期 | 变更 |
|---|---|
| 2026-10-14 | 抽取 `:http` 公共模块（`ApiResult`/`ApiError`、`safeApiCall`、`ProgressRequestBody`、`HttpConfig`、Hilt `HttpModule`）；`NetWorkModule` 精简为仅提供 `HttpConfig` 与 `ApiService`；app 内移除重复的通用实现。 |
| 2026-10-14 | 新增本架构文档。 |
| 2026-10-15 | 新增 `:log` 统一日志模块（级别管理、可插拔 Sink、惰性求值）；`MyApplication` 与 `:http/safeApiCall` 接入，同日完成全项目存量 `Log.x` 迁移（7 文件 13 处：GoogleAuther、UserManager、DataBaseModule、MainActivity、SendPostScreen、MainViewModel、PostViewModel）；后续规划 MMKV 持久化与崩溃级别日志上报。 |

## 8. 后续演进方向（待定）

- [ ] `:http` 增加单元测试（`safeApiCall` 的成功/失败路径）。
- [ ] 业务接口鉴权拦截器（token 自动附加与刷新）下沉至 `:http` 或独立模块。
- [ ] Repository 层引入接口抽象，便于替换数据源与测试。
- [ ] Navigation 3 目前为 alpha 版本，关注其稳定版发布并升级。
- [ ] 开启 release 混淆（`isMinifyEnabled` 当前为 false）并补充 R8 规则。
- [x] 存量 `Log.x` 调用迁移至 `:log` 的 `L`（GoogleAuther / UserManager / DataBaseModule / MainActivity / SendPostScreen / MainViewModel / PostViewModel，共 13 处）——已完成（2026-10-15）。
- [ ] `:log` 接入 MMKV 持久化（文件 Sink）与崩溃级别日志上报。
