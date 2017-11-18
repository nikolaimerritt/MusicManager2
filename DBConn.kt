package sample

import java.sql.DriverManager
import java.sql.ResultSet

class DBConn(private val username: String = "root", private val password: String = "yagamiLigh_t", private val dbName: String = "MusicLibrary")
{
	init { Class.forName("com.mysql.jdbc.Driver") }

	val connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/$dbName", username, password)

	fun resultSetFromQuery(query: String): ResultSet
	{
		val statement = connection.createStatement()
		return statement.executeQuery(query)
	}

	fun close() = connection.close()
}