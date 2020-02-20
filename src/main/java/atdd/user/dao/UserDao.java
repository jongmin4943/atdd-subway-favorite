package atdd.user.dao;

import atdd.user.domain.Email;
import atdd.user.domain.User;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class UserDao {

    private static final RowMapper<User> USER_MAPPER = (rs, rowNum) -> {
        final long id = rs.getLong("id");
        final String email = rs.getString("email");
        final String name = rs.getString("name");
        final String password = rs.getString("password");
        return User.of(id, new Email(email), name, password);
    };

    private static final String FIND_USER = "select * from USERS where id = :id";
    private static final String DELETE_USER = "delete from users where id = :id";

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;

    public UserDao(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.simpleJdbcInsert = new SimpleJdbcInsert(namedParameterJdbcTemplate.getJdbcTemplate())
                .withTableName(User.TABLE_NAME)
                .usingGeneratedKeyColumns("id");
    }

    public User findById(Long id) {
        final Map<String, Long> parameter = Collections.singletonMap("id", id);
        return namedParameterJdbcTemplate.queryForObject(FIND_USER, parameter, USER_MAPPER);
    }

    public User create(User user) {
        final Number id = simpleJdbcInsert.executeAndReturnKey(new BeanPropertySqlParameterSource(user));
        return User.of(id.longValue(), new Email(user.getEmail()), user.getName(), user.getPassword());
    }

    public void delete(Long id) {
        final Map<String, Long> parameter = Collections.singletonMap("id", id);
        final int result = namedParameterJdbcTemplate.update(DELETE_USER, parameter);
        checkResult(id, result);
    }

    private void checkResult(Long id, int result) {
        if (result < 1) {
            throw new IllegalArgumentException("존재하지 않는 User 입니다.id : [" + id + "]");
        }
    }

}