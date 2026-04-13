package com.example.xiaoyuan.module.ai.service;

import com.example.xiaoyuan.module.ai.dto.AiMessageVO;
import com.example.xiaoyuan.module.ai.dto.AiSendReq;

import java.util.List;

public interface AiService {

    List<AiMessageVO> send(Long userId, AiSendReq req);

    List<AiMessageVO> history(Long userId);
}
