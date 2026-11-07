# Doro五子棋 🎀

一个**最简单**的安卓五子棋 App：15×15 棋盘，黑白轮流，五子连珠即胜。

## 文件结构

```
DoroGomoku/
├── settings.gradle
├── build.gradle
├── gradle.properties
└── app/
    ├── build.gradle
    └── src/main/
        ├── AndroidManifest.xml
        ├── java/com/doro/gomoku/
        │   ├── MainActivity.kt   # 界面：提示文字 + 棋盘 + 重新开始
        │   └── GomokuView.kt     # 自绘棋盘：画格子、画棋子、判断胜负
        └── res/values/
            ├── strings.xml
            └── themes.xml
```

## 怎么跑起来

**方法一：Android Studio（推荐）**

1. 打开 Android Studio → `File` → `Open`，选中 `DoroGomoku` 这个文件夹
2. 等它自动同步 Gradle（第一次要下载依赖，喝杯水再回来）
3. 点绿色 ▶️ Run，选个模拟器或者插上手机

> 注意：本工程没有带 `gradle-wrapper.jar`，Android Studio 会提示自动补上，
> 或者手动执行 `gradle wrapper` 生成。

**方法二：命令行**

```bash
cd DoroGomoku
gradle wrapper          # 只需第一次
./gradlew assembleDebug  # 打 debug 包
# 产物在 app/build/outputs/apk/debug/app-debug.apk
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

## 玩法

- 黑棋先手，点棋盘交叉点落子
- 粉色小圆点标记刚才那一手
- 任意方向连成 5 子即获胜
- 下方「重新开始」按钮清盘重来

## 想加功能的话

代码里留了很好改的地方：

| 想实现 | 改哪里 |
|---|---|
| 改棋盘大小 | `GomokuView.SIZE`（比如 19 路） |
| 改连几子赢 | `GomokuView.WIN_COUNT` |
| 加悔棋 | 用 `ArrayDeque<IntArray>` 存历史落子，撤销时还原 board |
| 加 AI 对手 | 写个 `AiPlayer`，在落子后遍历空点算评分 |
| 换配色 | `GomokuView` 里各个 `Paint` 的 `color` |

---
Doro 出品，落子要轻轻的哦 (◕‿◕)
