# Campus Lost & Found - 智能校园失物招领平台

一个全栈校园失物招领系统，支持物品发布、AI 智能匹配寻物、WebSocket 实时私信、通知推送，包含移动端用户应用和桌面端管理后台。

## 功能特性

- **失物/招领发布** — 发布丢失或拾到的物品，支持图片上传、分类、地点标记
- **AI 智能寻物** — 上传物品照片，AI 自动匹配数据库中的相似物品（基于智谱 API）
- **实时私信** — 基于 WebSocket 的即时通讯，在物品详情页可直接联系发布者
- **通知中心** — 物品被留言、被收藏时实时推送通知
- **线索系统** — 对物品补充线索信息，帮助失主找回物品
- **收藏管理** — 收藏感兴趣的物品，方便后续跟踪
- **管理后台** — 用户管理、物品审核、线索/私信/通知管理、分类与地点字典维护

## 技术栈

| 层级 | 技术 |
|------|------|
| 后端 | Java 17, Spring Boot 3.4, MyBatis-Plus, WebSocket, MySQL 5.7, Redis |
| 用户端 | Vue 3, Vant 4, Pinia, UnoCSS, Vite |
| 管理端 | Vue 3, Element Plus, UnoCSS, Vite |
| 文件存储 | 阿里云 OSS |
| AI | 智谱大模型 API |
| 部署 | Docker Compose |

## 项目结构

```
├── slf-backend/           # Spring Boot 后端
├── slf-frontend-user/     # 移动端用户应用（Vant）
├── slf-frontend-admin/    # 桌面端管理后台（Element Plus）
├── docker-compose.yml     # Docker 编排
└── 数据库/                 # SQL 脚本
```

## 快速开始

### 环境要求

- Java 17+
- Node.js 18+
- pnpm
- MySQL 5.7+
- Redis

### 1. 启动基础设施

使用 Docker 一键启动 MySQL 和 Redis：

```bash
docker-compose up -d mysql redis
```

或手动安装 MySQL 和 Redis，然后导入 `数据库/school_lost_found.sql`。

### 2. 配置后端

```bash
cd slf-backend

# 复制配置模板并填入你的真实密码/密钥
cp src/main/resources/application-local.properties.example \
   src/main/resources/application-local.properties

# 编辑 application-local.properties，填入数据库密码、OSS 密钥、AI API Key 等
```

启动后端：

```bash
./mvnw spring-boot:run
```

后端默认运行在 `http://localhost:8080`。

### 3. 启动前端

```bash
# 安装依赖（在项目根目录）
cd slf-frontend-user && pnpm install
cd ../slf-frontend-admin && pnpm install
cd ..

# 同时启动两个前端
pnpm dev

# 或单独启动
pnpm dev:user    # 用户端 → http://localhost:5555
pnpm dev:admin   # 管理端 → http://localhost:5173
```

## 数据库

首次使用需导入数据库：

```bash
mysql -u root -p < 数据库/school_lost_found.sql
```

主要数据表：

| 表名 | 说明 |
|------|------|
| `slf_user` | 用户 |
| `slf_item` | 失物/招领物品 |
| `slf_category` | 物品分类 |
| `slf_location` | 地点 |
| `slf_clue` | 线索 |
| `slf_favorite` | 收藏 |
| `slf_conversation` | 私信会话 |
| `slf_private_message` | 私信消息 |
| `slf_notification` | 通知 |
| `slf_ai_message` | AI 对话记录 |

## 部署

```bash
# 构建用户端
cd slf-frontend-user && pnpm build

# 构建管理端
cd ../slf-frontend-admin && pnpm build

# 构建后端 JAR
cd ../slf-backend && ./mvnw package -DskipTests

# Docker 一键部署
docker-compose up -d
```

## 配置说明

敏感配置通过 `application-local.properties` 管理（已被 `.gitignore` 忽略），需要配置的项：

| 配置项 | 说明 |
|--------|------|
| `spring.datasource.password` | MySQL 密码 |
| `spring.data.redis.password` | Redis 密码 |
| `jwt.secret` | JWT 签名密钥 |
| `oss.access-key-id` | 阿里云 OSS AccessKey ID |
| `oss.access-key-secret` | 阿里云 OSS AccessKey Secret |
| `zhipu.api.key` | 智谱 AI API Key |

详见 `application-local.properties.example` 模板。

## License

MIT
