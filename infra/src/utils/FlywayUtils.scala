package utils

import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import repositories.RepoSqlContext.DbConf
import org.flywaydb.core.Flyway

object FlywayUtils {

  def createFlyway(c: DbConf): Flyway = {
    val config = new HikariConfig()
    config.setDriverClassName("org.h2.Driver")
    config.setJdbcUrl(c.url)
    config.setUsername(c.user)
    config.setPassword(c.pass)
    Flyway
      .configure()
      .dataSource(new HikariDataSource(config))
      .locations("classpath:sql")
      .load()

  }
}
