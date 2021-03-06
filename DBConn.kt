package sample

import com.jcraft.jsch.ChannelExec
import com.jcraft.jsch.JSch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*

class DBConn(private val hostUser: String = "pi", private val hostPassword: String = "enumaEli_s", private val hostURL: String = "www.musicmanager.duckdns.org", private val dbUser: String = "root", private val dbPassword: String = "yagamiLigh_t", private val dbName: String = "MusicLibrary")
{
	private var connected = false
	private val session = JSch().getSession(hostUser, hostURL, 22)
	private val config = Properties()

	fun connect()
	{
		if (!connected)
		{
			session.setPassword(hostPassword)
			config.put("StrictHostKeyChecking", "no")
			session.setConfig(config)
			session.connect()
		}
		connected = true

	}

	private fun outputFromCommand(command: String): String
	{
		if (!connected) { connect() }
		val channel = session.openChannel("exec")
		val instream = BufferedReader(InputStreamReader(channel.inputStream))

		(channel as ChannelExec).setCommand(command)
		channel.connect()

		var output: String?
		var totalOutput = ""
		do
		{
			output = instream.readLine()
			totalOutput += if (output != null) { output } else { "" }
		} while (output != null)

		channel.disconnect()
		return totalOutput
	}

	fun linesFromQuery(query: String): Array<String>
	{
		val resultsAsLines = outputFromCommand("mysql --user=$dbUser --password=$dbPassword -D $dbName -e \"$query\" > foo.txt && perl -pi -e 's/\n/#/g' foo.txt && cat foo.txt").split("#")
		return if (resultsAsLines.isNotEmpty()) resultsAsLines.subList(1, resultsAsLines.size).filter { it.isNotBlank() }.toTypedArray() // first element is the column headings
		else resultsAsLines.toTypedArray() // which is empty
	}

	fun runQuery(query: String) { outputFromCommand("mysql --user=$dbUser --password=$dbPassword -D $dbName -e \"$query\"") }

	fun userExists(username: String, password: String) = linesFromQuery("SELECT * FROM User WHERE username = '$username' and password = '$password';").isNotEmpty()

	fun makeNewUser(username: String, password: String)
	{
		runQuery("INSERT INTO User VALUES ('$username', '$password');")
		runQuery("INSERT INTO Playlist VALUES ('All Songs', '$username', 0);")
	}

	fun close() = session.disconnect()

}