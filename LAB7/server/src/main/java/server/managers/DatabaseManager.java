package server.managers;

import common.models.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final Logger logger = LogManager.getLogger(DatabaseManager.class);
    private final String url;
    private final String user;
    private final String password;
    private Connection connection;

    public DatabaseManager(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }
    public void connect() {
        try {
            connection = DriverManager.getConnection(url, user, password);
            logger.info("Успешное подключение к базе данных");
            createTables();
        } catch (SQLException e) {
            logger.error("Ошибка подключения к БД: " + e.getMessage());
            System.exit(1);
        }
    }
    private void createTables() throws SQLException {
        String createUsersTable = """
                CREATE TABLE IF NOT EXISTS users (
                    login VARCHAR(255) PRIMARY KEY,
                    password_hash VARCHAR(255) NOT NULL
                );
                """;
        String createCitiesTable = """
                CREATE TABLE IF NOT EXISTS cities (
                    id SERIAL PRIMARY KEY,
                    name VARCHAR(255) NOT NULL,
                    coord_x DOUBLE PRECISION NOT NULL,
                    coord_y DOUBLE PRECISION NOT NULL,
                    creation_date DATE NOT NULL,
                    area BIGINT NOT NULL,
                    population BIGINT NOT NULL,
                    meters_above_sea_level REAL,
                    capital BOOLEAN,
                    government VARCHAR(255),
                    standard_of_living VARCHAR(50),
                    governor_birthday TIMESTAMP,
                    owner_login VARCHAR(255) REFERENCES users(login) ON DELETE CASCADE
                );
                """;

        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(createUsersTable);
            stmt.executeUpdate(createCitiesTable);
            logger.info("Таблицы в БД проверены/созданы.");
        }
    }
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-224");
            byte[] bytes = md.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            logger.error("Алгоритм SHA-224 не найден", e);
            throw new RuntimeException(e);
        }
    }
    public synchronized boolean registerUser(String login, String password) {
        String query = "INSERT INTO users (login, password_hash) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, login);
            stmt.setString(2, hashPassword(password));
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public synchronized boolean validateUser(String login, String password) {
        String query = "SELECT password_hash FROM users WHERE login = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, login);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String dbHash = rs.getString("password_hash");
                    return dbHash.equals(hashPassword(password));
                }
            }
        } catch (SQLException e) {
            logger.error("Ошибка при проверке пользователя: " + e.getMessage());
        }
        return false;
    }
    public synchronized int addCityToDB(City city, String login) {
        String query = """
                INSERT INTO cities (name, coord_x, coord_y, creation_date, area, population,
                meters_above_sea_level, capital, government, standard_of_living, governor_birthday,
                owner_login)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id;
                """;
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            setCityParameters(stmt, city, login);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            logger.error("Ошибка при добавлении города в БД: " + e.getMessage());
        }
        return -1;
    }
    public synchronized boolean updateCityInBD(int id, City newCity, String login) {
        String query = """
                UPDATE cities SET name=?, coord_x=?, coord_y=?, creation_date=?,
                area=?, population=?, meters_above_sea_level=?, capital=?, government=?,
                standard_of_living=?, governor_birthday=?
                WHERE id=? AND owner_login=?;
                """;
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, newCity.getName());
            stmt.setDouble(2, newCity.getCoordinates().getX());
            stmt.setDouble(3, newCity.getCoordinates().getY());
            stmt.setDate(4, Date.valueOf(newCity.getCreationDate() != null ? newCity.getCreationDate() : LocalDate.now()));
            stmt.setLong(5, newCity.getArea());
            stmt.setLong(6, newCity.getPopulation());
            if (newCity.getMetersAboveSeaLevel() != null) {
                stmt.setFloat(7, newCity.getMetersAboveSeaLevel());
            } else {
                stmt.setNull(7, Types.REAL);
            }
            if (newCity.getCapital() != null) {
                stmt.setBoolean(8, newCity.getCapital());
            } else {
                stmt.setNull(8, Types.BOOLEAN);
            }
            if (newCity.getGovernment() != null) {
                stmt.setString(9, newCity.getGovernment().name());
            } else {
                stmt.setNull(9, Types.VARCHAR);
            }
            if (newCity.getStandardOfLiving() != null) {
                stmt.setString(10, newCity.getStandardOfLiving().name());
            } else {
                stmt.setNull(10, Types.VARCHAR);
            }
            if (newCity.getGovernor() != null && newCity.getGovernor().getBirthday() != null) {
                stmt.setTimestamp(11, Timestamp.valueOf(newCity.getGovernor().getBirthday()));
            } else {
                stmt.setNull(11, Types.TIMESTAMP);
            }
            stmt.setInt(12, id);
            stmt.setString(13, login);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            logger.error("Ошибка при обновлении города в БД: " + e.getMessage());
        }
        return false;
    }
    public synchronized boolean removeCityByIdFromDB(int id, String login) {
        String query = "DELETE FROM cities WHERE id=? AND owner_login=?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.setString(2, login);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Ошибка при удалении города из БД: " + e.getMessage());
        }
        return false;
    }
    public synchronized boolean clearCitiesOwnedByFromDb(String login) {
        String query = "DELETE FROM cities WHERE owner_login=?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, login);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            logger.error("Ошибка при очистке городов в БД: " + e.getMessage());
        }
        return false;
    }
    public synchronized List<City> loadAllCities() {
        List<City> cities = new ArrayList<>();
        String query = "SELECT * FROM cities";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                double x = rs.getDouble("coord_x");
                double y = rs.getDouble("coord_y");
                LocalDate creationDate = rs.getDate("creation_date").toLocalDate();
                Long area = rs.getLong("area");
                long population = rs.getLong("population");

                Float metersAboveSeaLevel = (Float) rs.getObject("meters_above_sea_level");
                Boolean capital = (Boolean) rs.getObject("capital");

                String govStr = rs.getString("government");
                Government government = govStr != null ? Government.valueOf(govStr) : null;

                String solStr = rs.getString("standard_of_living");
                StandardOfLiving standardOfLiving = solStr != null ? StandardOfLiving.valueOf(solStr) : null;

                Timestamp govBirthTS = rs.getTimestamp("governor_birthday");
                Human governor = govBirthTS != null ? new Human(govBirthTS.toLocalDateTime()) : null;

                String ownerLogin = rs.getString("owner_login");

                Coordinates coords = new Coordinates(x, y);
                City city = new City(name, coords, area, population, metersAboveSeaLevel,
                        capital, government, standardOfLiving, governor);

                city.setId(id);
                city.setCreationDate(creationDate);
                city.setOwnerLogin(ownerLogin);
                cities.add(city);
            }
        } catch (SQLException e) {
            logger.error("Ошибка при загрузке городов из БД: " + e.getMessage());
        }
        return cities;
    }
    private void setCityParameters(PreparedStatement stmt, City city, String login) throws SQLException {
        stmt.setString(1, city.getName());
        stmt.setDouble(2, city.getCoordinates().getX());
        stmt.setDouble(3, city.getCoordinates().getY());
        stmt.setDate(4, Date.valueOf(city.getCreationDate() != null ? city.getCreationDate() : LocalDate.now()));
        stmt.setLong(5, city.getArea());
        stmt.setLong(6, city.getPopulation());
        if (city.getMetersAboveSeaLevel() != null) {
            stmt.setFloat(7, city.getMetersAboveSeaLevel());
        } else {
            stmt.setNull(7, Types.REAL);
        }
        if (city.getCapital() != null) {
            stmt.setBoolean(8, city.getCapital());
        } else {
            stmt.setNull(8, Types.BOOLEAN);
        }
        if (city.getGovernment() != null) {
            stmt.setString(9, city.getGovernment().name());
        } else {
            stmt.setNull(9, Types.VARCHAR);
        }
        if (city.getStandardOfLiving() != null) {
            stmt.setString(10, city.getStandardOfLiving().name());
        } else {
            stmt.setNull(10, Types.VARCHAR);
        }
        if (city.getGovernor() != null && city.getGovernor().getBirthday() != null) {
            stmt.setTimestamp(11, Timestamp.valueOf(city.getGovernor().getBirthday()));
        } else {
            stmt.setNull(11, Types.TIMESTAMP);
        }
        if (stmt.getParameterMetaData().getParameterCount() == 12) {
            stmt.setString(12, login);
        }
    }
}