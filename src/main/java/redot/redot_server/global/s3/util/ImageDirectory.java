package redot.redot_server.global.s3.util;

import java.util.function.Function;

public enum ImageDirectory {
    ADMIN_PROFILE(id -> String.format("redot/admin/profile/%d", id)),
    CMS_MEMBER_PROFILE(id -> String.format("app/cms/member/profile/%d", id)),
    REDOT_MEMBER_PROFILE(id -> String.format("redot/member/profile/%d", id)),
    APP_LOGO(id -> String.format("app/%d/logo", id));

    private final Function<Long, String> pathResolver;

    ImageDirectory(Function<Long, String> pathResolver) {
        this.pathResolver = pathResolver;
    }

    public String resolve(Long ownerId) {
        return pathResolver.apply(ownerId);
    }
}
