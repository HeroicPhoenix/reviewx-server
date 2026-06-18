package com.lvwyh.reviewx.web.entity.auth;

/**
 * 系统接口权限实体。
 *
 * 映射 SYS_API 表，保存接口权限码、接口路径和请求方法。
 */
public class SysApi {

    /** 接口权限主键。 */
    private Long apiId;
    /** 权限编码，例如 question:view。 */
    private String apiCode;
    /** 权限名称。 */
    private String apiName;
    /** 接口路径。 */
    private String apiPath;
    /** HTTP 方法。 */
    private String httpMethod;
    /** 权限状态：1启用、2禁用。 */
    private Integer status;

    public Long getApiId() { return apiId; }
    public void setApiId(Long apiId) { this.apiId = apiId; }
    public String getApiCode() { return apiCode; }
    public void setApiCode(String apiCode) { this.apiCode = apiCode; }
    public String getApiName() { return apiName; }
    public void setApiName(String apiName) { this.apiName = apiName; }
    public String getApiPath() { return apiPath; }
    public void setApiPath(String apiPath) { this.apiPath = apiPath; }
    public String getHttpMethod() { return httpMethod; }
    public void setHttpMethod(String httpMethod) { this.httpMethod = httpMethod; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
}
