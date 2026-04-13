package com.example.xiaoyuan.common;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 通用的“名称+状态”字典类业务基类，封装分页、列表与增删改逻辑。
 *
 * @param <M> Mapper 类型
 * @param <T> 实体类型
 * @param <R> 请求体类型（包含 name/status）
 */
public abstract class BaseNameStatusServiceImpl<M extends BaseMapper<T>, T, R extends NameStatusReq> extends ServiceImpl<M, T> {
    /**
     * 创建一个新的实体实例。
     */
    protected abstract T createEntity();

    /**
     * 设置实体主键 ID。
     */
    protected abstract void setId(T entity, Long id);

    /**
     * 设置实体名称字段。
     */
    protected abstract void setName(T entity, String name);

    /**
     * 设置实体状态字段。
     */
    protected abstract void setStatus(T entity, Integer status);

    /**
     * 获取实体中文描述，用于异常提示。
     */
    protected abstract String getEntityLabel();

    /**
     * 获取启用状态列表（status=1），按 ID 升序。
     */
    public List<T> listEnabled() {
        return this.list(new QueryWrapper<T>()
                .eq("status", 1)
                .orderByAsc("id"));
    }

    /**
     * 管理端分页查询，支持名称关键字与状态筛选。
     */
    public PageResp<T> adminPage(int pageNo, int pageSize, String keyword, Integer status) {
        QueryWrapper<T> qw = new QueryWrapper<T>().orderByAsc("id");
        if (StringUtils.hasText(keyword)) {
            qw.like("name", keyword);
        }
        if (status != null) {
            qw.eq("status", status);
        }
        Page<T> page = this.page(Page.of(pageNo, pageSize), qw);
        return PageResp.of(page.getTotal(), page.getRecords());
    }

    /**
     * 管理端获取全部列表，支持按状态过滤。
     */
    public List<T> adminListAll(Integer status) {
        QueryWrapper<T> qw = new QueryWrapper<T>().orderByAsc("id");
        if (status != null) {
            qw.eq("status", status);
        }
        return this.list(qw);
    }

    /**
     * 管理端创建字典项。
     */
    public T adminCreate(R req) {
        if (req == null || !StringUtils.hasText(req.getName())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, getEntityLabel() + "名称不能为空");
        }
        T entity = createEntity();
        setName(entity, req.getName().trim());
        setStatus(entity, req.getStatus() == null ? 1 : req.getStatus());
        this.save(entity);
        return entity;
    }

    /**
     * 管理端更新字典项名称或状态。
     */
    public void adminUpdate(Long id, R req) {
        T exist = this.getById(id);
        if (exist == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, getEntityLabel() + "不存在");
        }
        T toUpdate = createEntity();
        setId(toUpdate, id);
        if (req != null && StringUtils.hasText(req.getName())) {
            setName(toUpdate, req.getName().trim());
        }
        if (req != null && req.getStatus() != null) {
            setStatus(toUpdate, req.getStatus());
        }
        this.updateById(toUpdate);
    }

    /**
     * 管理端删除字典项。
     */
    public void adminDelete(Long id) {
        T exist = this.getById(id);
        if (exist == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, getEntityLabel() + "不存在");
        }
        this.removeById(id);
    }
}
