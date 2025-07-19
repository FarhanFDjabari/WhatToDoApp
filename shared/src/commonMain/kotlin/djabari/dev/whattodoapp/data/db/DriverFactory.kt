package djabari.dev.whattodoapp.data.db

import app.cash.sqldelight.db.SqlDriver
import djabari.dev.whattodoapp.data.sqldelight.Database

expect class DriverFactory {
    fun createDriver(): SqlDriver
}

fun createDatabase(driverFactory: DriverFactory): Database {
    val driver = driverFactory.createDriver()
    return Database(driver)
}