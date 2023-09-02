package guru.qa.niffler.db.dao;

import guru.qa.niffler.db.DataSourceProvider;
import guru.qa.niffler.db.ServiceDB;
import guru.qa.niffler.db.mapper.UserEntityRowMapper;
import guru.qa.niffler.db.model.Authority;
import guru.qa.niffler.db.model.CurrencyValues;
import guru.qa.niffler.db.model.UserEntity;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

public class AuthUserDAOSpringJdbc implements AuthUserDAO, UserDataUserDAO {

    private final TransactionTemplate authTransactionTemplate;
    private final TransactionTemplate userdataTransactionTemplate;
    private final JdbcTemplate authJdbcTemplate;
    private final JdbcTemplate userdataJdbcTemplate;

    public AuthUserDAOSpringJdbc() {
        JdbcTransactionManager authTm = new JdbcTransactionManager(
                DataSourceProvider.INSTANCE.getDataSource(ServiceDB.AUTH));
        JdbcTransactionManager userdataTm = new JdbcTransactionManager(
                DataSourceProvider.INSTANCE.getDataSource(ServiceDB.USERDATA));

        this.authTransactionTemplate = new TransactionTemplate(authTm);
        this.userdataTransactionTemplate = new TransactionTemplate(userdataTm);
        this.authJdbcTemplate = new JdbcTemplate(authTm.getDataSource());
        this.userdataJdbcTemplate = new JdbcTemplate(userdataTm.getDataSource());
    }

    @Override
    public int createUser(UserEntity user) {
        return authTransactionTemplate.execute(status -> {
            KeyHolder kh = new GeneratedKeyHolder();

            authJdbcTemplate.update(con -> {
                PreparedStatement ps = con.prepareStatement("INSERT INTO users (username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired) " +
                        "VALUES (?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, user.getUsername());
                ps.setString(2, pe.encode(user.getPassword()));
                ps.setBoolean(3, user.getEnabled());
                ps.setBoolean(4, user.getAccountNonExpired());
                ps.setBoolean(5, user.getAccountNonLocked());
                ps.setBoolean(6, user.getCredentialsNonExpired());
                return ps;
            }, kh);
            final UUID userId = (UUID) kh.getKeyList().get(0).get("id");
            authJdbcTemplate.batchUpdate("INSERT INTO authorities (user_id, authority) VALUES (?, ?)", new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ps.setObject(1, userId);
                    ps.setObject(2, Authority.values()[i].name());
                }

                @Override
                public int getBatchSize() {
                    return Authority.values().length;
                }
            });
            user.setId(userId);
            return 1;
        });
    }

    @Override
    public UserEntity updateUser(UserEntity user) {
        authJdbcTemplate.update("UPDATE users SET password = ?, enabled = ?, account_non_expired = ?," +
                        "account_non_locked = ? WHERE id = ?",
                pe.encode(user.getPassword()), user.getEnabled(), user.getAccountNonExpired(),
                user.getAccountNonLocked(), user.getId()
        );
        return getUserById(user.getId());
    }
    @Override
    public void deleteUserById(UUID userId) {
        authTransactionTemplate.execute(status -> {
            authJdbcTemplate.update(con -> {
                PreparedStatement authorityPs = con.prepareStatement("DELETE from authorities WHERE user_id = ?");
                authorityPs.setObject(1, userId);
                return authorityPs;
            });
            authJdbcTemplate.update(con -> {
                PreparedStatement usersPs = con.prepareStatement("DELETE from users WHERE id = ?");
                usersPs.setObject(1, userId);
                return usersPs;
            });
            return 0;
        });
    }

    @Override
    public UserEntity getUserById(UUID userId) {
        return authJdbcTemplate.queryForObject(
                "SELECT * FROM users WHERE id = ? ",
                UserEntityRowMapper.instance,
                userId
        );
    }

    @Override
    public int createUserInUserData(UserEntity user) {
        return userdataJdbcTemplate.update(
                "INSERT INTO users (username, currency) VALUES (?, ?)",
                user.getUsername(),
                CurrencyValues.RUB.name()
        );
    }

    @Override
    public void deleteUserByIdInUserData(UUID userId) {
        userdataJdbcTemplate.update("DELETE FROM users WHERE id = ?", userId);
    }

    @Override
    public void deleteUserByNameInUserData(String username) {
        userdataJdbcTemplate.update("DELETE FROM users WHERE username = ?", username);
    }
}