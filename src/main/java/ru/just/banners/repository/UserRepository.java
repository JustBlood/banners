package ru.just.banners.repository;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import ru.just.banners.model.dao.UserRecord;
import ru.just.banners.security.TokenUtils;

import static ru.just.banners.tables.User.USER;

@RequiredArgsConstructor
@Repository
public class UserRepository {
    private final DSLContext jooq;

    public String createUser() {
        final String token = TokenUtils.generateToken();
        jooq.insertInto(USER)
                .set(USER.TOKEN, token)
                .set(USER.IS_ADMIN, false)
                .execute();
        return token;
    }

    public @Nullable UserRecord getUserByToken(String token) {
        return jooq.selectFrom(USER)
                .where(USER.TOKEN.eq(token))
                .fetchOneInto(UserRecord.class);
    }
}
