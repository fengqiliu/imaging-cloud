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

### 1.3 视觉风格
- 现代渐变风格，按钮、侧边栏、头像使用微妙渐变
- 毛玻璃效果增强层次感
- 多层级过渡动画提升交互体验

---

## 2. 色彩系统

### 2.1 主色调
```css
/* 品牌蓝 - 传达专业、科技、可信赖 */
--primary-color: #1890ff;
--primary-light: #40a9ff;
--primary-dark: #096dd9;
--primary-bg: #e6f7ff;
--primary-shadow: rgba(24, 144, 255, 0.22);

/* 渐变方案 - 用于按钮、侧边栏、头像 */
background: linear-gradient(135deg, #1890ff 0%, #096dd9 100%);
```

### 2.2 功能色
```css
/* 成功 - 用于正常、已完成、通过等 */
--success-color: #52c41a;
--success-bg: #f6ffed;

/* 警告 - 用于待处理、进行中、审核中等 */
--warning-color: #faad14;
--warning-bg: #fffbe6;

/* 危险 - 用于错误、删除、异常等 */
--danger-color: #ff4d4f;
--danger-bg: #fff1f0;

/* 信息 - 用于提示、说明等 */
--info-color: #1890ff;
--info-bg: #e6f7ff;
```

### 2.3 检查状态色
```css
--status-pending: #faad14;      /* 待检查 - 橙黄 */
--status-progress: #1890ff;    /* 检查中 - 蓝色 */
--status-completed: #52c41a;   /* 已完成 - 绿色 */
--status-reported: #722ed1;    /* 已出报告 - 紫色 */
```

### 2.4 影像模态色
```css
--modality-ct: #1890ff;        /* CT - 蓝色 */
--modality-mri: #52c41a;       /* MRI - 绿色 */
--modality-dr: #faad14;         /* DR/X光 - 橙黄 */
--modality-us: #ff4d4f;         /* 超声 - 红色 */
--modality-xa: #722ed1;         /* 血管造影 - 紫色 */
--modality-pet: #eb2f96;        /* PET - 粉红 */
```

### 2.5 中性色
```css
--text-primary: #303133;      /* 主要文字 */
--text-regular: #606266;      /* 正文文字 */
--text-secondary: #909399;     /* 次要文字 */
--border-color: #dcdfe6;       /* 边框色 */
--bg-color: #f5f7fa;           /* 页面背景 */
--card-bg: #ffffff;            /* 卡片背景 */
```

---

## 3. 字体系统

### 3.1 字体家族
```css
font-family: -apple-system, BlinkMacSystemFont,
             'Segoe UI', 'PingFang SC', 'Hiragino Sans GB',
             'Microsoft YaHei', 'Helvetica Neue', Arial, sans-serif;
```

### 3.2 字体层级
```css
/* 页面标题 - h1 */
font-size: 26px;
font-weight: 700;
letter-spacing: -0.5px;

/* 卡片标题 - h2 */
font-size: 16px;
font-weight: 600;

/* 正文 - p */
font-size: 14px;
line-height: 1.8;

/* 辅助文字 - small */
font-size: 13px;
color: var(--text-secondary);
```

### 3.3 数字显示
```css
.stat-value {
    font-size: 32px;
    font-weight: 700;
    letter-spacing: -1px;
    font-variant-numeric: tabular-nums;
}
```

---

## 4. 间距系统

### 4.1 基础间距单位
```css
--spacing-xs: 4px;
--spacing-sm: 8px;
--spacing-md: 12px;
--spacing-lg: 16px;
--spacing-xl: 24px;
--spacing-xxl: 28px;
```

### 4.2 组件间距规范
```css
.card { padding: 24px; }
.data-table td { padding: 16px; }
.form-item { margin-bottom: 20px; }
.btn + .btn { margin-left: 12px; }
```

---

## 5. 圆角系统

### 5.1 圆角规范
```css
--radius-sm: 4px;     /* 标签、徽章、状态 */
--radius-md: 8px;     /* 按钮、输入框、下拉框 */
--radius-lg: 12px;    /* 卡片、模态框 */
```

---

## 6. 阴影系统

### 6.1 阴影层级
```css
--shadow-sm: 0 2px 8px rgba(0, 0, 0, 0.06);    /* 卡片默认 */
--shadow-md: 0 4px 12px rgba(0, 0, 0, 0.08);   /* 卡片悬浮、下拉 */
--shadow-lg: 0 8px 24px rgba(0, 0, 0, 0.12);   /* 模态框 */
--shadow-xl: 0 20px 60px rgba(0, 0, 0, 0.15);  /* 弹出层 */
```

---

## 7. 动画系统

### 7.1 过渡动画
```css
--transition-fast: 0.2s ease;                           /* 快速交互 */
--transition-base: 0.3s cubic-bezier(0.4, 0, 0.2, 1);  /* 默认过渡 */
--transition-slow: 0.45s cubic-bezier(0.2, 0.8, 0.2, 1); /* 页面元素 */
```

### 7.2 关键动画
```css
/* 淡入上浮 - 页面元素 */
@keyframes fadeInUp {
    from { opacity: 0; transform: translateY(18px); }
    to { opacity: 1; transform: translateY(0); }
}

/* 缩放淡入 - 模态框 */
@keyframes fadeInScale {
    from { opacity: 0; transform: scale(0.96); }
    to { opacity: 1; transform: scale(1); }
}

/* 脉冲光晕 - 徽章提示 */
@keyframes pulseGlow {
    0%, 100% { box-shadow: 0 0 0 0 rgba(24, 144, 255, 0.18); }
    50% { box-shadow: 0 0 0 10px rgba(24, 144, 255, 0); }
}
```

### 7.3 元素入场动画
```css
.content > * {
    animation: fadeInUp var(--transition-slow) both;
}

.content > *:nth-child(1) { animation-delay: 0.05s; }
.content > *:nth-child(2) { animation-delay: 0.1s; }
.content > *:nth-child(3) { animation-delay: 0.15s; }
```

---

## 8. 布局规范

### 8.1 页面布局
```css
.main-layout { display: flex; min-height: 100vh; }

.sidebar {
    width: 260px;
    background: linear-gradient(180deg, #001529 0%, #0b1f36 100%);
    position: fixed;
    height: 100vh;
    box-shadow: 4px 0 20px rgba(0,0,0,0.08);
}

.main-content {
    flex: 1;
    margin-left: 260px;
    min-height: 100vh;
}

.header {
    height: 64px;
    background: rgba(255, 255, 255, 0.85);
    backdrop-filter: blur(12px);
    position: sticky;
    top: 0;
    z-index: 50;
}

.content { padding: 28px; }
```

### 8.2 栅格系统
```css
.stats-grid {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 20px;
}

.search-form {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 20px;
}
```

---

## 9. 组件规范

### 9.1 按钮

#### 渐变按钮风格
```css
.btn {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    padding: 10px 20px;
    border-radius: 8px;
    font-size: 14px;
    font-weight: 600;
    gap: 8px;
    border: none;
    cursor: pointer;
    transition: all var(--transition-base);
    box-shadow: 0 1px 2px rgba(0,0,0,0.05);
}

.btn:active { transform: scale(0.98); }
```

#### 按钮类型
```css
/* 主按钮 - 渐变蓝 */
.btn-primary {
    background: linear-gradient(135deg, var(--primary-color), var(--primary-dark));
    color: white;
    box-shadow: 0 8px 20px var(--primary-shadow);
}
.btn-primary:hover {
    background: linear-gradient(135deg, var(--primary-light), var(--primary-color));
    box-shadow: 0 12px 24px rgba(24, 144, 255, 0.28);
    transform: translateY(-2px);
}

/* 成功按钮 - 渐变绿 */
.btn-success {
    background: linear-gradient(135deg, var(--success-color), #059669);
    color: white;
    box-shadow: 0 4px 12px rgba(16, 185, 129, 0.3);
}

/* 危险按钮 - 渐变红 */
.btn-danger {
    background: linear-gradient(135deg, var(--danger-color), #dc2626);
    color: white;
    box-shadow: 0 4px 12px rgba(239, 68, 68, 0.3);
}

/* 警告按钮 - 渐变橙 */
.btn-warning {
    background: linear-gradient(135deg, var(--warning-color), #d97706);
    color: white;
    box-shadow: 0 4px 12px rgba(245, 158, 11, 0.3);
}

/* 默认按钮 - 白底 */
.btn-default {
    background: white;
    color: var(--text-regular);
    border: 1px solid var(--border-color);
}
.btn-default:hover {
    border-color: var(--primary-color);
    color: var(--primary-color);
    background: var(--primary-bg);
}
```

#### 按钮尺寸
```css
.btn-sm { padding: 7px 14px; font-size: 13px; }
.btn-xs { padding: 5px 10px; font-size: 12px; }
```

#### 图标按钮
```css
.icon-btn {
    width: 36px;
    height: 36px;
    border-radius: 8px;
    background: transparent;
    border: none;
    color: var(--text-secondary);
    cursor: pointer;
    transition: all var(--transition-base);
}

.icon-btn:hover {
    background: var(--primary-bg);
    color: var(--primary-color);
    transform: translateY(-2px);
}

.icon-btn.danger:hover {
    background: var(--danger-bg);
    color: var(--danger-color);
}
```

### 9.2 输入框
```css
.input {
    padding: 10px 14px;
    border: 1px solid var(--border-color);
    border-radius: 8px;
    font-size: 14px;
    outline: none;
    transition: all var(--transition-base);
    width: 100%;
    background: white;
}

.input:focus {
    border-color: var(--primary-color);
    box-shadow: 0 0 0 3px rgba(24, 144, 255, 0.14);
    transform: translateY(-1px);
}

.select {
    padding: 10px 14px;
    border: 1px solid var(--border-color);
    border-radius: 8px;
    font-size: 14px;
    background: white;
    cursor: pointer;
    min-width: 120px;
}

.select:focus, .select:hover {
    border-color: var(--primary-color);
    box-shadow: 0 0 0 3px rgba(24, 144, 255, 0.12);
}

.textarea {
    padding: 12px 14px;
    border: 1px solid var(--border-color);
    border-radius: 8px;
    font-size: 14px;
    resize: vertical;
}
```

### 9.3 卡片
```css
.card {
    background: var(--card-bg);
    border-radius: 12px;
    box-shadow: var(--shadow-sm);
    padding: 24px;
    margin-bottom: 24px;
    border: 1px solid rgba(0,0,0,0.02);
    transition: all var(--transition-base);
}

.card:hover {
    box-shadow: var(--shadow-md);
    transform: translateY(-4px);
}

.card-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding-bottom: 16px;
    margin-bottom: 20px;
    border-bottom: 1px solid var(--border-color);
}

.card-title {
    font-size: 16px;
    font-weight: 600;
    display: flex;
    align-items: center;
    gap: 10px;
}
```

### 9.4 统计卡片
```css
.stat-card {
    background: var(--card-bg);
    border-radius: 12px;
    padding: 24px;
    box-shadow: var(--shadow-sm);
    position: relative;
    overflow: hidden;
    transition: all var(--transition-base);
}

.stat-card:hover {
    transform: translateY(-6px);
    box-shadow: var(--shadow-lg);
}

/* 顶部渐变装饰条 */
.stat-card::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 3px;
}

.stat-card.blue::before {
    background: linear-gradient(90deg, var(--primary-color), var(--primary-light));
}
.stat-card.green::before {
    background: linear-gradient(90deg, var(--success-color), #34d399);
}
.stat-card.orange::before {
    background: linear-gradient(90deg, var(--warning-color), #fbbf24);
}
.stat-card.red::before {
    background: linear-gradient(90deg, var(--danger-color), #f87171);
}
.stat-card.purple::before {
    background: linear-gradient(90deg, #8b5cf6, #a78bfa);
}

.stat-icon {
    width: 52px;
    height: 52px;
    border-radius: 8px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 22px;
    margin-bottom: 18px;
}

.stat-value {
    font-size: 32px;
    font-weight: 700;
    margin-bottom: 4px;
    letter-spacing: -1px;
}
```

### 9.5 表格
```css
.data-table {
    width: 100%;
    border-collapse: collapse;
    border-radius: 8px;
    overflow: hidden;
}

.data-table th {
    background: var(--bg-color);
    padding: 14px 16px;
    text-align: left;
    font-weight: 600;
    font-size: 13px;
    text-transform: uppercase;
    letter-spacing: 0.5px;
    color: var(--text-secondary);
    border-bottom: 1px solid var(--border-color);
}

.data-table td {
    padding: 16px;
    border-bottom: 1px solid var(--border-color);
    font-size: 14px;
    color: var(--text-regular);
}

.data-table tbody tr:hover {
    background: var(--primary-bg);
}
```

### 9.6 状态标签
```css
.tag, .badge, .status-tag {
    display: inline-flex;
    align-items: center;
    padding: 5px 12px;
    border-radius: 20px;
    font-size: 12px;
    font-weight: 600;
}

.tag.pending, .status-tag.pending {
    background: var(--warning-bg);
    color: var(--warning-color);
}

.tag.in-progress, .status-tag.in-progress {
    background: var(--primary-bg);
    color: var(--primary-color);
}

.tag.completed, .status-tag.completed {
    background: var(--success-bg);
    color: var(--success-color);
}

.tag.reported, .status-tag.reported {
    background: #f3e8ff;
    color: #8b5cf6;
}
```

### 9.7 分页
```css
.table-pagination {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 16px 0 0;
    border-top: 1px solid var(--border-color);
    margin-top: 16px;
}

.pagination-controls {
    display: flex;
    align-items: center;
    gap: 16px;
}

.pagination-buttons {
    display: flex;
    align-items: center;
    gap: 4px;
}

.page-btn {
    min-width: 34px;
    height: 34px;
    padding: 0 8px;
    border: 1px solid var(--border-color);
    border-radius: 6px;
    background: #fff;
    font-size: 14px;
    cursor: pointer;
    transition: all 0.2s;
}

.page-btn:hover:not(:disabled):not(.active) {
    border-color: var(--primary-color);
    color: var(--primary-color);
}

.page-btn.active {
    background: var(--primary-color);
    border-color: var(--primary-color);
    color: #fff;
}

.page-btn:disabled {
    opacity: 0.5;
    cursor: not-allowed;
}
```

---

## 10. 导航规范

### 10.1 侧边栏
```css
.sidebar {
    background: linear-gradient(180deg, #001529 0%, #0b1f36 100%);
}

.sidebar-header {
    height: 64px;
    display: flex;
    align-items: center;
    padding: 0 20px;
    border-bottom: 1px solid rgba(255, 255, 255, 0.06);
}

.sidebar-logo {
    width: 40px;
    height: 40px;
    background: linear-gradient(135deg, var(--primary-color), var(--primary-dark));
    border-radius: 8px;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-right: 12px;
    box-shadow: 0 8px 20px rgba(24, 144, 255, 0.24);
}

.menu-section {
    padding: 16px 20px 8px;
    color: rgba(255, 255, 255, 0.35);
    font-size: 11px;
    text-transform: uppercase;
    letter-spacing: 1.2px;
}

.menu-item {
    padding: 11px 20px;
    color: rgba(255, 255, 255, 0.6);
    display: flex;
    align-items: center;
    margin: 2px 10px;
    border-radius: 8px;
    transition: all var(--transition-base);
}

.menu-item:hover {
    color: rgba(255, 255, 255, 0.95);
    background: rgba(255, 255, 255, 0.08);
    transform: translateX(4px);
}

.menu-item.active {
    color: white;
    background: linear-gradient(135deg, var(--primary-color), var(--primary-dark));
    box-shadow: 0 8px 20px var(--primary-shadow);
}
```

### 10.2 头部
```css
.header {
    background: rgba(255, 255, 255, 0.85);
    backdrop-filter: blur(12px);
    padding: 0 28px;
}

.user-avatar {
    width: 38px;
    height: 38px;
    border-radius: 50%;
    background: linear-gradient(135deg, var(--primary-color), var(--primary-dark));
    box-shadow: 0 8px 20px rgba(24, 144, 255, 0.24);
}
```

### 10.3 面包屑
```css
.breadcrumb {
    display: flex;
    align-items: center;
    color: var(--text-secondary);
    font-size: 14px;
}
```

---

## 11. 反馈组件

### 11.1 模态框
```css
.modal-overlay {
    position: fixed;
    top: 0; left: 0; right: 0; bottom: 0;
    background: rgba(15, 23, 42, 0.6);
    backdrop-filter: blur(4px);
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 1000;
}

.modal {
    background: white;
    border-radius: 12px;
    padding: 28px;
    min-width: 480px;
    max-width: 90vw;
    box-shadow: var(--shadow-xl);
    animation: slideUp var(--transition-base);
}

@keyframes slideUp {
    from { opacity: 0; transform: translateY(20px) scale(0.98); }
    to { opacity: 1; transform: translateY(0) scale(1); }
}
```

### 11.2 Toast 提示
```css
.toast {
    position: fixed;
    top: 20px;
    right: 20px;
    padding: 12px 20px;
    border-radius: 8px;
    font-size: 14px;
    z-index: 2000;
    display: flex;
    align-items: center;
    gap: 8px;
    box-shadow: var(--shadow-lg);
    animation: fadeInUp var(--transition-fast);
}

.toast.success {
    background: var(--success-bg);
    border: 1px solid #b7eb8f;
    color: var(--success-color);
}

.toast.error {
    background: var(--danger-bg);
    border: 1px solid #ffccc7;
    color: var(--danger-color);
}

.toast.info {
    background: var(--info-bg);
    border: 1px solid #91d5ff;
    color: var(--info-color);
}
```

### 11.3 空状态
```css
.empty-state {
    text-align: center;
    padding: 64px 24px;
}

.empty-state i {
    font-size: 56px;
    margin-bottom: 20px;
    color: var(--border-color);
}

.empty-state-title {
    font-size: 16px;
    font-weight: 600;
    margin-bottom: 8px;
}
```

### 11.4 加载骨架
```css
.loading-skeleton {
    background: linear-gradient(90deg, var(--bg-color) 25%, #ebeef2 50%, var(--bg-color) 75%);
    background-size: 200% 100%;
    animation: skeleton-loading 1.5s infinite;
    border-radius: 4px;
}

@keyframes skeleton-loading {
    0% { background-position: 200% 0; }
    100% { background-position: -200% 0; }
}
```

---

## 12. 工具栏与筛选

### 12.1 表格工具栏
```css
.table-toolbar {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 20px;
}

.toolbar-left, .toolbar-right {
    display: flex;
    align-items: center;
    gap: 12px;
}
```

### 12.2 搜索框
```css
.search-box {
    background: var(--card-bg);
    border-radius: 12px;
    padding: 24px;
    margin-bottom: 24px;
    box-shadow: var(--shadow-sm);
}

.search-input { width: 280px; }
```

### 12.3 筛选标签
```css
.filter-tabs {
    display: flex;
    gap: 6px;
    margin-bottom: 24px;
    background: var(--bg-color);
    padding: 4px;
    border-radius: 8px;
    width: fit-content;
}

.filter-tab {
    padding: 10px 20px;
    border-radius: 4px;
    font-size: 13px;
    background: transparent;
    color: var(--text-secondary);
    cursor: pointer;
    transition: all var(--transition-base);
}

.filter-tab:hover { color: var(--primary-color); }

.filter-tab.active {
    background: white;
    color: var(--primary-color);
    box-shadow: var(--shadow-sm);
}
```

---

## 13. 医学影像专用规范

### 13.1 DICOM 查看器
```css
.dicom-viewer {
    display: flex;
    height: calc(100vh - 64px - 48px);
    background: #1a1a1a;
    border-radius: 12px;
    overflow: hidden;
}

.dicom-toolbar {
    height: 56px;
    background: #252525;
    display: flex;
    align-items: center;
    padding: 0 16px;
    gap: 16px;
}

.dicom-tool {
    width: 40px;
    height: 40px;
    border-radius: 8px;
    cursor: pointer;
    color: rgba(255, 255, 255, 0.7);
    transition: all var(--transition-base);
}

.dicom-tool:hover {
    background: rgba(255, 255, 255, 0.1);
    color: white;
    transform: translateY(-2px);
}

.dicom-tool.active {
    background: var(--primary-color);
    color: white;
}

.dicom-sidebar {
    width: 280px;
    background: #252525;
    border-left: 1px solid #3a3a3a;
}

.series-item {
    background: #1a1a1a;
    border-radius: 8px;
    padding: 12px;
    margin-bottom: 12px;
    cursor: pointer;
    border: 2px solid transparent;
    transition: all var(--transition-base);
}

.series-item:hover {
    border-color: var(--primary-color);
    transform: translateX(4px);
}

.series-item.active {
    border-color: var(--primary-color);
    background: #2a2a2a;
}
```

### 13.2 报告编辑器
```css
.report-editor {
    background: white;
    border-radius: 12px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
}

.report-section { margin-bottom: 24px; }

.report-section-title {
    font-size: 14px;
    font-weight: 600;
    margin-bottom: 12px;
    display: flex;
    align-items: center;
    gap: 8px;
}

.report-section-title i { color: var(--primary-color); }

.report-textarea {
    width: 100%;
    min-height: 120px;
    padding: 16px;
    border: 1px solid var(--border-color);
    border-radius: 8px;
    font-size: 14px;
    resize: vertical;
}

.report-textarea:focus {
    border-color: var(--primary-color);
    box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.1);
}
```

---

## 14. 列表与排名

### 14.1 排名列表
```css
.rank-list {
    display: flex;
    flex-direction: column;
    gap: 8px;
}

.rank-item {
    display: flex;
    align-items: center;
    gap: 14px;
    padding: 12px 14px;
    border-radius: 8px;
    transition: all var(--transition-base);
}

.rank-item:hover {
    background: var(--primary-bg);
    transform: translateX(4px);
}

.rank-num {
    width: 28px;
    height: 28px;
    border-radius: 50%;
    background: var(--bg-color);
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 12px;
    font-weight: 700;
}

.rank-item:nth-child(1) .rank-num {
    background: linear-gradient(135deg, #fef3c7, #fde68a);
    color: #92400e;
}
.rank-item:nth-child(2) .rank-num {
    background: linear-gradient(135deg, #e5e7eb, #d1d5db);
    color: #374151;
}
.rank-item:nth-child(3) .rank-num {
    background: linear-gradient(135deg, #fed7aa, #fdba74);
    color: #9a3412;
}
```

### 14.2 字典类型列表
```css
.dict-type-list {
    display: flex;
    flex-direction: column;
    gap: 4px;
}

.dict-type-item {
    padding: 12px 14px;
    border-radius: 8px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    transition: all var(--transition-base);
}

.dict-type-item:hover {
    background: var(--primary-bg);
    transform: translateX(4px);
}

.dict-type-item.active {
    background: linear-gradient(135deg, var(--primary-color), var(--primary-dark));
    color: white;
    box-shadow: 0 8px 20px var(--primary-shadow);
}
```

---

## 15. 表单规范

### 15.1 模态表单
```css
.modal-form {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 20px;
}

.modal-form .form-item.full-width {
    grid-column: 1 / -1;
}

.modal-form .input,
.modal-form .select,
.modal-form textarea {
    background: white;
}
```

### 15.2 权限组
```css
.permission-group {
    border: 1px solid var(--border-color);
    border-radius: 8px;
    overflow: hidden;
}

.permission-header {
    padding: 12px 16px;
    background: var(--bg-color);
    font-weight: 500;
}

.permission-list {
    padding: 12px 16px;
    border-top: 1px solid var(--border-color);
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 8px;
}
```

---

## 16. 无障碍规范

### 16.1 减少动画
```css
@media (prefers-reduced-motion: reduce) {
    *,
    *::before,
    *::after {
        animation: none !important;
        transition: none !important;
    }
}
```

### 16.2 焦点状态
```css
:focus-visible {
    outline: 2px solid var(--primary-color);
    outline-offset: 2px;
}
```

---

## 17. 命名规范

### 17.1 CSS 变量
```css
/* 语义化命名 */
--primary-color: #1890ff;
--success-color: #52c41a;
--warning-color: #faad14;
--danger-color: #ff4d4f;

--text-primary: #303133;
--text-regular: #606266;
--text-secondary: #909399;

--border-color: #dcdfe6;
--bg-color: #f5f7fa;

--shadow-sm/md/lg/xl
--transition-fast/base/slow
```

---

*文档版本: V2.0*
*最后更新: 2026-04-01*
