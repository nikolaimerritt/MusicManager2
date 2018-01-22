package sample

import javafx.application.Application
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.control.Alert
import javafx.scene.control.Button
import javafx.scene.control.TextField
import javafx.scene.layout.GridPane
import javafx.stage.Stage


class Main : Application()
{
	override fun start(stage: Stage) // show login window
	{
		val grid = GridPane()
		val scene = Scene(grid)
		stage.scene = scene
		stage.title = "Log In"
		stage.setOnCloseRequest { System.exit(0) }
		grid.padding = Insets(10.0, 10.0, 10.0, 10.0)
		grid.vgap = 5.0
		grid.hgap = 5.0

		// login textbox
		val usernameBox = TextField()
		usernameBox.promptText = "User name"
		GridPane.setConstraints(usernameBox, 0, 0, 30, 1)
		grid.children.add(usernameBox)

		// password textbox
		val passwordBox = TextField()
		passwordBox.promptText = "Password"
		GridPane.setConstraints(passwordBox, 0, 1, 30, 1)
		grid.children.add(passwordBox)

		// login button
		val loginButton = Button("Log in!")
		loginButton.setOnAction {
			val dbConn = DBConn()
			println("u|${usernameBox.text}|, p|${passwordBox.text}|")
			if (dbConn.userExists(usernameBox.text, passwordBox.text))
			{
				MainWindow.show(User(usernameBox.text, passwordBox.text))
				stage.close()
			}
			else { Alert(Alert.AlertType.ERROR, "This user does not exist. Dobule-check you typed the details in correctly, or try creating a new user instead.").showAndWait() }
		}
		GridPane.setConstraints(loginButton, 0, 2)
		grid.children.add(loginButton)

		val newUserButton = Button("Make a new user!")
		newUserButton.setOnAction {
			val dbConn = DBConn()
			dbConn.connect()
			val username = usernameBox.text
			val password = passwordBox.text
			if (!dbConn.userExists(username, password))
			{
				dbConn.makeNewUser(username, password)
				MainWindow.show(User(username, password))
			}
			else { Alert(Alert.AlertType.ERROR, "This user alredy exists. Try logging in instead.").showAndWait() }
			dbConn.close()
		}
		GridPane.setConstraints(newUserButton, 5, 2)
		grid.children.add(newUserButton)

		stage.show()
	}

	private fun login(username: String, password: String) = MainWindow.show(User(username, password))

    companion object
    {
	    val FRESH_AF_PLAYLIST_NAME = "<New Playlist>"
        @JvmStatic fun main(args: Array<String>) { launch(Main::class.java) }
    }
}