package com.dsite.medical.cloudfilm.controller;

import com.dsite.medical.cloudfilm.domain.entity.CfImageShare;
import com.dsite.medical.cloudfilm.service.ICfImageShareService;
import com.dsite.medical.common.constant.Constants;
import com.dsite.medical.common.core.domain.AjaxResult;
import com.dsite.medical.common.core.domain.PageQuery;
import com.dsite.medical.common.core.domain.PageResult;
import com.dsite.medical.common.exception.ServiceException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 分享管理Controller
 */
@Tag(name = "分享管理")
@RestController
@RequestMapping("/cloudfilm/share")
@RequiredArgsConstructor
public class CfImageShareController {

    private final ICfImageShareService shareService;

    @Operation(summary = "分享列表")
    @PreAuthorize("@ss.hasPermi('cloudfilm:share:list')")
    @GetMapping("/list")
    public PageResult<CfImageShare> list(CfImageShare cfImageShare, PageQuery pageQuery) {
        return shareService.selectSharePage(cfImageShare, pageQuery);
    }

    @Operation(summary = "分享详情")
    @PreAuthorize("@ss.hasPermi('cloudfilm:share:query')")
    @GetMapping("/{shareId}")
    public AjaxResult getInfo(@PathVariable String shareId) {
        return AjaxResult.success(shareService.getById(shareId));
    }

    @Operation(summary = "创建分享")
    @PreAuthorize("@ss.hasPermi('cloudfilm:share:add')")
    @PostMapping
    public AjaxResult create(@RequestParam String examId,
                            @RequestParam(defaultValue = "7") int expireDays,
                            @RequestParam(defaultValue = "false") boolean allowDownload,
                            @RequestParam String createUser) {
        CfImageShare share = shareService.createShare(examId, expireDays, allowDownload, createUser);
        Map<String, Object> result = new HashMap<>();
        result.put("shareId", share.getShareId());
        result.put("shareNo", share.getShareNo());
        result.put("accessCode", share.getAccessCode());
        result.put("expireDate", share.getExpireDate());
        result.put("allowDownload", share.getAllowDownload());
        return AjaxResult.success(result);
    }

    @Operation(summary = "取消分享")
    @PreAuthorize("@ss.hasPermi('cloudfilm:share:remove')")
    @DeleteMapping("/{shareId}")
    public AjaxResult cancel(@PathVariable String shareId) {
        return AjaxResult.success(shareService.cancelShare(shareId) > 0);
    }

    @Operation(summary = "获取分享信息（外部访问）")
    @GetMapping("/info/{shareNo}")
    public AjaxResult getShareInfo(@PathVariable String shareNo) {
        CfImageShare share = shareService.getByShareNo(shareNo);
        if (share == null) {
            return AjaxResult.error("分享不存在");
        }
        // 检查是否过期
        if (new Date().after(share.getExpireDate())) {
            return AjaxResult.error("分享链接已过期");
        }
        // 增加查看次数
        shareService.incrementViewCount(share.getShareId());
        // 返回公开信息（不含提取码）
        Map<String, Object> result = new HashMap<>();
        result.put("shareNo", share.getShareNo());
        result.put("examId", share.getExamId());
        result.put("viewCount", share.getViewCount() + 1);
        result.put("downloadCount", share.getDownloadCount());
        result.put("allowDownload", share.getAllowDownload());
        result.put("expireDate", share.getExpireDate());
        return AjaxResult.success(result);
    }

    @Operation(summary = "验证提取码（外部访问）")
    @PostMapping("/verify")
    public AjaxResult verifyCode(@RequestParam String shareNo, @RequestParam String accessCode) {
        try {
            boolean valid = shareService.verifyAccessCode(shareNo, accessCode);
            if (valid) {
                CfImageShare share = shareService.getByShareNo(shareNo);
                return AjaxResult.success(share);
            }
            return AjaxResult.error("提取码错误");
        } catch (ServiceException e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @Operation(summary = "检查分享是否过期")
    @GetMapping("/check/{shareNo}")
    public AjaxResult checkExpired(@PathVariable String shareNo) {
        boolean expired = shareService.isExpired(shareNo);
        return AjaxResult.success(expired);
    }
}
