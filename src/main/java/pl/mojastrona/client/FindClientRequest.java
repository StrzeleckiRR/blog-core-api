package pl.mojastrona.client;


import jakarta.validation.constraints.NotNull;

import java.util.Objects;

public final class FindClientRequest {
    private final @NotNull Long accountantId;
    private final String name;

    public FindClientRequest(@NotNull Long accountantId, String name) {
        this.accountantId = accountantId;
        this.name = name;
    }

    public @NotNull Long accountantId() {
        return accountantId;
    }

    public String name() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (FindClientRequest) obj;
        return Objects.equals(this.accountantId, that.accountantId) &&
                Objects.equals(this.name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountantId, name);
    }

    @Override
    public String toString() {
        return "FindClientRequest[" +
                "accountantId=" + accountantId + ", " +
                "name=" + name + ']';
    }


}
