package com.carsale.utils;

import java.io.Serializable;

/**
 * 统一响应结果类
 * 用于标准化API返回格式
 */
public class Result<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 响应状态码：200表示成功，其他表示失败
     */
    private int code;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 分页信息
     */
    private PageInfo pageInfo;

    // getter and setter methods
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public PageInfo getPageInfo() {
        return pageInfo;
    }

    public void setPageInfo(PageInfo pageInfo) {
        this.pageInfo = pageInfo;
    }

    /**
     * 成功响应
     * @param data 响应数据
     * @param <T> 数据类型
     * @return 响应结果
     */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("操作成功");
        result.setData(data);
        return result;
    }

    /**
     * 成功响应（带分页）
     * @param data 响应数据
     * @param pageInfo 分页信息
     * @param <T> 数据类型
     * @return 响应结果
     */
    public static <T> Result<T> success(T data, PageInfo pageInfo) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("操作成功");
        result.setData(data);
        result.setPageInfo(pageInfo);
        return result;
    }

    /**
     * 失败响应
     * @param code 错误码
     * @param message 错误消息
     * @param <T> 数据类型
     * @return 响应结果
     */
    public static <T> Result<T> error(int code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

    /**
     * 默认失败响应
     * @param message 错误消息
     * @param <T> 数据类型
     * @return 响应结果
     */
    public static <T> Result<T> error(String message) {
        Result<T> result = new Result<>();
        result.setCode(500);
        result.setMessage(message);
        return result;
    }

    /**
     * 分页信息内部类
     */
    public static class PageInfo implements Serializable {
        private static final long serialVersionUID = 1L;

        /**
         * 当前页码
         */
        private long current;

        /**
         * 每页记录数
         */
        private long size;

        /**
         * 总记录数
         */
        private long total;

        /**
         * 总页数
         */
        private long pages;

        /**
         * 是否有前一页
         */
        private boolean hasPrevious;

        /**
         * 是否有后一页
         */
        private boolean hasNext;

        // getter and setter methods
        public long getCurrent() {
            return current;
        }

        public void setCurrent(long current) {
            this.current = current;
        }

        public long getSize() {
            return size;
        }

        public void setSize(long size) {
            this.size = size;
        }

        public long getTotal() {
            return total;
        }

        public void setTotal(long total) {
            this.total = total;
        }

        public long getPages() {
            return pages;
        }

        public void setPages(long pages) {
            this.pages = pages;
        }

        public boolean isHasPrevious() {
            return hasPrevious;
        }

        public void setHasPrevious(boolean hasPrevious) {
            this.hasPrevious = hasPrevious;
        }

        public boolean isHasNext() {
            return hasNext;
        }

        public void setHasNext(boolean hasNext) {
            this.hasNext = hasNext;
        }
    }
}
