package com.finder.genie_ai.model.session;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class SessionModel implements Serializable {

    private static final long serialVersionUID = 3L;
    private String ip;
    private LocalDateTime signinAt;
    private LocalDateTime lastUpdatedAt;

    public SessionModel(String ip, LocalDateTime signinAt, LocalDateTime lastUpdatedAt) {
        this.ip = ip;
        this.signinAt = signinAt;
        this.lastUpdatedAt = lastUpdatedAt;
    }

    public SessionModel(String ip, LocalDateTime lastUpdatedAt) {
        this.ip = ip;
        this.lastUpdatedAt = lastUpdatedAt;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public LocalDateTime getSigninAt() {
        return signinAt;
    }

    public void setSigninAt(LocalDateTime signinAt) {
        this.signinAt = signinAt;
    }

    public LocalDateTime getLastUpdatedAt() {
        return lastUpdatedAt;
    }

    public void setLastUpdatedAt(LocalDateTime lastUpdatedAt) {
        this.lastUpdatedAt = lastUpdatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SessionModel that = (SessionModel) o;
        return Objects.equals(ip, that.ip) &&
                Objects.equals(signinAt, that.signinAt) &&
                Objects.equals(lastUpdatedAt, that.lastUpdatedAt);
    }

    @Override
    public int hashCode() {

        return Objects.hash(ip, signinAt, lastUpdatedAt);
    }

    @Override
    public String toString() {
        return "SessionModel{" +
                "ip='" + ip + '\'' +
                ", signinAt=" + signinAt +
                ", lastUpdatedAt=" + lastUpdatedAt +
                '}';
    }
}
