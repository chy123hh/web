package org.example.common.dto;

import lombok.Data;

import java.util.List;

/**
 * 分页结果封装类
 * 用于封装分页查询的响应数据
 *
 * @param <T> 数据类型
 */
@Data
public class PageResult<T> {

    /**
     * 当前页数据列表
     */
    private List<T> records;

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 总页数
     */
    private Long pages;

    /**
     * 当前页码
     */
    private Long current;

    /**
     * 每页数量
     */
    private Long size;

    /**
     * 构造函数
     *
     * @param records 当前页数据列表
     * @param total   总记录数
     * @param pages   总页数
     * @param current 当前页码
     * @param size    每页数量
     */
    public PageResult(List<T> records, Long total, Long pages, Long current, Long size) {
        this.records = records;
        this.total = total;
        this.pages = pages;
        this.current = current;
        this.size = size;
    }

    /**
     * 无参构造函数
     */
    public PageResult() {
    }

    /**
     * 创建分页结果
     *
     * @param records 当前页数据列表
     * @param total   总记录数
     * @param pages   总页数
     * @param current 当前页码
     * @param size    每页数量
     * @param <T>     数据类型
     * @return PageResult 对象
     */
    public static <T> PageResult<T> of(List<T> records, Long total, Long pages, Long current, Long size) {
        return new PageResult<>(records, total, pages, current, size);
    }
}
