#!/bin/bash

# 自动化部署脚本 - 包含 MySQL 和后端应用
# 用法：./auto-deploy.sh

set -e

echo "=========================================="
echo "  开始自动化部署流程"
echo "=========================================="

# 配置变量
MYSQL_IMAGE="mysql:8.0"
APP_IMAGE="sale-system:latest"
MYSQL_CONTAINER="sale-mysql"
APP_CONTAINER="sale-system"
MYSQL_ROOT_PASSWORD="root"
MYSQL_DATABASE="sale_system"

# 1. 检查 Docker
echo "[1/7] 检查 Docker..."
if ! command -v docker &> /dev/null; then
    echo "错误：未找到 Docker"
    exit 1
fi
echo "Docker 版本：$(docker --version)"

# 2. 停止并删除旧容器
echo "[2/7] 清理旧容器..."
docker stop $APP_CONTAINER 2>/dev/null || true
docker rm $APP_CONTAINER 2>/dev/null || true
echo "应用容器已清理"

# 3. 删除旧的应用镜像
echo "[3/7] 删除旧应用镜像..."
docker rmi $APP_IMAGE 2>/dev/null || true
echo "旧应用镜像已删除"

# 4. 构建新镜像
echo "[4/7] 构建 Docker 镜像..."
docker build -t $APP_IMAGE .

# 5. 部署 MySQL（如果不存在）
echo "[5/7] 检查 MySQL 容器..."
if ! docker ps -a --format '{{.Names}}' | grep -q "^${MYSQL_CONTAINER}$"; then
    echo "创建 MySQL 容器..."
    docker run -d --name $MYSQL_CONTAINER -e MYSQL_ROOT_PASSWORD=$MYSQL_ROOT_PASSWORD -e MYSQL_DATABASE=$MYSQL_DATABASE -p 3306:3306 -v mysql-data:/var/lib/mysql -v $(pwd)/src/main/resources/db/schema.sql:/docker-entrypoint-initdb.d/schema.sql --restart unless-stopped $MYSQL_IMAGE
    echo "等待 MySQL 初始化 (约 15 秒)..."
    sleep 15
else
    echo "MySQL 容器已存在"
    if ! docker ps --format '{{.Names}}' | grep -q "^${MYSQL_CONTAINER}$"; then
        echo "启动 MySQL 容器..."
        docker start $MYSQL_CONTAINER
        sleep 5
    fi
fi

# 6. 启动应用容器
echo "[6/7] 启动应用容器..."
APP_RUN_CMD="docker run -d --name $APP_CONTAINER -p 8080:8080 --link $MYSQL_CONTAINER:mysql -e SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/$MYSQL_DATABASE?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true -e SPRING_DATASOURCE_USERNAME=root -e SPRING_DATASOURCE_PASSWORD=$MYSQL_ROOT_PASSWORD --restart unless-stopped $APP_IMAGE"
eval $APP_RUN_CMD

# 7. 验证状态
echo "[7/7] 验证容器状态..."
sleep 3
docker ps --filter "name=$APP_CONTAINER" --filter "name=$MYSQL_CONTAINER"

echo ""
echo "=========================================="
echo "  部署完成！"
echo "=========================================="
echo "  MySQL 容器：$MYSQL_CONTAINER"
echo "  应用容器：$APP_CONTAINER"
echo "  后端地址：http://localhost:8080"
echo "  前端地址：http://localhost:5174"
echo "  默认账号：admin / admin123"
echo "=========================================="
