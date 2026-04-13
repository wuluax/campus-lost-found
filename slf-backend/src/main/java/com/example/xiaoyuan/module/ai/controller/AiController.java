package com.example.xiaoyuan.module.ai.controller;

import com.example.xiaoyuan.common.ApiResp;
import com.example.xiaoyuan.common.UserContext;
import com.example.xiaoyuan.module.ai.dto.AiMessageVO;
import com.example.xiaoyuan.module.ai.dto.AiSendReq;
import com.example.xiaoyuan.module.ai.service.AiService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    private final AiService aiService;

    public AiController(AiService aiService) {
        this.aiService = aiService;
    }

    //发送AI对话消息接口：以当前登录用户身份提交消息并获取AI回复结果。
    @PostMapping("/send")
    public ApiResp<List<AiMessageVO>> send(@Valid @RequestBody AiSendReq req) {
        Long uid = UserContext.get();
        return ApiResp.ok(aiService.send(uid, req));
    }

    //获取AI对话历史接口：查询当前登录用户的历史对话记录列表。
    @GetMapping("/history")
    public ApiResp<List<AiMessageVO>> history() {
        Long uid = UserContext.get();
        return ApiResp.ok(aiService.history(uid));
    }
}
