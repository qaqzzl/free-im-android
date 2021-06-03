package net.qiujuer.italker.factory.model.api.account;

public class AppVersionModel {
    private int version_code;
    private String version_description;
    private String version_download;
    private String version_name;
    private int is_must;
    private String version_download_page;

    public String getVersion_download_page() {
        return version_download_page;
    }

    public void setVersion_download_page(String version_download_page) {
        this.version_download_page = version_download_page;
    }

    public int getIs_must() {
        return is_must;
    }

    public void setIs_must(int is_must) {
        this.is_must = is_must;
    }

    public int getVersion_code() {
        return version_code;
    }

    public void setVersion_code(int version_code) {
        this.version_code = version_code;
    }

    public String getVersion_description() {
        return version_description;
    }

    public void setVersion_description(String version_description) {
        this.version_description = version_description;
    }

    public String getVersion_download() {
        return version_download;
    }

    public void setVersion_download(String version_download) {
        this.version_download = version_download;
    }

    public String getVersion_name() {
        return version_name;
    }

    public void setVersion_name(String version_name) {
        this.version_name = version_name;
    }
}
