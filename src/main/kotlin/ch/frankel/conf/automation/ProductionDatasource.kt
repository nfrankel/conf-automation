package ch.frankel.conf.automation

import org.apache.commons.dbcp.BasicDataSource
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.core.env.Environment
import org.springframework.core.env.get
import java.net.URI
import javax.sql.DataSource


fun productionDatasource(env: Environment, props: DataSourceProperties): DataSource {
    env["DATABASE_URL"]?.let {
        val uri = URI(it)
        val (username, password) = uri.userInfo.split(":")
        return BasicDataSource().apply {
            this.username = username
            this.password = password
            this.driverClassName = props.driverClassName
            this.url = "jdbc:postgresql://${uri.host}:${uri.port}${uri.path}"
        }
    }?: throw IllegalStateException("Environment variable DATABASE_URL not found. Did you configure your Heroku instance properly to use a SQL database?")
}