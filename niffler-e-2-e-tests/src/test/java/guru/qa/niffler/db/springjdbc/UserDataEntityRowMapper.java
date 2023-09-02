package guru.qa.niffler.db.springjdbc;

import guru.qa.niffler.db.model.userdata.UserDataUserEntity;
import guru.qa.niffler.model.CurrencyValues;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class UserDataEntityRowMapper implements RowMapper<UserDataUserEntity> {

    public static final UserDataEntityRowMapper instance = new UserDataEntityRowMapper();

    @Override
    public UserDataUserEntity mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        UserDataUserEntity user = new UserDataUserEntity();
        user.setId(resultSet.getObject("id", UUID.class));
        user.setUsername(resultSet.getString("username"));
        user.setCurrency(CurrencyValues.valueOf(resultSet.getString("currency")));
        user.setFirstname(resultSet.getString("firstname"));
        user.setSurname(resultSet.getString("surname"));
        user.setPhoto(resultSet.getBytes("photo"));
        return user;
    }
}