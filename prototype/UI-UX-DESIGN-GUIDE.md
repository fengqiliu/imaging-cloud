# D-Site 云胶片管理系统 - UI/UX 设计规范

## 1. 设计原则

### 1.1 核心理念
- **专业可信赖**: 医疗系统需要传达专业、严谨、可信赖的视觉感受
- **高效直观**: 医生操作时间宝贵，界面需高效、信息层次清晰
- **清晰易读**: 医学影像数据需要高对比度、易读的界面
- **一致性**: 全系统保持统一的视觉语言和交互模式

### 1.2 设计法则
```
优先功能 > 简洁美观 > 创新突破
```

---

## 2. 色彩系统

### 2.1 主色调
```css
/* 品牌蓝 - 传达专业、科技、可信赖 */
--primary-color: #1890ff;
--primary-light: #40a9ff;
--primary-dark: #096dd9;
--primary-bg: #e6f7ff;      /* 浅蓝背景 */

/* 渐变方案 - 用于登录页、大banner */
background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
background: linear-gradient(135deg, #1890ff 0%, #096dd9 100%);
```

### 2.2 功能色
```css
/* 成功 - 用于正常、已完成、通过等 */
--success-color: #52c41a;
--success-bg: #f6ffed;
--success-border: #b7eb8f;

/* 警告 - 用于待处理、进行中、审核中等 */
--warning-color: #faad14;
--warning-bg: #fffbe6;
--warning-border: #ffe58f;

/* 危险 - 用于错误、删除、异常等 */
--danger-color: #ff4d4f;
--danger-bg: #fff1f0;
--danger-border: #ffccc7;

/* 信息 - 用于提示、说明等 */
--info-color: #1890ff;
--info-bg: #e6f7ff;
```

### 2.3 检查状态色
```css
--status-pending: #faad14;      /* 待检查 - 橙黄 */
--status-progress: #1890ff;    /* 检查中 - 蓝色 */
--status-completed: #52c41a;    /* 已完成 - 绿色 */
--status-reported: #722ed1;     /* 已出报告 - 紫色 */
```

### 2.4 影像模态色
```css
--modality-ct: #1890ff;         /* CT - 蓝色 */
--modality-mri: #52c41a;        /* MRI - 绿色 */
--modality-dr: #faad14;         /* DR/X光 - 橙黄 */
--modality-us: #ff4d4f;         /* 超声 - 红色 */
--modality-xa: #722ed1;         /* 血管造影 - 紫色 */
--modality-pet: #eb2f96;        /* PET - 粉红 */
```

### 2.5 中性色
```css
--gray-900: #141414;            /* 深色文字 */
--gray-800: #262626;
--gray-700: #434343;
--gray-600: #595959;
--gray-500: #8c8c8c;            /* 次要文字 */
--gray-400: #bfbfbf;
--gray-300: #d9d9d9;            /* 边框 */
--gray-200: #f0f0f0;            /* 背景 */
--gray-100: #f5f5f5;            /* 页面背景 */
--gray-50: #fafafa;             /* 悬停背景 */
```

---

## 3. 字体系统

### 3.1 字体家族
```css
/* 优先使用系统字体，确保跨平台一致性 */
font-family: -apple-system, BlinkMacSystemFont,
             'Segoe UI', 'PingFang SC', 'Hiragino Sans GB',
             'Microsoft YaHei', 'Helvetica Neue', Arial, sans-serif;

/* 代码/数字专用 */
font-family: 'SF Mono', 'Consolas', 'Monaco', monospace;
```

### 3.2 字体层级
```css
/* 页面标题 - h1 */
font-size: 24px;
font-weight: 600;
line-height: 1.5;
color: #303133;

/* 卡片标题 - h2 */
font-size: 16px;
font-weight: 600;
line-height: 1.5;
color: #303133;

/* 正文 - p */
font-size: 14px;
font-weight: 400;
line-height: 1.8;
color: #606266;

/* 辅助文字 - small */
font-size: 13px;
font-weight: 400;
color: #909399;

/* 标签/徽章 - span */
font-size: 12px;
font-weight: 500;
```

### 3.3 数字显示
```css
/* 统计数字 - 大号 */
.stat-value {
    font-size: 32px;
    font-weight: 600;
    font-variant-numeric: tabular-nums;  /* 等宽数字 */
}

/* 表格数字 */
font-variant-numeric: tabular-nums;
letter-spacing: 0;  /* 数字不要间距 */
```

---

## 4. 间距系统

### 4.1 基础间距单位
```css
--spacing-xs: 4px;   /* 紧凑元素内部 */
--spacing-sm: 8px;    /* 相关元素间距 */
--spacing-md: 12px;   /* 同组元素 */
--spacing-lg: 16px;   /* 区块内元素 */
--spacing-xl: 24px;   /* 区块间距 */
--spacing-xxl: 32px;  /* 页面分区 */
```

### 4.2 组件间距规范
```css
/* 卡片内边距 */
.card {
    padding: 24px;
}

/* 表格单元格 */
.data-table td {
    padding: 16px;
}

/* 表单项间距 */
.form-item {
    margin-bottom: 20px;
}

/* 按钮间距 */
.btn + .btn {
    margin-left: 12px;
}

/* 列表项间距 */
.list-item + .list-item {
    margin-top: 12px;
}
```

---

## 5. 圆角系统

### 5.1 圆角规范
```css
/* 小组件 */
--radius-sm: 4px;    /* 标签、徽章、状态 */

/* 常规组件 */
--radius-md: 8px;     /* 按钮、输入框、下拉框 */

/* 大组件 */
--radius-lg: 12px;    /* 卡片、模态框、面板 */

/* 特殊 */
--radius-xl: 16px;    /* 大卡片、登录面板、医疗影像查看器面板 */
--radius-full: 9999px; /* 圆形头像、胶囊按钮 */
```

### 5.2 组件圆角
```css
.button { border-radius: 8px; }
.input { border-radius: 8px; }
.card { border-radius: 12px; }
.modal { border-radius: 12px; }
.avatar { border-radius: 50%; }
.tag { border-radius: 4px; }
.badge { border-radius: 10px; }
```

---

## 6. 阴影系统

### 6.1 阴影层级
```css
/* 轻微阴影 - 卡片悬浮 */
--shadow-sm: 0 2px 8px rgba(0, 0, 0, 0.06);

/* 常规阴影 - 下拉菜单 */
--shadow-md: 0 4px 12px rgba(0, 0, 0, 0.08);

/* 深度阴影 - 模态框 */
--shadow-lg: 0 8px 24px rgba(0, 0, 0, 0.12);

/* 登录页阴影 */
box-shadow: -10px 0 30px rgba(0, 0, 0, 0.1);

/* 弹出层阴影 */
box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
```

---

## 7. 组件规范

### 7.1 按钮

#### 尺寸规范
```css
/* 大按钮 */
.btn-lg {
    height: 40px;
    padding: 0 24px;
    font-size: 14px;
}

/* 常规按钮 */
.btn {
    height: 36px;
    padding: 0 20px;
    font-size: 14px;
}

/* 小按钮 */
.btn-sm {
    height: 30px;
    padding: 0 12px;
    font-size: 12px;
}

/* 紧凑按钮 */
.btn-xs {
    height: 24px;
    padding: 0 8px;
    font-size: 12px;
}
```

#### 按钮状态
```css
/* 默认状态 */
.btn-default {
    background: #ffffff;
    border: 1px solid #dcdfe6;
    color: #606266;
}

/* 悬停状态 */
.btn:hover {
    border-color: #1890ff;
    color: #1890ff;
}

/* 点击状态 */
.btn:active {
    transform: scale(0.98);
}

/* 禁用状态 */
.btn:disabled {
    opacity: 0.6;
    cursor: not-allowed;
}

/* 加载状态 */
.btn.loading {
    pointer-events: none;
}
```

#### 按钮类型
```css
.btn-primary {     /* 主按钮 - 蓝色 */
    background: #1890ff;
    border-color: #1890ff;
    color: #ffffff;
}
.btn-primary:hover { background: #40a9ff; border-color: #40a9ff; }

.btn-success {     /* 成功按钮 - 绿色 */
    background: #52c41a;
    border-color: #52c41a;
    color: #ffffff;
}

.btn-danger {      /* 危险按钮 - 红色 */
    background: #ff4d4f;
    border-color: #ff4d4f;
    color: #ffffff;
}

.btn-warning {      /* 警告按钮 - 橙色 */
    background: #faad14;
    border-color: #faad14;
    color: #ffffff;
}

.btn-default {     /* 默认按钮 - 白色 */
    background: #ffffff;
    border: 1px solid #dcdfe6;
    color: #606266;
}

.btn-text {        /* 文字按钮 - 无边框 */
    background: transparent;
    border: none;
    color: #1890ff;
}
```

#### 图标按钮
```css
.icon-btn {
    width: 32px;
    height: 32px;
    padding: 0;
    display: inline-flex;
    align-items: center;
    justify-content: center;
    border-radius: 6px;
    background: transparent;
    border: none;
    color: #909399;
    cursor: pointer;
    transition: all 0.3s;
}

.icon-btn:hover {
    background: #f5f7fa;
    color: #1890ff;
}

.icon-btn.danger:hover {
    background: #fff1f0;
    color: #ff4d4f;
}
```

### 7.2 输入框

#### 尺寸规范
```css
.input {
    height: 36px;
    padding: 0 12px;
    font-size: 14px;
    border: 1px solid #dcdfe6;
    border-radius: 8px;
    outline: none;
    transition: all 0.3s;
}

/* 大尺寸输入框 */
.input-lg {
    height: 40px;
    font-size: 16px;
}

/* 小尺寸输入框 */
.input-sm {
    height: 32px;
    font-size: 13px;
}
```

#### 输入框状态
```css
/* 默认状态 */
.input {
    border-color: #dcdfe6;
}

/* 聚焦状态 */
.input:focus {
    border-color: #1890ff;
    box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.1);
}

/* 错误状态 */
.input.error {
    border-color: #ff4d4f;
}

.input.error:focus {
    box-shadow: 0 0 0 2px rgba(255, 77, 79, 0.1);
}

/* 禁用状态 */
.input:disabled {
    background: #f5f7fa;
    cursor: not-allowed;
}
```

#### 文本域
```css
.textarea {
    min-height: 120px;
    padding: 12px;
    resize: vertical;
}
```

### 7.3 下拉选择框
```css
.select {
    height: 36px;
    padding: 0 12px;
    font-size: 14px;
    border: 1px solid #dcdfe6;
    border-radius: 8px;
    background: #ffffff;
    cursor: pointer;
    outline: none;
}

.select:focus {
    border-color: #1890ff;
    box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.1);
}
```

### 7.4 卡片

#### 卡片规范
```css
.card {
    background: #ffffff;
    border-radius: 12px;
    padding: 24px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
}

/* 卡片标题区 */
.card-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding-bottom: 20px;
    margin-bottom: 20px;
    border-bottom: 1px solid #f0f0f0;
}

.card-title {
    font-size: 16px;
    font-weight: 600;
    color: #303133;
    display: flex;
    align-items: center;
    gap: 8px;
}

.card-title i {
    color: #1890ff;
}
```

### 7.5 表格

#### 表格规范
```css
.data-table {
    width: 100%;
    border-collapse: collapse;
}

.data-table th {
    background: #f5f7fa;
    padding: 12px 16px;
    text-align: left;
    font-weight: 500;
    font-size: 14px;
    color: #909399;
    border-bottom: 1px solid #dcdfe6;
}

.data-table td {
    padding: 16px;
    border-bottom: 1px solid #ebeef5;
    font-size: 14px;
    color: #606266;
}

.data-table tr:hover {
    background: #fafafa;
}

.data-table tr:last-child td {
    border-bottom: none;
}
```

### 7.6 标签/徽章

#### 状态标签
```css
.tag {
    display: inline-flex;
    align-items: center;
    padding: 4px 12px;
    border-radius: 4px;
    font-size: 12px;
    font-weight: 500;
}

/* 状态组合 */
.tag.pending {
    background: #fffbe6;
    color: #ad6800;
}

.tag.in-progress {
    background: #e6f7ff;
    color: #1890ff;
}

.tag.completed {
    background: #f6ffed;
    color: #52c41a;
}

.tag.reported {
    background: #f9f0ff;
    color: #722ed1;
}

.tag.disabled {
    background: #f5f5f5;
    color: #999999;
}
```

#### 数字徽章
```css
.badge {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    min-width: 20px;
    height: 20px;
    padding: 0 6px;
    border-radius: 10px;
    background: #ff4d4f;
    color: #ffffff;
    font-size: 12px;
    font-weight: 500;
}

.badge.dot {
    width: 8px;
    height: 8px;
    min-width: 8px;
    padding: 0;
    border-radius: 50%;
}
```

### 7.7 头像
```css
.avatar {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    border-radius: 50%;
    background: linear-gradient(135deg, #1890ff, #096dd9);
    color: #ffffff;
    font-weight: 500;
    overflow: hidden;
}

.avatar-sm { width: 32px; height: 32px; font-size: 12px; }
.avatar-md { width: 40px; height: 40px; font-size: 14px; }
.avatar-lg { width: 48px; height: 48px; font-size: 16px; }
.avatar-xl { width: 64px; height: 64px; font-size: 20px; }
```

### 7.8 分页
```css
.pagination {
    display: flex;
    align-items: center;
    justify-content: flex-end;
    margin-top: 20px;
    gap: 8px;
}

.pagination-info {
    color: #909399;
    font-size: 14px;
    margin-right: auto;
}

.page-btn {
    min-width: 32px;
    height: 32px;
    padding: 0 8px;
    border: 1px solid #dcdfe6;
    border-radius: 6px;
    background: #ffffff;
    color: #606266;
    font-size: 14px;
    cursor: pointer;
    transition: all 0.3s;
}

.page-btn:hover {
    border-color: #1890ff;
    color: #1890ff;
}

.page-btn.active {
    background: #1890ff;
    border-color: #1890ff;
    color: #ffffff;
}

.page-btn:disabled {
    opacity: 0.5;
    cursor: not-allowed;
}
```

---

## 8. 布局规范

### 8.1 页面布局
```css
.main-layout {
    display: flex;
    min-height: 100vh;
}

.sidebar {
    width: 220px;
    background: #001529;
    position: fixed;
    height: 100vh;
}

.main-content {
    flex: 1;
    margin-left: 220px;
    min-height: 100vh;
}

.header {
    height: 60px;
    background: #ffffff;
    border-bottom: 1px solid #dcdfe6;
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 24px;
    position: sticky;
    top: 0;
    z-index: 50;
}

.content {
    padding: 24px;
}
```

### 8.2 栅格系统
```css
/* 统计卡片网格 */
.stats-grid {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 24px;
}

@media (max-width: 1200px) {
    .stats-grid {
        grid-template-columns: repeat(2, 1fr);
    }
}

/* 两列布局 */
.two-columns {
    display: grid;
    grid-template-columns: 1fr 400px;
    gap: 24px;
}

/* 三列布局 */
.three-columns {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 24px;
}
```

### 8.3 响应式断点
```css
/* 超大屏 */
@media (min-width: 1400px) {
    .container { max-width: 1320px; }
}

/* 大屏 */
@media (min-width: 1200px) {
    .container { max-width: 1140px; }
}

/* 中屏 */
@media (min-width: 992px) {
    .container { max-width: 960px; }
}

/* 小屏 */
@media (min-width: 768px) {
    .container { max-width: 720px; }
}
```

---

## 9. 交互规范

### 9.1 过渡动画
```css
/* 基础过渡 */
transition: all 0.3s;

/* 快速过渡 */
transition: all 0.15s;

/* 慢速过渡 */
transition: all 0.5s;

/* 指定属性过渡 */
transition: background-color 0.3s, border-color 0.3s;
```

### 9.2 悬停效果
```css
/* 卡片悬停 */
.card:hover {
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
    transform: translateY(-2px);
}

/* 按钮悬停 */
.btn:hover {
    transform: translateY(-1px);
    box-shadow: 0 4px 12px rgba(24, 144, 255, 0.3);
}

/* 菜单项悬停 */
.menu-item:hover {
    background: rgba(255, 255, 255, 0.1);
}
```

### 9.3 点击效果
```css
.btn:active {
    transform: scale(0.98);
}

.icon-btn:active {
    transform: scale(0.95);
}
```

### 9.4 加载状态
```css
/* 按钮加载 */
.btn.loading {
    position: relative;
    color: transparent !important;
}

.btn.loading::after {
    content: '';
    position: absolute;
    width: 16px;
    height: 16px;
    border: 2px solid #ffffff;
    border-top-color: transparent;
    border-radius: 50%;
    animation: spin 0.8s linear infinite;
}

@keyframes spin {
    to { transform: rotate(360deg); }
}

/* 骨架屏 */
.skeleton {
    background: linear-gradient(
        90deg,
        #f0f0f0 25%,
        #e0e0e0 50%,
        #f0f0f0 75%
    );
    background-size: 200% 100%;
    animation: skeleton-loading 1.5s infinite;
}

@keyframes skeleton-loading {
    0% { background-position: 200% 0; }
    100% { background-position: -200% 0; }
}
```

### 9.5 空状态
```css
.empty-state {
    padding: 60px 20px;
    text-align: center;
    color: #909399;
}

.empty-state i {
    font-size: 48px;
    color: #dcdfe6;
    margin-bottom: 16px;
}

.empty-state h4 {
    font-size: 16px;
    color: #606266;
    margin-bottom: 8px;
}

.empty-state p {
    font-size: 14px;
    margin-bottom: 20px;
}
```

### 9.6 错误状态
```css
.error-state {
    padding: 40px;
    text-align: center;
    background: #fff1f0;
    border-radius: 8px;
}

.error-state i {
    font-size: 48px;
    color: #ff4d4f;
    margin-bottom: 16px;
}

/* 表单错误提示 */
.error-message {
    color: #ff4d4f;
    font-size: 12px;
    margin-top: 4px;
}
```

---

## 10. 导航规范

### 10.1 侧边栏
```css
.sidebar {
    background: #001529;  /* 深蓝黑色 */
}

.sidebar-header {
    height: 60px;
    display: flex;
    align-items: center;
    padding: 0 20px;
    border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.menu-section {
    padding: 10px 20px 5px;
    color: rgba(255, 255, 255, 0.4);
    font-size: 12px;
    text-transform: uppercase;
    letter-spacing: 1px;
}

.menu-item {
    padding: 12px 20px;
    color: rgba(255, 255, 255, 0.7);
    cursor: pointer;
    display: flex;
    align-items: center;
    gap: 10px;
    transition: all 0.3s;
}

.menu-item i {
    width: 24px;
    font-size: 16px;
}

.menu-item:hover {
    color: #ffffff;
    background: rgba(255, 255, 255, 0.1);
}

.menu-item.active {
    color: #ffffff;
    background: #1890ff;
}
```

### 10.2 面包屑
```css
.breadcrumb {
    display: flex;
    align-items: center;
    gap: 8px;
    color: #909399;
    font-size: 14px;
}

.breadcrumb span {
    margin: 0 8px;
}

.breadcrumb a {
    color: #909399;
    text-decoration: none;
}

.breadcrumb a:hover {
    color: #1890ff;
}
```

---

## 11. 医学影像专用规范

### 11.1 DICOM查看器
```css
/* 查看器容器 */
.dicom-viewer {
    display: flex;
    height: calc(100vh - 60px - 48px);
    background: #1a1a1a;  /* 深色背景 */
    border-radius: 12px;
    overflow: hidden;
}

/* 工具栏 */
.dicom-toolbar {
    height: 56px;
    background: #252525;
    display: flex;
    align-items: center;
    padding: 0 16px;
    gap: 8px;
}

.dicom-tool {
    width: 40px;
    height: 40px;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 8px;
    cursor: pointer;
    color: rgba(255, 255, 255, 0.7);
    transition: all 0.3s;
    background: transparent;
    border: none;
}

.dicom-tool:hover {
    background: rgba(255, 255, 255, 0.1);
    color: #ffffff;
}

.dicom-tool.active {
    background: #1890ff;
    color: #ffffff;
}

/* 序列侧边栏 */
.dicom-sidebar {
    width: 280px;
    background: #252525;
    border-left: 1px solid #3a3a3a;
}

.series-item {
    background: #1a1a1a;
    border-radius: 8px;
    padding: 12px;
    margin: 12px;
    cursor: pointer;
    border: 2px solid transparent;
    transition: all 0.3s;
}

.series-item:hover {
    border-color: #1890ff;
}

.series-item.active {
    border-color: #1890ff;
    background: #2a2a2a;
}

/* 图像信息栏 */
.viewer-footer {
    background: #252525;
    padding: 12px 20px;
    display: flex;
    gap: 24px;
    color: rgba(255, 255, 255, 0.8);
    font-size: 13px;
}
```

### 11.2 报告编辑器
```css
.report-editor {
    background: #ffffff;
    border-radius: 12px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
}

.report-section {
    margin-bottom: 24px;
}

.report-section-title {
    font-size: 14px;
    font-weight: 600;
    color: #303133;
    margin-bottom: 12px;
    display: flex;
    align-items: center;
    gap: 8px;
}

.report-section-title i {
    color: #1890ff;
}

.report-textarea {
    width: 100%;
    min-height: 120px;
    padding: 16px;
    border: 1px solid #dcdfe6;
    border-radius: 8px;
    font-size: 14px;
    line-height: 1.8;
    resize: vertical;
    outline: none;
}

.report-textarea:focus {
    border-color: #1890ff;
    box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.1);
}
```

---

## 12. 无障碍规范

### 12.1 颜色对比度
```css
/* 主要文字 - 对比度 ≥ 4.5:1 */
.text-primary { color: #303133; }  /* on white */

/* 次要文字 - 对比度 ≥ 3:1 */
.text-secondary { color: #909399; }  /* on white */

/* 大号文字 - 对比度 ≥ 3:1 */
.large-text { color: #606266; font-size: 18px; }
```

### 12.2 焦点状态
```css
/* 键盘焦点指示器 */
:focus-visible {
    outline: 2px solid #1890ff;
    outline-offset: 2px;
}

/* 移除默认焦点环 */
:focus:not(:focus-visible) {
    outline: none;
}
```

### 12.3 屏幕阅读器
```css
/* 隐藏但保持可访问 */
.sr-only {
    position: absolute;
    width: 1px;
    height: 1px;
    padding: 0;
    margin: -1px;
    overflow: hidden;
    clip: rect(0, 0, 0, 0);
    white-space: nowrap;
    border: 0;
}
```

---

## 13. 命名规范

### 13.1 CSS类名
```css
/* BEM命名法 */
.block {}
.block__element {}
.block--modifier {}

/* 示例 */
.card {}
.card__header {}
.card__body {}
.card--primary {}
.card--danger {}

/* 组件前缀 */
.btn-primary {}
.btn-default {}
.icon-btn {}
.data-table {}
.form-item {}
```

### 13.2 颜色变量
```css
/* 语义化命名 */
--color-primary: #1890ff;
--color-success: #52c41a;
--color-warning: #faad14;
--color-danger: #ff4d4f;
--color-info: #909399;

/* 功能化命名 */
--color-text-primary: #303133;
--color-text-secondary: #606266;
--color-text-placeholder: #909399;
--color-border: #dcdfe6;
--color-bg-page: #f5f7fa;
--color-bg-hover: #fafafa;
```

### 13.3 字体变量
```css
--font-size-xs: 12px;
--font-size-sm: 13px;
--font-size-base: 14px;
--font-size-lg: 16px;
--font-size-xl: 18px;
--font-size-xxl: 24px;

--font-weight-normal: 400;
--font-weight-medium: 500;
--font-weight-semibold: 600;
--font-weight-bold: 700;
```

---

## 14. 性能规范

### 14.1 图片优化
```css
/* 图片适应容器 */
img {
    max-width: 100%;
    height: auto;
}

/* 头像/图标使用CSS绘制 */
.avatar-icon {
    background: linear-gradient(...);
}
```

### 14.2 防抖节流
```css
/* 搜索输入防抖 */
.input-debounce {
    transition: border-color 0.3s;
}

/* 按钮防抖 */
.btn-debounce {
    pointer-events: none;
    opacity: 0.7;
}
```

---

## 附录

### 图标使用示例
```html
<!-- 菜单图标 -->
<i class="fas fa-home"></i>
<i class="fas fa-users"></i>
<i class="fas fa-image"></i>
<i class="fas fa-eye"></i>
<i class="fas fa-file-medical"></i>
<i class="fas fa-share-alt"></i>

<!-- 操作图标 -->
<i class="fas fa-plus"></i>
<i class="fas fa-edit"></i>
<i class="fas fa-trash"></i>
<i class="fas fa-search"></i>
<i class="fas fa-download"></i>
<i class="fas fa-upload"></i>

<!-- 状态图标 -->
<i class="fas fa-check-circle"></i>
<i class="fas fa-exclamation-circle"></i>
<i class="fas fa-times-circle"></i>
<i class="fas fa-info-circle"></i>
```

---

## 附录

### 图标使用示例
```html
<!-- 菜单图标 -->
<i class="fas fa-home"></i>
<i class="fas fa-users"></i>
<i class="fas fa-image"></i>
<i class="fas fa-eye"></i>
<i class="fas fa-file-medical"></i>
<i class="fas fa-share-alt"></i>

<!-- 操作图标 -->
<i class="fas fa-plus"></i>
<i class="fas fa-edit"></i>
<i class="fas fa-trash"></i>
<i class="fas fa-search"></i>
<i class="fas fa-download"></i>
<i class="fas fa-upload"></i>

<!-- 状态图标 -->
<i class="fas fa-check-circle"></i>
<i class="fas fa-exclamation-circle"></i>
<i class="fas fa-times-circle"></i>
<i class="fas fa-info-circle"></i>
```

---

## 15. 动画规范

### 15.1 页面过渡
```css
/* 淡入效果 */
.fade-enter {
    opacity: 0;
}
.fade-enter-active {
    opacity: 1;
    transition: opacity 0.3s;
}

/* 滑入效果 */
.slide-enter {
    transform: translateX(100%);
}
.slide-enter-active {
    transform: translateX(0);
    transition: transform 0.3s ease-out;
}
```

### 15.2 微交互动画
```css
/* 按钮点击反馈 */
.btn-click:active {
    transform: scale(0.95);
}

/* 卡片悬停上浮 */
.card-hover:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

/* 图标旋转 */
.icon-spin:hover i {
    transform: rotate(180deg);
    transition: transform 0.5s ease;
}

/* 数字滚动动画 */
@keyframes countUp {
    from { opacity: 0; transform: translateY(10px); }
    to { opacity: 1; transform: translateY(0); }
}
```

### 15.3 加载动画
```css
/* 脉冲动画 */
@keyframes pulse {
    0%, 100% { opacity: 1; }
    50% { opacity: 0.5; }
}

/* 圆圈旋转 */
.spinner {
    width: 24px;
    height: 24px;
    border: 3px solid var(--border-color);
    border-top-color: var(--primary-color);
    border-radius: 50%;
    animation: spin 0.8s linear infinite;
}

/* 骨架屏脉冲 */
.skeleton {
    background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
    background-size: 200% 100%;
    animation: skeleton-loading 1.5s infinite;
}
```

---

## 16. 表单规范

### 16.1 表单布局
```css
/* 栅格表单 */
.form-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 20px;
}

.form-grid .full-width {
    grid-column: 1 / -1;
}

/* 内联表单 */
.form-inline {
    display: flex;
    align-items: center;
    gap: 16px;
}

.form-inline .form-label {
    min-width: 80px;
    margin-bottom: 0;
}
```

### 16.2 表单验证状态
```css
/* 成功状态 */
.input-success {
    border-color: var(--success-color);
}

.input-success:focus {
    box-shadow: 0 0 0 2px rgba(82, 196, 26, 0.1);
}

/* 警告状态 */
.input-warning {
    border-color: var(--warning-color);
}

.input-warning:focus {
    box-shadow: 0 0 0 2px rgba(250, 173, 20, 0.1);
}

/* 错误状态 */
.input-error {
    border-color: var(--danger-color);
}

.input-error:focus {
    box-shadow: 0 0 0 2px rgba(255, 77, 79, 0.1);
}

/* 验证提示 */
.error-tip {
    color: var(--danger-color);
    font-size: 12px;
    margin-top: 4px;
}
```

### 16.3 必填标识
```css
.required::before {
    content: '*';
    color: var(--danger-color);
    margin-right: 4px;
}
```

---

## 17. 通知与反馈

### 17.1 消息提示
```css
/* 成功提示 */
.message-success {
    background: var(--success-bg);
    border: 1px solid var(--success-border);
    color: var(--success-color);
    padding: 12px 16px;
    border-radius: 8px;
    display: flex;
    align-items: center;
    gap: 8px;
}

/* 警告提示 */
.message-warning {
    background: var(--warning-bg);
    border: 1px solid var(--warning-border);
    color: var(--warning-color);
    padding: 12px 16px;
    border-radius: 8px;
}

/* 错误提示 */
.message-error {
    background: var(--danger-bg);
    border: 1px solid var(--danger-border);
    color: var(--danger-color);
    padding: 12px 16px;
    border-radius: 8px;
}
```

### 17.2 确认对话框
```css
.confirm-dialog {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(0, 0, 0, 0.5);
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 1000;
}

.confirm-content {
    background: white;
    border-radius: 12px;
    padding: 24px;
    min-width: 400px;
    max-width: 500px;
}

.confirm-icon {
    width: 48px;
    height: 48px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 16px;
}

.confirm-icon.warning {
    background: var(--warning-bg);
    color: var(--warning-color);
}

.confirm-icon.danger {
    background: var(--danger-bg);
    color: var(--danger-color);
}
```

---

## 18. 响应式设计

### 18.1 断点定义
```css
/* 移动端优先 */
/* Extra Small (<576px) */
/* Small (≥576px) */
/* Medium (≥768px) */
/* Large (≥992px) */
/* Extra Large (≥1200px) */
/* XXL (≥1400px) */
```

### 18.2 栅格响应式
```css
.stats-grid {
    grid-template-columns: repeat(1, 1fr);  /* 移动端 */
}

@media (min-width: 768px) {
    .stats-grid {
        grid-template-columns: repeat(2, 1fr);  /* 平板 */
    }
}

@media (min-width: 1200px) {
    .stats-grid {
        grid-template-columns: repeat(4, 1fr);  /* 桌面 */
    }
}
```

### 18.3 侧边栏响应式
```css
/* 桌面端 */
.sidebar {
    width: 220px;
}

/* 平板端 */
@media (max-width: 992px) {
    .sidebar {
        width: 180px;
    }
}

/* 移动端 */
@media (max-width: 768px) {
    .sidebar {
        transform: translateX(-100%);
        transition: transform 0.3s;
    }

    .sidebar.open {
        transform: translateX(0);
    }
}
```

---

## 19. 浏览器兼容

### 19.1 CSS 前缀
```css
/* 自动添加前缀以兼容旧版浏览器 */
.gradient {
    background: -webkit-linear-gradient(left, #667eea, #764ba2);
    background: -moz-linear-gradient(left, #667eea, #764ba2);
    background: -ms-linear-gradient(left, #667eea, #764ba2);
    background: -o-linear-gradient(left, #667eea, #764ba2);
    background: linear-gradient(left, #667eea, #764ba2);
}

/* Flexbox 兼容 */
.flexbox {
    display: -webkit-box;
    display: -ms-flexbox;
    display: flex;
}
```

---

*文档版本: V1.1*
*最后更新: 2026-03-28*
