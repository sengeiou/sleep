package com.szip.sleepee.Bean.HttpBean;

public class UpdataBean extends BaseApi{

    private Data data;


    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data{
        String versionNumber;
        String url;
        String fileSize;
        boolean hasNewVersion;


        public String getVersionNumber() {
            return versionNumber;
        }

        public void setVersionNumber(String versionNumber) {
            this.versionNumber = versionNumber;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getFileSize() {
            return fileSize;
        }

        public void setFileSize(String fileSize) {
            this.fileSize = fileSize;
        }

        public boolean isHasNewVersion() {
            return hasNewVersion;
        }

        public void setHasNewVersion(boolean hasNewVersion) {
            this.hasNewVersion = hasNewVersion;
        }
    }
}
